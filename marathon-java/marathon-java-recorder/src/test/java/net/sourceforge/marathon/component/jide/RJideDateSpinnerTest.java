package net.sourceforge.marathon.component.jide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.spinner.DateSpinner;

import net.sourceforge.marathon.component.LoggingRecorder;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.component.RComponentTest;
import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.javaagent.components.jide.DateSpinnerDemo;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class RJideDateSpinnerTest extends RComponentTest {
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        System.setProperty("marathon.mode", "recording");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                frame = new JFrame("DateSpinnerDemo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                DateSpinnerDemo panel = new DateSpinnerDemo();
                frame.getContentPane().add(panel);
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
    public void pickDate() {

        final DateSpinner dateSpinner = (DateSpinner) ComponentUtils.findComponent(DateSpinner.class, frame);
        final LoggingRecorder lr = new LoggingRecorder();

        RJideDateSpinnerElement rDateSpinner = new RJideDateSpinnerElement(dateSpinner, null, null, lr);
        dateSpinner.setValue(new Date());
        rDateSpinner.focusLost(null);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Call call = lr.getCall();
                AssertJUnit.assertEquals("select", call.getFunction());
                String text = dateSpinner._timeEditor.getTextField().getText();
                AssertJUnit.assertEquals(text, call.getState());

            }
        });
    }

    @Test
    public void getText() {
        final DateSpinner dateSpinner = (DateSpinner) ComponentUtils.findComponent(DateSpinner.class, frame);
        LoggingRecorder lr = new LoggingRecorder();
        final RJideDateSpinnerElement rDateSpinner = new RJideDateSpinnerElement(dateSpinner, null, null, lr);
        final List<String> text = new ArrayList<String>();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dateSpinner.setValue(new Date());
                text.add(rDateSpinner._getText());
            }
        });
        new Wait("Waiting for date picker text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals(dateSpinner._timeEditor.getTextField().getText(), text.get(0));
    }
}
