package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.TristateCheckBox;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.Wait;

public class JideTriStateCheckBoxTest {

    private JavaAgent driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {

                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("My First GUI");
                frame.setSize(300, 300);
                TristateCheckBox triCb1 = new TristateCheckBox("Jide TriStateCheckBox 1");
                TristateCheckBox triCb2 = new TristateCheckBox("Jide TriStateCheckBox 2");
                TristateCheckBox triCb3 = new TristateCheckBox("Jide TriStateCheckBox 3");
                JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
                jPanel.add(triCb1);
                jPanel.add(triCb2);
                jPanel.add(triCb3);
                frame.getContentPane().add(jPanel);
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
    public void getText() throws Throwable {
        driver = new JavaAgent();
        List<IJavaElement> tcbs = driver.findElementsByTagName("tristate-check-box");
        AssertJUnit.assertEquals(3, tcbs.size());
        AssertJUnit.assertEquals("Jide TriStateCheckBox 1", tcbs.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Jide TriStateCheckBox 2", tcbs.get(1).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Jide TriStateCheckBox 3", tcbs.get(2).getAttribute("buttonText"));
    }

    @Test
    void getAttributes() throws Throwable {
        driver = new JavaAgent();
        List<IJavaElement> tcbs = driver.findElementsByTagName("tristate-check-box");
        AssertJUnit.assertEquals(3, tcbs.size());
        AssertJUnit.assertEquals("Jide TriStateCheckBox 1", tcbs.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", tcbs.get(0).getAttribute("selected"));
        tcbs.get(0).click();
        AssertJUnit.assertEquals("true", tcbs.get(0).getAttribute("selected"));
        AssertJUnit.assertEquals("false", tcbs.get(1).getAttribute("selected"));
        AssertJUnit.assertEquals("Jide TriStateCheckBox 3", tcbs.get(2).getAttribute("actionCommand"));

    }

    @Test
    public void click() throws Throwable {
        driver = new JavaAgent();
        List<IJavaElement> tcbs = driver.findElementsByTagName("tristate-check-box");
        AssertJUnit.assertEquals(3, tcbs.size());
        AssertJUnit.assertEquals("Jide TriStateCheckBox 1", tcbs.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", tcbs.get(0).getAttribute("selected"));
        AssertJUnit.assertEquals("Jide TriStateCheckBox 2", tcbs.get(1).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", tcbs.get(1).getAttribute("selected"));
        AssertJUnit.assertEquals("Jide TriStateCheckBox 3", tcbs.get(2).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", tcbs.get(2).getAttribute("selected"));
        for (IJavaElement checkBox : tcbs) {
            checkBox.click();
            AssertJUnit.assertEquals("true", checkBox.getAttribute("selected"));
        }
        AssertJUnit.assertEquals("true", tcbs.get(0).getAttribute("selected"));
    }

    @Test
    public void selectCheckboxSelectedNotSelected() throws Throwable {
        driver = new JavaAgent();
        List<IJavaElement> tcbs = driver.findElementsByTagName("tristate-check-box");
        final IJavaElement tcb = tcbs.get(0);
        tcb.marathon_select("checked");
        tcb.marathon_select("unchecked");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return !tcb.isSelected();
            }
        };
    }

    @Test
    public void selectCheckboxNotSelectedNotSelected() throws Throwable {
        driver = new JavaAgent();
        List<IJavaElement> tcbs = driver.findElementsByTagName("tristate-check-box");
        final IJavaElement tcb = tcbs.get(0);
        tcb.marathon_select("unchecked");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return !tcb.isSelected();
            }
        };
    }

    @Test
    public void undefinedCheckboxNotSelectedNotSelected() throws Throwable {
        driver = new JavaAgent();
        List<IJavaElement> tcbs = driver.findElementsByTagName("tristate-check-box");
        final IJavaElement tcb = tcbs.get(0);
        tcb.marathon_select("indeterminate");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return tcb.getAttribute("mixed").equals("true");
            }
        };
    }

    @Test
    public void checkedCheckboxNotSelectedNotSelected() throws Throwable {
        driver = new JavaAgent();
        List<IJavaElement> tcbs = driver.findElementsByTagName("tristate-check-box");
        final IJavaElement tcb = tcbs.get(0);
        AssertJUnit.assertEquals(false, tcb.isSelected());
        tcb.marathon_select("checked");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return !tcb.getAttribute("mixed").equals("true") && tcb.isSelected();
            }
        };
    }

}
