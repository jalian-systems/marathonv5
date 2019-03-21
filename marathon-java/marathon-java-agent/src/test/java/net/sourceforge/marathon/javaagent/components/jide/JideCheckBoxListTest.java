package net.sourceforge.marathon.javaagent.components.jide;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.CheckBoxList;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.components.JavaElementTest;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class JideCheckBoxListTest extends JavaElementTest {

    private JavaAgent driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("CheckBoxListDemo");
                CheckBoxListDemo panel = new CheckBoxListDemo();
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
    public void getSelectedListItem() throws Throwable {
        driver = new JavaAgent();
        IJavaElement listItem;
        IJavaElement list = driver.findElementByCssSelector("list");
        listItem = driver.findElementByCssSelector("list::nth-item(2)");
        AssertJUnit.assertEquals("China", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("1", list.getAttribute("selectedIndex"));
        listItem = driver.findElementByCssSelector("list::nth-item(3)");
        AssertJUnit.assertEquals("USA", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("2", list.getAttribute("selectedIndex"));
    }

    @Test
    public void selectCheckboxSelectedNotSelected() throws Throwable {
        driver = new JavaAgent();
        IJavaElement listItem = driver.findElementByCssSelector("list::nth-item(2)::editor");
        listItem.marathon_select("true");
        CheckBoxList listComponent = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
        Assert.assertTrue(listComponent.getCheckBoxListSelectionModel().isSelectedIndex(1));
    }

    @Test
    public void selectCheckboxUnselectedSelected() throws Throwable {
        driver = new JavaAgent();
        IJavaElement listItem = driver.findElementByCssSelector("list::nth-item(3)::editor");
        listItem.marathon_select("false");
        CheckBoxList listComponent = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
        Assert.assertFalse(listComponent.getCheckBoxListSelectionModel().isSelectedIndex(2));
    }

    @Test
    public void selectCheckboxNotSelectedByProperties() throws Throwable {
        driver = new JavaAgent();
        IJavaElement listItem = driver.findElementByCssSelector("list::select-by-properties('{\\\"select\\\":\\\"India\\\"}')");
        IJavaElement cb1 = listItem.findElementByCssSelector(".::editor");
        cb1.marathon_select("true");
        CheckBoxList listComponent = (CheckBoxList) ComponentUtils.findComponent(CheckBoxList.class, frame);
        Assert.assertTrue(listComponent.getCheckBoxListSelectionModel().isSelectedIndex(0));
    }

}
