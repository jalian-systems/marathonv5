package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javafxrecorder.component.RColorChooser;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test public class RColorChooserTest extends RComponentTest {
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                JColorChooser colorChooser = new JColorChooser();
                frame.getContentPane().add(colorChooser);
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

    public void colorChooserWithValidRgbValues() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JColorChooser chooser = (JColorChooser) ComponentUtils.findComponent(JColorChooser.class, frame);
                chooser.setColor(0xba, 0x55, 0xd3);
                RColorChooser rColorChooser = new RColorChooser(chooser, null, null, lr);
                rColorChooser.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("#ba55d3", call.getState());
    }

    public void colorChooserWithColorName() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JColorChooser chooser = (JColorChooser) ComponentUtils.findComponent(JColorChooser.class, frame);
                chooser.setColor(Color.red);
                RColorChooser rColorChooser = new RColorChooser(chooser, null, null, lr);
                rColorChooser.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("#ff0000", call.getState());
    }

    public void colorChooserWithIntValue() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JColorChooser chooser = (JColorChooser) ComponentUtils.findComponent(JColorChooser.class, frame);
                chooser.setColor(0xFFFF00);
                RColorChooser rColorChooser = new RColorChooser(chooser, null, null, lr);
                rColorChooser.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("#ffff00", call.getState());
    }
}
