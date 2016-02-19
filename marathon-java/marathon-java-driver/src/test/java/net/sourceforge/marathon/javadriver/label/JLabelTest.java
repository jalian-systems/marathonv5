package net.sourceforge.marathon.javadriver.label;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.LabelDemo;

@Test public class JLabelTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JLabelTest.class.getSimpleName());
                frame.setName("frame-" + JLabelTest.class.getSimpleName());
                frame.getContentPane().add(new LabelDemo(), BorderLayout.CENTER);
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
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(3, labels.size());
        AssertJUnit.assertEquals("Image and Text", labels.get(0).getText());
        AssertJUnit.assertEquals("Text-Only Label", labels.get(1).getText());
        AssertJUnit.assertEquals(null, labels.get(2).getText());
    }

    public void getAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(3, labels.size());
        WebElement label = labels.get(0);
        AssertJUnit.assertEquals("Image and Text", label.getText());
        AssertJUnit.assertEquals("0", label.getAttribute("horizontalAlignment"));
        AssertJUnit.assertEquals("a pretty but meaningless splat", label.getAttribute("icon"));
        AssertJUnit.assertEquals("22", label.getAttribute("icon.iconHeight"));
    }

    public void getLabelsWithAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> labels = driver.findElements(By.cssSelector("label[text='Image and Text']"));
        AssertJUnit.assertEquals(1, labels.size());
        labels = driver.findElements(By.cssSelector("label[icon.description*='meaningless']"));
        AssertJUnit.assertEquals(2, labels.size());
    }
}