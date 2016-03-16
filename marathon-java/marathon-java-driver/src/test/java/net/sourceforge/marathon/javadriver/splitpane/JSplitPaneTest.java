package net.sourceforge.marathon.javadriver.splitpane;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.SplitPaneDemo;

@Test public class JSplitPaneTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JSplitPaneTest.class.getSimpleName());
                frame.setName("frame-" + JSplitPaneTest.class.getSimpleName());
                frame.getContentPane().add(new SplitPaneDemo().getSplitPane(), BorderLayout.CENTER);
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

    public void getText() throws Throwable {
        driver = new JavaDriver();
        WebElement splitPane = driver.findElement(By.cssSelector("split-pane"));
        AssertJUnit.assertEquals(null, splitPane.getText());
    }

    public void getAttributes() throws Throwable {
        driver = new JavaDriver();
        WebElement splitPane = driver.findElement(By.cssSelector("split-pane"));
        AssertJUnit.assertEquals("150", splitPane.getAttribute("dividerLocation"));
        WebElement splitpaneDivider = driver
                .findElement(By.cssSelector(":instance-of('javax.swing.plaf.basic.BasicSplitPaneDivider')"));
        new Actions(driver).moveToElement(splitpaneDivider).dragAndDropBy(splitpaneDivider, 25, 25).perform();
        AssertJUnit.assertEquals("175", splitPane.getAttribute("dividerLocation"));
    }

    public void getLeftRightComponents() throws Throwable {
        driver = new JavaDriver();
        WebElement splitPaneLeft = driver.findElement(By.cssSelector("split-pane::left"));
        splitPaneLeft.findElement(By.cssSelector("list"));
        WebElement splitPaneTop = driver.findElement(By.cssSelector("split-pane::top"));
        AssertJUnit.assertEquals(splitPaneTop, splitPaneLeft);
        WebElement splitPaneRight = driver.findElement(By.cssSelector("split-pane::right"));
        WebElement splitPaneBottom = driver.findElement(By.cssSelector("split-pane::bottom"));
        AssertJUnit.assertEquals(splitPaneBottom, splitPaneRight);
    }

}
