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
package net.sourceforge.marathon.javadriver.list;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.testhelpers.MissingException;

@SuppressWarnings({ "unchecked", "rawtypes" }) @Test public class JListXTest {

    private WebDriver driver;
    protected JFrame frame;
    protected JList list;
    private JTextField textfield;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                    @Override public void eventDispatched(AWTEvent event) {
                    }
                }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                Object[] listData = new Object[30];
                for (int i = 1; i <= listData.length; i++) {
                    if (i == 25) {
                        listData[i - 1] = "List Item - '" + i + "'";
                    } else {
                        listData[i - 1] = "List Item - " + i;
                    }
                }
                list = new JList(listData);
                list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                list.setName("list-1");
                list.setDragEnabled(true);
                JPanel p = new JPanel(new BorderLayout());
                p.add(new JScrollPane(list), BorderLayout.NORTH);
                textfield = new JTextField(80);
                textfield.setName("text-field");
                textfield.setDropMode(DropMode.USE_SELECTION);
                p.add(textfield, BorderLayout.SOUTH);
                frame.getContentPane().add(p);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
                list.requestFocusInWindow();
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

    public void cssSelector() throws Throwable {
        driver = new JavaDriver();
        WebElement list = driver.findElement(By.name("list-1"));
        JSONArray a = new JSONArray();
        for (int i = 1; i <= 30; i++) {
            if (i == 25) {
                a.put("List Item - '" + i + "'");
            } else {
                a.put("List Item - " + i);
            }
        }
        JSONArray b = new JSONArray();
        b.put(a);
        assertEquals(b.toString(), list.getAttribute("content"));
        WebElement listItem;
        List<WebElement> listItems;
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(1)"));
        assertEquals("List Item - 1", listItem.getText());
        listItems = driver.findElements(By.cssSelector("#list-1::all-items"));
        assertEquals(30, listItems.size());
        for (int i = 0; i < 30; i++) {
            if (i == 24) {
                assertEquals("List Item - '" + (i + 1) + "'", listItems.get(i).getText());
            } else {
                assertEquals("List Item - " + (i + 1), listItems.get(i).getText());
            }
        }
        List<WebElement> firstItem = driver.findElements(By.cssSelector("#list-1::all-items[text='List Item - 1']"));
        assertEquals(1, firstItem.size());
        assertEquals("List Item - 1", firstItem.get(0).getText());
    }

    public void listGetAnItemFromListWebelement() throws Throwable {
        driver = new JavaDriver();
        WebElement list = driver.findElement(By.name("list-1"));
        WebElement item21 = list.findElement(By.cssSelector(".::nth-item(21)"));
        assertEquals("List Item - 21", item21.getText());
        List<WebElement> allItems = list.findElements(By.cssSelector(".::all-items"));
        assertEquals(30, allItems.size());
    }

    public void listThrowsNSEWhenIndexOutOfBounds() throws Throwable {
        driver = new JavaDriver();
        try {
            WebElement findElement = driver.findElement(By.cssSelector("#list-1::nth-item(31)"));
            findElement.click();
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        }
    }

    public void listClickingOnAnItemSelectsIt() throws Throwable {
        EventQueueWait.waitTillShown(list);
        driver = new JavaDriver();
        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(21)"));
        assertEquals("List Item - 21", listItem.getText());
        listItem.click();
        assertEquals("20", driver.findElement(By.cssSelector("#list-1")).getAttribute("selectedIndex"));
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(3)"));
        assertEquals("List Item - 3", listItem.getText());
        listItem.click();
        assertEquals("2", driver.findElement(By.cssSelector("#list-1")).getAttribute("selectedIndex"));
    }

    public void unsupportedPseudoElement() throws Throwable {
        driver = new JavaDriver();
        try {
            driver.findElement(By.cssSelector("#list-1::xth-item(21)"));
            throw new MissingException(UnsupportedCommandException.class);
        } catch (UnsupportedCommandException e) {

        }
    }

    public void listClickMovesItemIntoView() throws Throwable {
        driver = new JavaDriver();
        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(21)"));
        listItem.click();
        assertTrue(listItem.isDisplayed());
    }

    public void listMultipleSelect() throws Throwable {
        driver = new JavaDriver();
        WebElement listItem;
        Logger.getLogger(JListXTest.class.getName()).info("List Multiple Select");
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(21)"));
        WebElement listItem2 = driver.findElement(By.cssSelector("#list-1::nth-item(3)"));
        new Actions(driver).click(listItem).keyDown(Keys.SHIFT).click(listItem2).keyUp(Keys.SHIFT).perform();
        ArrayList<Integer> al = new ArrayList<Integer>();
        int[] selectedIndices = EventQueueWait.call(list, "getSelectedIndices");
        for (int i : selectedIndices) {
            al.add(i);
        }
        assertEquals(al.toString(), driver.findElement(By.cssSelector("#list-1")).getAttribute("selectedIndices"));
    }

    public void listMultipleSelectWithControl() throws Throwable {
        driver = new JavaDriver();
        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(6)"));
        WebElement listItem2 = driver.findElement(By.cssSelector("#list-1::nth-item(3)"));
        WebElement listItem3 = driver.findElement(By.cssSelector("#list-1::nth-item(4)"));
        Keys command = Platform.getCurrent().is(Platform.MAC) ? Keys.COMMAND : Keys.CONTROL;
        new Actions(driver).click(listItem).sendKeys(command).click(listItem2).click(listItem3).sendKeys(command).perform();
        ArrayList<Integer> al = new ArrayList<Integer>();
        int[] selectedIndices = new int[] { 2, 3, 5 };
        for (int i : selectedIndices) {
            al.add(i);
        }
        assertEquals(al.toString(), driver.findElement(By.cssSelector("#list-1")).getAttribute("selectedIndices"));
    }

    public void listCheckoutDragDrop() throws InterruptedException {
        driver = new JavaDriver();
        WebElement listItem, textfield;
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(6)"));
        textfield = driver.findElement(By.cssSelector("text-field"));
        new Actions(driver).dragAndDrop(listItem, textfield).perform();
        assertEquals("List Item - 6", textfield.getText());
        Thread.sleep(1000);
    }

    public void allItems() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> items = driver.findElements(By.cssSelector("list::all-items"));
        assertEquals(30, items.size());
    }

    public void listClickingOnAnItemWithQuotesSelectsIt() throws Throwable {
        EventQueueWait.waitTillShown(list);
        driver = new JavaDriver();
        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("#list-1::nth-item(25)"));
        assertEquals("List Item - '25'", listItem.getText());
        listItem.click();
        assertEquals("24", driver.findElement(By.cssSelector("#list-1")).getAttribute("selectedIndex"));
        listItem = driver.findElement(By.cssSelector("#list-1::all-items[text='List Item - \\'25\\'']"));
        assertEquals("List Item - '25'", listItem.getText());
    }

}
