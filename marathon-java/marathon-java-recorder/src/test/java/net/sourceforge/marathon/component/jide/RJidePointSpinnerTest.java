package net.sourceforge.marathon.component.jide;

import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.spinner.PointSpinner;

import net.sourceforge.marathon.component.LoggingRecorder;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RComponentTest;
import net.sourceforge.marathon.component.RSpinner;
import net.sourceforge.marathon.javaagent.components.jide.PointSpinnerDemo;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class RJidePointSpinnerTest extends RComponentTest {

    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("PointSpinner Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                PointSpinnerDemo psDemo = new PointSpinnerDemo();
                frame.getContentPane().add(psDemo);
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
    public void pointSpinner() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override
            public void run() {
                PointSpinner spinnerComponent = (PointSpinner) ComponentUtils.findComponent(PointSpinner.class, frame);
                RSpinner rSpinner = new RSpinner(spinnerComponent, null, null, lr);
                rSpinner.focusGained(null);
                spinnerComponent.setValue(new Point(20, 21));
                rSpinner.focusLost(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select", call.getFunction());
        AssertJUnit.assertEquals("(20, 21)", call.getState());
    }

}
