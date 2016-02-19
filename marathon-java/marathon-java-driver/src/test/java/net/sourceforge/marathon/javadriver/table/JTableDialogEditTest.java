package net.sourceforge.marathon.javadriver.table;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TableDialogEditDemo;

@Test public class JTableDialogEditTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JTableDialogEditTest.class.getSimpleName());
                frame.setName("frame-" + JTableDialogEditTest.class.getSimpleName());
                frame.getContentPane().add(new TableDialogEditDemo(), BorderLayout.CENTER);
                frame.pack();
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

    public void gettable() throws Throwable {
        driver = new JavaDriver();
        driver.findElement(By.cssSelector("table"));
    }

    public void getCellEditor() throws Throwable {
        driver = new JavaDriver();
        WebElement cell = driver.findElement(By.cssSelector("table::mnth-cell(1,2)::editor"));
        AssertJUnit.assertEquals("button", cell.getTagName());
        cell.click();
        driver.switchTo().window("Pick a Color");
        WebElement tab = driver.findElement(By.cssSelector("tabbed-pane::all-tabs[text='RGB']"));
        tab.click();
        WebElement tabcomponent = driver.findElement(By.cssSelector("tabbed-pane::all-tabs[text='RGB']::component"));
        List<WebElement> spinners = tabcomponent.findElements(By.cssSelector("spinner"));
        List<WebElement> all = spinners.get(0).findElements(By.cssSelector("*"));
        List<String> s = new ArrayList<String>();
        for (WebElement webElement : all) {
            s.add(webElement.getTagName());
        }
        WebElement tf = spinners.get(0).findElement(By.cssSelector("formatted-text-field"));
        tf.clear();
        tf.sendKeys("234", Keys.TAB);
        driver.findElement(By.cssSelector("button[text='OK']")).click();
    }

}
