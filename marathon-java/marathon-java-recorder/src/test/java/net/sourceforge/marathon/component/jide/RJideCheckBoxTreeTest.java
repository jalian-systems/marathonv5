package net.sourceforge.marathon.component.jide;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.CheckBoxTree;

import net.sourceforge.marathon.component.LoggingRecorder;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RComponentTest;
import net.sourceforge.marathon.component.RTree;
import net.sourceforge.marathon.javaagent.components.jide.CheckBoxTreeDemo;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class RJideCheckBoxTreeTest extends RComponentTest {
    protected JFrame frame;
    private CheckBoxTree tree;

    @BeforeMethod
    public void showDialog() throws Throwable {
        System.setProperty("marathon.mode", "recording");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("CheckBoxTreeDemo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                CheckBoxTreeDemo panel = new CheckBoxTreeDemo();
                frame.getContentPane().add(panel);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);

            }
        });
    }

    @AfterMethod
    public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    @Test
    public void selectNoSelection() {
        tree = (CheckBoxTree) ComponentUtils.findComponent(CheckBoxTree.class, frame);
        siw(new Runnable() {
            @Override
            public void run() {
                tree.expandRow(0);
            }
        });
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                RTree rTree = new RTree(tree, null, null, lr);
                rTree.focusLost(null);
            }
        });
        AssertJUnit.assertEquals(0, lr.getCalls().size());
    }

    @Test
    public void selectUnCheckedCheckBox() throws Throwable {
        tree = (CheckBoxTree) ComponentUtils.findComponent(CheckBoxTree.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tree.expandRow(0);
                TreePath pathForRow = tree.getPathForRow(1);
                Rectangle cb = tree.getPathBounds(pathForRow);
                RTree rTree = new RTree(tree, null, new Point(cb.x + 2, cb.y + 2), lr);
                tree.getCheckBoxTreeSelectionModel().setSelectionPath(pathForRow);
                rTree.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("/Root/Child 1", call.getCellinfo());
        AssertJUnit.assertEquals("true", call.getState());
    }

    @Test
    public void selectCheckedCheckBoxUnchecked() {
        tree = (CheckBoxTree) ComponentUtils.findComponent(CheckBoxTree.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                tree.expandRow(0);
                TreePath row1 = tree.getPathForRow(1);
                Rectangle cb = tree.getPathBounds(row1);
                tree.getCheckBoxTreeSelectionModel().setSelectionPath(row1);
                RTree rTree = new RTree(tree, null, new Point(cb.x + 2, cb.y + 2), lr);
                tree.getCheckBoxTreeSelectionModel().removeSelectionPath(row1);
                rTree.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("/Root/Child 1", call.getCellinfo());
        AssertJUnit.assertEquals("false", call.getState());
    }
}
