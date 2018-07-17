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
public class JTableColumnSelectionTest {

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

    public void columnSelection() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> checkboxes = driver.findElements(By.cssSelector("check-box"));

        // Setting Column Selection
        checkboxes.get(1).click();
        List<WebElement> radiobuttons = driver.findElements(By.cssSelector("radio-button"));

        // Setting Single Selection
        radiobuttons.get(1).click();

        WebElement table = driver.findElement(By.cssSelector("table"));
        int columnCount = new Integer(table.getAttribute("columnCount"));
        AssertJUnit.assertEquals(5, columnCount);
        for (int colNum = 0; colNum < columnCount; colNum++) {
            assertClickOnColumn(table, colNum);
        }

        assertShiftClickSingleSelection(table);
    }

    private void assertShiftClickSingleSelection(WebElement table) throws Throwable {
        int colNum = 0;
        clickOnCol(table, colNum);

        // Checking whether the given col is selected.
        AssertJUnit.assertEquals(colNum + "", table.getAttribute("selectedColumn"));

        // Checking whether the given col is the only one col being selected.
        AssertJUnit.assertEquals("[" + colNum + "]", table.getAttribute("selectedColumns"));

        colNum += 2;
        shiftClickOnCol(table, colNum);

        // Checking whether the new col is selected.
        AssertJUnit.assertEquals(colNum + "", table.getAttribute("selectedColumn"));

        // Checking whether the new col is the only one col being selected.
        AssertJUnit.assertEquals("[" + colNum + "]", table.getAttribute("selectedColumns"));

        return;

    }

    private void assertClickOnColumn(WebElement table, int colNum) throws Throwable {
        clickOnCol(table, colNum);

        // Checking whether the given col is selected.
        AssertJUnit.assertEquals(colNum + "", table.getAttribute("selectedColumn"));

        // Checking whether the given col is the only one col being selected.
        AssertJUnit.assertEquals("[" + colNum + "]", table.getAttribute("selectedColumns"));
        return;
    }

    public void colSelectionSingleInterval() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> checkboxes = driver.findElements(By.cssSelector("check-box"));

        // Setting Column Selection
        checkboxes.get(1).click();
        List<WebElement> radiobuttons = driver.findElements(By.cssSelector("radio-button"));

        // Setting Single Interval Selection
        radiobuttons.get(2).click();

        WebElement table = driver.findElement(By.cssSelector("table"));
        int colCount = new Integer(table.getAttribute("columnCount"));
        AssertJUnit.assertEquals(5, colCount);
        for (int colNum = 0; colNum < colCount; colNum++) {
            assertClickOnColumn(table, colNum);
        }

        assertShiftClickSingleIntSelection(table, 1, 3, "1, 2, 3");
    }

    private void assertShiftClickSingleIntSelection(WebElement table, int colNum, int anotherColNum, String selectedCols)
            throws Throwable {
        clickOnCol(table, colNum);

        // Checking whether the given col is selected.
        AssertJUnit.assertEquals(colNum + "", table.getAttribute("selectedColumn"));

        // Checking whether the given col is the only one col being selected.
        AssertJUnit.assertEquals("[" + colNum + "]", table.getAttribute("selectedColumns"));

        shiftClickOnCol(table, anotherColNum);

        // Checking whether the col range is selected.
        AssertJUnit.assertEquals("[" + selectedCols + "]", table.getAttribute("selectedColumns"));

        return;

    }

    public void colSelectionMultipleInterval() throws Throwable {
        driver = new JavaDriver();

        List<WebElement> checkboxes = driver.findElements(By.cssSelector("check-box"));

        // Setting Column Selection
        checkboxes.get(1).click();

        List<WebElement> radiobuttons = driver.findElements(By.cssSelector("radio-button"));

        // Setting Single Interval Selection
        radiobuttons.get(0).click();

        WebElement table = driver.findElement(By.cssSelector("table"));
        int colCount = new Integer(table.getAttribute("columnCount"));
        AssertJUnit.assertEquals(5, colCount);
        for (int colNum = 0; colNum < colCount; colNum++) {
            assertClickOnColumn(table, colNum);
        }

        assertCtrlClickMultipleIntSelection(table);
    }

    private void assertCtrlClickMultipleIntSelection(WebElement table) throws Throwable {
        int firstCol = 0;
        clickOnCol(table, firstCol);

        // Checking whether the given col is selected.
        AssertJUnit.assertEquals(firstCol + "", table.getAttribute("selectedColumn"));

        // Checking whether the given col is the only one col being selected.
        AssertJUnit.assertEquals("[" + firstCol + "]", table.getAttribute("selectedColumns"));

        int secondCol = 2;
        ctrlOrCommandClickOnCol(table, secondCol);

        // Checking whether the clicked cols are selected.
        AssertJUnit.assertEquals("[" + firstCol + ", " + secondCol + "]", table.getAttribute("selectedColumns"));

        int thirdCol = 4;
        shiftClickOnCol(table, thirdCol);

        // Checking whether the clicked cols are selected.
        AssertJUnit.assertEquals("[2, 3, 4]", table.getAttribute("selectedColumns"));

        ctrlPlusShiftClickOnCol(table, firstCol);

        AssertJUnit.assertEquals("[0, 1, 2, 3, 4]", table.getAttribute("selectedColumns"));
        return;

    }

    private void ctrlPlusShiftClickOnCol(WebElement table, int colNum) {
        WebElement anotherCol = table.findElement(By.cssSelector(".::mnth-cell(1," + (colNum + 1) + ")"));
        new Actions(driver).sendKeys(OSUtils.getMenuKey()).keyDown(Keys.SHIFT).click(anotherCol).perform();
        table.sendKeys(Keys.NULL);
    }

    private void shiftClickOnCol(WebElement table, int colNum) {
        WebElement anotherCol = table.findElement(By.cssSelector(".::mnth-cell(1," + (colNum + 1) + ")"));
        new Actions(driver).keyDown(Keys.SHIFT).click(anotherCol).perform();
        table.sendKeys(Keys.NULL);
    }

    private void clickOnCol(WebElement table, int colNum) {
        // Index on the element is 1 based and index on the JTable is 0 based.
        // Hence adding 1 to the colNum
        WebElement col = table.findElement(By.cssSelector(".::mnth-cell(1," + (colNum + 1) + ")"));
        col.click();
        table.sendKeys(Keys.NULL);
    }

    private void ctrlOrCommandClickOnCol(WebElement table, int colNum) {
        WebElement anotherCol = table.findElement(By.cssSelector(".::mnth-cell(1," + (colNum + 1) + ")"));
        new Actions(driver).sendKeys(OSUtils.getMenuKey()).click(anotherCol).perform();
        table.sendKeys(Keys.NULL);
    }
}
