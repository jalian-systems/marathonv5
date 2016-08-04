/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.ruby;

import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyInstanceConfig;
import org.jruby.RubyInstanceConfig.CompileMode;
import org.jruby.embed.io.WriterOutputStream;
import org.jruby.exceptions.RaiseException;
import org.jruby.internal.runtime.GlobalVariable.Scope;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.GlobalVariable;
import org.jruby.runtime.builtin.IRubyObject;

import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IDebugger;
import net.sourceforge.marathon.runtime.api.IPlaybackListener;
import net.sourceforge.marathon.runtime.api.IPlayer;
import net.sourceforge.marathon.runtime.api.IScript;
import net.sourceforge.marathon.runtime.api.MarathonPlayer;
import net.sourceforge.marathon.runtime.api.Module;
import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;
import net.sourceforge.marathon.runtime.api.ScriptException;

public class RubyScript implements IScript {

    private static final Logger logger = Logger.getLogger(RubyScript.class.getName());

    private static final Pattern FUNCTION_PATTERN = Pattern.compile("(.*)/(.*\\(.*)", Pattern.DOTALL | Pattern.MULTILINE);
    public static final String PROP_APPLICATION_RUBYPATH = "marathon.application.rubypath";
    public static final String PROP_APPLICATION_RUBYHOME = "marathon.application.rubyhome";

    private final class FixtureRunner implements Runnable {
        private static final String MODULE = "Ruby Script";
        private final boolean fixture;
        private Thread thread;
        private boolean setupFailed = false;

        private FixtureRunner(boolean fixture, Thread playbackThread) {
            this.fixture = fixture;
            this.thread = playbackThread;
        }

        @Override public void run() {
            if (fixture) {
                invokeAndWaitForWindow(new Runnable() {
                    @Override public void run() {
                        try {
                            try {
                                RuntimeLogger.getRuntimeLogger().info(MODULE, "Running fixture setup...");
                                debugger.run("$marathon.execFixtureSetup");
                                RuntimeLogger.getRuntimeLogger().info(MODULE, "Running fixture setup... Done");
                            } catch (Throwable t) {
                                isTeardownCalled = true;
                                debugger.run("$marathon.execFixtureTeardown");
                                setupFailed = true;
                                thread.interrupt();
                            }
                        } catch (Throwable t) {
                            thread.interrupt();
                        }
                    }

                });
                if (setupFailed) {
                    return;
                }
                try {
                    debugger.run("$marathon.execTestSetup");
                    debugger.run("$marathon.execTest($test)");
                } finally {
                    isTeardownCalled = true;
                    debugger.run("$marathon.execFixtureTeardown");
                }
            } else {
                debugger.run("$marathon.execTestSetup");
                debugger.run("$marathon.execTest($test)");
            }
        }
    }

    private String script;
    private String filename;
    private Ruby interpreter;
    private MarathonRuby runtime;
    private RubyDebugger debugger;
    private ModuleList moduleList;
    private boolean isTeardownCalled = false;
    private ArrayList<String> assertionProviderList;
    private String driverURL;
    private String framework;

    private Properties dataVariables;

    public RubyScript(Writer out, Writer err, String script, String filename, boolean isDebugging, Properties dataVariables,
            String framework) {
        this.script = script;
        this.filename = filename;
        this.dataVariables = dataVariables;
        this.framework = framework;
        loadScript(out, err, isDebugging);
    }

    @Override public void setDriverURL(String driverURL) {
        this.driverURL = driverURL;
        readGlobals();
        debugger = new RubyDebugger(interpreter);
    }

    private void readGlobals() {
        interpreter.evalScriptlet("$marathon = RubyMarathon.new('" + driverURL + "')");
        IRubyObject marathon = interpreter.evalScriptlet("$marathon");
        interpreter.evalScriptlet("$test = proc { test }");
        runtime = (MarathonRuby) JavaEmbedUtils.rubyToJava(interpreter, marathon, MarathonRuby.class);
    }

    private void loadScript(final Writer out, final Writer err, boolean isDebugging) {
        try {
            interpreter = RubyInterpreters.get(getInitRuby(out, err));
            defineVariable("marathon_script_handle", this);
            moduleList = new ModuleList(interpreter, Constants.getMarathonDirectoriesAsStringArray(Constants.PROP_MODULE_DIRS));
            loadAssertionProviders();
            defineVariable("test_file", filename);
            defineVariable("test_name", getTestName());
            defineVariable("project_dir", System.getProperty(Constants.PROP_PROJECT_DIR));
            defineVariable("marathon_home", System.getProperty(Constants.PROP_HOME));
            defineVariable("marathon_project_name", System.getProperty(Constants.PROP_PROJECT_NAME));
            defineVariable("marathon_project_dir", System.getProperty(Constants.PROP_PROJECT_DIR));
            defineVariable("marathon_fixture_dir", System.getProperty("marathon.fixture.dir"));
            defineVariable("marathon_test_dir", System.getProperty(Constants.PROP_TEST_DIR));
            if (dataVariables != null) {
                setDataVariables(dataVariables);
            }
            interpreter.executeScript(script, filename);
        } catch (RaiseException e) {
            throw new ScriptException(e.getException().toString(), e);
        } catch (Throwable t) {
            throw new ScriptException(t.getMessage(), t);
        }
    }

