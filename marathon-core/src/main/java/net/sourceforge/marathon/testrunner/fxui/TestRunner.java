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
package net.sourceforge.marathon.testrunner.fxui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.StoppedByUserException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.display.ActionInjector;
import net.sourceforge.marathon.display.ISimpleAction;
import net.sourceforge.marathon.display.ITestListener;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.display.ResultPane.IResultPaneSelectionListener;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.fxdocking.Dockable;
import net.sourceforge.marathon.fxdocking.ToolBarPanel;
import net.sourceforge.marathon.fxdocking.VLToolBar;
import net.sourceforge.marathon.junit.MarathonDDTestSuite;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.junit.MarathonTestRunner;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.resource.IResourceActionHandler;
import net.sourceforge.marathon.resource.IResourceActionSource;
import net.sourceforge.marathon.resource.navigator.FileResource;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.testrunner.fxui.TestTreeItem.State;
import net.sourceforge.marathon.util.AbstractSimpleAction;
import net.sourceforge.marathon.util.AllureUtils;

public class TestRunner extends Dockable implements IResourceActionSource {

    private enum Mode {
        NORMAL, RUNNING, RESULTS
    }

    private class MarathonRunListener extends RunListener {
        @Override public void testStarted(Description description) throws Exception {
            Test t = (Test) TestAttributes.get("test_object");
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    collapseAllNodes();
                    TestTreeItem testNode = findTestItem(t);
                    testNode.setState(State.RUNNING);
                    isExpandNeeded(testNode);
                    testTree.scrollTo(testTree.getRow(testNode));
                    testTree.refresh();
                    String text = "Running " + t;
                    if (testNode.getParent() != null) {
                        text += " (" + ((TestTreeItem) testNode.getParent()).getTest() + ")";
                    }
                    status.setText(text);
                }

            });
        }

        @Override public void testFinished(Description description) throws Exception {
            Test t = (Test) TestAttributes.get("test_object");
            TestTreeItem testNode = findTestItem(t);
            Platform.runLater(() -> progressBar.increment());
            testNode.setState(State.SUCCESS);
            testTree.refresh();
        }

        @Override public void testAssumptionFailure(Failure failure) {
            Test t = (Test) TestAttributes.get("test_object");
            TestTreeItem testNode = findTestItem(t);
            testNode.setThrowable(new AssertionFailedError(failure.getMessage()));
            testNode.setState(State.FAILURE);
            testTree.refresh();
            Platform.runLater(() -> {
                progressBar.setError(true);
                progressBar.incrementFailures();
            });
        }

        @Override public void testFailure(Failure failure) throws Exception {
            Test t = (Test) TestAttributes.get("test_object");
            TestTreeItem testNode = findTestItem(t);
            testNode.setThrowable(failure.getException());
            testNode.setState(State.ERROR);
            testTree.refresh();
            Platform.runLater(() -> {
                progressBar.setError(true);
                progressBar.incrementErrors();
            });
        }

    }

    private static final Logger logger = Logger.getLogger(TestRunner.class.getCanonicalName());
    private static final DockKey DOCK_KEY = new DockKey("TestRunner", "Test Runner", "Marathon Test Runner",
            FXUIUtils.getIcon("testrunner"), TabPolicy.Closable, Side.LEFT);

    private Node component;
    private VBox topPane = new VBox();
    private Status status;
    private ToolBarPanel toolBar;
    private SplitMenuButton historyRunButton = new SplitMenuButton();
    private ObservableList<MenuItem> historyMenuItems = historyRunButton.getItems();
    private ProgressIndicatorBar progressBar;
    private TreeView<Test> testTree;
    private IConsole console;
    private ToggleButton failuresToggleButton;
    private FailureDetailView failureStackView;
    private Label errorMsgLabel = new Label();

    private RunListener runListener = new MarathonRunListener();
    @ISimpleAction(description = "Expand All") AbstractSimpleAction expandAllAction;
    @ISimpleAction(description = "Collapse All") AbstractSimpleAction collapseAllAction;
    @ISimpleAction(description = "Next Failure") AbstractSimpleAction nextFailureAction;
    @ISimpleAction(description = "Previous Failure") AbstractSimpleAction prevFailureAction;
    @ISimpleAction(description = "Show Failures only") AbstractSimpleAction failuresAction;
    @ISimpleAction(description = "Run History") AbstractSimpleAction runAction;
    @ISimpleAction(description = "Manage History...") AbstractSimpleAction manageHistoryAction;
    @ISimpleAction(description = "Stop") AbstractSimpleAction stopAction;
    @ISimpleAction(description = "Run Selected Test") AbstractSimpleAction runSelected;
    @ISimpleAction(description = "Test Report") AbstractSimpleAction reportAction;

    private Mode mode = Mode.NORMAL;
    private ITestListener testOpenListener = null;

    private Thread runnerThread;

    private File runReportDir;
    private File reportDir;

    private long startTime;
    private long endTime;
    public boolean acceptChecklist;
    protected MarathonTestRunner runner;
    private IResourceActionHandler resourceActionHandler;

    public TestRunner(IResourceActionHandler resourceActionHandler, IConsole console) {
        this.resourceActionHandler = resourceActionHandler;
        this.console = console;
        new ActionInjector(TestRunner.this).injectActions();
        reportDir = new File(new File(System.getProperty(Constants.PROP_PROJECT_DIR)), Constants.DIR_TESTREPORTS);
        if (!reportDir.exists()) {
            if (!reportDir.mkdir()) {
                logger.warning("Unable to create report directory: " + reportDir + "- Marathon might not function properly");
            }
        }
        component = getPane();
    }

    private Node getPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        initTopPane();
        failureStackView = new FailureDetailView();
        splitPane.getItems().addAll(topPane, failureStackView);
        return splitPane;
    }

    private Node initTopPane() {
        toolBar = createToolBar();
        status = new Status();
        progressBar = new ProgressIndicatorBar(0);
        testTree = new TreeView<Test>();
        testTree.setContextMenu(new ContextMenu(expandAllAction.getMenuItem(), collapseAllAction.getMenuItem()));
        testTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        testTree.setCellFactory(new Callback<TreeView<Test>, TreeCell<Test>>() {
            @Override public TreeCell<Test> call(TreeView<Test> param) {
                return new TestTreeItemCell(TestRunner.this);
            }
        });
        testTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            runSelected.setEnabled(testTree.getSelectionModel().getSelectedItems().size() > 0 && mode != Mode.RUNNING);
            TreeItem<Test> selectionPath = testTree.getSelectionModel().getSelectedItem();
            if (selectionPath == null) {
                failureStackView.reset();
                return;
            }
            failureStackView.setException(((TestTreeItem) selectionPath).getException());
        });
        testTree.setOnMousePressed((e) -> {
            if (e.getClickCount() > 1) {
                TreeItem<Test> selectionPath = testTree.getSelectionModel().getSelectedItem();
                if (selectionPath != null) {
                    TestTreeItem node = (TestTreeItem) selectionPath;
                    if (node != null && node.isLeaf()) {
                        testOpenListener.openTest(node.getTest());
                    }
                }
            }
        });
        errorMsgLabel.setGraphic(FXUIUtils.getIcon("error"));
        errorMsgLabel.setVisible(false);
        topPane.getChildren().addAll(toolBar, status, errorMsgLabel, progressBar, testTree);
        VBox.setVgrow(testTree, Priority.ALWAYS);
        return topPane;
    }

    private ToolBarPanel createToolBar() {
        ToolBarPanel toolBarPanel = new ToolBarPanel(net.sourceforge.marathon.fxdocking.ToolBarContainer.Orientation.RIGHT);
        VLToolBar vlToolBar = new VLToolBar();
        vlToolBar.add(nextFailureAction.getButton());
        vlToolBar.add(prevFailureAction.getButton());
        failuresToggleButton = failuresAction.getToggleButton();
        vlToolBar.add(failuresToggleButton);

        historyRunButton.setGraphic(runAction.getButton().getGraphic());
        historyRunButton.setOnAction(runAction.getButton().getOnAction());
        historyRunButton.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
            if (isNowShowing) {
                populateMenuItems();
            }
        });
        vlToolBar.add(historyRunButton);
        vlToolBar.add(stopAction.getButton());
        vlToolBar.add(runSelected.getButton());
        vlToolBar.add(reportAction.getButton());
        toolBarPanel.add(vlToolBar);
        return toolBarPanel;
    }

    private void populateMenuItems() {
        historyMenuItems.clear();
        populateSavedHistory(TestRunnerHistory.getInstance().getHistory("favourites"));
        populateUnSavedHistory(TestRunnerHistory.getInstance().getHistory("unsaved"));
        if (historyMenuItems.size() > 0) {
            historyMenuItems.add(new SeparatorMenuItem());
        }
        historyMenuItems.add(manageHistoryAction.getMenuItem());
    }

    private void populateSavedHistory(JSONArray savedHistory) {
        for (int i = 0; i < savedHistory.length(); i++) {
            JSONObject test = savedHistory.getJSONObject(i);
            MenuItem item = createMenuItem(test, true);
            item.setGraphic(FXUIUtils.getIcon("favourite"));
            historyMenuItems.add(item);
        }
    }

    private void populateUnSavedHistory(JSONArray unsavedHistory) {
        if (unsavedHistory.length() > 0 && historyMenuItems.size() > 0) {
            historyMenuItems.add(new SeparatorMenuItem());
        }
        ArrayList<MenuItem> items = new ArrayList<>();
        for (int i = 0; i < unsavedHistory.length(); i++) {
            JSONObject testJSON = unsavedHistory.getJSONObject(i);
            MenuItem item = createMenuItem(testJSON, false);
            item.setGraphic(getIcon(State.valueOf(testJSON.getString("state"))));
            items.add(0, item);
        }
        historyMenuItems.addAll(items);
    }

    private Node getIcon(State state) {
        Node icon = null;
        if (state == State.ERROR) {
            icon = FXUIUtils.getIcon("testerror");
        } else if (state == State.FAILURE) {
            icon = FXUIUtils.getIcon("testfail");
        } else if (state == State.SUCCESS) {
            icon = FXUIUtils.getIcon("tsuiteok");
        }
        return icon;
    }

    private MenuItem createMenuItem(JSONObject testJSON, boolean selectSave) {
        MenuItem item = new MenuItem(testJSON.getString("name") + " (" + testJSON.get("run-on") + ")");
        item.setOnAction((e) -> {
            errorMsgLabel.setVisible(false);
            errorMsgLabel.setText(null);
            Test test = createTest(testJSON);
            if (errorMsgLabel.getText() != null) {
                errorMsgLabel.setVisible(true);
            }
            console.clear();
            progressBar.reset(test.countTestCases());
            status.setText("Ready");
            TestTreeItem root = new TestTreeItem(test);
            testTree.setRoot(root);
            setState(root, testJSON);
            root.setExpanded(true);
            runReportDir = new File(reportDir, testJSON.getString("runReportDir"));
        });
        return item;
    }

    private void setState(TestTreeItem parent, JSONObject testJSON) {
        for (TreeItem<Test> item : parent.getChildren()) {
            TestTreeItem testItem = (TestTreeItem) item;
            if (testItem.isLeaf()) {
                testItem.setState(findState(getRelativePath(testItem.getValue()), testJSON.getJSONArray("tests")));
                testTree.refresh();
            } else {
                JSONObject testObject = findJSONObject(testJSON, ((TestSuite) testItem.getTest()).getName());
                if (testObject != null) {
                    setState(testItem, testObject);
                }
            }
        }
    }

    private JSONObject findJSONObject(JSONObject testJSON, String name) {
        JSONArray tests = testJSON.getJSONArray("tests");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject test = tests.getJSONObject(i);
            if (test.getString("name").equals(name)) {
                return test;
            }
        }
        return null;
    }

    private State findState(String path, JSONArray tests) {
        for (int i = 0; i < tests.length(); i++) {
            JSONObject test = tests.getJSONObject(i);
            if (test.has("tests")) {
                State s = findState(path, test.getJSONArray("tests"));
                if (s != null) {
                    return s;
                }
            } else {
                String oPath = test.getString("path");
                if (oPath.equals(path)) {
                    return State.valueOf(test.getString("state"));
                }
            }
        }
        return null;
    }

    private Test createTest(JSONObject his) {
        TestSuite testSuite = new TestSuite(his.getString("name"));
        JSONArray tests = his.getJSONArray("tests");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject test = tests.getJSONObject(i);
            if (test.has("tests")) {
                if (test.has("isDDT") && test.getBoolean("isDDT")) {
                    Test marathonTest = createMarathonTest(test);
                    if (marathonTest != null) {
                        testSuite.addTest(marathonTest);
                    }
                } else {
                    testSuite.addTest(createTest(test));
                }
            } else {
                Test marathonTest = createMarathonTest(test);
                if (marathonTest != null) {
                    testSuite.addTest(marathonTest);
                }
            }
        }
        return testSuite;
    }

    private Test createMarathonTest(JSONObject test) {
        try {
            TestCreator testCreator = new TestCreator(acceptChecklist, console);
            File base = new File(System.getProperty(Constants.PROP_TEST_DIR), test.getString("path"));
            if (!base.exists()) {
                errorMsgLabel.setText("Can not access some of the test files.");
                return null;
            }
            return testCreator.getTest(base, test.getString("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public DockKey getDockKey() {
        return DOCK_KEY;
    }

    @Override public Node getComponent() {
        if (component == null) {
            component = getPane();
        }
        return component;
    }

    public boolean showFailures() {
        return failuresToggleButton.isSelected();
    }

    public void addResultPaneListener(IResultPaneSelectionListener resultPaneSelectionListener) {
        failureStackView.setResultPaneListener(resultPaneSelectionListener);
    }

    private TestTreeItem findTestItem(Test test) {
        return findItem((TestTreeItem) testTree.getRoot(), test);
    }

    private TestTreeItem findItem(TestTreeItem current, Test test) {
        if (current.getTest() == test) {
            return current;
        }
        if (current.isLeaf()) {
            return null;
        }
        int childCount = current.getChildren().size();
        for (int i = 0; i < childCount; i++) {
            TestTreeItem node = findItem((TestTreeItem) current.getChildren().get(i), test);
            if (node != null) {
                return node;
            }
        }
        return null;
    }

    private void collapseAllNodes() {
        int rowCount = testTree.getExpandedItemCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            testTree.getTreeItem(i).setExpanded(false);
        }
    }

    private void isExpandNeeded(TestTreeItem testItem) {
        if (testItem == null) {
            return;
        }
        isExpandNeeded((TestTreeItem) testItem.getParent());
        testItem.setExpanded(true);
    }

    private void resetToolBar() {
        Runnable doRun = new Runnable() {
            @Override public void run() {
                nextFailureAction.setEnabled(mode == Mode.RESULTS);
                prevFailureAction.setEnabled(mode == Mode.RESULTS);
                failuresToggleButton.setDisable(!(mode == Mode.RESULTS));
                boolean selected = failuresToggleButton.isSelected();
                failuresToggleButton.setSelected(selected && mode == Mode.RESULTS);
                stopAction.setEnabled(mode == Mode.RUNNING);
                reportAction.setEnabled(mode == Mode.RESULTS);
                runAction.setEnabled(mode != Mode.RUNNING && historyMenuItems.size() > 0);
                runSelected.setEnabled(mode != Mode.RUNNING && testTree.getSelectionModel().getSelectedItems().size() > 0);
            }
        };
        if (Platform.isFxApplicationThread()) {
            doRun.run();
        } else {
            Platform.runLater(doRun);
        }
    }

    public void setTestOpenListener(ITestListener testListener) {
        this.testOpenListener = testListener;
    }

    public void onExpandAll() {
        expandTreeView(testTree.getRoot());
    }

    public void onCollapseAll() {
        collapseAllNodes();
    }

    public void onFailures() {
        expandTreeView(testTree.getRoot());
        testTree.refresh();
    }

    private void expandTreeView(TreeItem<Test> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<Test> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    public void onNextFailure() {
        if (testTree.getRoot() == null) {
            return;
        }
        TreeItem<Test> item = testTree.getSelectionModel().getSelectedItem();
        TestTreeItem nextFailure = null;
        if (item != null) {
            if (item.isLeaf()) {
                nextFailure = findNextFailureInSiblings((TestTreeItem) item);
            } else {
                nextFailure = findNextFailureInChildren((TestTreeItem) item, true);
            }
        } else {
            nextFailure = findNextFailureInChildren((TestTreeItem) testTree.getRoot(), false);
        }
        if (nextFailure == null) {
            return;
        }
        isExpandNeeded(nextFailure);
        int failureIndex = testTree.getRow(nextFailure);
        testTree.getSelectionModel().clearAndSelect(failureIndex);
        testTree.scrollTo(failureIndex);
    }

    private TestTreeItem findNextFailureInSiblings(TestTreeItem item) {
        TestTreeItem nxtSibling = (TestTreeItem) item.nextSibling();
        TestTreeItem found = null;
        while (nxtSibling != null) {
            if (isFailure(nxtSibling)) {
                found = nxtSibling;
                break;
            } else if (!nxtSibling.isLeaf()) {
                found = findNextFailureInChildren(nxtSibling, true);
                if (found != null) {
                    break;
                }
            }
            nxtSibling = (TestTreeItem) nxtSibling.nextSibling();
        }
        if (found == null && item.getParent() != null) {
            found = findNextFailureInSiblings((TestTreeItem) item.getParent());
        }
        return found;
    }

    private boolean isFailure(TestTreeItem nxtSibling) {
        if (nxtSibling == null) {
            return false;
        }
        return nxtSibling.isLeaf() && (nxtSibling.getState() == State.FAILURE || nxtSibling.getState() == State.ERROR);
    }

    private TestTreeItem findNextFailureInChildren(TestTreeItem parent, boolean findInSibling) {
        TestTreeItem found = null;
        for (TreeItem<Test> child : parent.getChildren()) {
            if (child.isLeaf()
                    && (((TestTreeItem) child).getState() == State.FAILURE || ((TestTreeItem) child).getState() == State.ERROR)) {
                found = (TestTreeItem) child;
                break;
            } else {
                found = findNextFailureInChildren((TestTreeItem) child, findInSibling);
                if (found != null) {
                    break;
                }
            }
        }
        if (found == null && findInSibling) {
            TestTreeItem sib = (TestTreeItem) parent.nextSibling();
            if (isFailure(sib)) {
                found = sib;
            } else {
                if (sib != null) {
                    found = findNextFailureInChildren(sib, true);
                }
            }
        }
        return found;
    }

    public void onPrevFailure() {
        TreeItem<Test> item = testTree.getSelectionModel().getSelectedItem();
        TestTreeItem prevFailure = null;
        if (item != null) {
            prevFailure = findPrevFailureInSiblings((TestTreeItem) item);
        }
        if (prevFailure == null) {
            return;
        }
        isExpandNeeded(prevFailure);
        int failureIndex = testTree.getRow(prevFailure);
        testTree.getSelectionModel().clearAndSelect(failureIndex);
        testTree.scrollTo(failureIndex);
    }

    private TestTreeItem findPrevFailureInSiblings(TestTreeItem item) {
        TestTreeItem prevSibling = (TestTreeItem) item.previousSibling();
        TestTreeItem found = null;
        while (prevSibling != null) {
            if (isFailure(prevSibling)) {
                found = prevSibling;
                break;
            } else if (!prevSibling.isLeaf()) {
                found = findPrevFailureInChildren(prevSibling);
                if (found != null) {
                    break;
                }
            }
            prevSibling = (TestTreeItem) prevSibling.previousSibling();
        }
        if (found == null && item.getParent() != null) {
            found = findPrevFailureInSiblings((TestTreeItem) item.getParent());
        }
        return found;
    }

    private TestTreeItem findPrevFailureInChildren(TestTreeItem parent) {
        TestTreeItem found = null;
        int childCount = parent.getChildren().size();
        for (int i = childCount - 1; i >= 0; i--) {
            TestTreeItem child = (TestTreeItem) parent.getChildren().get(i);
            if (child.isLeaf() && (child.getState() == State.FAILURE || child.getState() == State.ERROR)) {
                found = child;
                break;
            } else if (!child.isLeaf()) {
                found = findPrevFailureInChildren(child);
                if (found != null) {
                    break;
                }
            }
        }
        if (found == null) {
            TestTreeItem sib = (TestTreeItem) parent.previousSibling();
            if (isFailure(sib)) {
                found = sib;
            } else {
                found = findPrevFailureInChildren(sib);
            }
        }
        return found;
    }

    public void onRun() {
        errorMsgLabel.setVisible(false);
        console.clear();
        TreeItem<Test> root = testTree.getRoot();
        if (root != null) {
            Test testSuite = root.getValue();
            progressBar.reset(testSuite.countTestCases());
            doRunTest(testSuite);
        }
    }

    public void onManageHistory() {
        UnSavedHistoryStage unsavedHistoryStage = new UnSavedHistoryStage(new RunHistoryInfo("unsaved"));
        unsavedHistoryStage.getStage().showAndWait();
    }

    public void onStop() {
        if (runnerThread != null) {
            status.setText("Aborting...");
            if (runner != null) {
                runner.getNotifier().pleaseStop();
            }
            stopAction.setEnabled(false);
            runnerThread.interrupt();
        }
    }

    public void onRunSelected() {
        console.clear();
        final Test testSuite = getSelectedTest();
        if (testSuite != null) {
            progressBar.reset(testSuite.countTestCases());
            TestTreeItem value = new TestTreeItem(testSuite);
            testTree.setRoot(value);
            doRunTest(testSuite);
        }
    }

    private void doRunTest(Test testSuite) {
        mode = Mode.RUNNING;
        resetToolBar();
        startTime = System.currentTimeMillis();
        runnerThread = new Thread("TestRunner-Thread") {
            @Override public void run() {
                try {
                    runner = new MarathonTestRunner();
                    runner.addListener(runListener);
                    runner.addListener(new AllureMarathonRunListener());
                    @SuppressWarnings("unused")
                    Result result = runner.run(testSuite);
                    runFinished(testSuite);
                } catch (StoppedByUserException e) {
                    abort();
                } finally {
                    // always return control to UI
                    if (interrupted()) {
                        abort();
                    }
                    MarathonTestCase.reset();
                    runnerThread = null;
                    System.gc();
                }
            }

            private void abort() {
                endTime = System.currentTimeMillis();
                Platform.runLater(() -> status.setText("Aborted after " + (endTime - startTime) / 1000 + " seconds"));
                mode = Mode.RESULTS;
                resetToolBar();
            }

        };
        createTestReportDir();
        runnerThread.start();
    }

    private void createTestReportDir() {
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
            logger.warning("Unable to create folder: " + runReportDir + " - Ignoring report option");
        }
    }

    private String createTestReportDirName() {
        return "ju-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    }

    private void runFinished(Test testSuite) {
        endTime = System.currentTimeMillis();
        collapseAllNodes();
        Platform.runLater(() -> status.setText("Finished after " + (endTime - startTime) / 1000 + " seconds"));
        save();
        mode = Mode.RESULTS;
        resetToolBar();
        testTree.refresh();
    }

    private void save() {
        TreeItem<Test> root = testTree.getRoot();
        JSONObject history = createParentJSONObject(root, null);
        writeToParent(root, history);
        TestRunnerHistory testHistory = TestRunnerHistory.getInstance();
        testHistory.getHistory("unsaved").put(history);
        testHistory.save();
    }

    private JSONObject createParentJSONObject(TreeItem<Test> root, JSONObject parentJSON) {
        TestTreeItem r = (TestTreeItem) root;
        Test value = r.getValue();
        if (value instanceof TestSuite) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", ((TestSuite) value).getName());
            jsonObject.put("tests", new JSONArray());
            jsonObject.put("state", r.getState().name());
            if (value instanceof MarathonDDTestSuite) {
                jsonObject.put("isDDT", true);
                jsonObject.put("path", getRelativePath(value));
            }
            if (parentJSON != null) {
                JSONArray tests;
                if (parentJSON.has("tests")) {
                    tests = (JSONArray) parentJSON.get("tests");
                } else {
                    tests = new JSONArray();
                    parentJSON.put("tests", tests);
                }
                tests.put(jsonObject);
            } else {
                jsonObject.put("run-duration", (endTime - startTime) / 1000 + " seconds");
                jsonObject.put("run-on", new Date(startTime));
                jsonObject.put("runReportDir", reportDir.toPath().relativize(runReportDir.toPath()).toString());
            }
            return jsonObject;
        }
        return null;
    }

    private void writeToParent(TreeItem<Test> parent, JSONObject parentJSON) {
        ObservableList<TreeItem<Test>> children = parent.getChildren();
        for (TreeItem<Test> child : children) {
            if (child.isLeaf()) {
                writeLeafToParent(child, parentJSON);
            } else {
                writeToParent(child, createParentJSONObject(child, parentJSON));
            }
        }
    }

    private void writeLeafToParent(TreeItem<Test> child, JSONObject parentJSON) {
        Test value = child.getValue();
        if (value instanceof MarathonTestCase) {
            String relative = getRelativePath(value);
            JSONObject json = new JSONObject();
            json.put("name", ((MarathonTestCase) value).getName());
            json.put("path", relative);
            json.put("state", ((TestTreeItem) child).getState().name());
            JSONArray tests;
            if (parentJSON.has("tests")) {
                tests = (JSONArray) parentJSON.get("tests");
            } else {
                tests = new JSONArray();
                parentJSON.put("tests", tests);
            }
            tests.put(json);
        }
    }

    private String getRelativePath(Test value) {
        File base = new File(System.getProperty(Constants.PROP_TEST_DIR));
        File file;
        if (value instanceof MarathonDDTestSuite) {
            file = ((MarathonDDTestSuite) value).getFile();
        } else {
            file = ((MarathonTestCase) value).getFile();
        }
        String relative = base.toURI().relativize(file.toURI()).getPath();
        return relative.replace(File.separatorChar, '/');
    }

    private Test getSelectedTest() {
        ObservableList<TreeItem<Test>> items = testTree.getSelectionModel().getSelectedItems();
        if (items != null && items.size() > 0) {
            TestSuite suite = new TestSuite("SelectedTests");
            for (int i = 0; i < items.size(); i++) {
                TestTreeItem t = (TestTreeItem) items.get(i);
                suite.addTest(t.getTest());
            }
            return suite;
        }
        return null;
    }

    public void onReport() {
        File reportsDir = new File(runReportDir, "reports");
        File resultReporterHTMLFile = new File(reportsDir, "index.html");
        if (!resultReporterHTMLFile.exists()) {
            status.setText("Generating reports...");
            String resultsDir = new File(runReportDir, "results").getAbsolutePath();
            System.setProperty("allure.results.directory", resultsDir);
            new Thread(() -> {
                AllureUtils.launchAllure(new String[] { resultsDir, reportsDir.getAbsolutePath() });
                Platform.runLater(() -> {
                    status.setText("Ready");
                    resourceActionHandler.open(this, new FileResource(resultReporterHTMLFile));
                });
            }).start();
        } else {
            resourceActionHandler.open(this, new FileResource(resultReporterHTMLFile));
        }
    }

    public void run(Test test) {
        errorMsgLabel.setVisible(false);
        console.clear();
        progressBar.reset(test.countTestCases());
        TestTreeItem value = new TestTreeItem(test);
        testTree.setRoot(value);
        value.setExpanded(true);
        doRunTest(test);
    }

    public void setAcceptChecklist(boolean acceptChecklist) {
        this.acceptChecklist = acceptChecklist;
    }
}
