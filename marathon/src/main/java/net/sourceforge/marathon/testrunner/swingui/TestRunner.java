package net.sourceforge.marathon.testrunner.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import net.sourceforge.marathon.display.ActionInjector;
import net.sourceforge.marathon.display.FileEventHandler;
import net.sourceforge.marathon.display.ISimpleAction;
import net.sourceforge.marathon.display.ITestListener;
import net.sourceforge.marathon.display.ResultPane.IResultPaneSelectionListener;
import net.sourceforge.marathon.display.TextAreaOutput;
import net.sourceforge.marathon.junit.MarathonResultReporter;
import net.sourceforge.marathon.junit.MarathonTestCase;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.junit.textui.HTMLOutputter;
import net.sourceforge.marathon.junit.textui.MarathonJunitTestResult;
import net.sourceforge.marathon.junit.textui.TestLinkXMLOutputter;
import net.sourceforge.marathon.junit.textui.TextOutputter;
import net.sourceforge.marathon.junit.textui.XMLOutputter;
import net.sourceforge.marathon.navigator.IFileEventListener;
import net.sourceforge.marathon.runtime.api.ButtonBarFactory;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.UIUtils;
import net.sourceforge.marathon.testrunner.swingui.TestTreeNode.State;
import net.sourceforge.marathon.util.AbstractSimpleAction;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.toolbars.VLToolBar;

public class TestRunner implements Dockable, IFileEventListener, TestListener {

    private enum Mode {
        NORMAL, RUNNING, RESULTS
    }

    private static final Logger logger = Logger.getLogger(TestRunner.class.getCanonicalName());

    private static final Icon ICON_JUNIT = new ImageIcon(
            TextAreaOutput.class.getResource("/net/sourceforge/marathon/testrunner/swingui/icons/junit.gif"));

    private static final DockKey DOCK_KEY = new DockKey("JUnit", "JUnit", "Test navigator", ICON_JUNIT);
    private JPanel component;

    @ISimpleAction(mneumonic = 'n', description = "Next Failure") Action nextFailureAction;
    @ISimpleAction(mneumonic = 'p', description = "Previous Failure") Action prevFailureAction;
    @ISimpleAction(mneumonic = 'f', description = "Show Failures only") Action failuresAction;
    @ISimpleAction(mneumonic = 't', description = "Test View ") Action testViewAction;
    @ISimpleAction(mneumonic = 'r', description = "Run all tests") Action runAction;
    @ISimpleAction(mneumonic = 's', description = "Stop") Action stopAction;
    @ISimpleAction(mneumonic = 'l', description = "Run Selected Test") Action runSelected;
    @ISimpleAction(mneumonic = 'r', description = "Test Report") Action reportAction;
    @ISimpleAction(mneumonic = 's', description = "Toggle between suites and tests") Action suitesToggleAction;

    private FileEventHandler fileEventHandler;
    private boolean acceptChecklist;
    private IConsole console;

    private VLToolBar toolBar;
    private Status status;
    private ProgressBar progressBar;
    private JTree testTree;
    private FailureDetailView failureStackView;

    private DefaultTreeModel testTreeModel;

    private JToggleButton testsToggleButton;
    private JToggleButton suitesToggleButton;
    private JToggleButton failuresToggleButton;

    private Thread runnerThread;
    private TestResult testResult;

    private File resultReporterHTMLFile;
    private File runReportDir;
    private File reportDir;
    private MarathonResultReporter reporter;

    private long startTime;
    private long endTime;

    private ITestListener testOpenListener = null;

    private Mode mode = Mode.NORMAL;

    public TestRunner(IConsole console, FileEventHandler fileEventHandler) {
        this.console = console;
        this.fileEventHandler = fileEventHandler;
        new ActionInjector(TestRunner.this).injectActions();
        reportDir = new File(new File(System.getProperty(Constants.PROP_PROJECT_DIR)), "TestReports");
        if (!reportDir.exists())
            if (!reportDir.mkdir()) {
                logger.warning("Unable to create report directory: " + reportDir + " - Marathon might not function properly");
            }
        component = getPanel();
    }

