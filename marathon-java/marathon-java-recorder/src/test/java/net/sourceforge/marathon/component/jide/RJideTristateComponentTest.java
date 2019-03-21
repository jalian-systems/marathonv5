package net.sourceforge.marathon.component.jide;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.TristateCheckBox;

import net.sourceforge.marathon.component.LoggingRecorder;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RComponentTest;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class RJideTristateComponentTest extends RComponentTest {

    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("My First GUI");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(300, 300);
                TristateCheckBox triCb1 = new TristateCheckBox("Jide TriStateCheckBox 1");
                TristateCheckBox triCb2 = new TristateCheckBox("Jide TriStateCheckBox 2");
                TristateCheckBox triCb3 = new TristateCheckBox("Jide TriStateCheckBox 3");
                JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 5));
                jPanel.add(triCb1);
                jPanel.add(triCb2);
                jPanel.add(triCb3);
                frame.getContentPane().add(jPanel);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
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
    }

    @Test
    public void selectCheckBoxSelected() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                List<Component> comps = ComponentUtils.findComponents(TristateCheckBox.class, frame);
                TristateCheckBox tcb = (TristateCheckBox) comps.get(0);
                tcb.setSelected(true);
                RJideTristateComponent rtcb = new RJideTristateComponent(tcb, null, null, lr);
                rtcb.mouseClicked(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("checked", call.getState());
    }

    @Test
    public void selectCheckBoxNotSelected() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                List<Component> comps = ComponentUtils.findComponents(TristateCheckBox.class, frame);
                TristateCheckBox tcb = (TristateCheckBox) comps.get(0);
                tcb.setSelected(false);
                RJideTristateComponent rtcb = new RJideTristateComponent(tcb, null, null, lr);
                rtcb.mouseEntered(null);
                tcb.setSelected(true);
                rtcb.mouseClicked(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("checked", call.getState());
    }

    @Test
    public void selectCheckBoxSelectedTriState() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                List<Component> comps = ComponentUtils.findComponents(TristateCheckBox.class, frame);
                TristateCheckBox tcb = (TristateCheckBox) comps.get(0);
                tcb.setMixed(true);
                RJideTristateComponent rtcb = new RJideTristateComponent(tcb, null, null, lr);
                rtcb.mouseClicked(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("indeterminate", call.getState());
    }

}