    protected Callable<Ruby> getInitRuby(final Writer out, final Writer err) {
        return new Callable<Ruby>() {
            @Override public Ruby call() throws Exception {
                RubyInstanceConfig config = new RubyInstanceConfig();
                config.setCompileMode(CompileMode.OFF);
                List<String> loadPaths = new ArrayList<String>();
                setModule(loadPaths);
                String appRubyPath = System.getProperty(PROP_APPLICATION_RUBYPATH);
                if (appRubyPath != null) {
                    StringTokenizer tok = new StringTokenizer(appRubyPath, ";");
                    while (tok.hasMoreTokens()) {
                        loadPaths.add(tok.nextToken().replace('/', File.separatorChar));
                    }
                }
                config.setOutput(new PrintStream(new WriterOutputStream(out)));
                config.setError(new PrintStream(new WriterOutputStream(err)));
                Ruby interpreter = JavaEmbedUtils.initialize(loadPaths, config);
                interpreter.evalScriptlet("require 'selenium/webdriver'");
                interpreter.evalScriptlet("require 'marathon/results'");
                interpreter.evalScriptlet("require 'marathon/playback-" + framework + "'");
                return interpreter;
            }
        };
    }

    private void defineVariable(String variable, String value) {
        try {
            GlobalVariable v = new GlobalVariable(interpreter, "$" + variable, interpreter.newString(value));
            interpreter.defineVariable(v, Scope.GLOBAL);
        } catch (Throwable t) {
            throw new ScriptException("Unable to define variable " + variable + " value = " + value, t);
        }
    }

    private void defineVariable(String variable, int value) {
        try {
            GlobalVariable v = new GlobalVariable(interpreter, "$" + variable, interpreter.newFixnum(value));
            interpreter.defineVariable(v, Scope.GLOBAL);
        } catch (Throwable t) {
            throw new ScriptException(t.getMessage(), t);
        }
    }

    private void defineVariable(String variable, double value) {
        try {
            GlobalVariable v = new GlobalVariable(interpreter, "$" + variable, interpreter.newFloat(value));
            interpreter.defineVariable(v, Scope.GLOBAL);
        } catch (Throwable t) {
            throw new ScriptException(t.getMessage(), t);
        }
    }

    private void defineVariable(String variable, Object value) {
        try {
            GlobalVariable v = new GlobalVariable(interpreter, "$" + variable, JavaEmbedUtils.javaToRuby(interpreter, value));
            interpreter.defineVariable(v, Scope.GLOBAL);
        } catch (Throwable t) {
            throw new ScriptException("Failed to define variable " + variable + " value = " + value + ":" + t.getMessage(), t);
        }
    }

    private String getTestName() {
        String name = new File(filename).getName().toUpperCase();
        if (name.endsWith(".RB")) {
            return name.substring(0, name.length() - 3);
        }
        return name;
    }

