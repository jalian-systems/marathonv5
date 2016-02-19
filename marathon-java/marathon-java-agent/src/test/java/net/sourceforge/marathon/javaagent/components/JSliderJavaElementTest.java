package net.sourceforge.marathon.javaagent.components;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.components.JSliderJavaElement;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.SliderDemo;

@Test public class JSliderJavaElementTest {
    private JavaAgent driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        JavaElementFactory.add(JSlider.class, JSliderJavaElement.class);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JSliderJavaElementTest.class.getSimpleName());
                frame.setName("frame-" + JSliderJavaElementTest.class.getSimpleName());
                frame.getContentPane().add(new SliderDemo(), BorderLayout.CENTER);
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

    public void setSliderValue() {
        IJavaElement slider = driver.findElementByTagName("slider");
        marathon_select(slider, "10");
        AssertJUnit.assertEquals("10", slider.getAttribute("value"));
    }

    @Test(expectedExceptions = NumberFormatException.class) public void illegalArgumentException() {
        IJavaElement slider = driver.findElementByTagName("slider");
        marathon_select(slider, "ten");
        AssertJUnit.assertEquals("", slider.getAttribute("value"));
    }

    private void marathon_select(IJavaElement e, String state) {
        String encodedState = state.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'");
        e.findElementByCssSelector(".::call-select('" + encodedState + "')");
    }
}
