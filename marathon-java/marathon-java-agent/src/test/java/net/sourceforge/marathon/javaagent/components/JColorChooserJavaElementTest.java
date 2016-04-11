package net.sourceforge.marathon.javaagent.components;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test public class JColorChooserJavaElementTest extends JavaElementTest {
    protected JFrame frame;
    private IJavaAgent driver;

    @BeforeMethod public void showDialog() throws Throwable {
        JavaElementFactory.add(JColorChooser.class, JColorChooserJavaElement.class);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                JColorChooser colorChooser = new JColorChooser();
                frame.getContentPane().add(colorChooser);
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

    public void colorChooserWithValidColorCode() {
        IJavaElement colorChooser = driver.findElementByTagName("color-chooser");
        AssertJUnit.assertEquals("[r=255,g=255,b=255]", colorChooser.getAttribute("color"));
        marathon_select(colorChooser, "#800080");
        AssertJUnit.assertEquals("[r=128,g=0,b=128]", colorChooser.getAttribute("color"));
    }

    @Test(expectedExceptions = NumberFormatException.class) public void colorChooserWithInvalidColorCode() {
        IJavaElement colorChooser = driver.findElementByTagName("color-chooser");
        marathon_select(colorChooser, "#87436278");
    }
}
