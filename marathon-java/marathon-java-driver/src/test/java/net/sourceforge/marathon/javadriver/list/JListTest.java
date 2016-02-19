package net.sourceforge.marathon.javadriver.list;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.testhelpers.MissingException;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.ListDemo;

@Test public class JListTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JListTest.class.getSimpleName());
                frame.setName("frame-" + JListTest.class.getSimpleName());
                frame.getContentPane().add(new ListDemo(), BorderLayout.CENTER);
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

    public void getAllListItems() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> list = driver.findElements(By.cssSelector("list"));
        AssertJUnit.assertEquals(1, list.size());
        List<WebElement> listItems;
        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(3, listItems.size());
        String[] expectedItems = { "Jane Doe", "John Smith", "Kathy Green" };
        for (int i = 0; i < expectedItems.length; i++) {
            assertEquals(expectedItems[i], listItems.get(i).getText());
        }
    }

    public void getNthListItem() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> listItems;
        WebElement list = driver.findElement(By.cssSelector("list"));
        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(3, listItems.size());

        WebElement listItem1 = driver.findElement(By.cssSelector("list::nth-item(1)"));
        WebElement listItem2 = driver.findElement(By.cssSelector("list::nth-item(2)"));
        WebElement listItem3 = driver.findElement(By.cssSelector("list::nth-item(3)"));

        AssertJUnit.assertEquals("Jane Doe", listItem1.getText());
        AssertJUnit.assertEquals("John Smith", listItem2.getText());
        AssertJUnit.assertEquals("Kathy Green", listItem3.getText());

        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(2, buttons.size());
        WebElement fireButton = buttons.get(0);
        WebElement hireButton = buttons.get(1);

        AssertJUnit.assertEquals("Fire", fireButton.getText());
        AssertJUnit.assertEquals("Hire", hireButton.getText());

        List<WebElement> textField = driver.findElements(By.cssSelector("text-field"));
        AssertJUnit.assertEquals(1, textField.size());

        textField.get(0).sendKeys("James Miller");
        hireButton.click();

        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(4, listItems.size());

        AssertJUnit.assertEquals("true", fireButton.getAttribute("enabled"));
        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("list::nth-item(2)"));
        AssertJUnit.assertEquals("James Miller", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("1", list.getAttribute("selectedIndex"));
        fireButton.click();

        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(3, listItems.size());
    }

    public void getSelectedListItem() throws Throwable {
        driver = new JavaDriver();
        WebElement listItem;
        WebElement list = driver.findElement(By.cssSelector("list"));
        listItem = driver.findElement(By.cssSelector("list::nth-item(2)"));
        AssertJUnit.assertEquals("John Smith", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("1", list.getAttribute("selectedIndex"));
        listItem = driver.findElement(By.cssSelector("list::nth-item(3)"));
        AssertJUnit.assertEquals("Kathy Green", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("2", list.getAttribute("selectedIndex"));
    }

    public void isSelectedItemDisplayed() throws Throwable {
        driver = new JavaDriver();
        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("list::nth-item(2)"));
        listItem.click();
        assertTrue(listItem.isDisplayed());
    }

    public void indexOutOfBoundException() throws Throwable {
        driver = new JavaDriver();
        try {
            WebElement findElement = driver.findElement(By.cssSelector("list::nth-item(6)"));
            findElement.click();
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        }
    }

}
