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
package net.sourceforge.marathon.display;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;

import com.google.inject.Inject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.marathon.ProjectHTTPDServer;
import net.sourceforge.marathon.Version;
import net.sourceforge.marathon.api.LogRecord;
import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.checklist.CheckList;
import net.sourceforge.marathon.checklist.CheckListForm;
import net.sourceforge.marathon.checklist.CheckListForm.CheckListElement;
import net.sourceforge.marathon.checklist.CheckListFormNode;
import net.sourceforge.marathon.checklist.CheckListFormNode.Mode;
import net.sourceforge.marathon.checklist.CheckListStage;
import net.sourceforge.marathon.checklist.IInsertCheckListHandler;
import net.sourceforge.marathon.checklist.MarathonCheckListStage;
import net.sourceforge.marathon.checklist.NewChekListInputStage;
import net.sourceforge.marathon.editor.IContentChangeListener;
import net.sourceforge.marathon.editor.IEditor;
import net.sourceforge.marathon.editor.IEditor.IGutterListener;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.editor.IEditorProvider.EditorType;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.display.AddPropertiesStage;
import net.sourceforge.marathon.fx.display.FXContextMenuTriggers;
import net.sourceforge.marathon.fx.display.FixtureStage;
import net.sourceforge.marathon.fx.display.FixtureStageInfo;
import net.sourceforge.marathon.fx.display.FunctionInfo;
import net.sourceforge.marathon.fx.display.FunctionStage;
import net.sourceforge.marathon.fx.display.IFixtureStageInfoHandler;
import net.sourceforge.marathon.fx.display.IFunctionArgumentHandler;
import net.sourceforge.marathon.fx.display.IInputHanler;
import net.sourceforge.marathon.fx.display.IModuleFunctionHandler;
import net.sourceforge.marathon.fx.display.IPreferenceHandler;
import net.sourceforge.marathon.fx.display.LineNumberStage;
import net.sourceforge.marathon.fx.display.LogView;
import net.sourceforge.marathon.fx.display.MarathonInputStage;
import net.sourceforge.marathon.fx.display.MarathonModuleStage;
import net.sourceforge.marathon.fx.display.MarathonPreferencesInfo;
import net.sourceforge.marathon.fx.display.ModuleInfo;
import net.sourceforge.marathon.fx.display.PreferencesStage;
import net.sourceforge.marathon.fx.display.ResultPane;
import net.sourceforge.marathon.fx.display.ResultPane.IResultPaneSelectionListener;
import net.sourceforge.marathon.fx.display.StatusBar;
import net.sourceforge.marathon.fx.display.TestPropertiesInfo;
import net.sourceforge.marathon.fx.display.TextAreaOutput;
import net.sourceforge.marathon.fx.projectselection.MPFConfigurationInfo;
import net.sourceforge.marathon.fx.projectselection.MPFConfigurationStage;
import net.sourceforge.marathon.fxdocking.DockGroup;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.Dockable;
import net.sourceforge.marathon.fxdocking.DockableResolver;
import net.sourceforge.marathon.fxdocking.DockableSelectionEvent;
import net.sourceforge.marathon.fxdocking.DockableSelectionListener;
import net.sourceforge.marathon.fxdocking.DockableState;
import net.sourceforge.marathon.fxdocking.DockableStateChangeEvent;
import net.sourceforge.marathon.fxdocking.DockableStateChangeListener;
import net.sourceforge.marathon.fxdocking.DockableStateWillChangeEvent;
import net.sourceforge.marathon.fxdocking.DockableStateWillChangeListener;
import net.sourceforge.marathon.fxdocking.DockingConstants.Split;
import net.sourceforge.marathon.fxdocking.DockingDesktop;
import net.sourceforge.marathon.fxdocking.DockingUtilities;
import net.sourceforge.marathon.fxdocking.TabbedDockableContainer;
import net.sourceforge.marathon.fxdocking.ToolBarContainer;
import net.sourceforge.marathon.fxdocking.ToolBarContainer.Orientation;
import net.sourceforge.marathon.fxdocking.ToolBarPanel;
import net.sourceforge.marathon.fxdocking.VLToolBar;
import net.sourceforge.marathon.junit.MarathonAssertion;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.junit.StdOutConsole;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.Group.FeaturesPanel;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.model.Group.IssuesPanel;
import net.sourceforge.marathon.model.Group.StoriesPanel;
import net.sourceforge.marathon.model.Group.SuitesPanel;
import net.sourceforge.marathon.resource.IResourceActionHandler;
import net.sourceforge.marathon.resource.IResourceActionSource;
import net.sourceforge.marathon.resource.IResourceChangeListener;
import net.sourceforge.marathon.resource.Project;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.navigator.FileResource;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Function;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.ILogger;
import net.sourceforge.marathon.runtime.api.IPlaybackListener;
import net.sourceforge.marathon.runtime.api.IPreferenceChangeListener;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.IScriptModel.SCRIPT_FILE_TYPE;
import net.sourceforge.marathon.runtime.api.MPFUtils;
import net.sourceforge.marathon.runtime.api.MarathonRuntimeException;
import net.sourceforge.marathon.runtime.api.Module;
import net.sourceforge.marathon.runtime.api.OSUtils;
import net.sourceforge.marathon.runtime.api.PlaybackResult;
import net.sourceforge.marathon.runtime.api.Preferences;
import net.sourceforge.marathon.runtime.api.ProjectFile;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.SourceLine;
import net.sourceforge.marathon.runtime.api.UsedAssertion;
import net.sourceforge.marathon.screencapture.AnnotateScreenCapture;
import net.sourceforge.marathon.suite.editor.GroupInputInfo;
import net.sourceforge.marathon.suite.editor.GroupInputStage;
import net.sourceforge.marathon.suite.editor.IGroupInputHanler;
import net.sourceforge.marathon.testrunner.fxui.AllureMarathonRunListener;
import net.sourceforge.marathon.testrunner.fxui.TestRunner;
import net.sourceforge.marathon.util.AbstractSimpleAction;
import net.sourceforge.marathon.util.AllureUtils;
import net.sourceforge.marathon.util.ExceptionUtil;
import net.sourceforge.marathon.util.INameValidateChecker;
import net.sourceforge.marathon.util.LauncherModelHelper;

/**
 * DisplayWindow provides the main user interface for Marathon from which the
 * user selects various options for using Marathon.
 */
public class DisplayWindow extends Stage implements INameValidateChecker, IResourceActionSource {

    public static final Logger LOGGER = Logger.getLogger(DisplayWindow.class.getName());

    private static final String EOL = System.getProperty("line.separator");

    private static final Logger logger = Logger.getLogger(DisplayWindow.class.getCanonicalName());

    private static DisplayWindow _instance;

    private class DockingListener
            implements DockableSelectionListener, DockableStateWillChangeListener, DockableStateChangeListener {

        @Override
        public void selectionChanged(DockableSelectionEvent e) {
            Dockable selectedDockable = e.getSelectedDockable();
            if (selectedDockable instanceof EditorDockable) {
                setCurrentEditorDockable((EditorDockable) selectedDockable);
                if (currentEditor != null) {
                    currentEditor.runWhenReady(() -> currentEditor.refresh());
                }
            }
            if (selectedDockable == null)
                setCurrentEditorDockable(null);
        }

        @Override
        public void dockableStateWillChange(DockableStateWillChangeEvent event) {
            if (resetWorkspaceOperation) {
                return;
            }
            DockableState dockableState = event.getFutureState();
            Dockable dockable = dockableState.getDockable();
            if (dockableState.isClosed() && !canCloseComponent(dockable)) {
                event.cancel();
            }
        }

        @Override
        public void dockableStateChanged(DockableStateChangeEvent event) {
            DockableState dockableState = event.getNewState();
            Dockable dockable = dockableState.getDockable();
            if (dockableState.isClosed() && dockable instanceof EditorDockable) {
                workspace.unregisterDockable(dockable);
            }
        }

    }

    private DockingListener dockingListener = new DockingListener();

    private class ContentChangeListener implements IContentChangeListener {
        @Override
        public void contentChanged() {
            updateView();
        }

    }

    private ContentChangeListener contentChangeListener = new ContentChangeListener();

    private class GutterListener implements IGutterListener {
        @Override
        public boolean hasBreakpointAtLine(int line) {
            return isBreakPointAtLine(line);
        }

        @Override
        public void gutterDoubleClickedAt(int caretLine) {
            toggleBreakPoint(caretLine);
        }

    }

    private GutterListener gutterListener = new GutterListener();

    private class ResultPaneSelectionListener implements IResultPaneSelectionListener {
        @Override
        public void resultSelected(SourceLine line) {
            goToFile(line.fileName, line.lineNumber);
        }

    }

    ResultPaneSelectionListener resultPaneSelectionListener = new ResultPaneSelectionListener();

    private class ScriptConsoleListener implements IScriptConsoleListener {
        @Override
        public String evaluateScript(String command) {
            return display.evaluateScript(command);
        }

        @Override
        public void sessionClosed() {
            closeScriptConsole();
            setState();
            if (state.isRecordingPaused()) {
                display.resume();
            }
        }
    }

    private class TestListener implements ITestListener {
        @Override
        public void openTest(Test suite) {
            Test test = null;
            if (suite instanceof TestSuite) {
                test = ((TestSuite) suite).testAt(0);
            } else {
                test = suite;
            }
            if (test != null && test instanceof MarathonTestCase) {
                openFile(((MarathonTestCase) test).getFile());
            }
        }
    }

    private TestListener testListener = new TestListener();

    private ScriptConsoleListener scriptConsoleListener = new ScriptConsoleListener();

    public class DisplayView implements IDisplayView {

        @Override
        public void setError(Throwable exception, String message) {
            RuntimeLogger.getRuntimeLogger().error("Marathon", exception.getMessage(), ExceptionUtil.getTrace(exception));
            WaitMessageDialog.setVisible(false);
            if (exception instanceof MarathonRuntimeException) {
                if (!"true".equals(System.getProperty("marathon.unittests"))) {
                    Platform.runLater(() -> FXUIUtils.showMessageDialog(DisplayWindow.this, "Application Under Test Aborted!!",
                            "Error", AlertType.ERROR));
                }
            } else {
                if (!"true".equals(System.getProperty("marathon.unittests"))) {
                    Platform.runLater(() -> FXUIUtils.showMessageDialog(DisplayWindow.this, message, "Error", AlertType.ERROR));
                }
            }
        }

        @Override
        public void setState(final State newState) {
            if (Platform.isFxApplicationThread()) {
                _setState(newState);
            } else {
                Platform.runLater(() -> {
                    _setState(newState);

                });
            }
        }

        public void _setState(State newState) {
            State oldState = state;
            state = newState;
            playAction.setEnabled((state.isStoppedWithAppClosed() || state.isStoppedWithAppOpen()) && isTestFile());
            stopPlayAction.setEnabled(state.isPlaying() || state.isPlayingPaused());
            debugAction.setEnabled((state.isStoppedWithAppClosed() || state.isStoppedWithAppOpen()) && isTestFile());
            slowPlayAction.setEnabled((state.isStoppedWithAppClosed() || state.isStoppedWithAppOpen()) && isTestFile());
            recordAction.setEnabled(state.isStopped() && isProjectFile() && newState != State.RECORDING_ABOUT_TO_START);
            etAction.setEnabled(state.isStopped() && newState != State.RECORDING_ABOUT_TO_START);
            toggleBreakpointAction.setEnabled(isProjectFile());
            clearAllBreakpointsAction.setEnabled(breakpoints != null && breakpoints.size() > 0);
            newTestcaseAction.setEnabled(state.isStopped());
            newModuleAction.setEnabled(state.isStopped());
            newFixtureAction.setEnabled(state.isStopped());
            newCheckListAction.setEnabled(state.isStopped());
            insertScriptAction.setEnabled(state.isRecording() && getModuleFunctions() != null
                    && getModuleFunctions().getChildren() != null && getModuleFunctions().getChildren().size() > 0);
            insertChecklistAction.setEnabled(state.isRecording());
            pauseAction.setEnabled(state.isRecording());
            resumeRecordingAction.setEnabled(state.isRecordingPaused());
            resumePlayingAction.setEnabled(state.isPlayingPaused());
            stopAction.setEnabled(state.isRecording() || state.isRecordingPaused());
            saveAction.setEnabled(state.isStopped() && currentEditor != null && currentEditor.isDirty());
            saveAsAction.setEnabled(state.isStopped() && currentEditor != null && !currentEditor.getNode().isDisabled()
                    && currentEditor.canSaveAs());
            saveAllAction.setEnabled(state.isStopped() && nDirty() > 0);
            openApplicationAction.setEnabled(state.isStoppedWithAppClosed() && isProjectFile());
            closeApplicationAction.setEnabled(state.isStoppedWithAppOpen());
            // update msg on status bar
            statusPanel.setApplicationState(state.toString());
            if (oldState.isRecording() && !state.isRecording() && scriptConsole == null) {
                endController();
            }
            if (!oldState.isRecording() && state.isRecording()) {
                startController();
            }
            stepIntoAction.setEnabled(state.isPlayingPaused());
            stepOverAction.setEnabled(state.isPlayingPaused());
            stepReturnAction.setEnabled(state.isPlayingPaused());
            playerConsoleAction.setEnabled(state.isPlayingPaused() && scriptConsole == null);
            recorderConsoleAction.setEnabled((state.isRecording() || state.isRecordingPaused()) && scriptConsole == null);
            showReportAction.setEnabled(runReportDir != null);
        }

        @Override
        public IStdOut getOutputPane() {
            if (scriptConsole != null) {
                return scriptConsole;
            }
            return outputPane;
        }

        @Override
        public void setResult(PlaybackResult result) {
        }

