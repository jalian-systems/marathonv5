package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

import net.sourceforge.marathon.javafxrecorder.component.RSplitPane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.SplitPaneDemo;

@Test public class RSplitPaneTest extends RComponentTest {
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RSplitPaneTest.class.getSimpleName());
                frame.setName("frame-" + RSplitPaneTest.class.getSimpleName());
                frame.getContentPane().add(new SplitPaneDemo().getSplitPane(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void selectSplitPane() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JSplitPane splitPane = (JSplitPane) ComponentUtils.findComponent(JSplitPane.class, frame);
                splitPane.setDividerLocation(150);
                RSplitPane rSplitPane = new RSplitPane(splitPane, null, null, lr);
                rSplitPane.mouseReleased(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("150", call.getState());
    }

    public void selectSplitPaneDividerLocation() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JSplitPane splitPane = (JSplitPane) ComponentUtils.findComponent(JSplitPane.class, frame);
                splitPane.setDividerLocation(300);
                BasicSplitPaneDivider divider = (BasicSplitPaneDivider) ComponentUtils.findComponent(BasicSplitPaneDivider.class,
                        splitPane);
                RSplitPane rDivider = new RSplitPane(divider, null, null, lr);
                rDivider.mouseReleased(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("300", call.getState());
    }
}
