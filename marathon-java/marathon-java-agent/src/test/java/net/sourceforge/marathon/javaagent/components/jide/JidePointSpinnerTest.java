package net.sourceforge.marathon.javaagent.components.jide;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.components.JavaElementTest;

@Test
public class JidePointSpinnerTest extends JavaElementTest {

    private IJavaAgent driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame("Point Spinner");
                frame.setName("dialog-1");
                PointSpinnerDemo pspinnerDemo = new PointSpinnerDemo();
                frame.getContentPane().add(pspinnerDemo);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
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
        JavaElementFactory.reset();
    }

    @Test
    public void numberSpinner() {
        driver = new JavaAgent();
        IJavaElement numberSpinner = driver.findElementByCssSelector("spinner");
        marathon_select(numberSpinner, "(20,12)");
        String attribute = numberSpinner.getAttribute("text");
        AssertJUnit.assertEquals("(20, 12)", attribute);
    }
}
