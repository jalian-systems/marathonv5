package net.sourceforge.marathon.javadriver.dnd;

import static org.testng.AssertJUnit.assertEquals;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

@Test public class DragAndDropTest {

    private JavaDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override public void run() {
                frame = DropDemo.createAndShowGUI();
                frame.setAlwaysOnTop(true);
            }
        });
        EventQueueWait.waitTillShown(frame);
    }

    @AfterMethod public void disposeDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.dispose();
            }
        });
        EventQueueWait.empty();
        if (driver != null)
            driver.quit();
    }

    public void dndWithMove() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities();
        // caps.setCapability("nativeEvents", true);
        driver = new JavaDriver(caps, caps);
        WebElement list = driver.findElement(By.cssSelector("list"));
        assertEquals(
                "[[\"List Item 0\",\"List Item 1\",\"List Item 2\",\"List Item 3\",\"List Item 4\",\"List Item 5\",\"List Item 6\",\"List Item 7\",\"List Item 8\",\"List Item 9\"]]",
                list.getAttribute("content"));
        WebElement listitem1 = driver.findElement(By.cssSelector("list::nth-item(1)"));
        WebElement listitem5 = driver.findElement(By.cssSelector("list::nth-item(5)"));
        driver.clearlogs(LogType.DRIVER);
        System.err.println("About to sleep");
        new Actions(driver).dragAndDrop(listitem1, listitem5).perform();
        waitTillDropCompletes(
                "[[\"List Item 1\",\"List Item 2\",\"List Item 3\",\"List Item 0\",\"List Item 5\",\"List Item 6\",\"List Item 7\",\"List Item 8\",\"List Item 9\"]]",
                list);
        assertEquals(
                "[[\"List Item 1\",\"List Item 2\",\"List Item 3\",\"List Item 0\",\"List Item 5\",\"List Item 6\",\"List Item 7\",\"List Item 8\",\"List Item 9\"]]",
                list.getAttribute("content"));
    }

    public void dndWithCopy() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities();
        // caps.setCapability("nativeEvents", true);
        driver = new JavaDriver(caps, caps);
        WebElement list = driver.findElement(By.cssSelector("list"));
        assertEquals(
                "[[\"List Item 0\",\"List Item 1\",\"List Item 2\",\"List Item 3\",\"List Item 4\",\"List Item 5\",\"List Item 6\",\"List Item 7\",\"List Item 8\",\"List Item 9\"]]",
                list.getAttribute("content"));
        WebElement listitem1 = driver.findElement(By.cssSelector("list::nth-item(1)"));
        WebElement listitem5 = driver.findElement(By.cssSelector("list::nth-item(5)"));
        listitem1.click();
        driver.clearlogs(LogType.DRIVER);
        new Actions(driver).keyDown(Keys.ALT).dragAndDrop(listitem1, listitem5).keyUp(Keys.ALT).perform();
        waitTillDropCompletes(
                "[[\"List Item 0\",\"List Item 1\",\"List Item 2\",\"List Item 3\",\"List Item 0(1)\",\"List Item 5\",\"List Item 6\",\"List Item 7\",\"List Item 8\",\"List Item 9\"]]",
                list);
        assertEquals(
                "[[\"List Item 0\",\"List Item 1\",\"List Item 2\",\"List Item 3\",\"List Item 0(1)\",\"List Item 5\",\"List Item 6\",\"List Item 7\",\"List Item 8\",\"List Item 9\"]]",
                list.getAttribute("content"));
    }

    private void waitTillDropCompletes(final String string, final WebElement list) {
        try {
            new WebDriverWait(driver, 5, 500).until(new Predicate<WebDriver>() {
                public boolean apply(WebDriver driver) {
                    return string.equals(list.getAttribute("content"));
                }
            });
        } catch (Throwable t) {
        }
    }

}