        @Override
        public int trackProgress(final SourceLine line, int type) {
            if (scriptConsole != null) {
                return IPlaybackListener.CONTINUE;
            }
            if (getFilePath().equals(line.fileName)) {
                if (Platform.isFxApplicationThread()) {
                    currentEditor.highlightLine(line.lineNumber - 1);
                } else {
                    Platform.runLater(() -> {
                        currentEditor.highlightLine(line.lineNumber - 1);
                    });
                }
            }
            if (debugging == false) {
                return IPlaybackListener.CONTINUE;
            }
            callStack.update(type, line);
            BreakPoint bp = new BreakPoint(line.fileName, line.lineNumber - 1);
            if (type == Display.LINE_REACHED && (stepIntoActive || breakpoints.contains(bp)
                    || breakStackDepth != -1 && callStack.getStackDepth() <= breakStackDepth)) {
                if (!getFilePath().equals(line.fileName)) {
                    File file = new File(line.fileName);
                    if (file.exists()) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                goToFile(line.fileName, line.lineNumber - 1);
                            }
                        });
                    } else {
                        return IPlaybackListener.CONTINUE;
                    }
                }
                stepIntoActive = false;
                breakStackDepth = -1;
                Platform.runLater(() -> display.pausePlay());
                return IPlaybackListener.PAUSE;
            }
            return IPlaybackListener.CONTINUE;
        }

        @Override
        public String getScript() {
            List<String> texts = new ArrayList<String>();
            if (Platform.isFxApplicationThread()) {
                texts.add(currentEditor.getText());
            } else {
                Object lock = new Object();
                Platform.runLater(() -> {
                    texts.add(currentEditor.getText());
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                });
                synchronized (lock) {
                    if (texts.size() == 0) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return texts.get(0);
        }

        @Override
        public String getFilePath() {
            if (currentEditor == null) {
                return null;
            }
            return currentEditor.getResourcePath();
        }

        @Override
        public void insertScript(String script) {
            if (controller.isShowing()) {
                controller.insertScript(script);
            }
            Platform.runLater(() -> currentEditor.insertScript(script));
        }

        @Override
        public void trackProgress() {
            currentEditor.highlightLine(-1);
        }

        @Override
        public void startInserting() {
            currentEditor.startInserting();
        }

        @Override
        public void stopInserting() {
            if (Platform.isFxApplicationThread()) {
                currentEditor.stopInserting();
                if (exploratoryTest) {
                    displayView.endTest(null);
                    displayView.endTestRun();
                    save();
                    exploratoryTest = false;
                }
                if (importStatements != null && importStatements.size() > 0) {
                    String text = scriptModel.updateScriptWithImports(currentEditor.getText(), importStatements);
                    currentEditor.setText(text);
                    importStatements = null;
                }
            } else {
                Platform.runLater(() -> {
                    currentEditor.stopInserting();
                    if (exploratoryTest) {
                        displayView.endTest(null);
                        displayView.endTestRun();
                        save();
                        exploratoryTest = false;
                    }
                    if (importStatements != null && importStatements.size() > 0) {
                        String text = scriptModel.updateScriptWithImports(currentEditor.getText(), importStatements);
                        currentEditor.setText(text);
                        importStatements = null;
                    }
                });
            }
        }

        @Override
        public boolean isDebugging() {
            return debugging;
        }

        @Override
        public int acceptChecklist(final String fileName) {
            Platform.runLater(() -> {
                fillUpChecklist(fileName);
                display.resume();
            });
            return 0;
        }

        @Override
        public int showChecklist(final String fileName) {
            Platform.runLater(() -> {
                final File file = new File(reportDir, fileName);
                final CheckList checklist;
                try {
                    checklist = CheckList.read(file);
                    CheckListFormNode checklistForm = new CheckListFormNode(checklist, Mode.DISPLAY);
                    final CheckListStage dialog = new CheckListStage(checklistForm);

                    Button screenCapture = null;
                    if (checklist.getCaptureFile() != null) {
                        screenCapture = FXUIUtils.createButton("Screen Capture", "Create screen capture");

                        screenCapture.setOnAction(new EventHandler<ActionEvent>() {
                            File captureFile = new File(file.getParent(), checklist.getCaptureFile());

                            @Override
                            public void handle(ActionEvent e) {
                                try {
                                    AnnotateScreenCapture annotate = new AnnotateScreenCapture(captureFile, false);
                                    annotate.getStage().show();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                    }
                    Button doneButton = FXUIUtils.createButton("ok", "Done", true, "Done");
                    doneButton.setOnAction((e) -> {
                        dialog.dispose();
                    });
                    if (screenCapture != null) {
                        dialog.setActionButtons(new Button[] { screenCapture, doneButton });
                    } else {
                        dialog.setActionButtons(new Button[] { doneButton });
                    }
                    dialog.getStage().showAndWait();
                } catch (Exception e1) {
                    FXUIUtils.showMessageDialog(DisplayWindow.this, "Unable to read the checklist file", "Error",
                            AlertType.INFORMATION);
                }
                display.resume();
            });
            return IPlaybackListener.PAUSE;

        }

        @Override
        public void insertChecklistAction(final String name) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    insertChecklist(name);
                }
            });
        }

        private boolean needReports() {
            return exploratoryTest || generateReportsMenuItem.isSelected();
        }

        @Override
        public void endTest(final PlaybackResult result) {
            if (!exploratoryTest) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        resultPane.addResult(result);
                    }
                });
            }
            if (!needReports()) {
                return;
            }
            if (!exploratoryTest && result.hasFailure()) {
                MarathonAssertion assertion = new MarathonAssertion(result.failures(), getFilePath());
                runListener.testAssumptionFailure(
                        new Failure(Description.createTestDescription(MarathonTestCase.class, getFilePath()), assertion));
            }
            TestAttributes.put("test_object", testCase);
            runListener.testFinished(Description.createTestDescription(MarathonTestCase.class, getFilePath()));
        }

        @Override
        public void endTestRun() {
            // Disable slowplay if set
            System.setProperty(Constants.PROP_RUNTIME_DELAY, "");
            if (!needReports()) {
                return;
            }
            try {
                if (exploratoryTest) {
                    ArrayList<CheckList> checklists = testCase.getChecklists();
                    for (CheckList checkList : checklists) {
                        File dataFile = checkList.xgetDataFile();
                        if (dataFile != null) {
                            checkList.save(new FileOutputStream(dataFile));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            runListener.testRunFinished(null);
            if (runReportDir != null) {
                File reportsDir = new File(runReportDir, "reports");
                File resultReporterHTMLFile = new File(reportsDir, "index.html");
                if (!resultReporterHTMLFile.exists()) {
                    String resultsDir = new File(runReportDir, "results").getAbsolutePath();
                    System.setProperty("allure.results.directory", resultsDir);
                    AllureUtils.launchAllure(resultsDir, reportsDir.getAbsolutePath());
                }
                if (resultReporterHTMLFile.exists()) {
                    Platform.runLater(() -> {
                        openFile(resultReporterHTMLFile);
                    });
                }
            }
            DisplayWindow.this.setState();
        }

        @Override
        public void startTest() {
            if (!needReports()) {
                return;
            }
            testCase = new MarathonTestCase(new File(getFilePath()), false, new StdOutConsole()) {
                String t_suffix = display.getDDTestRunner() == null ? "" : display.getDDTestRunner().getName();
                String name = exploratoryTest ? runReportDir.getName() : super.getName() + t_suffix;

                @Override
                public String getName() {
                    return name;
                };
            };
            testSuite.addTest(testCase);

            TestAttributes.put("test_object", testCase);
            runListener.testStarted(Description.createTestDescription(MarathonTestCase.class, getFilePath()));
        }

        @Override
        public void startTestRun() {
            if (!needReports()) {
                return;
            }
            runReportDir = new File(reportDir, createTestReportDirName());
            if (runReportDir.mkdir()) {
                try {
                    System.setProperty(Constants.PROP_REPORT_DIR, runReportDir.getCanonicalPath());
                    System.setProperty(Constants.PROP_IMAGE_CAPTURE_DIR, runReportDir.getCanonicalPath());
                    System.setProperty("allure.results.directory", new File(runReportDir, "results").getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.warning("Unable to create report directory: " + runReportDir + " - Ignoring report option");
            }
            testSuite = new TestSuite("Marathon Test");
            runListener = new AllureMarathonRunListener();
            try {
                runListener.testRunStarted(Description.createTestDescription(MarathonTestCase.class, getFilePath()));
            } catch (Exception e) {
                logger.warning("Unable to start the test run: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void addImport(String ims) {
            importStatements.add(ims);
        }

        @Override
        public void updateOMapFile() {
            File omapFile = new File(System.getProperty(Constants.PROP_PROJECT_DIR),
                    System.getProperty(Constants.PROP_OMAP_FILE, Constants.FILE_OMAP));
            fileUpdated(omapFile);
        }

        public LogView getLogView() {
            return logView;
        }

        @Override
        public void addErrorScreenShotEntry(AssertionFailedError error, String fileName) {
            if (testCase != null)
                testCase.addErrorScreenShotEntry(error, fileName);
        }

        @Override
        public void addScreenShotEntry(String title, String filePath, List<UsedAssertion> assertions) {
            if (testCase != null)
                testCase.addScreenShotEntry(title, filePath, assertions);
        }

    }

    public DisplayView displayView = new DisplayView();

    private static final String MODULE = "Marathon";

    @Inject
    private Display display;
    @Inject
    private IScriptModel scriptModel;
    @Inject
    private FixtureSelector fixtureSelector;
    @Inject
    private TextAreaOutput outputPane;
    @Inject
    private ResultPane resultPane;
    @Inject
    private LogView logView;
    @Inject
    private StatusBar statusPanel;
    @Inject
    private CallStack callStack;
    @Inject
    private IEditorProvider editorProvider;
    @Inject(optional = true)
    private IActionProvider actionProvider;

    /**
     * Editor panel
     */
    private IEditor currentEditor;
    /**
     * Line number dialog to accept a line number
     */
    private LineNumberStage lineNumberStage = new LineNumberStage();
    /**
     * Default fixture to be used for new test cases
     */
    private String fixture;
    /**
     * Is current recording in raw mode?
     */
    boolean isRawRecording = false;
    private boolean debugging = false;
    protected boolean exploratoryTest = false;

    private List<BreakPoint> breakpoints;
    private MarathonTestCase testCase;
    private File reportDir;
    private File runReportDir;

    ToggleButton rawRecordButton;

    public static final class EditorDockable extends Dockable {
        private DockKey dockKey;
        private final IEditor dockableEditor;
        private final DockGroup dockGroup;

        public EditorDockable(IEditor e, DockGroup dockGroup) {
            this.dockableEditor = e;
            this.dockGroup = dockGroup;
        }

        @Override
        public Node getComponent() {
            return dockableEditor.getNode();
        }

        @Override
        public DockKey getDockKey() {
            if (dockKey == null) {
                String key = dockableEditor.getDockKey();
                dockKey = new DockKey(key, dockableEditor.getName());
                dockKey.setCloseOptions(true);
                dockKey.setDockGroup(dockGroup);
            }
            return dockKey;
        }

        public IEditor getEditor() {
            return dockableEditor;
        }

        @Override
        public String toString() {
            return super.toString() + getDockKey();
        }

        public void updateKey() {
            dockKey.setKey(dockableEditor.getDockKey());
        }
    }

    private TestSuite testSuite;

    /**
     * Create Menu with new file/folder options
     * 
     * @return
     */
    private Menu createNewMenu() {
        Menu newMenu = new Menu("New");
        newMenu.getItems().add(newTestcaseAction.getMenuItem());
        newMenu.getItems().add(etAction.getMenuItem());
        newMenu.getItems().add(newModuleAction.getMenuItem());
        newMenu.getItems().add(newFixtureAction.getMenuItem());
        newMenu.getItems().add(newCheckListAction.getMenuItem());
        newMenu.getItems().add(newModuleDirAction.getMenuItem());
        newMenu.getItems().add(newSuiteFileAction.getMenuItem());
        newMenu.getItems().add(newFeatureFileAction.getMenuItem());
        newMenu.getItems().add(newStoryFileAction.getMenuItem());
        newMenu.getItems().add(newIssueFileAction.getMenuItem());
        return newMenu;
    }

    private SplitMenuButton createNewButton() {
        SplitMenuButton newButton = new SplitMenuButton(newTestcaseAction.getMenuItem(), etAction.getMenuItem());
        newButton.setGraphic(newTestcaseAction.getButton().getGraphic());
        newButton.setOnAction(newTestcaseAction.getButton().getOnAction());
        return newButton;
    }

    /**
     * Removes the given directory name from the module directories in the
     * project file.
     * 
     * 
     */
    public void removeModDirFromProjFile() {
        String[] moduleDirs = Constants.getMarathonDirectoriesAsStringArray(Constants.PROP_MODULE_DIRS);
        StringBuilder sbr = new StringBuilder();
        for (String moduleDir : moduleDirs) {
            File f = new File(moduleDir);
            if (!f.exists()) {
                continue;
            }
            sbr.append(getProjectRelativeName(moduleDir) + ";");
        }
        try {
            updateProjectFile(Constants.PROP_MODULE_DIRS, sbr.toString());
            resetModuleFunctions();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createTestReportDirName() {
        return (exploratoryTest ? "et-" : "tr-") + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    }

    public void onShowReport() {
        if (runReportDir != null) {
            File reportsDir = new File(runReportDir, "reports");
            File resultReporterHTMLFile = new File(reportsDir, "index.html");
            if (resultReporterHTMLFile.exists()) {
                openFile(resultReporterHTMLFile);
            }
        }
    }

    public void insertChecklist(String name) {
        if (exploratoryTest) {
            CheckList checklist = fillUpChecklist(name);
            if (checklist == null) {
                return;
            }
            try {
                File file = File.createTempFile(name, ".data", runReportDir);
                checklist.xsetDataFile(file);
                display.recordShowChecklist(runReportDir.getName() + "/" + file.getName());
            } catch (IOException e) {
                Platform.runLater(() -> {
                    FXUIUtils.showMessageDialog(DisplayWindow.this, "Unable to create a checklist data file", "Error",
                            AlertType.INFORMATION);
                });
                e.printStackTrace();
                return;
            }
        } else {
            display.insertChecklist(name);
        }
    }

    private void resumePlay() {
        closeScriptConsole();
        display.resume();
    }

    private void closeScriptConsole() {
        if (scriptConsole != null) {
            scriptConsole.dispose();
            scriptConsole = null;
        }
    }

    private void saveBreakPoints() {
        String projectDir = System.getProperty(Constants.PROP_PROJECT_DIR);
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(new FileOutputStream(new File(projectDir, ".breakpoints")));
            List<BreakPoint> bps = new ArrayList<BreakPoint>();
            for (BreakPoint breakPoint : breakpoints) {
                if (breakPoint.shouldSave()) {
                    bps.add(breakPoint);
                }
            }
            os.writeObject(bps);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadBreakPoints() {
        String projectDir = System.getProperty(Constants.PROP_PROJECT_DIR);
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new FileInputStream(new File(projectDir, ".breakpoints")));
            breakpoints = (List<BreakPoint>) is.readObject();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public void toggleBreakPoint(int line) {
        if (currentEditor == null) {
            return;
        }
        currentEditor.setFocus();
        if (currentEditor.isProjectFile()) {
            BreakPoint bp = new BreakPoint(displayView.getFilePath(), line);
            if (breakpoints.contains(bp)) {
                breakpoints.remove(bp);
            } else {
                breakpoints.add(bp);
            }
            setState();
            currentEditor.refresh();
        }
    }

    /**
     * The Navigator panel.
     */
    private NavigatorPanel navigatorPanel;
    @Inject
    @SuitesPanel
    private AbstractGroupsPanel suitesPanel;
    @Inject
    @FeaturesPanel
    private AbstractGroupsPanel featuresPanel;
    @Inject
    @StoriesPanel
    private AbstractGroupsPanel storiesPanel;
    @Inject
    @IssuesPanel
    private AbstractGroupsPanel issuesPanel;

    /**
     * The current state of Marathon.
     */
    State state = State.STOPPED_WITH_APP_CLOSED;

    public class ControllerStage extends Stage implements IErrorListener {

        VBox content = new VBox();
        private ToolBar toolBar = new ToolBar();
        private TextArea textArea = new TextArea();
        private Label msgLabel = new Label("    ");
        private DisplayWindow displayWindow;

        public ControllerStage(DisplayWindow stage) {
            this.displayWindow = stage;
            setTitle("Marathon Control Center");
            getIcons().add(FXUIUtils.getImageURL("logo16"));
            getIcons().add(FXUIUtils.getImageURL("logo32"));
            initComponents();
            setScene(new Scene(content));
            setAlwaysOnTop(true);
            setOnShown((e) -> {
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                setX(screenBounds.getWidth() - getWidth());
                setY(0);
            });
            sizeToScene();
            setResizable(false);
            setOnCloseRequest(e -> displayWindow.onStop());
        }

        private void initComponents() {
            toolBar.setId("controller-toolbar");
            toolBar.setOrientation(javafx.geometry.Orientation.VERTICAL);
            this.displayWindow.rawRecordButton = this.displayWindow.rawRecordAction.getToggleButton();
            toolBar.getItems().addAll(this.displayWindow.getActionButton(this.displayWindow.pauseAction),
                    this.displayWindow.getActionButton(this.displayWindow.insertScriptAction),
                    this.displayWindow.getActionButton(this.displayWindow.insertChecklistAction),
                    this.displayWindow.getActionButton(this.displayWindow.stopAction), this.displayWindow.rawRecordButton,
                    this.displayWindow.getActionButton(this.displayWindow.recorderConsoleAction));
            textArea.setId("textArea");
            textArea.setEditable(false);
            textArea.setStyle(
                    "-fx-background-color: black;-fx-control-inner-background: black;-fx-text-inner-color: rgb(0, 250, 0);-fx-text-fill: rgb(0, 250, 0);-fx-font: normal 16px monospace;");
            HBox top = new HBox();
            top.getChildren().add(toolBar);
            top.getChildren().add(textArea);
            content.getChildren().addAll(top, msgLabel);
        }

        public void insertScript(String script) {
            BufferedReader reader = new BufferedReader(new StringReader(script));
            String line;
            String[] lines = new String[5];
            int index = 0;
            try {
                while ((line = reader.readLine()) != null) {
                    lines[index++] = line;
                    if (index == 5) {
                        index = 0;
                    }
                }
            } catch (IOException e) {
            }
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                if (lines[index] != null) {
                    text.append(lines[index].trim()).append('\n');
                }
                if (++index == 5) {
                    index = 0;
                }
            }
            textArea.setText(text.toString());
        }

        @Override
        public void addError(LogRecord result) {
            msgLabel.setGraphic(FXUIUtils.getIcon("error"));
            msgLabel.setText(result.getMessage());
        }

        public void clear() {
            textArea.setText("");
            this.displayWindow.isRawRecording = false;
            this.displayWindow.rawRecordButton.setSelected(false);
            msgLabel.setText("   ");
            msgLabel.setGraphic(null);
        }

    }

    void endController() {
        show();
        controller.close();
    }

    void startController() {
        WaitMessageDialog.setVisible(false);
        controller.show();
        close();
    }

    private ControllerStage controller;
    private boolean stepIntoActive;
    private ScriptConsole scriptConsole;
    protected int breakStackDepth = -1;
    private CheckMenuItem enableChecklistMenuItem;
    private CheckMenuItem generateReportsMenuItem;
    private Button recordActionButton;

    private DockingDesktop workspace;

    private TestRunner testRunner;

    private IResourceActionHandler resourceActionHandler;
    private IResourceChangeListener resourceChangeListener;

    private Project project;

    /**
     * Constructs a DisplayWindow object.
     */
    public DisplayWindow() {
        WaitMessageDialog.setVisible(false);
        new ActionInjector(DisplayWindow.this).injectActions();
        controller = new ControllerStage(this);
        reportDir = new File(new File(System.getProperty(Constants.PROP_PROJECT_DIR)), Constants.DIR_TESTREPORTS);
        if (!reportDir.exists()) {
            if (!reportDir.mkdir()) {
                logger.warning("Unable to create report directory: " + reportDir + " - Marathon might not function properly");
            }
        }
        breakpoints = new ArrayList<BreakPoint>();
        _instance = this;
        project = new Project();
        resourceActionHandler = new ResourceActionHandler();
        resourceChangeListener = new ResourceChangeListener();
        GlobalResourceChangeListener.set(resourceChangeListener);
        startHTTPDServer();
    }

    private void startHTTPDServer() {
        try {
            ProjectHTTPDServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Inject
    public void setDisplay() {
        initUI();
        display.setView(displayView);
        setDefaultFixture(getDefaultFixture());
        logViewLogger = new LogViewLogger(logView);
        RuntimeLogger.setRuntimeLogger(logViewLogger);
        logView.setErrorListener(controller);
    }

    public void dispose() {
        super.close();
        if (controller != null) {
            controller.close();
        }
    }

    /**
     * Open and display a file in the editor
     * 
     * @param directory
     *            , the directory
     * @param fileName
     *            , name of the file
     * @return true, if the file given is accessible and shown
     */
    private boolean showFile(String directory, String fileName) {
        File file;
        file = new File(directory, fileName);
        if (file.canRead()) {
            openFile(file);
            return true;
        }
        return false;
    }

    /**
     * Sets up the default fixture. Note: The given fixture is saved into the
     * preferrences and selected when Marathon is restarted.
     * 
     * @param fixture
     *            , the fixture
     */
    public void setDefaultFixture(String fixture) {
        this.fixture = fixture;
        statusPanel.setFixture(" " + fixture + " ");
        JSONObject p = Preferences.instance().getSection("project");
        p.put("fixture", fixture);
        Preferences.instance().save("project");
        display.setDefaultFixture(fixture);
    }

    /**
     * Get the default fixture from user preferrences.
     * 
     * @return
     */
    public String getDefaultFixture() {
        JSONObject p = Preferences.instance().getSection("project");
        return p.optString("fixture", "default");
    }

    /**
     * Initialize the UI for the Main window.
     */
    private void initUI() {
        setName("DisplayWindow");

        setWindowState();
        setTheme();
        String projectName = System.getProperty(Constants.PROP_PROJECT_NAME, "");
        if (projectName.equals("")) {
            projectName = "Marathon";
        }
        setTitle(projectName);
        getIcons().add(FXUIUtils.getImageURL("logo16"));
        getIcons().add(FXUIUtils.getImageURL("logo32"));
        getIcons().add(FXUIUtils.getImageURL("logo64"));
        getIcons().add(FXUIUtils.getImageURL("logo128"));
        getIcons().add(FXUIUtils.getImageURL("logo256"));
        workspace = new DockingDesktop("Marathon");
        workspace.addDockableSelectionListener(dockingListener);
        workspace.addDockableStateWillChangeListener(dockingListener);
        workspace.addDockableStateChangeListener(dockingListener);
        ToolBarContainer container = ToolBarContainer.createDefaultContainer(Orientation.LEFT);
        createToolBar(container);
        container.setContent(workspace);
        BorderPane fxBorderPane = new BorderPane(container);
        fxBorderPane.setTop(createMenuBar());
        fxBorderPane.setBottom(statusPanel);
        Scene scene = new Scene(fxBorderPane);
        scene.getStylesheets().add(ModalDialog.class.getClassLoader()
                .getResource("net/sourceforge/marathon/fx/api/css/marathon.css").toExternalForm());
        setScene(scene);
        initStatusBar();
        initDesktop();
        addEventFilter(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode() == KeyCode.F10)
                setFullScreen(true);
        });
        setExitHook();
    }

    public void setWindowState() {
        JSONObject p = Preferences.instance().getSection("display");
        if (p.optBoolean("maximized"))
            setMaximized(true);
        else if (p.optBoolean("fullscreen"))
            setFullScreen(true);
        else {
            double x = p.optDouble("window.x", 0);
            double y = p.optDouble("window.y", 0);
            double w = p.optDouble("window.w", 1280);
            double h = p.optDouble("window.h", 1024);
            if (x < 0)
                x = 0;
            if (y < 0)
                y = 0;
            if (w < 0)
                w = 1280;
            if (h < 0)
                h = 1024;
            setWidth(w);
            setHeight(h);
            setX(x);
            setY(y);
            if (p.optBoolean("iconified"))
                setIconified(true);
        }
    }

    public void setTheme() {
        JSONObject themeSection = Preferences.instance().getSection("theme");
        boolean builtin = themeSection.optBoolean("builtin");
        String path = themeSection.optString("path", "/themes/marathon.css");
        String name = themeSection.optString("name", "Marathon");
        if (builtin) {
            if (name != null) {
                Application.setUserAgentStylesheet(name);
            }
        } else {
            if (path != null) {
                URL resource = getClass().getResource(path);
                if (resource != null) {
                    Application.setUserAgentStylesheet(resource.toExternalForm());
                }
            }
        }
    }

    private void setName(String name) {
        // TODO Set ID or name for stage
    }

    /**
     * Initialize the desktop. If available, the previous desktop configuration
     * is restored.
     */
    public void initDesktop() {
        loadBreakPoints();
        createNavigatorPanel();
        createGroupPanels();
        createJUnitPanel();
        resultPane.addSelectionListener(resultPaneSelectionListener);

        editorDockGroup = new DockGroup("Editors");
        JSONObject preferences = Preferences.instance().getSection("workspace");
        JSONObject state = preferences.optJSONObject("state");
        if (state != null) {
            List<Dockable> dockables = workspace.readDockableState(state, new DockableResolver() {
                @Override
                public Dockable resolveDockable(final String keyName) {
                    if (keyName.equals("Navigator")) {
                        return navigatorPanel;
                    } else if (keyName.equals("Suites")) {
                        return suitesPanel;
                    } else if (keyName.equals("Features")) {
                        return featuresPanel;
                    } else if (keyName.equals("Stories")) {
                        return storiesPanel;
                    } else if (keyName.equals("Issues")) {
                        return issuesPanel;
                    } else if (keyName.equals("TestRunner")) {
                        return testRunner;
                    } else if (keyName.equals("Output")) {
                        return outputPane;
                    } else if (keyName.equals("Results")) {
                        return resultPane;
                    } else if (keyName.equals("Log")) {
                        return logView;
                    } else {
                        Path projectPath = Constants.getProjectPath();
                        File file = projectPath.resolve(keyName).toFile();
                        if (!file.exists()) {
                            return null;
                        }
                        try {
                            IEditor e;
                            e = createEditor(file);
                            Dockable dockable = (Dockable) e.getData("dockable");
                            return dockable;
                        } catch (Throwable t) {
                            t.printStackTrace();
                            FXUIUtils.showMessageDialog(DisplayWindow.this,
                                    "Error Opening Editor for file: " + file.getAbsolutePath() + ":" + t.getMessage(), "Error",
                                    AlertType.ERROR);
                            return null;
                        }
                    }
                }

            });
            createWorkspace(dockables);
            return;
        }
        EditorDockable[] editors = new EditorDockable[] {};
        IEditor readmeEditor = null;
        try {
            readmeEditor = getReadmeEditor();
            if (readmeEditor != null) {
                editors = new EditorDockable[] { (EditorDockable) readmeEditor.getData("dockable") };
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        createDefaultWorkspace(editors);
    }

    private void createJUnitPanel() {
        testRunner = new TestRunner(resourceActionHandler, taConsole);
        testRunner.setTestOpenListener(testListener);
        testRunner.addResultPaneListener(resultPaneSelectionListener);
        testRunner.setAcceptChecklist(enableChecklistMenuItem.isSelected());
    }

    public void setVisible(boolean b) {
        if (b) {
            show();
        }
        if (currentEditor != null && b) {
            currentEditor.setFocus();
        }
        updateView();
    }

    private void createDefaultWorkspace(EditorDockable[] editorDockables) {
        EditorDockable selectedEditor = null;
        if (currentEditor != null) {
            selectedEditor = (EditorDockable) currentEditor.getData("dockable");
        }
        DockableState[] dockableStates = workspace.getDockables();
        for (DockableState dockableState : dockableStates) {
            workspace.close(dockableState.getDockable());
        }
        workspace.addDockable(resultPane);
        workspace.split(resultPane, navigatorPanel, Split.LEFT, 0.4);
        workspace.createTab(resultPane, outputPane, 0, false);
        workspace.createTab(resultPane, logView, 1, false);
        Dockable dockable = null;
        int order = 1;
        for (EditorDockable editorDockable : editorDockables) {
            if (dockable == null) {
                workspace.split(outputPane, editorDockable, Split.TOP, 0.7);
            } else {
                workspace.createTab(dockable, editorDockable, order++, false);
            }
            dockable = editorDockable;
            if (selectedEditor == null) {
                selectedEditor = editorDockable;
            }
        }
        if (selectedEditor != null) {
            selectEditor(selectedEditor);
        }
    }

    private void createWorkspace(List<Dockable> dockables) {
        EditorDockable selectedEditor = null;
        workspace.addDockable(resultPane);
        workspace.split(resultPane, navigatorPanel, Split.LEFT, 0.4);
        int index = 1;
        if (dockables.contains(suitesPanel)) {
            workspace.createTab(navigatorPanel, suitesPanel, index++, false);
        }
        if (dockables.contains(featuresPanel)) {
            workspace.createTab(navigatorPanel, featuresPanel, index++, false);
        }
        if (dockables.contains(storiesPanel)) {
            workspace.createTab(navigatorPanel, storiesPanel, index++, false);
        }
        if (dockables.contains(issuesPanel)) {
            workspace.createTab(navigatorPanel, issuesPanel, index++, false);
        }
        if (dockables.contains(testRunner)) {
            workspace.createTab(navigatorPanel, testRunner, index++, false);
        }
        workspace.createTab(resultPane, outputPane, 1, false);
        workspace.createTab(resultPane, logView, 1, false);
        int order = 1;
        for (Dockable dockable : dockables) {
            if (dockable instanceof EditorDockable) {
                if (selectedEditor == null) {
                    selectedEditor = (EditorDockable) dockable;
                    workspace.split(outputPane, dockable, Split.TOP, 0.7);
                } else {
                    workspace.createTab(selectedEditor, dockable, order++, false);
                }
            }
        }
        if (selectedEditor != null) {
            selectEditor(selectedEditor);
        }
    }

    /**
     * Returns the contents of readme file in a Component from either the
     * project directory or Marathon README.txt
     * 
     * @return Component containing the contents of readme file.
     */
    private IEditor getReadmeEditor() throws IOException {
        File readmeFile = new File(System.getProperty(Constants.PROP_HOME) + "/readme/index.html");
        if (readmeFile.exists()) {
            return createEditor(readmeFile);
        }
        return null;
    }

    /**
     * Initialize the status bar
     */
    private void initStatusBar() {
        statusPanel.getFixtureLabel().setOnMouseClicked((e) -> {
            onSelectFixture();
        });
        statusPanel.getRowLabel().setOnMouseClicked((e) -> {
            gotoLine();
        });
        statusPanel.getInsertLabel().setOnMouseClicked((e) -> {
            Platform.runLater(() -> {
                if (currentEditor != null) {
                    currentEditor.toggleInsertMode();
                }
            });
        });
    }

    /**
     * Set the accelerator keys. Note: the accelerator keys need to be added
     * while MenuItems are created as well as set them in the editor.
     * 
     * @param editor
     */
    private void setAcceleratorKeys(IEditor editor) {
        editor.addKeyBinding("^-S", saveAction);
        editor.addKeyBinding("^-Shift-A", saveAsAction);
        editor.addKeyBinding("^-P", playAction);
        editor.addKeyBinding("^-R", recordAction);
        editor.addKeyBinding("^-B", toggleBreakpointAction);
        editor.addKeyBinding("^-N", newTestcaseAction);
    }

    /**
     * Set the exit hook for the Main window.
     */
    private void setExitHook() {
        setOnCloseRequest((e) -> {
            if (!handleQuit()) {
                e.consume();
            } else {
                System.exit(0);
            }
        });
    }

    /**
     * Create the navigator panel.
     * 
     * 
     * @return navigatorPanel, new Navigator panel.
     */
    private void createNavigatorPanel() {
        navigatorPanel = new NavigatorPanel(resourceActionHandler, resourceChangeListener, project);
    }

    private void createGroupPanels() {
        suitesPanel.initialize(resourceActionHandler, resourceChangeListener);
        featuresPanel.initialize(resourceActionHandler, resourceChangeListener);
        storiesPanel.initialize(resourceActionHandler, resourceChangeListener);
        issuesPanel.initialize(resourceActionHandler, resourceChangeListener);
    }

    protected void desktopOpen(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Throwable e) {
            logger.info(e.getMessage());
        }
    }

    /**
     * Create the menu bar for the Main window.
     * 
     * @return menubar
     */
    private Node createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        menu.getItems().add(createNewMenu());
        menu.getItems().add(saveAction.getMenuItem());
        menu.getItems().add(saveAsAction.getMenuItem());
        menu.getItems().add(saveAllAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(exitAction.getMenuItem());
        menuBar.getMenus().add(menu);
        menu = new Menu("Edit");
        menu.getItems().add(refreshAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(preferencesAction.getMenuItem());
        menuBar.getMenus().add(menu);
        menu = new Menu("Marathon");
        menu.getItems().add(playAction.getMenuItem());
        menu.getItems().add(stopPlayAction.getMenuItem());
        menu.getItems().add(slowPlayAction.getMenuItem());
        menu.getItems().add(debugAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(recordAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(openApplicationAction.getMenuItem());
        menu.getItems().add(closeApplicationAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(selectFixtureAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(toggleBreakpointAction.getMenuItem());
        menu.getItems().add(clearAllBreakpointsAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(projectSettingsAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        enableChecklistMenuItem = new CheckMenuItem("Enable Checklists");
        enableChecklistMenuItem.setOnAction((e) -> {
            display.setAcceptChecklist(enableChecklistMenuItem.isSelected());
            testRunner.setAcceptChecklist(enableChecklistMenuItem.isSelected());
            if (enableChecklistMenuItem.isSelected() && !generateReportsMenuItem.isSelected()) {
                FXUIUtils.showMessageDialog(DisplayWindow.this, "Enabling generate reports (needed for checklists)", "Info",
                        AlertType.INFORMATION);
                generateReportsMenuItem.setSelected(true);
            }
        });
        enableChecklistMenuItem.setSelected(false);
        display.setAcceptChecklist(false);
        menu.getItems().add(enableChecklistMenuItem);
        menu.getItems().add(new SeparatorMenuItem());
        generateReportsMenuItem = new CheckMenuItem("Generate Reports");
        generateReportsMenuItem.setOnAction((e) -> {
            Properties props = System.getProperties();
            props.remove(Constants.PROP_IMAGE_CAPTURE_DIR);
            if (enableChecklistMenuItem.isSelected() && !generateReportsMenuItem.isSelected()) {
                FXUIUtils.showMessageDialog(DisplayWindow.this, "Disabling checklists (Generate reports required)", "Info",
                        AlertType.INFORMATION);
                enableChecklistMenuItem.setSelected(false);
            }
            setState();
        });
        generateReportsMenuItem.setSelected(false);
        menu.getItems().add(generateReportsMenuItem);
        menuBar.getMenus().add(menu);
        if (actionProvider != null) {
            IMarathonAction[] actions = actionProvider.getActions();
            for (IMarathonAction action : actions) {
                if (!action.isMenuBarAction()) {
                    continue;
                }
                String menuName = action.getMenuName();
                if (menuName == null) {
                    continue;
                }
                Menu menux = findMenu(menuBar, menuName);
                if (action.isSeperator()) {
                    menux.getItems().add(new SeparatorMenuItem());
                    continue;
                }
                if (action.isPopupMenu()) {
                    menux.getItems().add(action.getPopupMenu());
                    continue;
                }
                String accelKey = action.getAccelKey();
                if (accelKey != null) {
                    menux.getItems().add(createAction(action).getMenuItem());
                } else {
                    if (action.getButtonGroup() != null) {
                        ToggleGroup group = action.getButtonGroup();
                        AbstractSimpleAction asa = createAction(action);
                        RadioMenuItem radio = new RadioMenuItem(action.getName(), asa.getIcon());
                        radio.setOnAction((e) -> {
                            asa.handle(e);
                        });
                        radio.setToggleGroup(group);
                        menux.getItems().add(radio);
                        if (action.isSelected()) {
                            radio.setSelected(true);
                        }
                    } else {
                        menux.getItems().add(createAction(action).getMenuItem());
                    }
                }
            }
        }
        menu = new Menu("Window");
        menu.getItems().add(resetWorkspaceAction.getMenuItem());
        menu.getItems().add(new SeparatorMenuItem());
        addThemeMenu(menu);
        addViews(menu);
        menuBar.getMenus().add(menuBar.getMenus().size() - 1, menu);
        menu = findMenu(menuBar, "Help");
        menu.getItems().add(0, releaseNotes.getMenuItem());
        menu.getItems().add(1, changeLog.getMenuItem());
        menu.getItems().add(2, visitWebsite.getMenuItem());
        if (Version._message == null)
            return menuBar;
        VBox vbox = new VBox();
        vbox.getChildren().addAll(getMessageBar(vbox), menuBar);
        return vbox;
    }

    private Node getMessageBar(VBox vbox) {
        HBox hb = new HBox(10);
        hb.setPrefHeight(32);
        hb.setStyle("-fx-padding: 0 5px 0 5px; -fx-background-color: " + Version._message_bg + ";");
        CheckBox cb = new CheckBox("Do Not Show Again");
        cb.setStyle("-fx-text-fill: " + Version._message_fg + ";-fx-fill: " + Version._message_fg + ";");
        Text b = FXUIUtils.getIconAsText("close");
        b.setOnMouseClicked((e) -> {
            JSONObject preferences = Preferences.instance().getSection("display");
            preferences.put("_doNotShowMessage", cb.isSelected());
            Preferences.instance().save("display");
            vbox.getChildren().remove(0);
        });
        Text t = new Text(Version._message);
        hb.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(t, Priority.ALWAYS);
        t.setStyle("-fx-fill: " + Version._message_fg + "; -fx-font-size: 14px; -fx-font-weight:bold; -fx-font-family: Tahoma;");
        b.setStyle("-fx-fill: " + Version._message_fg + "; -fx-font-size: 14px; -fx-font-weight:bold;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hb.getChildren().addAll(t, spacer, b);
        return hb;
    }

    private void addViews(Menu windowMenu) {
        MenuItem menu = FXUIUtils.createMenuItem("testrunner", "Test Runner", "");
        menu.setOnAction((e) -> {
            if (testRunner.getContainer() == null) {
                workspace.createTab(navigatorPanel, testRunner, 1, false);
            }
            selectDockable(testRunner);
        });
        windowMenu.getItems().add(menu);
        menu = FXUIUtils.createMenuItem("tsuite", "Test Suites", "");
        menu.setOnAction((e) -> {
            if (suitesPanel.getContainer() == null) {
                workspace.createTab(navigatorPanel, suitesPanel, 2, false);
            }
            selectDockable(suitesPanel);
        });
        windowMenu.getItems().add(menu);
        menu = FXUIUtils.createMenuItem("tfeature", "Features", "");
        menu.setOnAction((e) -> {
            if (featuresPanel.getContainer() == null) {
                workspace.createTab(navigatorPanel, featuresPanel, 3, false);
            }
            selectDockable(featuresPanel);
        });
        windowMenu.getItems().add(menu);
        menu = FXUIUtils.createMenuItem("tstory", "Stories", "");
        menu.setOnAction((e) -> {
            if (storiesPanel.getContainer() == null) {
                workspace.createTab(navigatorPanel, storiesPanel, 3, false);
            }
            selectDockable(storiesPanel);
        });
        windowMenu.getItems().add(menu);
        menu = FXUIUtils.createMenuItem("tissue", "Issues", "");
        menu.setOnAction((e) -> {
            if (issuesPanel.getContainer() == null) {
                workspace.createTab(navigatorPanel, issuesPanel, 3, false);
            }
            selectDockable(issuesPanel);
        });
        windowMenu.getItems().add(menu);
    }

    private void selectDockable(Dockable dockable) {
        TabbedDockableContainer container = DockingUtilities.findTabbedDockableContainer(dockable);
        if (container != null) {
            container.setSelectedDockable(dockable);
        }
        dockable.getComponent().requestFocus();
    }

    public void addThemeMenu(Menu windowMenu) {
        Menu menu = new Menu("Theme");
        ToggleGroup tg = new ToggleGroup();
        String name = Preferences.instance().getSection("theme").optString("name");
        String[] themes = new String[] { "Modena", "Caspian" };
        for (String theme : themes) {
            RadioMenuItem mi = new RadioMenuItem(theme);
            mi.setToggleGroup(tg);
            if (theme.equals(name)) {
                mi.setSelected(true);
            }
            mi.selectedProperty().addListener((event, o, n) -> {
                if (!n) {
                    return;
                }
                JSONObject themeSection = Preferences.instance().getSection("theme");
                themeSection.put("name", theme);
                themeSection.put("builtin", true);
                Preferences.instance().save("theme");
            });
            menu.getItems().add(mi);
        }
        try {
            JSONArray customThemes = new JSONArray(
                    IOUtils.toString(getClass().getResourceAsStream("/themes.json"), Charset.defaultCharset()));
            for (int i = 0; i < customThemes.length(); i++) {
                JSONObject theme = customThemes.getJSONObject(i);
                String nm = theme.getString("name");
                RadioMenuItem mi = new RadioMenuItem(nm);
                mi.setToggleGroup(tg);
                if (nm.equals(name)) {
                    mi.setSelected(true);
                }
                mi.selectedProperty().addListener((event, o, n) -> {
                    if (!n) {
                        return;
                    }
                    JSONObject themeSection = Preferences.instance().getSection("theme");
                    themeSection.put("name", nm);
                    themeSection.put("path", theme.getString("path"));
                    themeSection.put("builtin", false);
                    Preferences.instance().save("theme");
                });
                menu.getItems().add(mi);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Preferences.instance().addPreferenceChangeListener("theme", new IPreferenceChangeListener() {
            @Override
            public void preferencesChanged(String section, JSONObject preferences) {
                setTheme();
                ObservableList<MenuItem> items = menu.getItems();
                for (MenuItem menuItem : items) {
                    if (preferences.has("name")) {
                        String theme = preferences.getString("name");
                        if (menuItem.getText().equals(theme))
                            ((RadioMenuItem) menuItem).setSelected(true);
                    }
                }
            }
        });
        windowMenu.getItems().add(menu);
    }

    private Menu findMenu(MenuBar menuBar, String menuName) {
        ObservableList<Menu> menus = menuBar.getMenus();
        int n = menus.size();
        for (int i = 0; i < n; i++) {
            Menu menu = menus.get(i);
            if (menu.getText().equals(menuName)) {
                return menu;
            }
        }
        Menu menu = new Menu(menuName);
        menuBar.getMenus().add(menu);
        return menu;
    }

    /**
     * Create a toolbar the Main window. Since we use VLDocking framework the
     * ToolBarContainer provides the panel where to attach the toolbar.
     * 
     * @param container
     */
    private void createToolBar(ToolBarContainer container) {
        ToolBarPanel toolBarPanel = container.getToolBarPanel();
        VLToolBar toolBar = new VLToolBar();
        toolBar.add(createNewButton());
        toolBar.add(getActionButton(saveAction));
        toolBar.add(getActionButton(saveAsAction));
        toolBar.add(getActionButton(saveAllAction));
        toolBarPanel.add(toolBar);
        toolBar = new VLToolBar();
        recordActionButton = getActionButton(recordAction);
        toolBar.add(recordActionButton);
        toolBar.add(getActionButton(resumeRecordingAction));
        toolBar.add(getActionButton(stopAction));
        toolBarPanel.add(toolBar);
        toolBar = new VLToolBar();
        toolBar.add(getActionButton(openApplicationAction));
        toolBar.add(getActionButton(closeApplicationAction));
        toolBarPanel.add(toolBar);
        toolBar = new VLToolBar();
        toolBar.add(getActionButton(playAction));
        toolBar.add(getActionButton(stopPlayAction));
        toolBar.add(getActionButton(slowPlayAction));
        toolBar.add(getActionButton(debugAction));
        toolBar.add(getActionButton(toggleBreakpointAction));
        toolBar.add(getActionButton(resumePlayingAction));
        toolBar.add(getActionButton(stepIntoAction));
        toolBar.add(getActionButton(stepOverAction));
        toolBar.add(getActionButton(stepReturnAction));
        toolBar.add(getActionButton(playerConsoleAction));
        toolBar.add(getActionButton(showReportAction));
        toolBarPanel.add(toolBar);
        if (actionProvider != null) {
            toolBar = new VLToolBar();
            IMarathonAction[] actions = actionProvider.getActions();
            for (IMarathonAction action2 : actions) {
                final IMarathonAction action = action2;
                if (!action.isToolBarAction()) {
                    continue;
                }
                if (action.isSeperator()) {
                    toolBarPanel.add(toolBar);
                    toolBar = new VLToolBar();
                } else {
                    toolBar.add(getActionButton(createAction(action)));
                }
            }
            toolBarPanel.add(toolBar);
        }
        showReportAction.setEnabled(false);
        return;
    }

    private AbstractSimpleAction createAction(final IMarathonAction action) {
        return new AbstractSimpleAction(action.getName(), action.getDescription(), action.getMneumonic(), action.getCommand()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void handle(ActionEvent arg0) {
                try {
                    int selectionStart = -1;
                    int selectionEnd = -1;
                    int startLine = -1;
                    int startOffsetOfStartLine = -1;
                    int startOffsetOfEndLine = -1;
                    String text = null;
                    int endOffsetOfEndLine = -1;
                    int endLine = -1;

                    if (currentEditor != null) {
                        selectionStart = currentEditor.getSelectionStart();
                        selectionEnd = currentEditor.getSelectionEnd();
                        startLine = currentEditor.getLineOfOffset(selectionStart);
                        endLine = currentEditor.getLineOfOffset(selectionEnd);
                        startOffsetOfStartLine = currentEditor.getLineStartOffset(startLine);
                        startOffsetOfEndLine = currentEditor.getLineStartOffset(endLine);
                        text = currentEditor.getText();
                        if (selectionEnd == startOffsetOfEndLine && selectionStart != selectionEnd) {
                            endLine = endLine - 1;
                        }
                        endOffsetOfEndLine = currentEditor.getLineEndOffset(endLine);
                    }

                    action.actionPerformed(DisplayWindow.this, scriptModel, text, startOffsetOfStartLine, endOffsetOfEndLine,
                            startLine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Node getIcon() {
                Node icon = action.getIcon();
                if (icon != null)
                    return icon;
                return super.getIcon();
            }
        };
    }

    Button getActionButton(AbstractSimpleAction action) {
        return action.getButton();
    }

    private int nDirty() {
        int n = 0;
        DockableState[] dockables = workspace.getDockables();
        for (DockableState dockableState : dockables) {
            Dockable dockable = dockableState.getDockable();
            if (dockable instanceof EditorDockable) {
                IEditor editor = ((EditorDockable) dockable).getEditor();
                if (editor.isDirty()) {
                    n++;
                }
            }
        }
        return n;
    }

    private void setState() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                displayView.setState(state);
            }
        });
    }

    /**
     * Save any unsaved buffers from the editor.
     * 
     * @return
     */
    private boolean saveFileIfNeeded() {
        DockableState[] dockables = workspace.getDockables();
        for (DockableState dockableState : dockables) {
            Dockable dockable = dockableState.getDockable();
            if (dockable instanceof EditorDockable) {
                IEditor editor = ((EditorDockable) dockable).getEditor();
                if (editor.isDirty()) {
                    if (!closeEditor(editor)) {
                        return false;
                    }
                }
            }
        }
        updateView();
        return true;
    }

    /**
     * Get the insertAction - for testing purpose.
     * 
     * @return
     */
    public AbstractSimpleAction getInsertAction() {
        return insertScriptAction;
    }

    /**
     * Get the currently available fixtures.
     * 
     * @return
     */
    private String[] getFixtures() {
        return scriptModel.getFixtures();
    }

    /**
     * Select a fixture from the available fixtures.
     */
    public void onSelectFixture() {
        Platform.runLater(() -> {
            String selectedFixture = fixtureSelector.selectFixture(this, getFixtures(), fixture);
            if (selectedFixture != null) {
                setDefaultFixture(selectedFixture);
            }
        });
    }

    /**
     * Goto a line
     */
    private void gotoLine() {
        if (currentEditor == null || currentEditor.getData("editorType") != IEditorProvider.EditorType.OTHER) {
            return;
        }
        int lastOffset = currentEditor.getText().length();
        int lastLine;
        lastLine = currentEditor.getLineOfOffset(lastOffset);
        Platform.runLater(() -> {
            Stage stage = lineNumberStage.getStage();
            lineNumberStage.setMaxLineNumber(lastLine + 1);
            lineNumberStage.setLine(currentEditor.getCaretLine() + 1);
            lineNumberStage.setInputHandler(new GoToLineHandler());
            stage.showAndWait();
        });
    }

    public class GoToLineHandler implements IInputHanler {

        @Override
        public void handleInput(String line) {
            int lineNumber = Integer.parseInt(line);
            if (lineNumber != -1) {
                currentEditor.setCaretLine(lineNumber - 1);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.display.DisplayView#goToFile(java.lang.String,
     * int)
     */
    public void goToFile(String fileName, int lineNumber) {
        File file = new File(fileName);
        if (file.exists()) {
            openFile(file);
            currentEditor.runWhenContentLoaded(() -> currentEditor.setCaretLine(lineNumber - 1));
            currentEditor.highlightLine(lineNumber);
        } else {
            EditorDockable editorDockable = findEditorDockable(fileName);
            if (editorDockable != null) {
                selectEditor(editorDockable);
                currentEditor.runWhenContentLoaded(() -> currentEditor.setCaretLine(lineNumber - 1));
                currentEditor.highlightLine(lineNumber);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.display.DisplayView#updateView()
     */
    public void updateView() {
        String projectName = System.getProperty(Constants.PROP_PROJECT_NAME, "");
        if (projectName.equals("")) {
            projectName = "Marathon";
        }
        String suffix = "";
        if (currentEditor != null && currentEditor.isDirty()) {
            suffix = "*";
        }
        if (currentEditor != null) {
            setStageTitle(projectName + " - " + currentEditor.getDisplayName() + suffix);
            updateDockName(currentEditor);
        } else {
            setStageTitle(projectName);
        }
        setState();
    }

    private void setStageTitle(String title) {
        Platform.runLater(() -> {
            DisplayWindow.this.setTitle(title);
        });
    }

    /**
     * Check whether the current file in the editor is a file belonging to
     * Marathon project.
     * 
     * @return true, if the file belongs to Marathon project.
     */
    private boolean isProjectFile() {
        return currentEditor != null && currentEditor.isProjectFile();
    }

    /**
     * Check whether the current file in the editor is a test file.
     * 
     * @return true, if the current file is a test file.
     */
    public boolean isTestFile() {
        return currentEditor != null && currentEditor.isTestFile();
    }

    /**
     * Create a new module in the file specified.
     * 
     * @return true, if the module is created.
     */
    private void newModuleFile() {
        String[] moduleDirs = Constants.getMarathonDirectoriesAsStringArray(Constants.PROP_MODULE_DIRS);
        MarathonModuleStage marathonModuleStage = new MarathonModuleStage(
                new ModuleInfo("New Module Function", Arrays.asList(moduleDirs), scriptModel.getSuffix()));
        marathonModuleStage.setModuleFunctionHandler(new ModuleFunctionHandler());
        marathonModuleStage.getStage().showAndWait();
    }

    class ModuleFunctionHandler implements IModuleFunctionHandler {
        @Override
        public void handleModule(ModuleInfo moduleInfo) {
            File moduleFile = new File(moduleInfo.getModuleDirElement().getFile(), moduleInfo.getFileName());
            int offset = 0;
            try {
                boolean fileExists = moduleFile.exists();
                addImportStatement(moduleFile, fileExists);
                if (fileExists && canAppend(moduleFile)) {
                    offset = (int) moduleFile.length();
                }
                FileWriter writer = new FileWriter(moduleFile, true);
                writer.write((offset > 0 ? EOL : "")
                        + getModuleHeader(moduleInfo.getModuleFunctionName(), moduleInfo.getModulefunctionDescription()));
                writer.close();
                fileUpdated(moduleFile);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        openFile(moduleFile);
                    }
                });
                final int o = offset;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentEditor.setCaretPosition(scriptModel.getLinePositionForInsertionModule() + o);
                    }
                });
                resetModuleFunctions();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                FXUIUtils.showMessageDialog(DisplayWindow.this, "IOError: " + e.getMessage(), "Error", AlertType.ERROR);
                return;
            }
        }

    }

    /**
     * Adds the playback import statement to the module file.
     * 
     * @param moduleFile
     * @param fileExists
     * @throws IOException
     */
    private void addImportStatement(File moduleFile, boolean fileExists) throws IOException {
        String importStatement = scriptModel.getPlaybackImportStatement();
        if (importStatement == null || importStatement.trim().length() == 0) {
            return;
        }
        String startMarker = scriptModel.getMarathonStartMarker();
        String endMarker = scriptModel.getMarathonEndMarker();
        String defaultMarkersImportStmt = startMarker + "\n" + importStatement + "\n" + endMarker + "\n";

        if (fileExists) {
            String moduleContents = "";
            moduleContents = readFile(moduleFile);
            int startMarkerIndex = moduleContents.indexOf(startMarker);
            int endMarkerIndex = moduleContents.indexOf(endMarker, startMarkerIndex);
            boolean importStatementFound = false;
            if (startMarkerIndex != -1 && endMarkerIndex != -1) {
                importStatementFound = importStatementExists(importStatement,
                        moduleContents.substring(startMarkerIndex + startMarker.length(), endMarkerIndex));
            }
            if (!importStatementFound) {
                int insertIndex;
                String insertContents;
                if (startMarkerIndex == -1 || endMarkerIndex == -1) {
                    insertIndex = 0;
                    insertContents = defaultMarkersImportStmt;
                } else {
                    insertIndex = startMarkerIndex + startMarker.length();
                    insertContents = "\n" + scriptModel.getPlaybackImportStatement() + "\n";
                }
                StringBuilder sbr = new StringBuilder(moduleContents);
                sbr.insert(insertIndex, insertContents);
                writeToFile(sbr.toString(), moduleFile);
            }
        } else {
            writeToFile(defaultMarkersImportStmt, moduleFile);
        }

    }

    /**
     * Writes the given contents to the file. Previous contents are lost.
     * 
     * @param string
     * @param moduleFile
     * @throws IOException
     */
    private void writeToFile(String string, File moduleFile) throws IOException {
        Writer writer = new FileWriter(moduleFile);
        writer.write(string);
        writer.flush();
        writer.close();
    }

    /**
     * Checks whether the given statement exists and exists in a single line.
     * 
     * @param statement
     * @param contents
     * @return
     */
    private boolean importStatementExists(String statement, String contents) {
        String[] lines = contents.split("\n");
        boolean importStatementFound = false;
        for (String line : lines) {
            if (line.equals(statement)) {
                importStatementFound = true;
                break;
            }
        }
        return importStatementFound;
    }

    /**
     * Reads the given file and returns the contents of the file as a string.
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String readFile(File file) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "";
        StringBuilder moduleContents = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            moduleContents.append(line + "\n");
        }
        reader.close();
        return moduleContents.toString();
    }

    private void newFXModuleDir() {
        MarathonInputStage mid = new MarathonInputStage("New Module Directory",
                "Create a new module folder to store extracted methods", FXUIUtils.getIcon("fldr")) {

            @Override
            protected String validateInput(String moduleDirName) {
                String errorMessage = null;
                if (moduleDirName.length() == 0 || moduleDirName.trim().isEmpty()) {
                    errorMessage = "Enter a valid folder name";
                } else if (moduleDirName.charAt(0) == ' ' || moduleDirName.charAt(moduleDirName.length() - 1) == ' ') {
                    errorMessage = "Module Directory Name cannot begin/end with a whitespace.";
                }
                return errorMessage;
            }

            @Override
            protected String getInputFiledLabelText() {
                return "Module Directory: ";
            }

            @Override
            protected void setDefaultButton() {
                okButton.setDefaultButton(true);
            }
        };
        mid.setInputHandler(new NewModuleDirHandler());
        mid.getStage().showAndWait();
    }

    class NewModuleDirHandler implements IInputHanler {

        @Override
        public void handleInput(String moduleDirName) {
            try {
                if (moduleDirName == null || moduleDirName.trim().equals("")) {
                    return;
                }
                File moduleDir = new File(new File(System.getProperty(Constants.PROP_PROJECT_DIR)), moduleDirName);
                if (moduleDir.exists()) {
                    FXUIUtils.showMessageDialog(DisplayWindow.this, "A directory with the given name already exits", "Error",
                            AlertType.ERROR);
                    return;
                }
                if (!moduleDir.mkdir()) {
                    throw new IOException("Unable to create module folder: " + moduleDir);
                }
                addModuleDirToMPF(moduleDirName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                FXUIUtils.showMessageDialog(DisplayWindow.this, "Could not complete creation of module directory.", "Error",
                        AlertType.ERROR);
            } catch (IOException e) {
                e.printStackTrace();
                FXUIUtils.showMessageDialog(DisplayWindow.this, "Could not complete creation of module directory.", "Error",
                        AlertType.ERROR);
            }
        }

    }

    private void newFXSuiteFile() {
        GroupInputStage suiteInputStage = new GroupInputStage(Group.GroupType.SUITE);
        suiteInputStage.setInputHandler(new NewGroupHandler(Group.GroupType.SUITE));
        suiteInputStage.show(this);
    }

    private void newFXFeatureFile() {
        GroupInputStage suiteInputStage = new GroupInputStage(Group.GroupType.FEATURE);
        suiteInputStage.setInputHandler(new NewGroupHandler(Group.GroupType.FEATURE));
        suiteInputStage.show(this);
    }

    private void newFXStoryFile() {
        GroupInputStage suiteInputStage = new GroupInputStage(Group.GroupType.STORY);
        suiteInputStage.setInputHandler(new NewGroupHandler(Group.GroupType.STORY));
        suiteInputStage.show(this);
    }

    private void newFXIssueFile() {
        GroupInputStage suiteInputStage = new GroupInputStage(Group.GroupType.ISSUE);
        suiteInputStage.setInputHandler(new NewGroupHandler(Group.GroupType.ISSUE));
        suiteInputStage.show(this);
    }

    private class NewGroupHandler implements IGroupInputHanler {

        private GroupType type;

        public NewGroupHandler(GroupType type) {
            this.type = type;
        }

        @Override
        public void handleInput(GroupInputInfo info) {
            try {
                File file = info.getFile();
                if (file == null) {
                    return;
                }
                Group group = Group.createGroup(type, file.toPath(), info.getName());
                if (group == null) {
                    return;
                }
                fileUpdated(file);
                navigatorPanel.updated(DisplayWindow.this, new FileResource(file.getParentFile()));
                suitesPanel.updated(DisplayWindow.this, new FileResource(file));
                featuresPanel.updated(DisplayWindow.this, new FileResource(file));
                storiesPanel.updated(DisplayWindow.this, new FileResource(file));
                issuesPanel.updated(DisplayWindow.this, new FileResource(file));
                Platform.runLater(() -> openFile(file));
            } catch (Exception e) {
                e.printStackTrace();
                FXUIUtils.showConfirmDialog(DisplayWindow.this,
                        "Could not complete creation of " + type.fileType().toLowerCase() + " file.",
                        "Error in creating a " + type.fileType().toLowerCase() + " file", AlertType.ERROR);
            }
        }

    }

    /**
     * Adds the given directory as a module directory to Marathon Project File.
     * 
     * @param moduleDirName
     * @throws IOException
     */
    private void addModuleDirToMPF(String moduleDirName) throws IOException {
        String[] currentModuleDirs = Constants.getMarathonDirectoriesAsStringArray(Constants.PROP_MODULE_DIRS);
        StringBuilder sbr = new StringBuilder();
        for (String currentModuleDir : currentModuleDirs) {
            sbr.append(getProjectRelativeName(currentModuleDir) + ";");
        }
        sbr.append("%" + Constants.PROP_PROJECT_DIR + "%/" + moduleDirName);
        updateProjectFile(Constants.PROP_MODULE_DIRS, sbr.toString());

    }

    /**
     * Returns the name substituting the Marathon Project Directory in the given
     * string by appropriate global variable.
     * 
     * @param path
     * @return
     */
    private String getProjectRelativeName(String path) {
        String projDirPath = System.getProperty(Constants.PROP_PROJECT_DIR);
        int index = path.indexOf(projDirPath);
        if (index != 0) {
            return path;
        }
        String relativePath = path.replace(projDirPath, "%" + Constants.PROP_PROJECT_DIR + "%");
        return relativePath;
    }

    private void updateProjectFile(String property, String value) throws IOException {
        ProjectFile.updateProjectProperty(property, value);
        Properties mpfProps = ProjectFile.getProjectProperties();
        MPFUtils.replaceEnviron(mpfProps);
        String sysModDirs = mpfProps.getProperty(property).replace(';', File.pathSeparatorChar);
        sysModDirs = sysModDirs.replace('/', File.separatorChar);

        System.setProperty(property, sysModDirs);
    }

    private String getModuleHeader(String functionName, String description) {
        return scriptModel.getModuleHeader(functionName, description);
    }

    public IEditor newFile(String script, File directory) {
        IEditor newEditor = createEditor(IEditorProvider.EditorType.OTHER);
        newEditor.createNewResource(script, directory);
        setCurrentEditorDockable((EditorDockable) newEditor.getData("dockable"));
        return newEditor;
    }

    private void setCurrentEditorDockable(EditorDockable editorDockable) {
        if (editorDockable == null) {
            setCurrentEditor(null);
        } else if (currentEditor != null) {
            Dockable dockable = (Dockable) currentEditor.getData("dockable");
            TabbedDockableContainer dockableContainer = DockingUtilities.findTabbedDockableContainer(dockable);
            int order = 1;
            if (dockableContainer != null) {
                order = dockableContainer.indexOfDockable(dockable) + 1;
            }
            DockableState dockableState = workspace.getDockableState(editorDockable);
            if (dockableState == null) {
                workspace.createTab(dockable, editorDockable, order, true);
            }
        } else {
            DockableState dockableState = workspace.getDockableState(editorDockable);
            DockableState OPDockableState = workspace.getDockableState(outputPane);
            if (dockableState == null && OPDockableState != null && OPDockableState.isDocked()) {
                workspace.split(outputPane, editorDockable, Split.TOP, 0.8);
            } else if (dockableState == null) {
                DockableState RDockableState = workspace.getDockableState(resultPane);
                if (RDockableState != null && RDockableState.isDocked()) {
                    workspace.split(resultPane, editorDockable, Split.TOP, 0.8);
                } else {
                    workspace.addDockable(editorDockable);
                }
            }
        }
        if (editorDockable != null) {
            setCurrentEditor(editorDockable.getEditor());
        }
        updateView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.display.DisplayView#newFile()
     */
    public void newTestCaseFile() {
        String testHeader = getDefaultTestHeader();
        if (testHeader == null) {
            return;
        }
        newFile(testHeader, new File(System.getProperty(Constants.PROP_TEST_DIR)));
        final int line = scriptModel.getLinePositionForInsertion();
        currentEditor.runWhenContentLoaded(() -> currentEditor.setCaretLine(line));
    }

    public void newCheckListFile() {
        NewChekListInputStage chekListInputStage = new NewChekListInputStage();
        chekListInputStage.getStage().showAndWait();
        if (chekListInputStage.isOk()) {
            IEditor newEditor = createEditor(IEditorProvider.EditorType.CHECKLIST);
            newEditor.createNewResource(chekListInputStage.getScript(), new File(System.getProperty(Constants.PROP_CHECKLIST_DIR)));
            setCurrentEditorDockable((EditorDockable) newEditor.getData("dockable"));
        }
    }

    /**
     * Get the default test header.
     * 
     * @return header, the test header
     */
    private String getDefaultTestHeader() {
        File fixtureFile = new File(System.getProperty(Constants.PROP_FIXTURE_DIR), fixture + scriptModel.getSuffix());
        if (!fixtureFile.exists()) {
            Platform.runLater(() -> {
                FXUIUtils.showMessageDialog(DisplayWindow.this, "Selected Fixture does not exists", "Invalid Fixture",
                        AlertType.ERROR);
            });
            return null;
        }
        return scriptModel.getDefaultTestHeader(fixture);
    }

    /**
     * Create a new Fixture file.
     */

    private void newFixtureFile() {
        FixtureStage fixtureStage = new FixtureStage(
                new FixtureStageInfo(Arrays.asList(new File(System.getProperty(Constants.PROP_FIXTURE_DIR)).list())));
        fixtureStage.setFixtureStageInfoHandler(new NewFixtureHandler());
        fixtureStage.getStage().showAndWait();
    }

    class NewFixtureHandler implements IFixtureStageInfoHandler {

        @Override
        public void handle(FixtureStageInfo fixtureInfo) {
            newFile(getFixtureHeader(fixtureInfo.getProperties(), fixtureInfo.getSelectedLauncher()),
                    new File(System.getProperty(Constants.PROP_FIXTURE_DIR)));
            currentEditor.setDirty(true);
            File fixtureFile = new File(System.getProperty(Constants.PROP_FIXTURE_DIR),
                    fixtureInfo.getFixtureName() + scriptModel.getSuffix());
            try {
                currentEditor.setData("filename", fixtureFile.getName());
                saveTo(fixtureFile);
                currentEditor.setDirty(false);
                updateView();
            } catch (IOException e) {
                FXUIUtils.showMessageDialog(DisplayWindow.this, "Unable to save the fixture: " + e.getMessage(), "Invalid File",
                        AlertType.ERROR);
                return;
            }
            setDefaultFixture(fixtureInfo.getFixtureName());
        }
    }

    /**
     * Get the fixture header for the current fixture.
     * 
     * @param props
     * @param launcher
     * 
     * @param arguments
     * @param className
     * @param string
     * 
     * @return
     */
    private String getFixtureHeader(Properties props, String launcher) {
        IRuntimeLauncherModel launcherModel = LauncherModelHelper.getLauncherModel(launcher);
        if (launcherModel == null) {
            return "";
        }
        return scriptModel.getDefaultFixtureHeader(props, launcher, launcherModel.getPropertyKeys());
    }

    void openFile(File file) {
        openFile(file, null);
    }

    void openFile(File file, EditorType type) {
        final EditorDockable dockable = findEditorDockable(file);
        if (dockable != null) {
            selectEditor(dockable);
            return;
        }
        IEditor openEditor = createEditor(file, type);
        if (openEditor != null) {
            setCurrentEditorDockable((EditorDockable) openEditor.getData("dockable"));
            openEditor.setFocus();
        }
    }

    private void selectEditor(final EditorDockable dockable) {
        TabbedDockableContainer container = DockingUtilities.findTabbedDockableContainer(dockable);
        if (container != null) {
            container.setSelectedDockable(dockable);
        }
        dockable.getEditor().setFocus();
        setCurrentEditor(dockable.getEditor());
        return;
    }

    private EditorDockable findEditorDockable(File file) {
        DockableState[] dockables = workspace.getDockables();
        EditorType editorType = findEditorType(file);
        for (DockableState dockableState : dockables) {
            if (dockableState.getDockable() instanceof EditorDockable) {
                EditorDockable editorDockable = (EditorDockable) dockableState.getDockable();
                IEditor e = editorDockable.getEditor();
                if (e.isEditingResource(file) || !e.isFileBased() && editorType.equals(e.getData("editorType"))) {
                    return editorDockable;
                }
            }
        }
        return null;
    }

    private EditorDockable findEditorDockable(String fileName) {
        DockableState[] dockables = workspace.getDockables();
        for (DockableState dockableState : dockables) {
            if (dockableState.getDockable() instanceof EditorDockable) {
                EditorDockable editorDockable = (EditorDockable) dockableState.getDockable();
                String name = editorDockable.getEditor().getName();
                if (fileName.equals(name)) {
                    return editorDockable;
                }
            }
        }
        return null;
    }

    /**
     * Report the exception to the user.
     * 
     * @param e
     *            , exception to be reported.
     */
    private void reportException(Throwable e) {
        RuntimeLogger.getRuntimeLogger().error(MODULE, e.getMessage(), ExceptionUtil.getTrace(e));
        outputPane.append(e.getMessage(), IStdOut.STD_ERR);
    }

    private File saveAs() {
        File file = null;
        try {
            file = currentEditor.saveAs();
            if (file != null) {
                currentEditor.setDirty(false);
                EditorDockable dockable = (EditorDockable) currentEditor.getData("dockable");
                dockable.updateKey();
            }
            updateView();
        } catch (IOException e) {
            reportException(e);
        }
        return file;
    }

    /**
     * Save all unsaved buffers in the editor.
     */
    public void saveAll() {
        DockableState[] dockables = workspace.getDockables();
        for (DockableState dockableState : dockables) {
            Dockable dockable = dockableState.getDockable();
            if (dockable instanceof EditorDockable) {
                IEditor editor = ((EditorDockable) dockable).getEditor();
                if (editor.isDirty()) {
                    save(editor);
                }
            }
        }
        updateView();
    }

    private File save() {
        if (exploratoryTest) {
            String testName = runReportDir.getName() + scriptModel.getSuffix();
            File testDir = new File(System.getProperty(Constants.PROP_TEST_DIR), "ExploratoryTests");
            if (!testDir.exists()) {
                if (!testDir.mkdir()) {
                    throw new RuntimeException("Unable to create the test directory: " + testDir);
                }
            }
            File file = new File(testDir, testName);
            try {
                saveTo(file);
                updateView();
            } catch (IOException e) {
                reportException(e);
            }
            return file;
        }
        return save(currentEditor);
    }

    private File save(IEditor e) {
        int caretLine = e.getCaretPosition();
        File file = null;
        try {
            file = e.save();
            if (file != null) {
                e.setDirty(false);
                if (isModuleFile()) {
                    scriptModel.fileUpdated(file, SCRIPT_FILE_TYPE.MODULE);
                }
            } else if (!e.isFileBased()) {
                e.setDirty(false);
            }
            updateDockName(e);
            updateView();
            ((EditorDockable) e.getData("dockable")).updateKey();
            if (e.isModuleFile()) {
                resetModuleFunctions();
            }
        } catch (Exception e1) {
            FXUIUtils.showMessageDialog(this, "Unable to save file: " + e1.getMessage(), "Error in Saving", AlertType.ERROR);
        }
        if (file != null) {
            suitesPanel.updated(DisplayWindow.this, new FileResource(file));
            featuresPanel.updated(DisplayWindow.this, new FileResource(file));
            storiesPanel.updated(DisplayWindow.this, new FileResource(file));
            issuesPanel.updated(DisplayWindow.this, new FileResource(file));
            if (file.getName().equals("logging.properties"))
                OSUtils.setLogConfiguration(Constants.getMarathonProjectDirectory().getAbsolutePath());
            if (file.getName().equals("project.json"))
                Preferences.resetInstance();
            navigatorPanel.updated(DisplayWindow.this, new FileResource(file));
            e.refreshResource();
            e.refresh();
            e.runWhenContentLoaded(() -> e.setCaretPosition(caretLine));
        }
        return file;
    }

    private void updateDockName(IEditor e) {
        String suffix = "";
        if (e.isDirty()) {
            suffix = "*";
        }
        String title = "";
        title = e.getName() + suffix;
        Dockable dockable = (Dockable) e.getData("dockable");
        if (dockable != null) {
            DockKey dk = dockable.getDockKey();
            dk.setName(title);
        }
    }

    public void saveTo(File file) throws IOException {
        currentEditor.runWhenContentLoaded(() -> {
            try {
                currentEditor.saveTo(file);
            } catch (IOException e) {
            }
            if (file != null) {
                currentEditor.setDirty(false);
            }
        });
    }

    private CheckList fillUpChecklist(final String fileName) {
        File file = new File(System.getProperty(Constants.PROP_CHECKLIST_DIR), fileName);
        return display.fillUpChecklist(testCase, file, this);
    }

    private boolean canCloseComponent(Dockable dockable) {
        if (dockable instanceof EditorDockable) {
            return closeEditor(((EditorDockable) dockable).getEditor());
        }
        return true;
    }

    private boolean closeEditor(IEditor e) {
        if (e == null) {
            return true;
        }
        if (e.isDirty()) {
            Optional<ButtonType> result = FXUIUtils.showConfirmDialog(DisplayWindow.this,
                    "File \"" + e.getName() + "\" Modified. Do you want to save the changes ",
                    "File \"" + e.getName() + "\" Modified", AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO,
                    ButtonType.CANCEL);
            ButtonType shouldSaveFile = result.get();
            if (shouldSaveFile == ButtonType.CANCEL) {
                return false;
            }
            if (shouldSaveFile == ButtonType.YES) {
                File file = save(e);
                if (file == null) {
                    return false;
                }
                EditorDockable dockable = (EditorDockable) e.getData("dockable");
                dockable.updateKey();
            }
        }
        return true;
    }

    /**
     * Marathon Actions available from Menu/Toolbars
     */

    @ISimpleAction(mneumonic = "Shortcut+P", description = "Play the testcase")
    AbstractSimpleAction playAction;

    @ISimpleAction(description = "Stop the playing testcase")
    AbstractSimpleAction stopPlayAction;

    @ISimpleAction(description = "Show report for last test run")
    AbstractSimpleAction showReportAction;

    @ISimpleAction(mneumonic = "Shortcut+Alt+P", description = "Debug the testcase")
    AbstractSimpleAction debugAction;

    @ISimpleAction(mneumonic = "Shortcut+Shift+P", description = "Play the testcase with a delay")
    AbstractSimpleAction slowPlayAction;

    @ISimpleAction(description = "Pause recording")
    AbstractSimpleAction pauseAction;

    @ISimpleAction(description = "Resume recording")
    AbstractSimpleAction resumeRecordingAction;

    @ISimpleAction(description = "Resume playing")
    AbstractSimpleAction resumePlayingAction;

    @ISimpleAction()
    AbstractSimpleAction selectFixtureAction;

    @ISimpleAction(mneumonic = "Shortcut+R", description = "Start recording")
    AbstractSimpleAction recordAction;

    @ISimpleAction(mneumonic = "Shortcut+Shift+N", value = "Exploratory Test", description = "Record an exploratory test")
    AbstractSimpleAction etAction;

    @ISimpleAction(description = "Stop recording")
    AbstractSimpleAction stopAction;

    @ISimpleAction(description = "Start raw recording")
    AbstractSimpleAction rawRecordAction;

    @ISimpleAction(description = "Open Application")
    AbstractSimpleAction openApplicationAction;

    @ISimpleAction(description = "Close Application")
    AbstractSimpleAction closeApplicationAction;

    @ISimpleAction(description = "Insert a module method")
    AbstractSimpleAction insertScriptAction;

    @ISimpleAction(description = "Insert a checklist")
    AbstractSimpleAction insertChecklistAction;

    @ISimpleAction(description = "Change project settings", value = "Project Settings...")
    AbstractSimpleAction projectSettingsAction;

    @ISimpleAction(mneumonic = "Shortcut+N", description = "Create a new testcase")
    AbstractSimpleAction newTestcaseAction;

    @ISimpleAction(description = "Create a new module method")
    AbstractSimpleAction newModuleAction;

    @ISimpleAction(description = "Create a new fixture")
    AbstractSimpleAction newFixtureAction;

    @ISimpleAction(description = "Create a new CheckList")
    AbstractSimpleAction newCheckListAction;

    @ISimpleAction(mneumonic = "Shortcut+S", description = "Save current file")
    AbstractSimpleAction saveAction;

    @ISimpleAction(description = "Save as")
    AbstractSimpleAction saveAsAction;

    @ISimpleAction(mneumonic = "Shortcut+Shift+S", description = "Save all modifications")
    AbstractSimpleAction saveAllAction;

    @ISimpleAction(mneumonic = "Shortcut+Q", description = "Exit Marathon")
    AbstractSimpleAction exitAction;

    @ISimpleAction(description = "Show release notes", value = "Read me")
    AbstractSimpleAction releaseNotes;

    @ISimpleAction(description = "Show change log")
    AbstractSimpleAction changeLog;

    @ISimpleAction(description = "Show marathon website", value = "Marathon on web")
    AbstractSimpleAction visitWebsite;

    @ISimpleAction(description = "Change preferences", value = "Preferences...")
    AbstractSimpleAction preferencesAction;

    @ISimpleAction(description = "Reset workspace to default")
    AbstractSimpleAction resetWorkspaceAction;

    @ISimpleAction(mneumonic = "Shortcut+B", description = "Toggle breakpoint at the current line")
    AbstractSimpleAction toggleBreakpointAction;

    @ISimpleAction(description = "Remove all breakpoints", value = "Remove all breakpoints")
    AbstractSimpleAction clearAllBreakpointsAction;

    @ISimpleAction(description = "Step into the method")
    AbstractSimpleAction stepIntoAction;

    @ISimpleAction(description = "Step over the method")
    AbstractSimpleAction stepOverAction;

    @ISimpleAction(description = "Return from current method")
    AbstractSimpleAction stepReturnAction;

    @ISimpleAction(description = "Player console")
    AbstractSimpleAction playerConsoleAction;

    @ISimpleAction(description = "Recorder console")
    AbstractSimpleAction recorderConsoleAction;

    @ISimpleAction(description = "Create a new Module directory", value = "New Module Directory")
    AbstractSimpleAction newModuleDirAction;

    @ISimpleAction(description = "Create a new Suite file", value = "New Suite")
    AbstractSimpleAction newSuiteFileAction;

    @ISimpleAction(description = "Create a new Feature file", value = "New Feature")
    AbstractSimpleAction newFeatureFileAction;

    @ISimpleAction(description = "Create a new Story file", value = "New Story")
    AbstractSimpleAction newStoryFileAction;

    @ISimpleAction(description = "Create a new Issue file", value = "New Issue")
    AbstractSimpleAction newIssueFileAction;

    AbstractSimpleAction refreshAction = new AbstractSimpleAction("refresh", "Refresh Editor", "F5", "Refresh Editor Content") {
        private static final long serialVersionUID = 1L;

        @Override
        public void handle(ActionEvent e) {
            if (currentEditor != null) {
                currentEditor.refreshResource();
                currentEditor.refresh();
            }
        }
    };

    private DockGroup editorDockGroup;

    private boolean resetWorkspaceOperation;

    private transient IConsole taConsole = new EditorConsole(displayView);

    public transient ILogger logViewLogger;

    private HashSet<String> importStatements;

    public void onPlay() {
        if (editingObjectMap())
            return;
        resultPane.clear();
        outputPane.clear();
        debugging = false;
        callStack.clear();
        displayView.getOutputPane().clear();
        display.play(taConsole, debugging);
    }

    public void onStopPlay() {
        display.stop();
    }

    public void onDebug() {
        if (editingObjectMap())
            return;
        resultPane.clear();
        outputPane.clear();
        breakStackDepth = -1;
        stepIntoActive = false;
        debugging = true;
        displayView.getOutputPane().clear();
        display.play(taConsole, debugging);
    }

    public void onSlowPlay() {
        if (editingObjectMap())
            return;
        String delay = System.getProperty(Constants.PROP_RUNTIME_DEFAULT_DELAY, "1000");
        if (delay.equals("")) {
            delay = "1000";
        }
        System.setProperty(Constants.PROP_RUNTIME_DELAY, delay);
        resultPane.clear();
        outputPane.clear();
        debugging = false;
        callStack.clear();
        displayView.getOutputPane().clear();
        display.play(taConsole, debugging);
    }

    public void onPause() {
        System.setProperty(Constants.PROP_RUNTIME_DELAY, "0");
        display.pauseRecording();
    }

    public void onResumeRecording() {
        resumePlay();
    }

    public void onResumePlaying() {
        resumePlay();
    }

    public void onRecord() {
        if (editingObjectMap())
            return;
        importStatements = new HashSet<String>();
        resultPane.clear();
        outputPane.clear();
        controller.clear();
        WaitMessageDialog.setVisible(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                display.record(taConsole);
            }
        }).start();
        displayView.setState(State.RECORDING_ABOUT_TO_START);
    }

    public void onEt() {
        if (editingObjectMap())
            return;
        exploratoryTest = true;
        etAction.setEnabled(false);
        newTestCaseFile();
        displayView.startTestRun();
        displayView.startTest();
        currentEditor.runWhenContentLoaded(new Runnable() {
            @Override
            public void run() {
                recordActionButton.fire();
            }
        });
    }

    public void onStop() {
        display.stop();
        if (exploratoryTest) {
            displayView.endTest(null);
            displayView.endTestRun();
            save();
            exploratoryTest = false;
        }
    }

    public void onRawRecord() {
        isRawRecording = rawRecordButton.isSelected();
        display.setRawRecording(isRawRecording);
    }

    public void onOpenApplication() {
        resultPane.clear();
        outputPane.clear();
        display.openApplication(taConsole);
    }

    public void onCloseApplication() {
        display.closeApplication(true);
    }

    public void onInsertScript() {
        Platform.runLater(() -> {
            Module root = getModuleFunctions();
            FunctionStage functionStage = new FunctionStage(new FunctionInfo(DisplayWindow.this, display.getTopWindowName(), root));
            functionStage.setFunctionArgumentHandler(new FunctionArgumentHandler(functionStage));
            functionStage.getStage().showAndWait();
        });
    }

    private final class FunctionArgumentHandler implements IFunctionArgumentHandler {
        private FunctionStage functionStage;

        public FunctionArgumentHandler(FunctionStage functionStage) {
            this.functionStage = functionStage;
        }

        @Override
        public void handle(String[] arguments, Function function) {
            new Thread() {
                @Override
                public void run() {
                    insertScript(ScriptModel.getModel().getFunctionCallForInsertDialog(function, arguments));
                    Platform.runLater(() -> functionStage.dispose());
                }
            }.start();
        }
    }

    public void onInsertChecklist() {
        String checklistDir = System.getProperty(Constants.PROP_CHECKLIST_DIR);
        File dir = new File(checklistDir);
        CheckListForm checkListInfo = new CheckListForm(dir, true);
        MarathonCheckListStage checklistStage = new MarathonCheckListStage(checkListInfo);
        checklistStage.setInsertCheckListHandler(new IInsertCheckListHandler() {
            @Override
            public boolean insert(CheckListElement selectedItem) {
                insertChecklist(selectedItem.getFile().getName());
                return true;
            }
        });
        Stage stage = checklistStage.getStage();
        stage.showAndWait();
    }

    public void onProjectSettings() {
        Platform.runLater(() -> {
            String projectDir = System.getProperty(Constants.PROP_PROJECT_DIR);
            List<Boolean> projectEdited = new ArrayList<>();
            String title = "Configure";
            Properties properties = new Properties();
            try {
                properties = ProjectFile.getProjectProperties();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
            }
            properties.setProperty(Constants.PROP_PROJECT_DIR, projectDir);
            setFrameWork(properties);
            String name = properties.getProperty(Constants.PROP_PROJECT_NAME);
            if (name != null) {
                title = "Configure - " + name;
            }
            MPFConfigurationInfo mpfConfigurationInfo = new MPFConfigurationInfo(title, projectDir, properties);
            MPFConfigurationStage mpfConfigurationStage = new MPFConfigurationStage(null, mpfConfigurationInfo) {
                @Override
                public void onSave() {
                    if (validInupt()) {
                        mpfConfigurationInfo.saveProjectFile(layouts);
                        System.getProperty(Constants.PROP_PROJECT_NAME);
                        projectEdited.add(true);
                        dispose();
                        Preferences.resetInstance();
                    }
                }
            };
            mpfConfigurationStage.getStage().showAndWait();
            RuntimeLogger.setRuntimeLogger(logViewLogger);
            navigatorPanel.updated(DisplayWindow.this, new FileResource(new File(projectDir, ProjectFile.PROJECT_FILE)));
        });
    }

    private void setFrameWork(Properties properties) {
        String framework = properties.getProperty(Constants.PROP_PROJECT_FRAMEWORK);
        if (framework != null) {
            System.setProperty(Constants.PROP_PROJECT_FRAMEWORK, framework);
        }
        String launcherModel = properties.getProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL);
        if (launcherModel != null) {
            System.setProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL, launcherModel);
        }
    }

    public void onNewTestcase() {
        newTestCaseFile();
    }

    public void onNewModule() {
        Platform.runLater(() -> newModuleFile());
    }

    public void onNewModuleDir() {
        Platform.runLater(() -> newFXModuleDir());
    }

    public void onNewSuiteFile() {
        Platform.runLater(() -> newFXSuiteFile());
    }

    public void onNewFeatureFile() {
        Platform.runLater(() -> newFXFeatureFile());
    }

    public void onNewStoryFile() {
        Platform.runLater(() -> newFXStoryFile());
    }

    public void onNewIssueFile() {
        Platform.runLater(() -> newFXIssueFile());
    }

    public void onNewFixture() {
        Platform.runLater(() -> newFixtureFile());
    }

    public void onNewCheckList() {
        newCheckListFile();
    }

    public void onSave() {
        save();
    }

    public void onSaveAs() {
        File file = saveAs();
        if (file != null) {
            if (isModuleFile()) {
                scriptModel.fileUpdated(file, SCRIPT_FILE_TYPE.MODULE);
            }
        }
    }

    public void onSaveAll() {
        saveAll();
    }

    public boolean handleQuit() {
        if (!saveFileIfNeeded()) {
            return false;
        }
        saveBreakPoints();
        saveWorkspaceLayout();
        saveWindowState();
        display.destroyRuntime();
        return true;
    }

    private void saveWindowState() {
        JSONObject preferences = Preferences.instance().getSection("display");
        preferences.put("maximized", isMaximized());
        preferences.put("fullScreen", isFullScreen());
        preferences.put("iconified", isIconified());
        preferences.put("window.x", getX());
        preferences.put("window.y", getY());
        preferences.put("window.w", getWidth());
        preferences.put("window.h", getHeight());
        Preferences.instance().save("display");
    }

    private void saveWorkspaceLayout() {
        DockableState[] dockables = workspace.getDockables();
        for (DockableState dockableState : dockables) {
            if (dockableState.getDockable() instanceof EditorDockable && !dockableState.isDocked()) {
                workspace.unregisterDockable(dockableState.getDockable());
            }
        }
        JSONObject state = workspace.saveDockableState();
        if (state != null) {
            JSONObject workspace = Preferences.instance().getSection("workspace");
            workspace.put("state", state);
            Preferences.instance().save("workspace");
        }
    }

    public void onExit() {
        if (handleQuit()) {
            System.exit(0);
        }
    }

    public void onReleaseNotes() {
        showFile(System.getProperty(Constants.PROP_HOME) + "/readme", "index.html");
    }

    public void onChangeLog() {
        showFile(System.getProperty(Constants.PROP_HOME), "ChangeLog");
    }

    public void onVisitWebsite() {
        try {
            Desktop.getDesktop().browse(new URI("http://www.marathontesting.com/"));
        } catch (Exception e1) {
            displayView.setError(e1, "Could not launch the browser");
        }
    }

    public void onPreferences() {
        Platform.runLater(() -> {
            MarathonPreferencesInfo preferenceInfo = new MarathonPreferencesInfo(false);
            PreferencesStage preferencesStage = new PreferencesStage(preferenceInfo);
            preferencesStage.setPreferenceHandler(new PreferenceHandler());
            preferencesStage.getStage().showAndWait();
        });
    }

    public class PreferenceHandler implements IPreferenceHandler {

        @Override
        public void setPreferences(MarathonPreferencesInfo preferenceInfo) {
            JSONObject prefs = preferenceInfo.getPreferences();
            prefs.put(Constants.PREF_RECORDER_MOUSE_TRIGGER, preferenceInfo.getMouseTriggerText());
            prefs.put(Constants.PREF_RECORDER_KEYBOARD_TRIGGER, preferenceInfo.getKeyTriggerText());
            prefs.put(Constants.PREF_ITE_BLURBS, Boolean.toString(preferenceInfo.isHideBlurb()));
            preferenceInfo.save();
            System.setProperty(Constants.PROP_RECORDER_KEYTRIGGER, preferenceInfo.getKeyTriggerText());
            System.setProperty(Constants.PROP_RECORDER_MOUSETRIGGER, preferenceInfo.getMouseTriggerText());
            FXContextMenuTriggers.setContextMenuKey();
            FXContextMenuTriggers.setContextMenuModifiers();
            if (currentEditor != null) {
                currentEditor.refresh();
            }
        }
    }

    public void onResetWorkspace() {
        DockableState[] dockableStates = workspace.getDockables();
        List<EditorDockable> editorDockables = new ArrayList<DisplayWindow.EditorDockable>();
        for (DockableState dockableState : dockableStates) {
            if (dockableState.getDockable() instanceof EditorDockable) {
                editorDockables.add((EditorDockable) dockableState.getDockable());
            }
        }
        resetWorkspaceOperation = true;
        createDefaultWorkspace(editorDockables.toArray(new EditorDockable[editorDockables.size()]));
        resetWorkspaceOperation = false;
    }

    public void onToggleBreakpoint() {
        if (isProjectFile() && currentEditor != null) {
            toggleBreakPoint(currentEditor.getCaretLine());
        }
    }

    public void onClearAllBreakpoints() {
        breakpoints.clear();
        setState();
        currentEditor.refresh();
    }

    public void onStepInto() {
        stepIntoActive = true;
        resumePlay();
    }

    public void onStepOver() {
        breakStackDepth = callStack.getStackDepth();
        resumePlay();
    }

    public void onStepReturn() {
        breakStackDepth = callStack.getStackDepth() - 1;
        if (breakStackDepth < 0) {
            breakStackDepth = 0;
        }
        resumePlay();
    }

    public void onPlayerConsole() {
        scriptConsole = new ScriptConsole(scriptConsoleListener, scriptModel.getSuffix());
        scriptConsole.setVisible(true);
        setState();
    }

    public void onRecorderConsole() {
        scriptConsole = new ScriptConsole(scriptConsoleListener, scriptModel.getSuffix());
        scriptConsole.setVisible(true);
        System.setProperty(Constants.PROP_RUNTIME_DELAY, "0");
        if (!state.isRecordingPaused()) {
            display.pauseRecording();
        }
        setState();
    }

    public String getFixture() {
        return fixture;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    private boolean isBreakPointAtLine(int line) {
        BreakPoint bp = new BreakPoint(displayView.getFilePath(), line);
        return breakpoints != null && breakpoints.contains(bp);
    }

    private IEditor createEditor(EditorType editorType) {
        try {
            IEditor e = editorProvider.get(true, 1, editorType, true);
            e.setData("fileHandler", e.createResourceHandler(editorType, this));
            e.addGutterListener(gutterListener);
            e.addContentChangeListener(contentChangeListener);
            setAcceleratorKeys(e);
            setMenuItems(e);
            e.setStatusBar(statusPanel);
            Dockable editorDockable = new EditorDockable(e, editorDockGroup);
            e.setData("dockable", editorDockable);
            return e;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private void setMenuItems(IEditor e) {
    }

    private IEditor createEditor(File file) {
        return createEditor(file, null);
    }

    private IEditor createEditor(File file, EditorType type) {
        try {
            EditorType editorType = type == null ? findEditorType(file) : type;
            IEditor e = createEditor(editorType);
            e.setData("editorType", editorType);
            e.readResource(file);
            e.runWhenReady(() -> e.refresh());
            e.runWhenContentLoaded(() -> e.setCaretLine(0));
            e.setDirty(false);
            return e;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private EditorType findEditorType(File file) {
        EditorType editorType;
        String checklistDir = "";
        try {
            checklistDir = Constants.getMarathonDirectory(Constants.PROP_CHECKLIST_DIR).toPath().toAbsolutePath().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.getName().endsWith(".csv")) {
            editorType = IEditorProvider.EditorType.CSV;
        } else if (Constants.isSuiteFile(file)) {
            editorType = IEditorProvider.EditorType.SUITE;
        } else if (Constants.isFeatureFile(file)) {
            editorType = IEditorProvider.EditorType.FEATURE;
        } else if (Constants.isStoryFile(file)) {
            editorType = IEditorProvider.EditorType.STORY;
        } else if (Constants.isIssueFile(file)) {
            editorType = IEditorProvider.EditorType.ISSUE;
        } else if (file.getName().endsWith(".html")) {
            editorType = IEditorProvider.EditorType.HTML;
        } else if (file.getName().equals("omap-configuration.yaml")) {
            editorType = IEditorProvider.EditorType.OBJECTMAP_CONFIGURATION;
        } else if (file.getName().equals("omap.yaml")) {
            editorType = IEditorProvider.EditorType.OBJECTMAP;
        } else if (file.getName().endsWith(".xml") && file.toPath().startsWith(checklistDir)) {
            editorType = IEditorProvider.EditorType.CHECKLIST;
        } else {
            editorType = IEditorProvider.EditorType.OTHER;
        }
        return editorType;
    }

    public void setGenerateReports(boolean b) {
        generateReportsMenuItem.setSelected(true);
    }

    private AllureMarathonRunListener runListener;
    private Module moduleFunctions;

    /** Listener for test runner */
    public void testFinished() {
    }

    public void testStarted() {
    }

    public void updateScript(String script) {
        currentEditor.setText(script);
        if (!currentEditor.isDirty()) {
            currentEditor.setDirty(true);
            updateDockName(currentEditor);
            updateView();
        }
    }

    public void insertScript(String function) {
        String s = display.insertScript(function);
        importStatements.add(s);
    }

    public void fileUpdated(File selectedFile) {
    }

    public IEditorProvider getEditorProvider() {
        return editorProvider;
    }

    private Module getModuleFunctions() {
        if (moduleFunctions == null) {
            moduleFunctions = display.getModuleFuctions();
        }
        return moduleFunctions;
    }

    public Module refreshModuleFunctions() {
        resetModuleFunctions();
        return getModuleFunctions();
    }

    public void resetModuleFunctions() {
        moduleFunctions = null;
    }

    public boolean canAppend(File file) {
        EditorDockable dockable = findEditorDockable(file);
        if (dockable == null) {
            return true;
        }
        if (!dockable.getEditor().isDirty()) {
            return true;
        }
        Optional<ButtonType> result = FXUIUtils.showConfirmDialog(DisplayWindow.this,
                "File " + file.getName() + " being edited. Do you want to save the file?", "Save Module", AlertType.CONFIRMATION,
                ButtonType.YES, ButtonType.NO);
        ButtonType option = result.get();
        if (option == ButtonType.YES) {
            save(dockable.getEditor());
            return true;
        }
        return false;
    }

    @Override
    public boolean okToOverwrite(File file) {
        EditorDockable dockable = findEditorDockable(file);
        if (dockable == null) {
            return true;
        }
        Platform.runLater(() -> {
            FXUIUtils.showMessageDialog(DisplayWindow.this, "The selected file is being edited in another editor. Close it and try",
                    "Error", AlertType.INFORMATION);
        });
        return false;
    }

    public boolean isModuleFile() {
        return currentEditor != null && currentEditor.isModuleFile();
    }

    public void onOMapCreation() {
        display.omapCreate(taConsole);
    }

    public StatusBar getStatusPanel() {
        return statusPanel;
    }

    public static DisplayWindow instance() {
        return _instance;
    }

    public void onEditObjectMapConfiguration() {
        openFile(new File(Constants.getMarathonProjectDirectory(), "omap-configuration.yaml"));
    }

    public void onObjectMapEdit() {
        openFile(new File(Constants.getMarathonProjectDirectory(), "omap.yaml"));
    }

    public class ResourceActionHandler implements IResourceActionHandler {

        @Override
        public void openAsText(IResourceActionSource source, Resource resource) {
            Path filePath = resource.getFilePath();
            if (filePath != null) {
                openFile(filePath.toFile(), EditorType.OTHER);
            }
        }

        @Override
        public void openWithSystem(IResourceActionSource source, Resource resource) {
            Path filePath = resource.getFilePath();
            if (filePath != null) {
                try {
                    Desktop.getDesktop().open(filePath.toFile());
                } catch (IOException e) {
                    FXUIUtils._showMessageDialog(DisplayWindow.this, e.getMessage(), "Can't open file with system editor",
                            AlertType.ERROR);
                }
            }
        }

        @Override
        public void open(IResourceActionSource source, Resource resource) {
            Path filePath = resource.getFilePath();
            if (filePath != null) {
                openFile(filePath.toFile());
            }
        }

        @Override
        public void play(IResourceActionSource source, List<Resource> resources) {
            if (editingObjectMap())
                return;
            Test test = null;
            if (resources.size() == 1 && resources.get(0).canPlaySingle()) {
                open(source, resources.get(0));
                currentEditor.runWhenReady(() -> onPlay());
            } else {
                TestSuite suite;
                if (resources.size() == 1) {
                    try {
                        test = resources.get(0).getTest(enableChecklistMenuItem.isSelected(), taConsole);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    suite = new TestSuite(makeName(resources));
                    resources.forEach((r) -> {
                        try {
                            Test testx = r.getTest(enableChecklistMenuItem.isSelected(), taConsole);
                            if (testx != null) {
                                suite.addTest(testx);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    test = suite;
                }
                if (test != null) {
                    if (testRunner.getContainer() == null) {
                        workspace.createTab(navigatorPanel, testRunner, 1, true);
                    }
                    selectDockable(testRunner);
                    testRunner.run(test);
                }
            }
        }

        private String makeName(List<Resource> resources) {
            return "Multiple Tests";
        }

        @Override
        public void slowPlay(IResourceActionSource source, Resource resource) {
            open(source, resource);
            currentEditor.runWhenReady(() -> onSlowPlay());
        }

        @Override
        public void debug(IResourceActionSource source, Resource resource) {
            open(source, resource);
            currentEditor.runWhenReady(() -> onDebug());
        }

        @Override
        public void addProperties(IResourceActionSource source, Resource item) {
            AddPropertiesStage propertiesStage = new AddPropertiesStage(new TestPropertiesInfo(item.getFilePath().toFile()));
            propertiesStage.getStage().showAndWait();
        }
    }

    public class ResourceChangeListener implements IResourceChangeListener {

        @Override
        public void deleted(IResourceActionSource source, Resource resource) {
            String[] moduleDirs = Constants.getMarathonDirectoriesAsStringArray(Constants.PROP_MODULE_DIRS);
            for (String moduleDir : moduleDirs) {
                if (moduleDir.equals(resource.getFilePath().toString())) {
                    removeModDirFromProjFile();
                } else if (resource.getFilePath() != null) {
                    File f = resource.getFilePath().toFile();
                    if (f.getParentFile().getPath().contains(new File(moduleDir).getAbsolutePath())) {
                        resetModuleFunctions();
                    }
                }
            }
            if (resource.getFilePath() != null) {
                File file = resource.getFilePath().toFile();
                EditorDockable dockable = findEditorDockable(file);
                if (dockable != null) {
                    workspace.close(dockable);
                }
                File fixtureDirectory = new File(System.getProperty(Constants.PROP_FIXTURE_DIR));
                if (file.getParentFile().toPath().equals(fixtureDirectory.toPath())) {
                    String[] fixtures = fixtureDirectory.list();
                    List<String> list = Arrays.asList(fixtures).stream().map(p -> p.substring(0, p.indexOf(".")))
                            .collect(Collectors.toList());
                    if (list.size() > 0) {
                        setDefaultFixture(list.get(0));
                    }
                }
            }
            if (source != navigatorPanel) {
                navigatorPanel.deleted(source, resource);
            }
            if (source != suitesPanel) {
                suitesPanel.deleted(source, resource);
            }
            if (source != featuresPanel) {
                featuresPanel.deleted(source, resource);
            }
            if (source != storiesPanel) {
                storiesPanel.deleted(source, resource);
            }
            if (source != issuesPanel) {
                issuesPanel.deleted(source, resource);
            }
        }

        @Override
        public void updated(IResourceActionSource source, Resource resource) {
            if (resource.getFilePath() != null) {
                File file = resource.getFilePath().toFile();
                EditorDockable dockable = findEditorDockable(file);
                if (dockable != null) {
                    IEditor editor = dockable.getEditor();
                    if (editor.isDirty() && !editor.isNewFile()) {
                        Optional<ButtonType> option = FXUIUtils.showConfirmDialog(DisplayWindow.this,
                                "File `" + file + "` has been modified outside the editor. Do you want to reload it?",
                                "File being modified", AlertType.CONFIRMATION);
                        if (option.isPresent() && option.get() == ButtonType.OK) {
                            Platform.runLater(() -> editor.refreshResource());
                        }
                    } else {
                        Platform.runLater(() -> editor.refreshResource());
                    }
                }
            }
            if (source != navigatorPanel) {
                navigatorPanel.updated(source, resource);
            }
            if (source != suitesPanel) {
                suitesPanel.updated(source, resource);
            }
            if (source != featuresPanel) {
                featuresPanel.updated(source, resource);
            }
            if (source != storiesPanel) {
                storiesPanel.updated(source, resource);
            }
            if (source != issuesPanel) {
                issuesPanel.updated(source, resource);
            }
        }

        @Override
        public void moved(IResourceActionSource source, Resource from, Resource to) {
            if (from.getFilePath() != null && to.getFilePath() != null) {
                File file = from.getFilePath().toFile();
                EditorDockable dockable = findEditorDockable(file);
                if (dockable != null) {
                    IEditor editor = dockable.getEditor();
                    Platform.runLater(() -> {
                        editor.changeResource(to.getFilePath().toFile());
                        updateView(editor);
                    });
                }
            }
            if (source != navigatorPanel) {
                navigatorPanel.moved(source, from, to);
            }
            if (source != suitesPanel) {
                suitesPanel.moved(source, from, to);
            }
            if (source != featuresPanel) {
                featuresPanel.moved(source, from, to);
            }
            if (source != storiesPanel) {
                storiesPanel.moved(source, from, to);
            }
            if (source != issuesPanel) {
                issuesPanel.moved(source, from, to);
            }
        }

        @Override
        public void copied(IResourceActionSource source, Resource from, Resource to) {
            if (source != navigatorPanel) {
                navigatorPanel.copied(source, from, to);
            }
            if (source != suitesPanel) {
                suitesPanel.copied(source, from, to);
            }
            if (source != featuresPanel) {
                featuresPanel.copied(source, from, to);
            }
            if (source != storiesPanel) {
                storiesPanel.copied(source, from, to);
            }
            if (source != issuesPanel) {
                issuesPanel.copied(source, from, to);
            }
        }
    }

    private void updateView(IEditor editor) {
        String projectName = System.getProperty(Constants.PROP_PROJECT_NAME, "");
        if (projectName.equals("")) {
            projectName = "Marathon";
        }
        String suffix = "";
        if (editor != null && editor.isDirty()) {
            suffix = "*";
        }
        if (editor != null) {
            setStageTitle(projectName + " - " + editor.getDisplayName() + suffix);
            updateDockName(editor);
        } else {
            setStageTitle(projectName);
        }
        setState();
    }

    private boolean editingObjectMap() {
        if (editingFile(new File(Constants.getMarathonProjectDirectory(), "omap.yaml"))) {
            FXUIUtils.showMessageDialog(DisplayWindow.this,
                    "Object map is being edited and there are unsaved changes.\n" + "Please save the object map before proceeding.",
                    "Unsaved Changes in Object Map", AlertType.WARNING);
            return true;
        }
        return false;
    }

    private boolean editingFile(File file) {
        final EditorDockable dockable = findEditorDockable(file);
        if (dockable == null || !dockable.getEditor().isDirty())
            return false;
        return true;
    }

    public void setCurrentEditor(IEditor currentEditor) {
        this.currentEditor = currentEditor;
    }

}
