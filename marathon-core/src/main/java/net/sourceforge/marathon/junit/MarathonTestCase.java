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
package net.sourceforge.marathon.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import net.sourceforge.marathon.api.GuiceInjector;
import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.checklist.CheckList;
import net.sourceforge.marathon.checklist.CheckListFormNode;
import net.sourceforge.marathon.checklist.CheckListStage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.resource.Project;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Constants.MarathonMode;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.IMarathonRuntime;
import net.sourceforge.marathon.runtime.api.IPlaybackListener;
import net.sourceforge.marathon.runtime.api.IPlayer;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.IScript;
import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.SourceLine;
import net.sourceforge.marathon.screencapture.AnnotateScreenCapture;
import net.sourceforge.marathon.util.LauncherModelHelper;

public class MarathonTestCase extends TestCase implements IPlaybackListener, Test, IHasFullname {

    public static final Logger LOGGER = Logger.getLogger(MarathonTestCase.class.getCanonicalName());

    private @Inject IRuntimeFactory runtimeFactory;

    private File file;
    private static IMarathonRuntime runtime = null;
    private final Object waitLock = new Object();
    private PlaybackResult result;
    private IScript script;
    private ArrayList<CheckList> checkLists = new ArrayList<CheckList>();
    private ArrayList<File> screenCaptures = new ArrayList<File>();
    private boolean acceptChecklist;
    private IConsole console;
    private Properties dataVariables;
    @SuppressWarnings("unused") private String nameSuffix = "";

    private String fullName;

    private boolean reuseFixture;
    private boolean ignoreReuse;

    private boolean shouldRunFixture;

    public MarathonTestCase(File file, boolean acceptChecklist, IConsole console) {
        this(file, null);
        this.acceptChecklist = acceptChecklist;
        this.console = console;
    }

    MarathonTestCase(File file, IMarathonRuntime runtime) {
        this.file = file;
        if (runtime != null) {
            MarathonTestCase.runtime = runtime;
        }
        this.acceptChecklist = false;
        Injector injector = GuiceInjector.get();
        injector.injectMembers(this);
    }

    public MarathonTestCase(File file, boolean acceptChecklist, IConsole console, Properties dataVariables, String name) {
        this(file, acceptChecklist, console);
        this.dataVariables = dataVariables;
        this.nameSuffix = name;
    }

    @Override public String getName() {
        return Project.getTestName(file);
    }

    @Override public void setName(String name) {
        Project.setTestName(name, file);
    }

    @Override public void run(TestResult result) {
        TestAttributes.put("test_object", this);
        super.run(result);
    }

    @Override protected synchronized void runTest() throws Throwable {
        if (script == null) {
            initialize();
        }
        try {
            IPlayer player = script.getPlayer(MarathonTestCase.this, new PlaybackResult());
            player.setAcceptCheckList(acceptChecklist);
            synchronized (waitLock) {
                player.play(shouldRunFixture);
                waitLock.wait();
            }
            confirmResult();
        } catch (Throwable t) {
            throw t;
        } finally {
            if (runtime != null)
                runtime.releaseInterpreters();
            if (runtime != null && (!reuseFixture || ignoreReuse)) {
                LOGGER.info("Destroying VM");
                runtime.destroy();
                runtime = null;
            }
            script = null;
        }
    }

