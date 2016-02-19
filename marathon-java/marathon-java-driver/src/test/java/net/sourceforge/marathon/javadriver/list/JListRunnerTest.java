package net.sourceforge.marathon.javadriver.list;

import static org.testng.AssertJUnit.assertTrue;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.testhelpers.MissingException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.ListDialogRunner;

@Test public class JListRunnerTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JListRunnerTest.class.getSimpleName());
                frame.setName("frame-" + JListRunnerTest.class.getSimpleName());
                frame.getContentPane().add(ListDialogRunner.createUI(), BorderLayout.CENTER);
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

    public void getAllListItemsInNameChooserWindow() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(2, labels.size());
        AssertJUnit.assertEquals("The chosen name: Cosmo", labels.get(0).getText() + " " + labels.get(1).getText());

        WebElement pickNameButton = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Pick a new name...", pickNameButton.getText());
        pickNameButton.click();

        String parent = driver.getWindowHandle();
        Set<String> pops = driver.getWindowHandles();
        Iterator<String> it = pops.iterator();

        while (it.hasNext()) {
            String popupHandle = it.next().toString();
            if (!popupHandle.contains(parent)) {
                WebDriver nameChooserWindow = driver.switchTo().window(popupHandle);
                AssertJUnit.assertEquals("Name Chooser", nameChooserWindow.getTitle());
                List<WebElement> list = driver.findElements(By.cssSelector("list"));
                AssertJUnit.assertEquals(1, list.size());

                List<WebElement> listItems;
                listItems = driver.findElements(By.cssSelector("list::all-items"));
                AssertJUnit.assertEquals(12, listItems.size());

                String[] names = { "Arlo", "Cosmo", "Elmo", "Hugo", "Jethro", "Laszlo", "Milo", "Nemo", "Otto", "Ringo", "Rocco",
                        "Rollo" };

                for (int i = 0; i < names.length; i++) {
                    AssertJUnit.assertEquals(names[i], listItems.get(i).getText());
                }
            }
        }

        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        buttons.get(1).click();
        driver.switchTo().window(parent);
        AssertJUnit.assertEquals("The chosen name: Cosmo", labels.get(0).getText() + " " + labels.get(1).getText());
    }

    public void getNthListItem() throws Throwable {
        driver = new JavaDriver();
        String parent = driver.getWindowHandle();
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(2, labels.size());
        AssertJUnit.assertEquals("The chosen name: Cosmo", labels.get(0).getText() + " " + labels.get(1).getText());

        WebElement pickNameButton = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Pick a new name...", pickNameButton.getText());
        pickNameButton.click();

        WebDriver nameChooserWindow = driver.switchTo().window("Name Chooser");
        AssertJUnit.assertEquals("Name Chooser", nameChooserWindow.getTitle());

        List<WebElement> list = driver.findElements(By.cssSelector("list"));
        AssertJUnit.assertEquals(1, list.size());

        List<WebElement> listItems;
        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(12, listItems.size());

        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("list::nth-item(3)"));
        AssertJUnit.assertEquals("Elmo", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("2", list.get(0).getAttribute("selectedIndex"));

        listItem = driver.findElement(By.cssSelector("list::nth-item(6)"));
        AssertJUnit.assertEquals("Laszlo", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("5", list.get(0).getAttribute("selectedIndex"));

        listItem = driver.findElement(By.cssSelector("list::nth-item(9)"));
        AssertJUnit.assertEquals("Otto", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("8", list.get(0).getAttribute("selectedIndex"));

        listItem = driver.findElement(By.cssSelector("list::nth-item(12)"));
        AssertJUnit.assertEquals("Rollo", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("11", list.get(0).getAttribute("selectedIndex"));

        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(2, buttons.size());
        WebElement cancelButton = buttons.get(0);
        AssertJUnit.assertEquals("Cancel", cancelButton.getText());

        WebElement setButton = buttons.get(1);
        AssertJUnit.assertEquals("Set", setButton.getText());
        setButton.click();

        driver.switchTo().window((parent));
        AssertJUnit.assertEquals("The chosen name: Rollo", labels.get(0).getText() + " " + labels.get(1).getText());
    }

    public void getSelectedListItem() throws Throwable {
        driver = new JavaDriver();
        WebElement pickNameButton = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Pick a new name...", pickNameButton.getText());
        pickNameButton.click();

        WebDriver nameChooserWindow = driver.switchTo().window("Name Chooser");
        AssertJUnit.assertEquals("Name Chooser", nameChooserWindow.getTitle());

        List<WebElement> list = driver.findElements(By.cssSelector("list"));
        AssertJUnit.assertEquals(1, list.size());

        List<WebElement> listItems;
        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(12, listItems.size());

        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("list::nth-item(2)"));
        AssertJUnit.assertEquals("Cosmo", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("1", list.get(0).getAttribute("selectedIndex"));
        listItem = driver.findElement(By.cssSelector("list::nth-item(8)"));
        AssertJUnit.assertEquals("Nemo", listItem.getText());
        listItem.click();
        AssertJUnit.assertEquals("7", list.get(0).getAttribute("selectedIndex"));
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        buttons.get(0).click();
    }

    public void isSelectedItemDisplayed() throws Throwable {
        driver = new JavaDriver();

        WebElement pickNameButton = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Pick a new name...", pickNameButton.getText());
        pickNameButton.click();

        WebDriver nameChooserWindow = driver.switchTo().window("Name Chooser");
        AssertJUnit.assertEquals("Name Chooser", nameChooserWindow.getTitle());

        List<WebElement> list = driver.findElements(By.cssSelector("list"));
        AssertJUnit.assertEquals(1, list.size());

        List<WebElement> listItems;
        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(12, listItems.size());
        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("list::nth-item(10)"));
        listItem.click();
        assertTrue(listItem.isDisplayed());
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        buttons.get(1).click();
    }

    public void listThrowsNSEWhenIndexOutOfBounds() throws Throwable {
        driver = new JavaDriver();

        WebElement pickNameButton = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Pick a new name...", pickNameButton.getText());
        pickNameButton.click();

        WebDriver nameChooserWindow = driver.switchTo().window("Name Chooser");
        AssertJUnit.assertEquals("Name Chooser", nameChooserWindow.getTitle());

        List<WebElement> list = driver.findElements(By.cssSelector("list"));
        AssertJUnit.assertEquals(1, list.size());

        List<WebElement> listItems;
        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(12, listItems.size());
        try {
            WebElement findElement = driver.findElement(By.cssSelector("#list::nth-item(20)"));
            findElement.click();
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        } finally {
        }
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        buttons.get(1).click();
    }

    public void listMultipleSelectWithControl() throws Throwable {
        driver = new JavaDriver();

        WebElement pickNameButton = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Pick a new name...", pickNameButton.getText());
        pickNameButton.click();

        WebDriver nameChooserWindow = driver.switchTo().window("Name Chooser");
        AssertJUnit.assertEquals("Name Chooser", nameChooserWindow.getTitle());

        List<WebElement> list = driver.findElements(By.cssSelector("list"));
        AssertJUnit.assertEquals(1, list.size());

        List<WebElement> listItems;
        listItems = driver.findElements(By.cssSelector("list::all-items"));
        AssertJUnit.assertEquals(12, listItems.size());

        WebElement listItem;
        listItem = driver.findElement(By.cssSelector("list::nth-item(1)"));
        WebElement listItem2 = driver.findElement(By.cssSelector("list::nth-item(3)"));
        new Actions(driver).click(listItem).sendKeys(Keys.COMMAND).click(listItem2).sendKeys(Keys.COMMAND).perform();
        AssertJUnit.assertEquals("[2]", list.get(0).getAttribute("selectedIndices"));
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        buttons.get(1).click();
        Thread.sleep(1000);
    }
}
