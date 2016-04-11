package net.sourceforge.marathon.javaagent.components;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.DynamicTreeDemo;

@Test public class JTreeJavaElementTest extends JavaElementTest {
    protected JFrame frame;
    private IJavaAgent driver;

    @BeforeMethod public void showDialog() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JTreeJavaElementTest.class.getSimpleName());
                frame.setName("frame-" + JTreeJavaElementTest.class.getSimpleName());
                frame.getContentPane().add(new DynamicTreeDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void assertContent() {
        IJavaElement tree = driver.findElementByTagName("tree");
        String expected = "[[\"Root Node\",\"Parent 1\",\"Child 1\",\"Child 2\",\"Parent 2\",\"Child 1\",\"Child 2\"]]";
        AssertJUnit.assertEquals(expected, tree.getAttribute("content"));
    }
}
