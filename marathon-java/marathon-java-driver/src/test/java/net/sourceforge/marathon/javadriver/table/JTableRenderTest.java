/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javadriver.table;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TableRenderDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;

@Test
public class JTableRenderTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame(JTableRenderTest.class.getSimpleName());
                frame.setName("frame-" + JTableRenderTest.class.getSimpleName());
                frame.getContentPane().add(new TableRenderDemo(), BorderLayout.CENTER);
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
        if (driver != null) {
            driver.quit();
        }
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
