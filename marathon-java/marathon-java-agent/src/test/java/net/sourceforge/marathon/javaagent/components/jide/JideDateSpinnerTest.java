package net.sourceforge.marathon.javaagent.components.jide;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.spinner.DateSpinner;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

public class JideDateSpinnerTest {
    private JavaAgent driver;
    protected JFrame frame;
    private Date date = new Date();

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
                DateSpinnerDemo dsd = new DateSpinnerDemo();
                frame = new JFrame("DateSpinner Demo");
                frame.getContentPane().add(dsd);
                frame.pack();
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
    public void getValue() {
        driver = new JavaAgent();
        IJavaElement dspElement = driver.findElementByCssSelector("date-spinner");

        DateSpinner dspComponent = (DateSpinner) ComponentUtils.findComponent(DateSpinner.class, frame);
        SimpleDateFormat format = dspComponent._timeEditor.getFormat();
        String expectedValue = format.format(date);

        String actualValue = dspElement.getText();

        AssertJUnit.assertEquals(expectedValue, actualValue);
    }

    @Test
    public void selectDate() throws Throwable {
        driver = new JavaAgent();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        Date date = format.parse("05/24/1994");

        DateSpinner dspComponent = (DateSpinner) ComponentUtils.findComponent(DateSpinner.class, frame);
        format = dspComponent._timeEditor.getFormat();
        String expected = format.format(date);

        IJavaElement dspElement = driver.findElementByCssSelector("date-spinner");
        dspElement.marathon_select(expected);
        String actual = dspElement.getText();
        AssertJUnit.assertEquals(expected, actual);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void datePickerWithInvalidDateFormat() {
        driver = new JavaAgent();
        IJavaElement dspElement = driver.findElementByCssSelector("date-spinner");
        dspElement.marathon_select("19/23");
    }

}
