package net.sourceforge.marathon.javadriver;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Test public class JavaDriverCapabilitiesTest {

    private WebDriver driver;

    protected JFrame frame;
    protected JTextField textField;
    protected JButton button;
    protected JMenu menu;
    protected JMenuItem exitItem;
    protected boolean buttonClicked = false;
    protected StringBuilder buttonMouseActions;

    @AfterMethod public void quitDriver() {
        if (driver != null)
            driver.quit();
    }

    public void javaDriver() {
        driver = new JavaDriver();
        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
        AssertJUnit.assertEquals("java", capabilities.getBrowserName());
        AssertJUnit.assertEquals(true, capabilities.is("takesScreenshot"));
        AssertJUnit.assertEquals(false, capabilities.is("nativeEvents"));
    }

    public void createSessionWithDefaultCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setVersion("");
        driver = new JavaDriver(desiredCapabilities, desiredCapabilities);
    }
}