    private JPanel getPanel() {
        toolBar = createToolBar();
        status = new Status();
        progressBar = new ProgressBar();
        testTreeModel = new DefaultTreeModel(new TestTreeNode(getTest()));
        testTree = new JTree(testTreeModel);
        testTree.setRootVisible(false);
        testTree.setShowsRootHandles(true);
        testTree.setRowHeight(0);
        testTree.setCellRenderer(new TestTreeNodeRenderer(this));
        testTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override public void valueChanged(TreeSelectionEvent e) {
                runSelected.setEnabled(testTree.getSelectionCount() > 0 && mode != Mode.RUNNING);
                TreePath selectionPath = testTree.getSelectionPath();
                if (selectionPath == null) {
                    failureStackView.reset();
                    return;
                }
                TestTreeNode node = (TestTreeNode) selectionPath.getLastPathComponent();
                if (node == null) {
                    failureStackView.reset();
                    return;
                }
                failureStackView.setException(node.getException());
            }
        });
        testTree.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    TreePath selectionPath = testTree.getSelectionPath();
                    if (selectionPath != null) {
                        TestTreeNode node = (TestTreeNode) selectionPath.getLastPathComponent();
                        if (node != null && node.isLeaf()) {
                            testOpenListener.openTest(node.getTest());
                        }
                    }
                }
            }
        });
        failureStackView = new FailureDetailView();

        JScrollPane testTreeScrollPane = new JScrollPane(testTree);
        testTreeScrollPane.setBorder(null);
        JScrollPane failureStackViewScrollPane = new JScrollPane(failureStackView);
        failureStackViewScrollPane.setBorder(null);
        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, testTreeScrollPane, failureStackViewScrollPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setBorder(null);

        CellConstraints cc = new CellConstraints();
        DefaultFormBuilder formBuilder = new DefaultFormBuilder(new FormLayout("fill:pref:grow, pref", ""));
        formBuilder.background(Color.WHITE);
        formBuilder.appendRow("pref");
        formBuilder.add(toolBar, cc.xyw(1, 1, 2, "right, bottom"));
        formBuilder.nextLine();
        formBuilder.append(status, 2);
        formBuilder.append(progressBar, 2);
        formBuilder.appendSeparator();
        formBuilder.appendRow("fill:p:grow");
        formBuilder.append(splitPane, 2);
        final JPanel panel = formBuilder.getPanel();
        panel.addComponentListener(new ComponentAdapter() {
            @Override public void componentShown(ComponentEvent e) {
                resizeSplitPane();
            }

            @Override public void componentResized(ComponentEvent e) {
                resizeSplitPane();
            }

            protected void resizeSplitPane() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        splitPane.setDividerLocation(0.7);
                    }
                });
                panel.removeComponentListener(this);
            }

        });
        return panel;
    }

    private VLToolBar createToolBar() {
        VLToolBar toolBar = new VLToolBar();
        toolBar.setCollapsible(false);
        toolBar.setDraggedBorder(null);
        suitesToggleButton = getActionToggleButton(suitesToggleAction);
        suitesToggleButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (suitesToggleButton.isSelected() == testsToggleButton.isSelected())
                    testsToggleButton.setSelected(!suitesToggleButton.isSelected());
            }
        });
        toolBar.add(suitesToggleButton);
        testsToggleButton = getActionToggleButton(testViewAction);
        testsToggleButton.setSelected(true);
        testsToggleButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (suitesToggleButton.isSelected() == testsToggleButton.isSelected())
                    suitesToggleButton.setSelected(!testsToggleButton.isSelected());
            }
        });
        toolBar.add(testsToggleButton);
        toolBar.addSeparator();
        toolBar.add(getActionButton(nextFailureAction));
        toolBar.add(getActionButton(prevFailureAction));
        failuresToggleButton = getActionToggleButton(failuresAction);
        toolBar.add(failuresToggleButton);
        toolBar.addSeparator();
        toolBar.add(getActionButton(runAction));
        toolBar.add(getActionButton(stopAction));
        toolBar.add(getActionButton(runSelected));
        toolBar.addSeparator();
        toolBar.add(getActionButton(reportAction));
        toolBar.setBackground(Color.white);
        return toolBar;
    }

    private JButton getActionButton(Action action) {
        if (action instanceof AbstractSimpleAction)
            return ((AbstractSimpleAction) action).getButton();
        JButton button = UIUtils.createActionButton(action);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        if (action.getValue(Action.SMALL_ICON) != null)
            button.setText(null);
        return button;
    }

    private JToggleButton getActionToggleButton(Action action) {
        JToggleButton button = new JToggleButton(action);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        if (action.getValue(Action.SMALL_ICON) != null)
            button.setText(null);
        return button;
    }

    private Test getTest() {
        TestCreator creator;
        try {
            creator = new TestCreator(acceptChecklist, console);
            Test rootTest = creator.getTest(showSuites() ? "AllSuites" : "AllTests");
            if (rootTest == null)
                throw new IOException();
            return rootTest;
        } catch (IOException e) {
            return new Test() {
                @Override public void run(TestResult arg0) {
                }

                @Override public int countTestCases() {
                    return 0;
                }

                @Override public String toString() {
                    return "Error in creating a test heirarchy";
                }
            };
        }
    }

    public void onTestView() {
        resetTestView();
    }

    public void onFailures() {
        testTreeModel.nodeStructureChanged((TreeNode) testTreeModel.getRoot());
    }

    public void onNextFailure() {
        TestTreeNode fromNode = (TestTreeNode) testTreeModel.getRoot();
        TreePath path = testTree.getSelectionPath();
        if (path != null) {
            fromNode = (TestTreeNode) path.getLastPathComponent();
        }
        TestTreeNode nextFailure = findNextFailure(fromNode);
        if (nextFailure == null)
            return;
        TreePath treePath = new TreePath(nextFailure.getPath());
        testTree.setSelectionPath(treePath);
        testTree.scrollPathToVisible(treePath);
    }

    private TestTreeNode findNextFailure(TestTreeNode fromNode) {
        return findNextNodeByIndex(fromNode.getIndex());
    }

    private TestTreeNode findNextNodeByIndex(int index) {
        TestTreeNode root = (TestTreeNode) testTreeModel.getRoot();
        return findNextNodeByIndex(root, index);
    }

    private TestTreeNode findNextNodeByIndex(TestTreeNode parent, int index) {
        if (parent.isLeaf() && parent.getIndex() > index
                && (parent.getState() == State.FAILURE || parent.getState() == State.ERROR))
            return parent;
        Enumeration<TestTreeNode> children = parent.children();
        while (children.hasMoreElements()) {
            TestTreeNode node = findNextNodeByIndex((TestTreeNode) children.nextElement(), index);
            if (node != null)
                return node;
        }
        return null;
    }

    public void onPrevFailure() {
        TestTreeNode fromNode = (TestTreeNode) testTreeModel.getRoot();
        TreePath path = testTree.getSelectionPath();
        if (path != null) {
            fromNode = (TestTreeNode) path.getLastPathComponent();
        }
        TestTreeNode prevFailure = findPrevFailure(fromNode);
        if (prevFailure == null)
            return;
        TreePath treePath = new TreePath(prevFailure.getPath());
        testTree.setSelectionPath(treePath);
        testTree.scrollPathToVisible(treePath);
    }

    private TestTreeNode findPrevFailure(TestTreeNode fromNode) {
        return findPrevNodeByIndex(fromNode.getIndex());
    }

    private TestTreeNode findPrevNodeByIndex(int index) {
        TestTreeNode root = (TestTreeNode) testTreeModel.getRoot();
        return findPrevNodeByIndex(root, index);
    }

    private TestTreeNode findPrevNodeByIndex(TestTreeNode parent, int index) {
        if (parent.getIndex() >= index)
            return null;
        TestTreeNode current = null;
        if (parent.isLeaf() && parent.getIndex() < index
                && (parent.getState() == State.FAILURE || parent.getState() == State.ERROR))
            current = parent;
        Enumeration<TestTreeNode> children = parent.children();
        while (children.hasMoreElements()) {
            TestTreeNode node = findPrevNodeByIndex((TestTreeNode) children.nextElement(), index);
            if (node != null)
                current = node;
        }
        return current;
    }

    public void onRun() {
        runSuite();
    }

    public void onStop() {
        if (runnerThread != null) {
            status.setText("Aborting.. Click again to force abort.");
            if (!testResult.shouldStop())
                testResult.stop();
            else {
                stopAction.setEnabled(false);
                runnerThread.interrupt();
            }
        }
    }

    public void onRunSelected() {
        console.clear();
        final Test testSuite = getSelectedTest();
        progressBar.reset(testSuite.countTestCases());
        if (testSuite != null) {
            testTreeModel.setRoot(new TestTreeNode(testSuite));
            doRunTest(testSuite);
        }
    }

    public Test getSelectedTest() {
        TreePath[] paths = testTree.getSelectionPaths();
        if (paths != null) {
            TestSuite suite = new TestSuite("SelectedTests");
            for (int i = 0; i < paths.length; i++) {
                TestTreeNode t = (TestTreeNode) paths[i].getLastPathComponent();
                suite.addTest(t.getTest());
            }
            return suite;
        }
        return null;
    }

    public void onReport() {
        if (resultReporterHTMLFile != null) {
            try {
                Desktop.getDesktop().open(resultReporterHTMLFile);
            } catch (Exception e) {
                displayTextReport();
            }
        }
    }

    private void displayTextReport() {
        File temp = null;
        try {
            temp = File.createTempFile("marathon", ".txt");
            temp.deleteOnExit();
            if (reporter != null)
                reporter.generateReport(new TextOutputter(), temp.getCanonicalPath());
            new ReportViewer(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ReportViewer extends JDialog {
        private static final long serialVersionUID = 1L;

        public ReportViewer(final File reportFile) {
            super((JFrame) (SwingUtilities.windowForComponent(TestRunner.this.getComponent()) instanceof JFrame
                    ? SwingUtilities.windowForComponent(TestRunner.this.getComponent()) : null));
            setTitle("Marathon Test Report");
            setModal(true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            JEditorPane editorPane = new JEditorPane("text/plain", "");
            editorPane.setEditable(false);
            try {
                editorPane.read(new FileReader(reportFile), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JScrollPane pane = new JScrollPane(editorPane);
            pane.setBorder(Borders.DIALOG);
            getContentPane().add(pane);
            JButton closeButton = UIUtils.createCloseButton();
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            JButton saveButton = UIUtils.createSaveButton();
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    int chosenOption = fileChooser.showSaveDialog(ReportViewer.this);
                    if (chosenOption == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        try {
                            copy(new FileInputStream(reportFile), new FileOutputStream(selectedFile));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
            JPanel buttonPanel = ButtonBarFactory.buildRightAlignedBar(saveButton, closeButton);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            setSize(800, 600);
            setLocationRelativeTo(SwingUtilities.windowForComponent(TestRunner.this.getComponent()));
            setVisible(true);
        }

        void copy(InputStream in, OutputStream out) throws IOException {
            try {
                byte[] buffer = new byte[4096];
                int nrOfBytes = -1;
                while ((nrOfBytes = in.read(buffer)) != -1) {
                    out.write(buffer, 0, nrOfBytes);
                }
                out.flush();
            } finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public void onSuitesToggle() {
        resetTestView();
    }

    @Override public Component getComponent() {
        return component;
    }

    @Override public DockKey getDockKey() {
        return DOCK_KEY;
    }

    public void setAcceptChecklist(boolean acceptChecklist) {
        this.acceptChecklist = acceptChecklist;
        resetTestView();
    }

    public void setTestOpenListener(ITestListener testListener) {
        testOpenListener = testListener;
    }

    public void resetTestView() {
        mode = Mode.NORMAL;
        Test rootTest = getTest();
        resetToolBar();
        status.reset();
        progressBar.reset(rootTest.countTestCases());
        testTreeModel.setRoot(new TestTreeNode(rootTest));
        failureStackView.reset();
    }

    private void resetToolBar() {
        Runnable doRun = new Runnable() {
            @Override public void run() {
                nextFailureAction.setEnabled(mode == Mode.RESULTS);
                prevFailureAction.setEnabled(mode == Mode.RESULTS);
                failuresToggleButton.setEnabled(mode == Mode.RESULTS);
                boolean selected = failuresToggleButton.isSelected();
                failuresToggleButton.setSelected(selected && mode == Mode.RESULTS);
                stopAction.setEnabled(mode == Mode.RUNNING);
                reportAction.setEnabled(mode == Mode.RESULTS && resultReporterHTMLFile != null);
                runAction.setEnabled(mode != Mode.RUNNING);
                testViewAction.setEnabled(mode != Mode.RUNNING);
                suitesToggleAction.setEnabled(mode != Mode.RUNNING);
                runSelected.setEnabled(mode != Mode.RUNNING && testTree.getSelectionCount() > 0);
            }
        };
        if (EventQueue.isDispatchThread()) {
            doRun.run();
        } else {
            SwingUtilities.invokeLater(doRun);
        }
    }

    public boolean showFailures() {
        return failuresToggleButton.isSelected();
    }

    public boolean showSuites() {
        return suitesToggleButton.isSelected();
    }

    public void fileRenamed(File from, File to) {
        if (isTestFile(from) || isTestFile(to))
            if (mode != Mode.RUNNING)
                resetTestView();
    }

    private boolean isTestFile(File file) {
        if (file.getPath().startsWith(System.getProperty(Constants.PROP_TEST_DIR)))
            return true;
        return false;
    }

    public void fileDeleted(File file) {
        if (isTestFile(file))
            if (mode != Mode.RUNNING)
                resetTestView();
    }

    public void fileCopied(File from, File to) {
        if (isTestFile(from) || isTestFile(to))
            if (mode != Mode.RUNNING)
                resetTestView();
    }

    public void fileMoved(File from, File to) {
        if (isTestFile(from) || isTestFile(to))
            if (mode != Mode.RUNNING)
                resetTestView();
    }

    public void fileCreated(File file, boolean openInEditor) {
        if (isTestFile(file))
            if (mode != Mode.RUNNING)
                resetTestView();
    }

    public void fileUpdated(File file) {
        if (isTestFile(file))
            if (mode != Mode.RUNNING)
                resetTestView();
    }

    synchronized public void runSuite() {
        console.clear();
        resetTestView();
        final Test testSuite = ((TestTreeNode) testTreeModel.getRoot()).getTest();
        if (testSuite != null) {
            doRunTest(testSuite);
        }
    }

    private void doRunTest(final Test testSuite) {
        mode = Mode.RUNNING;
        resultReporterHTMLFile = null;
        resetToolBar();
        reporter = new MarathonResultReporter(testSuite);
        startTime = System.currentTimeMillis();
        runnerThread = new Thread("TestRunner-Thread") {
            public void run() {
                try {
                    testSuite.run(testResult);
                    if (!testResult.shouldStop())
                        runFinished(testSuite);
                    else {
                        abort();
                    }
                } finally {
                    // always return control to UI
                    if (interrupted())
                        abort();
                    MarathonTestCase.reset();
                    runnerThread = null;
                    System.gc();
                }
            }

            private void abort() {
                endTime = System.currentTimeMillis();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override public void run() {
                        status.setText("Aborted after " + (endTime - startTime) / 1000 + " seconds");
                    }
                });
                mode = Mode.RESULTS;
                resetToolBar();
            }

        };
        // make sure that the test result is created before we start the
        // test runner thread so that listeners can register for it.
        testResult = createTestResult();
        testResult.addListener(TestRunner.this);
        testResult.addListener(reporter);
        runnerThread.start();
    }

    private void runFinished(Test testSuite) {
        try {
            resultReporterHTMLFile = new File(runReportDir, "results.html");
            if (reporter != null)
                reporter.generateReport(new HTMLOutputter(), resultReporterHTMLFile.getCanonicalPath());
            File resultReporterXMLFile = new File(runReportDir, "results.xml");
            if (reporter != null)
                reporter.generateReport(new XMLOutputter(), resultReporterXMLFile.getCanonicalPath());
            File resultReporterTestLinkXMLFile = new File(runReportDir, "testlink-results.xml");
            if (reporter != null)
                reporter.generateReport(new TestLinkXMLOutputter(), resultReporterTestLinkXMLFile.getCanonicalPath());
            fileEventHandler.fireNewEvent(resultReporterHTMLFile, false);
            fileEventHandler.fireNewEvent(resultReporterXMLFile, false);
            fileEventHandler.fireNewEvent(resultReporterTestLinkXMLFile, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        endTime = System.currentTimeMillis();
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                status.setText("Finished after " + (endTime - startTime) / 1000 + " seconds");
            }
        });
        mode = Mode.RESULTS;
        resetToolBar();
    }

    protected TestResult createTestResult() {
        runReportDir = new File(reportDir, createTestReportDirName());
        if (runReportDir.mkdir()) {
            try {
                System.setProperty(Constants.PROP_REPORT_DIR, runReportDir.getCanonicalPath());
                System.setProperty(Constants.PROP_IMAGE_CAPTURE_DIR, runReportDir.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.warning("Unable to create folder: " + runReportDir + " - Ignoring report option");
        }
        return new MarathonJunitTestResult();
    }

    private String createTestReportDirName() {
        return "ju-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    }

    @Override public void addError(Test test, Throwable e) {
        addFailure(test, e, State.ERROR);
    }

    @Override public void addFailure(Test test, AssertionFailedError e) {
        addFailure(test, e, State.FAILURE);
    }

    private void addFailure(Test test, Throwable e, final State state) {
        TestTreeNode testNode = findTestNode(test);
        testNode.setThrowable(e);
        testNode.setState(state);
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                progressBar.setError(true);
                if (state == State.ERROR)
                    progressBar.incrementErrors();
                else
                    progressBar.incrementFailures();
            }
        });
    }

    @Override public void endTest(Test test) {
        TestTreeNode testNode = findTestNode(test);
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                progressBar.increment();
            }
        });
        testNode.setState(State.SUCCESS);
        testTreeModel.nodeChanged(testNode);
    }

    @Override public void startTest(final Test test) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                collapseAllNodes();
                TestTreeNode testNode = findTestNode(test);
                testNode.setState(State.RUNNING);
                TreeNode[] path = testNode.getPath();
                testTree.expandPath(new TreePath(path));
                testTree.scrollPathToVisible(new TreePath(path));
                String text = "Running " + test;
                if (testNode.getParent() != null)
                    text += " (" + ((TestTreeNode) testNode.getParent()).getTest() + ")";
                status.setText(text);
            }

            private void collapseAllNodes() {
                int rowCount = testTree.getRowCount();
                for (int i = rowCount - 1; i >= 0; i--)
                    testTree.collapseRow(i);
            }
        });
    }

    private TestTreeNode findTestNode(Test test) {
        return findNode((TestTreeNode) testTreeModel.getRoot(), test);
    }

    private TestTreeNode findNode(TestTreeNode current, Test test) {
        if (current.getTest() == test)
            return current;
        if (current.isLeaf())
            return null;
        int childCount = current.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TestTreeNode node = findNode((TestTreeNode) current.getChildAt(i), test);
            if (node != null)
                return node;
        }
        return null;
    }

    public void addResultPaneListener(IResultPaneSelectionListener resultPaneSelectionListener) {
        failureStackView.setResultPaneListener(resultPaneSelectionListener);
    }

}
