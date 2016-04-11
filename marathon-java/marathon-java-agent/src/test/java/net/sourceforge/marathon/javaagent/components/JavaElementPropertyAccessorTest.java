package net.sourceforge.marathon.javaagent.components;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.JavaAgent;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test public class JavaElementPropertyAccessorTest {

    private IJavaAgent driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                Object[] listData = new Object[30];
                for (int i = 1; i <= listData.length; i++) {
                    listData[i - 1] = "List Item - " + i;
                }
                JList list = new JList(listData);
                list.setName("list-1");
                frame.getContentPane().add(list);
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

    public void findElemenyByTagName() throws Throwable {
        driver.findElementByTagName("list");
    }

}
