package net.sourceforge.marathon.javadriver.table;

import java.awt.BorderLayout;

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

import components.TableRenderDemo;

@Test public class JTableRenderTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JTableRenderTest.class.getSimpleName());
                frame.setName("frame-" + JTableRenderTest.class.getSimpleName());
                frame.getContentPane().add(new TableRenderDemo(), BorderLayout.CENTER);
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

    public void getCellEditorCombobox() throws Throwable {
        driver = new JavaDriver();
        WebElement comboCell = driver.findElement(By.cssSelector("table::mnth-cell(1,3)"));
        AssertJUnit.assertEquals("Snowboarding", comboCell.getText());
        WebElement comboCellEditor = driver.findElement(By.cssSelector("table::mnth-cell(1,3)::editor"));
        AssertJUnit.assertEquals("combo-box", comboCellEditor.getTagName());
        WebElement option = comboCellEditor.findElement(By.cssSelector(".::all-options[text='Knitting']"));
        // Needs the click to put in editing mode
        // comboCell.click();
        option.click();
        AssertJUnit.assertEquals("Knitting", comboCell.getText());
    }

    public void getCellEditorText() throws Throwable {
        driver = new JavaDriver();
        WebElement comboCell = driver.findElement(By.cssSelector("table::mnth-cell(1,4)"));
        AssertJUnit.assertEquals("5", comboCell.getText());
        WebElement comboCellEditor = driver.findElement(By.cssSelector("table::mnth-cell(1,4)::editor"));
        comboCellEditor.clear();
        comboCellEditor.sendKeys("100", Keys.ENTER);
        AssertJUnit.assertEquals("100", comboCell.getText());
    }

    public void getCellEditorCheckbox() throws Throwable {
        driver = new JavaDriver();
        WebElement comboCell = driver.findElement(By.cssSelector("table::mnth-cell(1,5)"));
        AssertJUnit.assertFalse(comboCell.isSelected());
        WebElement comboCellEditor = driver.findElement(By.cssSelector("table::mnth-cell(1,5)::editor"));
        // The editor itself generates a click. So we need two to switch the
        // value here.
        comboCellEditor.click();
        AssertJUnit.assertTrue(comboCellEditor.isSelected());
    }

}