    private String getScriptContents() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringWriter sWriter = new StringWriter(8192);
        PrintWriter writer = new PrintWriter(sWriter);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } finally {
            writer.close();
            reader.close();
        }
        return sWriter.toString();
    }

    @Override public void playbackFinished(PlaybackResult result, boolean shutdown) {
        this.result = result;
        synchronized (waitLock) {
            waitLock.notify();
        }
    }

    @Override public int lineReached(SourceLine line) {
        return CONTINUE;
    }

    private void confirmResult() {
        ignoreReuse = false;
        if (result.failureCount() == 0) {
            return;
        }
        ignoreReuse = true;
        String dirName = System.getProperty(Constants.PROP_IMAGE_CAPTURE_DIR);
        if (dirName != null) {
            File dir = new File(dirName);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override public boolean accept(File dir, String name) {
                    return name.matches(Pattern.quote(getFullName()) + "-error[0-9]*.png");
                }
            });
            if (files != null) {
                for (File file : files) {
                    addErrorScreenCapture(file);
                }
            }
        }
        MarathonAssertion e = new MarathonAssertion(result.failures(), this.getName());
        throw e;
    }

    private void addErrorScreenCapture(File file) {
        screenCaptures.add(file);
    }

    public File getFile() {
        return file;
    }

    @Override public int methodReturned(SourceLine line) {
        return CONTINUE;
    }

    @Override public int methodCalled(SourceLine line) {
        return CONTINUE;
    }

    @Override public int acceptChecklist(final String fileName) {
        Platform.runLater(() -> {
            File file = new File(System.getProperty(Constants.PROP_CHECKLIST_DIR), fileName);
            showAndEnterChecklist(file, runtime, null);
            script.getDebugger().resume();
        });
        return 0;
    }

    @Override public int showChecklist(final String fileName) {
        return 0;
    }

    public ArrayList<CheckList> getChecklists() {
        return checkLists;
    }

    public void addChecklist(CheckList checkList) {
        checkLists.add(checkList);
    }

    public File[] getScreenCaptures() {
        return screenCaptures.toArray(new File[screenCaptures.size()]);
    }

    public void addScreenCapture(File newFile) {
        screenCaptures.add(newFile);
    }

    public CheckList showAndEnterChecklist(File file, final IMarathonRuntime runtime, final Stage instance) {
        final CheckList checklist;
        try {
            checklist = CheckList.read(file);
            CheckListFormNode checklistForm = new CheckListFormNode(checklist, CheckListFormNode.Mode.ENTER);
            final CheckListStage dialog = new CheckListStage(checklistForm);

            Button screenCapture = FXUIUtils.createButton("Screen Capture", "Create screen capture");
            screenCapture.setOnAction((e) -> {
                dialog.getStage().setIconified(true);
                File captureFile = null;
                try {
                    if (captureFile == null) {
                        captureFile = runtime.getScreenCapture();
                    }
                    if (captureFile == null) {
                        FXUIUtils.showMessageDialog(null, "Could not create a screen capture", "Error", AlertType.ERROR);
                        return;
                    }
                    try {
                        AnnotateScreenCapture annotate = new AnnotateScreenCapture(captureFile, true);
                        annotate.getStage().showAndWait();
                        dialog.getStage().setIconified(false);
                        if (annotate.isSaved()) {
                            annotate.saveToFile(captureFile);
                            checklist.setCaptureFile(captureFile.getName());
                        }
                    } catch (IOException x) {
                        x.printStackTrace();
                    }
                } finally {
                }
            });
            Button saveButton = FXUIUtils.createButton("save", "Save", true, "Save");
            saveButton.setOnAction((e) -> {
                dialog.dispose();
            });
            dialog.setActionButtons(new Button[] { screenCapture, saveButton });
            dialog.getStage().showAndWait();
        } catch (Exception e1) {
            FXUIUtils.showConfirmDialog(instance, "Unable to read the checklist file", "Warning", AlertType.WARNING);
            return null;
        }
        addChecklist(checklist);
        return checklist;
    }

    @Override public String toString() {
        return getName();
    }

    public synchronized void setDataVariables(Properties dataVariables) {
        this.dataVariables = dataVariables;
    }

    public IRuntimeFactory getRuntimeFactory(String scriptText) {
        Map<String, Object> fixtureProperties = ScriptModel.getModel().getFixtureProperties(scriptText);
        if (fixtureProperties == null || fixtureProperties.size() == 0) {
            return runtimeFactory;
        }
        reuseFixture = Boolean.valueOf((String) fixtureProperties.get(Constants.FIXTURE_REUSE)).booleanValue();
        System.out.println("MarathonTestCase.getRuntimeFactory() " + TestAttributes.get("reuseFixture"));
        String launcherModel = (String) fixtureProperties.get(Constants.PROP_PROJECT_LAUNCHER_MODEL);
        IRuntimeLauncherModel lm = LauncherModelHelper.getLauncherModel(launcherModel);
        if (lm == null) {
            return runtimeFactory;
        }
        return lm.getRuntimeFactory();
    }

    public void setFullName(String name) {
        this.fullName = name;
    }

    @Override public String getFullName() {
        return fullName;
    }

    public static void reset() {
        if (runtime != null) {
            runtime.releaseInterpreters();
            runtime.destroy();
        }
        runtime = null;
    }

    public void initialize() throws Exception {
        checkLists.clear();
        screenCaptures.clear();
        String scriptText = getScriptContents();
        IRuntimeFactory rf = getRuntimeFactory(scriptText);
        shouldRunFixture = false;
        if (runtime != null)
            runtime.releaseInterpreters();
        if (runtime == null || !reuseFixture) {
            // This condition is added for Unit Testing purposes.
            if (runtime == null || !runtime.getClass().getName().equals("net.sourceforge.marathon.runtime.RuntimeStub")) {
                if (runtime != null) {
                    runtime.destroy();
                }
                shouldRunFixture = true;
                runtime = rf.createRuntime();
            }
        }
        script = runtime.createScript(MarathonMode.PLAYING, console, scriptText, file.getAbsolutePath(), false, true,
                dataVariables);
    }
}
