package net.sourceforge.marathon.javadriver.internalframe;

import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.InternalFrameDemo;

@Test public class JInternalFrameTest {

    private WebDriver driver;
    protected InternalFrameDemo frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new InternalFrameDemo();
                frame.setName("frame-" + JInternalFrameTest.class.getSimpleName());
                frame.setSize(640, 480);
                frame.setAlwaysOnTop(true);
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
        if (driver != null)
            driver.quit();
    }

    public void contextualTitle() {
        driver = new JavaDriver();
        List<WebElement> frames = driver.findElements(By.cssSelector("internal-frame"));
        AssertJUnit.assertEquals(1, frames.size());
        WebElement frame = frames.get(0);
        AssertJUnit.assertEquals("Document #1", frame.getAttribute("title"));
    }
}
