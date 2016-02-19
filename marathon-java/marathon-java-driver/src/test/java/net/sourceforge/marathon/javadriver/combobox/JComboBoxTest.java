package net.sourceforge.marathon.javadriver.combobox;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.ComboBoxDemo;

@Test public class JComboBoxTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JComboBoxTest.class.getSimpleName());
                frame.setName("frame-" + JComboBoxTest.class.getSimpleName());
                frame.getContentPane().add(new ComboBoxDemo(), BorderLayout.CENTER);
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
        List<WebElement> combos = driver.findElements(By.cssSelector("combo-box"));
        String[] patternExamples = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

        AssertJUnit.assertEquals(1, combos.size());
        JSONArray result = new JSONArray();
        result.put(new JSONArray(Arrays.asList(patternExamples)));
        AssertJUnit.assertEquals(result.toString(), combos.get(0).getAttribute("content"));
    }

    public void getAllOptions() throws Throwable {
        driver = new JavaDriver();
        String[] patternExamples = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

        List<WebElement> options = driver.findElements(By.cssSelector("combo-box::all-options"));
        AssertJUnit.assertEquals(patternExamples.length, options.size());
        for (int i = 0; i < patternExamples.length; i++)
            AssertJUnit.assertEquals(patternExamples[i], options.get(i).getText());
    }

    public void getNthOption() throws Throwable {
        driver = new JavaDriver();

        AssertJUnit.assertEquals(1, driver.findElements(By.cssSelector("combo-box")).size());
        AssertJUnit.assertEquals("Bird", driver.findElement(By.cssSelector("combo-box::nth-option(1)")).getText());
        AssertJUnit.assertEquals("Cat", driver.findElement(By.cssSelector("combo-box::nth-option(2)")).getText());
        AssertJUnit.assertEquals("Dog", driver.findElement(By.cssSelector("combo-box::nth-option(3)")).getText());
        AssertJUnit.assertEquals("Rabbit", driver.findElement(By.cssSelector("combo-box::nth-option(4)")).getText());
        AssertJUnit.assertEquals("Pig", driver.findElement(By.cssSelector("combo-box::nth-option(5)")).getText());
    }

    public void clickItem() throws Throwable {
        driver = new JavaDriver();
        String[] patternExamples = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

        WebElement combo = driver.findElement(By.cssSelector("combo-box"));
        AssertJUnit.assertEquals("" + 4, combo.getAttribute("selectedIndex"));
        WebElement option;
        for (int i = 0; i < patternExamples.length; i++) {
            option = driver.findElement(By.cssSelector("combo-box::nth-option(" + (i + 1) + ")"));
            option.click();
            AssertJUnit.assertEquals("" + i, combo.getAttribute("selectedIndex"));
        }
        for (int i = patternExamples.length - 1; i >= 0; i--) {
            option = driver.findElement(By.cssSelector("combo-box::nth-option(" + (i + 1) + ")"));
            option.click();
            AssertJUnit.assertEquals("" + i, combo.getAttribute("selectedIndex"));
        }
    }

    public void checkWhetherEditable() throws Throwable {
        driver = new JavaDriver();

        WebElement combo = driver.findElement(By.cssSelector("combo-box"));
        AssertJUnit.assertEquals("false", combo.getAttribute("editable"));
    }

}
