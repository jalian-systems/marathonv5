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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jruby.Ruby;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Argument;
import net.sourceforge.marathon.runtime.api.Argument.Type;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.FixturePropertyHelper;
import net.sourceforge.marathon.runtime.api.Function;
import net.sourceforge.marathon.runtime.api.IScript;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.Module;
import net.sourceforge.marathon.runtime.api.WindowId;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;

public class RubyScriptModel implements IScriptModel {

    private static final String EOL = System.getProperty("line.separator");
    public static final String MARATHON_START_MARKER = "#{{{ Marathon";
    public static final String MARATHON_END_MARKER = "#}}} Marathon";

    private static Ruby ruby;
    private int lastModuleInsertionPoint;

    static {
        RubyInstanceConfig.FULL_TRACE_ENABLED = true;
        ruby = JavaEmbedUtils.initialize(new ArrayList<String>());
    }

    protected FixtureGenerator getFixtureGenerator() {
        FixtureGenerator fixtureGenerator = new FixtureGenerator();
        return fixtureGenerator;
    }

    @Override public String getDefaultFixtureHeader(Properties props, String launcher, List<String> keys) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new FixtureGenerator().printFixture(props, ps, launcher, keys);
        return baos.toString();
    }

    @Override public String getDefaultTestHeader(String fixture) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ps.println(MARATHON_START_MARKER);
        ps.print("require_fixture '");
        ps.print(fixture);
        ps.println("'");
        ps.println(MARATHON_END_MARKER);
        ps.println();
        ps.println("def test");
        ps.println();
        ps.println();
        ps.println("end");

        return new String(baos.toByteArray());
    }

    @Override public String getFixtureHeader(String fixture) {
        return "require_fixture '" + fixture + "'\n";
    }

    @Override public String[] getFixtures() {
        File fixtureDir = new File(System.getProperty(Constants.PROP_FIXTURE_DIR));
        File[] fixtureFiles = fixtureDir.listFiles(new FileFilter() {
            @Override public boolean accept(File pathname) {
                if (pathname.getName().endsWith(".rb")) {
                    return true;
                }
                return false;
            }
        });
        if (fixtureFiles == null) {
            fixtureFiles = new File[0];
        }
        String[] fixtures = new String[fixtureFiles.length];
        for (int i = 0; i < fixtureFiles.length; i++) {
            File file = fixtureFiles[i];
            fixtures[i] = file.getName().substring(0, file.getName().length() - 3);
        }
        Arrays.sort(fixtures);
        return fixtures;
    }

    private String encodeArg(String text, Argument argument) {
        if (argument.getType() == Type.REGEX) {
            return "/" + text + "/";
        }
        String decoded = ruby.evalScriptlet("\"" + text + "\"").toString();
        return encode(decoded);
    }

    @Override public String getFunctionCallForInsertDialog(Function function, String[] arguments) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < arguments.length - 1; i += 1) {
            buffer.append(encodeArg(arguments[i], function.getArguments().get(i)));
            buffer.append(", ");
        }
        if (arguments.length != 0) {
            buffer.append(encodeArg(arguments[arguments.length - 1], function.getArguments().get(arguments.length - 1)));
        }
        String require = getRequire(function);
        return require + function.getName() + "(" + buffer.toString() + ")";
    }

    private String getRequire(Function function) {
        StringBuilder require = new StringBuilder();

        Module parent = function.getParent();
        while (parent.getParent() != null) {
            require.insert(0, "/").insert(0, parent.getName());
            parent = parent.getParent();
        }
        return require.toString();
    }

    @Override public String getModuleHeader(String moduleFunction, String description) {
        String prefix = "=begin" + EOL + description + EOL + "=end" + EOL + EOL + "def " + moduleFunction + "()" + EOL + EOL
                + "    ";
        lastModuleInsertionPoint = prefix.length();
        return prefix + EOL + "end" + EOL;
    }

    public static String escape(String encode) {
        if (encode.startsWith("/")) {
            return "/" + encode;
        }
        return encode;
    }

    public String getScriptCodeForCapture(String windowName, String fileName) {
        String result;
        if (windowName == null) {
            result = "screen_capture(" + encode(fileName) + ")\n";
        } else {
            result = "window_capture(" + encode(fileName) + ", " + encode(windowName) + ")\n";
        }
        return result;
    }

    @Override public String getScriptCodeForImportAction(String pkg, String function) {
        return "require '" + pkg + "'";
    }

    @Override public String getScriptCodeForWindow(WindowId windowId2) {
        return "with_" + windowId2.getContainerType() + "(" + encode(windowId2.getTitle()) + ") {\n";
    }

    @Override public String getScriptCodeForWindowClose(WindowId windowId) {
        return "}\n";
    }

    @Override public String getSuffix() {
        return ".rb";
    }

    @Override public boolean isSourceFile(File f) {
        return f.getName().endsWith(".rb") && !f.getName().startsWith(".");
    }

    @Override public String[] parseMessage(String msg) {
        Pattern p = Pattern.compile(".*\\((.*.rb):(.*)\\).*");
        Matcher matcher = p.matcher(msg);
        String[] elements = null;
        if (matcher.matches()) {
            elements = new String[2];
            elements[0] = matcher.group(1);
            elements[1] = matcher.group(2);
        }
        return elements;
    }

    @Override public String getFunctionFromInsertDialog(String function) {
        String pkg = getPackageName(function);
        if (pkg != null) {
            return function.substring(pkg.length() + 1);
        }
        return function;
    }

    @Override public String getPackageFromInsertDialog(String function) {
        return getPackageName(function);
    }

    private String getPackageName(String f) {
        int index = f.indexOf('(');
        if (index != -1) {
            f = f.substring(0, index);
        }
        f = f.trim();
        index = f.lastIndexOf('/');
        if (index == -1) {
            return null;
        }
        return f.substring(0, index);
    }

    @Override public boolean isTestFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches(".*def.*test.*")) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
        }
        return false;
    }

    @Override public int getLinePositionForInsertion() {
        return 6;
    }

    public String getScriptCodeForWindowClosing(WindowId id) {
        return "window_closed(" + encode(id.toString()) + ")\n";
    }

    @Override public String getScriptCodeForInsertChecklist(String fileName) {
        return "accept_checklist(" + encode(fileName) + ")\n";
    }

    @Override public String getScriptCodeForShowChecklist(String fileName) {
        return "show_checklist(" + encode(fileName) + ")\n";
    }

    public static String encode(String name) {
        if (name == null) {
            name = "";
        }
        return inspect(name);
    }

    public static String inspect(String string) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"' || c == '\\') {
                sb.append("\\").append(c);
            } else if (c == '#' && chars.length != i + 1 && chars[i + 1] == '{') {
                sb.append("\\").append(c);
            } else if (c == '\n') {
                sb.append("\\").append('n');
            } else if (c == '\r') {
                sb.append("\\").append('r');
            } else if (c == '\t') {
                sb.append("\\").append('t');
            } else if (c == '\f') {
                sb.append("\\").append('f');
            } else if (c == '\013') {
                sb.append("\\").append('v');
            } else if (c == '\010') {
                sb.append("\\").append('b');
            } else if (c == '\007') {
                sb.append("\\").append('a');
            } else if (c == '\033') {
                sb.append("\\").append('e');
            } else {
                sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    @Override public int getLinePositionForInsertionModule() {
        return lastModuleInsertionPoint;
    }

    @Override public String updateScriptWithImports(String text, HashSet<String> importStatements) {
        StringBuilder sb = new StringBuilder(text);
        int endOffset = sb.indexOf(RubyScriptModel.MARATHON_END_MARKER);
        if (endOffset == -1) {
            StringWriter sw = new StringWriter();
            PrintWriter ps = new PrintWriter(sw);
            ps.println(MARATHON_START_MARKER);
            for (String ims : importStatements) {
                ps.println(ims);
            }
            ps.println(MARATHON_END_MARKER);
            ps.close();
            sb.replace(0, 0, sw.toString());
        } else {
            int startOffset = sb.indexOf(MARATHON_START_MARKER);
            if (startOffset == -1) {
                startOffset = 0;
            }
            String header = text.substring(startOffset, endOffset);
            for (String ims : importStatements) {
                if (!header.contains(ims)) {
                    sb.replace(endOffset, endOffset, ims + EOL);
                    endOffset = sb.indexOf(MARATHON_END_MARKER);
                }
            }
        }
        return sb.toString();
    }

    public static Ruby getRubyInterpreter() {
        return ruby;
    }

    public String getJavaRecordedVersionTag() {
        return "$java_recorded_version=\"" + System.getProperty("java.version") + "\"";
    }

    @Override public void fileUpdated(File file, SCRIPT_FILE_TYPE type) {
        if (type == SCRIPT_FILE_TYPE.MODULE || type == SCRIPT_FILE_TYPE.FIXTURE) {
            RubyInterpreters.clear();
        }
    }

    @Override public String getMarathonStartMarker() {
        return MARATHON_START_MARKER;
    }

    @Override public String getMarathonEndMarker() {
        return MARATHON_END_MARKER;
    }

    @Override public String getPlaybackImportStatement() {
        return "";
    }

    private static final Pattern FIXTURE_IMPORT_MATCHER = Pattern.compile("\\s*require_fixture\\s\\s*['\"](.*)['\"].*");

    @Override public Map<String, Object> getFixtureProperties(String script) {
        return new FixturePropertyHelper(this).getFixtureProperties(script, FIXTURE_IMPORT_MATCHER);
    }

    @Override public Object eval(String script) {
        return ruby.evalScriptlet(script);
    }

    @Override public IScript createScript(Writer out, Writer err, String scriptText, String filePath, boolean isRecording,
            boolean isDebugging, Properties dataVariables, String framework) {
        return new RubyScript(out, err, scriptText, filePath, isDebugging, dataVariables, framework);
    }

    @Override public String getScriptCodeForGenericAction(String method, String name, Object... params) {
        return method + "(" + encode(name) + paramString(params) + ")\n";
    }

    private String paramString(Object[] params) {
        if (params.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(", ");
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof String) {
                sb.append(encode((String) params[i]));
            } else {
                sb.append(params[i].toString());
            }
            if (i != params.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override public void createDefaultFixture(Stage window, Properties props, File fixtureDir, List<String> keys) {
        FixtureGenerator fixtureGenerator = getFixtureGenerator();
        File fixtureFile = new File(fixtureDir, "default.rb");
        if (fixtureFile.exists()) {
            Optional<ButtonType> result = FXUIUtils.showConfirmDialog(null,
                    "File " + fixtureFile + " exists\nDo you want to overwrite", "File Exists", AlertType.CONFIRMATION,
                    ButtonType.YES, ButtonType.NO);
            if (result.get() != ButtonType.YES) {
                return;
            }
        }
        PrintStream ps = null;
        try {
            ps = new PrintStream(new FileOutputStream(fixtureFile));
            String launcher = props.getProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL);
            props.setProperty(Constants.FIXTURE_DESCRIPTION, props.getProperty(Constants.FIXTURE_DESCRIPTION, "Default Fixture"));
            fixtureGenerator.printFixture(props, ps, launcher, keys);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override public ISubPropertiesLayout[] getSublayouts(ModalDialog<?> parent) {
        return new ISubPropertiesLayout[] { new RubyPathLayout(parent) };
    }
}
