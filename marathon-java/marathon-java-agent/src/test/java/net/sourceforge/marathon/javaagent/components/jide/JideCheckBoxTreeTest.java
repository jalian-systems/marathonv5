package net.sourceforge.marathon.javaagent.components.jide;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.CheckBoxTree;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.components.JavaElementTest;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class JideCheckBoxTreeTest extends JavaElementTest {

    private JavaAgent driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("CheckBoxTreeDemo");
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
    public void getroot() throws Throwable {
        driver = new JavaAgent();
        IJavaElement tree = driver.findElementByCssSelector("tree");
        tree.click();
        IJavaElement root = tree.findElementByCssSelector(".::root");
        AssertJUnit.assertEquals("Root", root.getText());
    }

    @Test
    public void clickANode() throws Throwable {
        driver = new JavaAgent();
        IJavaElement tree = driver.findElementByCssSelector("tree");
        tree.click();
        IJavaElement root = tree.findElementByCssSelector(".::root");
        AssertJUnit.assertEquals(0 + "", tree.getAttribute("selectionCount"));
        root.click();
        AssertJUnit.assertEquals(1 + "", tree.getAttribute("selectionCount"));
    }

    @Test
    public void selectCheckBoxNode() throws Throwable {
        driver = new JavaAgent();
        IJavaElement tree = driver.findElementByCssSelector("tree");
        tree.click();
        IJavaElement root = tree.findElementByCssSelector(".::root::editor");
        AssertJUnit.assertEquals("Root", root.getText());
        root.click();
        root.marathon_select("true");
        CheckBoxTree listComponent = (CheckBoxTree) ComponentUtils.findComponent(CheckBoxTree.class, frame);
        Assert.assertTrue(listComponent.getCheckBoxTreeSelectionModel().isRowSelected(0));
    }

    @Test
    public void uncheckCheckedCheckBoxRoot() throws Throwable {
        driver = new JavaAgent();
        IJavaElement tree = driver.findElementByCssSelector("tree");
        tree.click();
        IJavaElement root = tree.findElementByCssSelector(".::root::editor");
        AssertJUnit.assertEquals("Root", root.getText());
        root.click();
        CheckBoxTree listComponent = (CheckBoxTree) ComponentUtils.findComponent(CheckBoxTree.class, frame);
        root.marathon_select("true");
        Assert.assertTrue(listComponent.getCheckBoxTreeSelectionModel().isRowSelected(0));
        root.marathon_select("false");
        Assert.assertFalse(listComponent.getCheckBoxTreeSelectionModel().isRowSelected(0));

    }
}
