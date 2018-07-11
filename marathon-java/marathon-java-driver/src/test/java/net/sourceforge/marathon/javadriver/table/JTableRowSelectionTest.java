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
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TableSelectionDemo;
import net.sourceforge.marathon.javaagent.NoSuchWindowException;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.OSUtils;

@Test
public class JTableRowSelectionTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws InterruptedException, InvocationTargetException, NoSuchWindowException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame(JTableRowSelectionTest.class.getSimpleName());
                frame.setName("frame-" + JTableRowSelectionTest.class.getSimpleName());
                frame.getContentPane().add(new TableSelectionDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod
    public void disposeDriver() throws InterruptedException, InvocationTargetException {
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

    public void rowSelection() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> radiobuttons = driver.findElements(By.cssSelector("radio-button"));

        // Setting Single Selection
        radiobuttons.get(1).click();
        WebElement table = driver.findElement(By.cssSelector("table"));
        int rowCount = new Integer(table.getAttribute("rowCount"));
        AssertJUnit.assertEquals(5, rowCount);
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            assertClickOnRow(table, rowNum);
        }

        assertShiftClickSingleSelection(table);
    }

    private void assertShiftClickSingleSelection(WebElement table) throws Throwable {
        int rowNum = 0;
        clickOnRow(table, rowNum);

        // Checking whether the given row is selected.
        AssertJUnit.assertEquals(rowNum + "", table.getAttribute("selectedRow"));

        // Checking whether the given row is the only one row being selected.
        AssertJUnit.assertEquals("[" + rowNum + "]", table.getAttribute("selectedRows"));

        rowNum += 2;
        shiftClickOnRow(table, rowNum);

        // Checking whether the new row is selected.
        AssertJUnit.assertEquals(rowNum + "", table.getAttribute("selectedRow"));

        // Checking whether the new row is the only one row being selected.
        AssertJUnit.assertEquals("[" + rowNum + "]", table.getAttribute("selectedRows"));

        return;

    }

    private void assertClickOnRow(WebElement table, int rowNum) throws Throwable {
        clickOnRow(table, rowNum);

        // Checking whether the given row is selected.
        AssertJUnit.assertEquals(rowNum + "", table.getAttribute("selectedRow"));

        // Checking whether the given row is the only one row being selected.
        AssertJUnit.assertEquals("[" + rowNum + "]", table.getAttribute("selectedRows"));
        return;
    }

    public void rowSelectionSingleInterval() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> radiobuttons = driver.findElements(By.cssSelector("radio-button"));

        // Setting Single Interval Selection
        radiobuttons.get(2).click();
        WebElement table = driver.findElement(By.cssSelector("table"));
        int rowCount = new Integer(table.getAttribute("rowCount"));
        AssertJUnit.assertEquals(5, rowCount);
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            assertClickOnRow(table, rowNum);
        }

        assertShiftClickSingleIntSelection(table, 1, 3, "1, 2, 3");
    }

    private void assertShiftClickSingleIntSelection(WebElement table, int rowNum, int anotherRowNum, String selectedRows)
            throws Throwable {
        clickOnRow(table, rowNum);

        // Checking whether the given row is selected.
        AssertJUnit.assertEquals(rowNum + "", table.getAttribute("selectedRow"));

        // Checking whether the given row is the only one row being selected.
        AssertJUnit.assertEquals("[" + rowNum + "]", table.getAttribute("selectedRows"));

        shiftClickOnRow(table, anotherRowNum);

        // Checking whether the row range is selected.
        AssertJUnit.assertEquals("[" + selectedRows + "]", table.getAttribute("selectedRows"));

        return;

    }

    public void rowSelectionMultipleInterval() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> radiobuttons = driver.findElements(By.cssSelector("radio-button"));

        // Setting Single Interval Selection
        radiobuttons.get(0).click();
        WebElement table = driver.findElement(By.cssSelector("table"));
        int rowCount = new Integer(table.getAttribute("rowCount"));
        AssertJUnit.assertEquals(5, rowCount);
        for (int rowNum = 0; rowNum < rowCount; rowNum++) {
            assertClickOnRow(table, rowNum);
        }

        assertCtrlClickMultipleIntSelection(table);
    }

    private void assertCtrlClickMultipleIntSelection(WebElement table) throws Throwable {
        int firstRow = 0;
        clickOnRow(table, firstRow);

        // Checking whether the given row is selected.
        AssertJUnit.assertEquals(firstRow + "", table.getAttribute("selectedRow"));

        // Checking whether the given row is the only one row being selected.
        AssertJUnit.assertEquals("[" + firstRow + "]", table.getAttribute("selectedRows"));

        int secondRow = 2;
        ctrlOrCommandClickOnRow(table, secondRow);

        // Checking whether the clicked rows are selected.
        AssertJUnit.assertEquals("[" + firstRow + ", " + secondRow + "]", table.getAttribute("selectedRows"));

        int thirdRow = 4;
        shiftClickOnRow(table, thirdRow);

        // Checking whether the clicked rows are selected.
        AssertJUnit.assertEquals("[2, 3, 4]", table.getAttribute("selectedRows"));

        ctrlPlusShiftClickOnRow(table, firstRow);

        AssertJUnit.assertEquals("[0, 1, 2, 3, 4]", table.getAttribute("selectedRows"));

        return;

    }

    private void ctrlPlusShiftClickOnRow(WebElement table, int rowNum) {
        WebElement anotherRow = table.findElement(By.cssSelector(".::mnth-cell(" + (rowNum + 1) + ",3)"));
        new Actions(driver).sendKeys(OSUtils.getMenuKey()).keyDown(Keys.SHIFT).click(anotherRow).perform();
        table.sendKeys(Keys.NULL);
    }

    private void shiftClickOnRow(WebElement table, int rowNum) {
        WebElement anotherRow = table.findElement(By.cssSelector(".::mnth-cell(" + (rowNum + 1) + ",3)"));
        new Actions(driver).keyDown(Keys.SHIFT).click(anotherRow).perform();
        table.sendKeys(Keys.NULL);
    }

    private void clickOnRow(WebElement table, int rowNum) {
        // Index on the element is 1 based and index on the JTable is 0 based.
        // Hence adding 1 to the rowNum
        WebElement row = table.findElement(By.cssSelector(".::mnth-cell(" + (rowNum + 1) + ",3)"));
        row.click();
        table.sendKeys(Keys.NULL);
    }

    private void ctrlOrCommandClickOnRow(WebElement table, int rowNum) {
        WebElement anotherRow = table.findElement(By.cssSelector(".::mnth-cell(" + (rowNum + 1) + ",3)"));
        new Actions(driver).sendKeys(OSUtils.getMenuKey()).click(anotherRow).perform();
        table.sendKeys(Keys.NULL);
    }

}
