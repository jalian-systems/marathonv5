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
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

import components.TableFilterDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.OSUtils;
import net.sourceforge.marathon.testhelpers.ComponentUtils;
import net.sourceforge.marathon.testhelpers.MissingException;

@Test public class JTableTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JTableTest.class.getSimpleName());
                frame.setName("frame-" + JTableTest.class.getSimpleName());
                frame.getContentPane().add(new TableFilterDemo(), BorderLayout.CENTER);
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
        if (driver != null) {
            driver.quit();
        }
    }

    public void gettable() throws Throwable {
        driver = new JavaDriver();
        driver.findElement(By.cssSelector("table"));
    }

    public void gettableText() throws Throwable {
        driver = new JavaDriver();
        WebElement table = driver.findElement(By.cssSelector("table"));
        AssertJUnit.assertEquals(
                "[[\"Kathy\",\"Smith\",\"Snowboarding\",\"5\",\"false\"],[\"John\",\"Doe\",\"Rowing\",\"3\",\"true\"],[\"Sue\",\"Black\",\"Knitting\",\"2\",\"false\"],[\"Jane\",\"White\",\"Speed reading\",\"20\",\"true\"],[\"Joe\",\"Brown\",\"Pool\",\"10\",\"false\"]]",
                table.getText());
    }

    public void gettableAttributes() throws Throwable {
        driver = new JavaDriver();
        WebElement table = driver.findElement(By.cssSelector("table"));
        AssertJUnit.assertEquals(5 + "", table.getAttribute("rowCount"));
        AssertJUnit.assertEquals(5 + "", table.getAttribute("columnCount"));
    }

    public void gettableCell() throws Throwable {
        driver = new JavaDriver();
        Object[][] data = { { "Kathy", "Smith", "Snowboarding", new Integer(5), new Boolean(false) },
                { "John", "Doe", "Rowing", new Integer(3), new Boolean(true) },
                { "Sue", "Black", "Knitting", new Integer(2), new Boolean(false) },
                { "Jane", "White", "Speed reading", new Integer(20), new Boolean(true) },
                { "Joe", "Brown", "Pool", new Integer(10), new Boolean(false) } };
        for (int i = 0, ii = 1; i < data.length; i++, ii++) {
            for (int j = 0, jj = 1; j < data[i].length; j++, jj++) {
                if (j == 4) {
                    continue;
                }
                AssertJUnit.assertEquals(data[i][j].toString(),
                        driver.findElement(By.cssSelector("table::mnth-cell(" + ii + ", " + jj + ")")).getText());
            }
        }
        // Check the value from the check-boxes
        for (int i = 0, ii = 1; i < data.length; i++, ii++) {
            WebElement cbCell = driver.findElement(By.cssSelector("table::mnth-cell(" + ii + ", " + 5 + ")"));
            AssertJUnit.assertEquals(data[i][4].toString(), cbCell.getAttribute("selected"));
        }
    }

    public void gettableAllCells() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> cells = driver.findElements(By.cssSelector("table::all-cells[text^='S']"));
        AssertJUnit.assertEquals(4, cells.size());
    }

    public void gettableCellNonexistant() throws Throwable {
        driver = new JavaDriver();
        WebElement errCell = driver.findElement(By.cssSelector("table::mnth-cell(20,20)"));
        try {
            errCell.getText();
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        }
    }

    public void clickTableCell() throws Throwable {
        driver = new JavaDriver();
        WebElement table = driver.findElement(By.cssSelector("table"));
        driver.findElement(By.cssSelector("table::mnth-cell(" + 3 + ", " + 3 + ")")).click();
        AssertJUnit.assertEquals(2 + "", table.getAttribute("selectedRow"));
        driver.findElement(By.cssSelector("table::mnth-cell(" + 5 + ", " + 1 + ")")).click();
        AssertJUnit.assertEquals(4 + "", table.getAttribute("selectedRow"));
    }

    public void clickTableCellUsingViewRowCol() throws Throwable {
        driver = new JavaDriver();
        WebElement table = driver.findElement(By.cssSelector("table"));
        driver.findElement(By.cssSelector("table::all-cells[viewRow=3][viewColumn=3]")).click();
        AssertJUnit.assertEquals(2 + "", table.getAttribute("selectedRow"));
        driver.findElement(By.cssSelector("table::all-cells[viewRow=5][viewColumn=1]")).click();
        AssertJUnit.assertEquals(4 + "", table.getAttribute("selectedRow"));
    }

    public void tableCellDisplayed() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setSize(640, 150);
                frame.requestFocus();
            }
        });
        AssertJUnit.assertFalse(driver.findElement(By.cssSelector("table::mnth-cell(5,3)")).isDisplayed());
        AssertJUnit.assertTrue(driver.findElement(By.cssSelector("table::mnth-cell(1,3)")).isDisplayed());
    }

    public void tableCellEdit() throws Throwable {
        driver = new JavaDriver();
        WebElement cell = driver.findElement(By.cssSelector("table::mnth-cell(3,3)::editor"));
        AssertJUnit.assertEquals("text-field", cell.getTagName());
        cell.clear();
        cell.sendKeys("Hello World", Keys.ENTER);
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,3)"));
        AssertJUnit.assertEquals("Hello World", cell.getText());
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,5)"));
        AssertJUnit.assertEquals("boolean-renderer", cell.getTagName());
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,5)::editor"));
        AssertJUnit.assertEquals("check-box", cell.getTagName());
    }

    public void tableCellGetFromTableSelf() throws Throwable {
        driver = new JavaDriver();
        WebElement table = driver.findElement(By.cssSelector("table"));
        WebElement cell = table.findElement(By.cssSelector(".::mnth-cell(3,3)::editor"));
        AssertJUnit.assertEquals("text-field", cell.getTagName());
        cell.clear();
        cell.sendKeys("Hello World", Keys.ENTER);
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,3)"));
        AssertJUnit.assertEquals("Hello World", cell.getText());
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,5)"));
        AssertJUnit.assertEquals("boolean-renderer", cell.getTagName());
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,5)::editor"));
        AssertJUnit.assertEquals("check-box", cell.getTagName());
    }

    public void tableCellEditUneditable() throws Throwable {
        driver = new JavaDriver();
        try {
            WebElement cell = driver.findElement(By.cssSelector("table::mnth-cell(3,1)::editor"));
            cell.sendKeys("Hello World", Keys.ENTER);
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        }
    }

    public void getheader() throws Throwable {
        driver = new JavaDriver();
        WebElement header = driver.findElement(By.cssSelector("table::header"));
        AssertJUnit.assertEquals(5 + "", header.getAttribute("count"));
        WebElement header2 = driver.findElement(By.cssSelector("table-header"));
        AssertJUnit.assertEquals(5 + "", header2.getAttribute("count"));
    }

    public void getheaderText() throws Throwable {
        driver = new JavaDriver();
        WebElement header = driver.findElement(By.cssSelector("table::header"));
        AssertJUnit.assertEquals("[\"First Name\",\"Last Name\",\"Sport\",\"# of Years\",\"Vegetarian\"]", header.getText());
    }

    public void getheaderItem() throws Throwable {
        String[] columnNames = { "First Name", "Last Name", "Sport", "# of Years", "Vegetarian" };
        driver = new JavaDriver();
        WebElement header = driver.findElement(By.cssSelector("table::header"));
        WebElement firstItem = header.findElement(By.cssSelector(".::nth-item(1)"));
        AssertJUnit.assertEquals("First Name", firstItem.getText());
        WebElement secondItem = header.findElement(By.cssSelector(".::nth-item(2)"));
        AssertJUnit.assertEquals("Last Name", secondItem.getText());
        secondItem = header.findElement(By.cssSelector(".::all-items[text='Last Name']"));
        AssertJUnit.assertEquals("Last Name", secondItem.getText());
        for (int i = 0, j = 1; i < columnNames.length; i++, j++) {
            WebElement item = header.findElement(By.cssSelector(".::nth-item(" + j + ")"));
            AssertJUnit.assertEquals(columnNames[i], item.getText());
        }
    }

    public void clickHeaderItem() throws Throwable {
        driver = new JavaDriver();
        WebElement header = driver.findElement(By.cssSelector("table::header"));
        final WebElement firstItem = header.findElement(By.cssSelector(".::nth-item(1)"));
        AssertJUnit.assertNull(firstItem.getAttribute("icon"));
        firstItem.click();
        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver input) {
                return firstItem.getAttribute("icon") != null;
            }
        });
        AssertJUnit.assertTrue(firstItem.getAttribute("icon").length() > 0);
        WebElement secondItem = header.findElement(By.cssSelector(".::nth-item(2)"));
        AssertJUnit.assertNull(secondItem.getAttribute("icon"));
        secondItem.click();
        AssertJUnit.assertTrue(secondItem.getAttribute("icon").length() > 0);
    }

    public void clickMultipleTableCells() throws Throwable {
        driver = new JavaDriver();
        final JTable ctable = (JTable) ComponentUtils.findComponent(JTable.class, frame);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                ctable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            }
        });
        final WebElement table = driver.findElement(By.cssSelector("table"));
        Keys menuKey = getMenuKey(table);
        WebElement cell_3_3 = driver.findElement(By.cssSelector("table::mnth-cell(" + 3 + ", " + 3 + ")"));
        cell_3_3.click();
        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver input) {
                return "2".equals(table.getAttribute("selectedRow"));
            }
        });
        AssertJUnit.assertEquals(2 + "", table.getAttribute("selectedRow"));
        WebElement cell_5_1 = driver.findElement(By.cssSelector("table::mnth-cell(" + 5 + ", " + 1 + ")"));
        new Actions(driver).moveToElement(cell_5_1).sendKeys(menuKey).click().sendKeys(Keys.NULL).perform();
        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver input) {
                return "[2, 4]".equals(table.getAttribute("selectedRows"));
            }
        });
        AssertJUnit.assertEquals("[2, 4]", table.getAttribute("selectedRows"));
    }

    private Keys getMenuKey(WebElement table) {
        return OSUtils.getMenuKey();
    }

    public void tableCellEditSelectByProps() throws Throwable {
        driver = new JavaDriver();
        String selector = "{ \"select\": \"{2, Sport}\" }";
        WebElement cell = driver.findElement(By.cssSelector("table::select-by-properties('" + selector + "')::editor"));
        AssertJUnit.assertEquals("text-field", cell.getTagName());
        cell.clear();
        cell.sendKeys("Hello World", Keys.ENTER);
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,3)"));
        AssertJUnit.assertEquals("Hello World", cell.getText());
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,5)"));
        AssertJUnit.assertEquals("boolean-renderer", cell.getTagName());
        cell = driver.findElement(By.cssSelector("table::mnth-cell(3,5)::editor"));
        AssertJUnit.assertEquals("check-box", cell.getTagName());
    }

}
