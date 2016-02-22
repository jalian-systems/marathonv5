package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sourceforge.marathon.javafxrecorder.component.RAbstractButton;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.DynamicTreeDemo;

@Test public class RAbstractButtonTest extends RComponentTest {

    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RAbstractButtonTest.class.getSimpleName());
                frame.setName("frame-" + RAbstractButtonTest.class.getSimpleName());
                frame.getContentPane().add(new DynamicTreeDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        siw(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void click() throws Throwable, InvocationTargetException {
        final JButton button = (JButton) ComponentUtils.findComponent(JButton.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                RAbstractButton rButton = new RAbstractButton(button, null, null, lr);
                MouseEvent me = new MouseEvent(button, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 5, 5, 1, false,
                        MouseEvent.BUTTON1);
                rButton.mouseButton1Pressed(me);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("click", call.getFunction());
        AssertJUnit.assertEquals("", call.getState());
    }
}