    private void setModule(List<String> segments) {
        try {
            String[] ModuleDirs = Constants.getMarathonDirectoriesAsStringArray(Constants.PROP_MODULE_DIRS);
            for (String moduleDir : ModuleDirs) {
                segments.add(new File(moduleDir).getCanonicalFile().getCanonicalPath());
            }
            // segments.add(new
            // File(System.getProperty(Constants.PROP_FIXTURE_DIR)).getCanonicalFile().getCanonicalPath());
            File assertionDir = new File(System.getProperty(Constants.PROP_PROJECT_DIR), "Assertions");
            if (assertionDir.exists() && assertionDir.isDirectory()) {
                segments.add(assertionDir.getCanonicalFile().getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void exec(String function) {
        try {
            Matcher matcher = FUNCTION_PATTERN.matcher(function);
            if (matcher.matches()) {
                interpreter.evalScriptlet("load '" + matcher.group(1) + ".rb'");
                interpreter.evalScriptlet(matcher.group(2));
            } else {
                interpreter.evalScriptlet(function);
            }
        } catch (Throwable t) {
            if (t instanceof ScriptException) {
                throw (ScriptException) t;
            }
            throw new ScriptException(t.getMessage(), t);
        }
    }

    @Override public IDebugger getDebugger() {
        return debugger;
    }

    @Override public Module getModuleFunctions() {
        return moduleList.getTop();
    }

    @Override public IPlayer getPlayer(IPlaybackListener playbackListener, PlaybackResult result) {
        runtime.result = result;
        return new MarathonPlayer(this, playbackListener, result);
    }

    private void invokeAndWaitForWindow(Runnable runnable) {
        runnable.run();
    }

    protected IRubyObject getFixture() {
        return interpreter.evalScriptlet("$fixture");
    }

    public void runFixtureTeardown() {
        if (!isTeardownCalled) {
            isTeardownCalled = true;
            getFixture().callMethod(interpreter.getCurrentContext(), "teardown");
        }
    }

    public void topLevelWindowCreated(Window arg0) {
        synchronized (RubyScript.this) {
            notifyAll();
        }
    }

    public void topLevelWindowDestroyed(Window arg0) {
    }

    public Ruby getInterpreter() {
        return interpreter;
    }

    @Override public void attachPlaybackListener(IPlaybackListener listener) {
        debugger.setListener(listener);
    }

    @Override public Runnable playbackBody(boolean shouldRunFixture, Thread playbackThread) {
        return new FixtureRunner(shouldRunFixture, playbackThread);
    }

    @Override public String evaluate(String code) {
        try {
            return interpreter.evalScriptlet(code).inspect().toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return "";
    }

    void loadAssertionProviders() {
        File defaultDir = new File(System.getProperty(Constants.PROP_PROJECT_DIR), "Assertions");
        if (defaultDir.exists() && defaultDir.isDirectory()) {
            loadAssertionProvidersFromDir(defaultDir);
        }
    }

    private void findAssertionProviderMethods() {
        IRubyObject ro = interpreter.evalScriptlet("Object.private_instance_methods");
        Object[] methods = ((RubyArray) JavaEmbedUtils.rubyToJava(interpreter, ro, String[].class)).toArray();
        assertionProviderList = new ArrayList<String>();
        for (Object method : methods) {
            if (method.toString().startsWith("marathon_assert_")) {
                assertionProviderList.add(method.toString());
            }
        }
    }

    private void loadAssertionProvidersFromDir(final File dirFile) {
        File[] listFiles = dirFile.listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
                return dir.equals(dirFile) && name.endsWith(".rb");
            }
        });
        if (listFiles != null) {
            for (File listFile : listFiles) {
                try {
                    String fileName = listFile.getName();
                    interpreter.executeScript("require '" + fileName.substring(0, fileName.length() - 3) + "'", "<internal>");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        findAssertionProviderMethods();
    }

    public boolean isCustomAssertionsAvailable() {
        return true;
    }

    public String[][] getCustomAssertions(Object o) {
        ArrayList<String[]> assertions = new ArrayList<String[]>();
        return assertions.toArray(new String[assertions.size()][]);
    }

    @Override public void setDataVariables(Properties dataVariables) {
        Set<Entry<Object, Object>> set = dataVariables.entrySet();
        for (Entry<Object, Object> entry : set) {
            try {
                String key = (String) entry.getKey();
                String value = entry.getValue().toString();
                if (value.startsWith("\"") && value.endsWith("\"") || value.startsWith("'") || value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1);
                    defineVariable(key, value);
                } else {
                    try {
                        int v = Integer.parseInt(value);
                        defineVariable(key, v);
                    } catch (NumberFormatException e) {
                        try {
                            double v = Double.parseDouble(value);
                            defineVariable(key, v);
                        } catch (NumberFormatException e1) {
                            defineVariable(key, value);
                        }
                    }
                }
            } catch (Throwable t) {
                throw new ScriptException(t.getMessage(), t);
            }
        }
    }

    @Override public void quit() {
        try {
            runtime.quit();
        } catch (Throwable t) {
            logger.warning("Ignoring exception " + t.getClass().getName() + " on quit()");
        } finally {
            RubyInterpreters.release(interpreter);
        }
    }

    @Override public File getScreenCapture() {
        return runtime.getScreenCapture();
    }

    @Override public void runFixtureSetup() {
        getFixture().callMethod(interpreter.getCurrentContext(), "setup");
        if (getFixture().respondsTo("test_setup")) {
            getFixture().callMethod(interpreter.getCurrentContext(), "test_setup");
        }
    }

    @Override public void onWSConnectionClose(int port) {
        if (runtime != null) {
            runtime.onWSConnectionClose(port);
        }
    }

    @Override public boolean isDriverAvailable() {
        return runtime.isDriverAvailable();
    }
}
