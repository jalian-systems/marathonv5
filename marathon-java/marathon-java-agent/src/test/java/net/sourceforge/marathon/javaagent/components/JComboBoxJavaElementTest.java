package net.sourceforge.marathon.javaagent.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.swingset3.demos.combobox.ComboBoxDemo;

@Test public class JComboBoxJavaElementTest extends JavaElementTest {

    protected JFrame frame;
    private JavaAgent driver;

    @BeforeMethod public void showDialog() throws Throwable {
        JavaElementFactory.add(JComboBox.class, JComboBoxJavaElement.class);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JComboBoxJavaElementTest.class.getSimpleName());
                frame.setName("frame-" + JComboBoxJavaElementTest.class.getSimpleName());
                frame.getContentPane().add(new ComboBoxDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void defaultSelection() {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(0);
        marathon_select(presets, "Philip, Howard, Jeff");
        String attribute = presets.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Philip, Howard, Jeff", attribute);
    }

    public void selectOption() throws InterruptedException {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(0);
        marathon_select(presets, "Howard, Scott, Hans");
        String attribute;
        attribute = presets.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Howard, Scott, Hans", attribute);

        marathon_select(presets, "James, Philip, Michael");
        attribute = presets.getAttribute("selectedItem");
        AssertJUnit.assertEquals("James, Philip, Michael", attribute);

        marathon_select(presets, "Brent, Jon, Scott");
        attribute = presets.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Brent, Jon, Scott", attribute);

    }

    public void selectOption2() throws InterruptedException {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(1);
        marathon_select(presets, "Jon");
        String attribute;
        attribute = presets.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Jon", attribute);

        marathon_select(presets, "Michael");
        attribute = presets.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Michael", attribute);

        marathon_select(presets, "Scott");
        attribute = presets.getAttribute("selectedItem");
        AssertJUnit.assertEquals("Scott", attribute);

    }

    public void selectDuplicateOption() throws InterruptedException {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(0);
        marathon_select(presets, "Howard, Scott, Hans");
        IJavaElement nthOption;
        nthOption = presets.findElementByCssSelector(".::nth-option(3)");
        AssertJUnit.assertEquals("Howard, Scott, Hans", nthOption.getText());

        siw(new Runnable() {
            @Override public void run() {
                List<Component> combos = ComponentUtils.findComponents(JComboBox.class, frame);
                JComboBox presets = (JComboBox) combos.get(0);
                presets.insertItemAt("Howard, Scott, Hans", 4);
                presets.insertItemAt("Howard, Scott, Hans", 4);
            }
        });
        marathon_select(presets, "Howard, Scott, Hans(1)");
        nthOption = presets.findElementByCssSelector(".::nth-option(5)");
        AssertJUnit.assertEquals("Howard, Scott, Hans(1)", nthOption.getText());
    }

    public void selectMultipleDuplicateOptions() throws InterruptedException {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(0);
        marathon_select(presets, "Howard, Scott, Hans");
        IJavaElement nthOption;
        nthOption = presets.findElementByCssSelector(".::nth-option(3)");
        AssertJUnit.assertEquals("Howard, Scott, Hans", nthOption.getText());

        siw(new Runnable() {
            @Override public void run() {
                List<Component> combos = ComponentUtils.findComponents(JComboBox.class, frame);
                JComboBox presets = (JComboBox) combos.get(0);
                presets.insertItemAt("Howard, Scott, Hans", 4);
                presets.insertItemAt("Howard, Scott, Hans", 4);
            }
        });
        marathon_select(presets, "Howard, Scott, Hans(1)");
        nthOption = presets.findElementByCssSelector(".::nth-option(5)");
        AssertJUnit.assertEquals("Howard, Scott, Hans(1)", nthOption.getText());
        nthOption = presets.findElementByCssSelector(".::nth-option(6)");
        AssertJUnit.assertEquals("Howard, Scott, Hans(2)", nthOption.getText());
    }

    public void assertContent() {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(0);
        String expected = "[[\"Philip, Howard, Jeff\",\"Jeff, Larry, Philip\",\"Howard, Scott, Hans\",\"Philip, Jeff, Hans\",\"Brent, Jon, Scott\",\"Lara, Larry, Lisa\",\"James, Philip, Michael\",\"Philip, Lisa, Brent\",\"James, Philip, Jon\",\"Lara, Jon, Scott\"]]";
        AssertJUnit.assertEquals(expected, presets.getAttribute("content"));
    }

    public void assertContentWithDuplicates() {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(0);
        siw(new Runnable() {
            @Override public void run() {
                List<Component> combos = ComponentUtils.findComponents(JComboBox.class, frame);
                JComboBox presets = (JComboBox) combos.get(0);
                presets.insertItemAt("Howard, Scott, Hans", 4);
            }
        });
        String expected = "[[\"Philip, Howard, Jeff\",\"Jeff, Larry, Philip\",\"Howard, Scott, Hans\",\"Philip, Jeff, Hans\",\"Howard, Scott, Hans(1)\",\"Brent, Jon, Scott\",\"Lara, Larry, Lisa\",\"James, Philip, Michael\",\"Philip, Lisa, Brent\",\"James, Philip, Jon\",\"Lara, Jon, Scott\"]]";
        AssertJUnit.assertEquals(expected, presets.getAttribute("content"));
    }

    public void assertContentWithMultipleDuplicates() {
        List<IJavaElement> combos = driver.findElementsByTagName("combo-box");
        IJavaElement presets = combos.get(0);
        siw(new Runnable() {
            @Override public void run() {
                List<Component> combos = ComponentUtils.findComponents(JComboBox.class, frame);
                JComboBox presets = (JComboBox) combos.get(0);
                presets.insertItemAt("Howard, Scott, Hans", 4);
                presets.insertItemAt("Howard, Scott, Hans", 4);
            }
        });
        String expected = "[[\"Philip, Howard, Jeff\",\"Jeff, Larry, Philip\",\"Howard, Scott, Hans\",\"Philip, Jeff, Hans\",\"Howard, Scott, Hans(1)\",\"Howard, Scott, Hans(2)\",\"Brent, Jon, Scott\",\"Lara, Larry, Lisa\",\"James, Philip, Michael\",\"Philip, Lisa, Brent\",\"James, Philip, Jon\",\"Lara, Jon, Scott\"]]";
        AssertJUnit.assertEquals(expected, presets.getAttribute("content"));
    }
}
