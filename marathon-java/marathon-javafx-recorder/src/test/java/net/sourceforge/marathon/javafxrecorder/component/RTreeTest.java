package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.marathon.javafxrecorder.component.RTree;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.DynamicTreeDemo;

@Test public class RTreeTest extends RComponentTest {

    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RTreeTest.class.getSimpleName());
                frame.setName("frame-" + RTreeTest.class.getSimpleName());
                frame.getContentPane().add(new DynamicTreeDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void noNodesSelected() throws Throwable, InvocationTargetException {
        final JTree tree = (JTree) ComponentUtils.findComponent(JTree.class, frame);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        siw(new Runnable() {
            @Override public void run() {
                tree.expandRow(0);
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                RTree rTree = new RTree(tree, null, null, lr);
                rTree.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[]", call.getState());
    }

    public void singleNodeSelected() throws Throwable, InvocationTargetException {
        final JTree tree = (JTree) ComponentUtils.findComponent(JTree.class, frame);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        siw(new Runnable() {
            @Override public void run() {
                tree.expandRow(0);
                tree.setSelectionRow(0);
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                RTree rTree = new RTree(tree, null, null, lr);
                rTree.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[/Root Node]", call.getState());
    }

    public void multpleNodesSelected() throws Throwable, InvocationTargetException {
        final JTree tree = (JTree) ComponentUtils.findComponent(JTree.class, frame);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        siw(new Runnable() {
            @Override public void run() {
                tree.expandRow(0);
                tree.setSelectionRows(new int[] { 0, 2 });
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                RTree rTree = new RTree(tree, null, null, lr);
                rTree.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[/Root Node, /Root Node/Parent 2]", call.getState());
    }

    public void singleNodeWithSpecialChars() throws Throwable, InvocationTargetException {
        final JTree tree = (JTree) ComponentUtils.findComponent(JTree.class, frame);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        siw(new Runnable() {
            @Override public void run() {
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                root.setUserObject("Root Node [] , / Special");
                tree.expandRow(0);
                tree.setSelectionRow(0);
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                RTree rTree = new RTree(tree, null, null, lr);
                rTree.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[/Root Node [] \\, \\\\/ Special]", call.getState());
    }

    public void nodeModification() throws Throwable, InvocationTargetException {
        final JTree tree = (JTree) ComponentUtils.findComponent(JTree.class, frame);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                Rectangle rowBounds = tree.getRowBounds(0);
                RTree rTree = new RTree(tree, null, rowBounds.getLocation(), lr);
                rTree.focusGained(null);
                tree.expandRow(0);
                tree.setSelectionRow(0);
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                root.setUserObject("Root Node - modified");
                rTree.focusLost(null);
            }
        });
        List<Call> calls = lr.getCalls();
        Call call = calls.get(1);
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("[/Root Node - modified]", call.getState());
        call = calls.get(0);
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("Root Node - modified", call.getState());
        AssertJUnit.assertTrue(call.isWithCellInfo());
        AssertJUnit.assertEquals("/Root Node", call.getCellinfo());
    }

    public void assertContent() {
        JTree tree = (JTree) ComponentUtils.findComponent(JTree.class, frame);
        final RTree rTree = new RTree(tree, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        siw(new Runnable() {
            @Override public void run() {
                content[0] = rTree.getContent();
            }
        });
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Root Node\",\"Parent 1\",\"Child 1\",\"Child 2\",\"Parent 2\",\"Child 1\",\"Child 2\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }
}
