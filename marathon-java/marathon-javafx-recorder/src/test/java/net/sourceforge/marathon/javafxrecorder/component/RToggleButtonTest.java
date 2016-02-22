package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javafxrecorder.component.RToggleButton;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Call;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.CheckBoxDemo;
import components.RadioButtonDemo;

@Test public class RToggleButtonTest extends RComponentTest {

    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(RToggleButtonTest.class.getSimpleName());
                frame.setName("frame-" + RToggleButtonTest.class.getSimpleName());
                frame.getContentPane().add(new CheckBoxDemo(), BorderLayout.CENTER);
                frame.getContentPane().add(new RadioButtonDemo(), BorderLayout.EAST);
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

    public void selectCheckBoxSelected() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                List<Component> comps = ComponentUtils.findComponents(JCheckBox.class, frame);
                JCheckBox checkBox = (JCheckBox) comps.get(3);
                checkBox.setSelected(true);
                RToggleButton rButton = new RToggleButton(checkBox, null, null, lr);
                rButton.mouseClicked(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("true", call.getState());
    }

    public void selectCheckBoxNotSelected() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                List<Component> comps = ComponentUtils.findComponents(JCheckBox.class, frame);
                JCheckBox checkBox = (JCheckBox) comps.get(3);
                RToggleButton rButton = new RToggleButton(checkBox, null, null, lr);
                checkBox.setSelected(false);
                rButton.mouseEntered(null);
                checkBox.setSelected(true);
                rButton.mouseClicked(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("true", call.getState());
    }

    public void selectRadioButtonSelected() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                List<Component> comps = ComponentUtils.findComponents(JRadioButton.class, frame);
                JRadioButton button = (JRadioButton) comps.get(2);
                button.setSelected(true);
                RToggleButton rButton = new RToggleButton(button, null, null, lr);
                rButton.mouseClicked(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("true", call.getState());
    }

    public void selectRadioButtonNotSelected() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                List<Component> comps = ComponentUtils.findComponents(JRadioButton.class, frame);
                JRadioButton button = (JRadioButton) comps.get(2);
                RToggleButton rButton = new RToggleButton(button, null, null, lr);
                button.setSelected(false);
                rButton.mouseEntered(null);
                button.setSelected(true);
                rButton.mouseClicked(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("true", call.getState());
    }
}
