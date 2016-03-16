package net.sourceforge.marathon.javadriver.menu;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

import components.MenuDemo;

@Test public class JMenuTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JMenuTest.class.getSimpleName());
                frame.setName("frame-" + JMenuTest.class.getSimpleName());
                MenuDemo demo = new MenuDemo();
                frame.setJMenuBar(demo.createMenuBar());
                frame.setContentPane(demo.createContentPane());
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        MenuSelectionManager.defaultManager().clearSelectedPath();
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
        MenuSelectionManager.defaultManager().clearSelectedPath();
    }

    public void getTextOnMenu() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));
        WebElement menu = menus.get(0);
        AssertJUnit.assertEquals("A Menu", menu.getText());
    }

    public void menuItems() throws Throwable {
        driver = new JavaDriver();
        WebElement menu = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("A Menu", menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu-item"));
        int i = 0;
        assertMenuItem(menuItems, i++, "A text-only menu item", "", "84");
        assertMenuItem(menuItems, i++, "Both text and icon", "middle.gif", "66");
        assertMenuItem(menuItems, i++, "middle", "middle.gif", "68");

        /*
         * NOTE: Clicking on the menu again to ensure that MenuManager sets the
         * state of this menu to closed. Without this call the state of the menu
         * remains open and this test or the next test which clicks on the same
         * menu and perform any actions or request attributes will fail. This
         * behavior is noticed on OSX
         */
        new Actions(driver).moveToElement(menu).click().perform();
    }

    public void menuItemClicks() throws Throwable {
        driver = new JavaDriver();
        WebElement menu = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("A Menu", menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu-item"));
        int i = 0;
        assertMenuItemClick(menuItems, i++, "A text-only menu item (an instance of JMenuItem)\n");
        new Actions(driver).moveToElement(menu).click().perform();
        assertMenuItemClick(menuItems, i++, "Both text and icon (an instance of JMenuItem)\n");
        new Actions(driver).moveToElement(menu).click().perform();
        assertMenuItemClick(menuItems, i++, "Event source:  (an instance of JMenuItem)\n");
    }

    public void mCheckBoxMenuItems() throws Throwable {
        driver = new JavaDriver();
        WebElement menu = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("A Menu", menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        List<WebElement> menuItems = driver.findElements(By.cssSelector("check-box-menu-item"));
        int i = 0;
        assertMenuItem(menuItems, i++, "A check box menu item", "", "67");
        assertMenuItem(menuItems, i++, "Another one", "", "72");

        /*
         * NOTE: Clicking on the menu again to ensure that MenuManager sets the
         * state of this menu to closed. Without this call the state of the menu
         * remains open and this test or the next test which clicks on the same
         * menu and perform any actions or request attributes will fail. This
         * behavior is noticed on OSX
         */
        new Actions(driver).moveToElement(menu).click().perform();
    }

    public void checkBoxMenuItemClick() throws Throwable {
        driver = new JavaDriver();
        WebElement menu = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("A Menu", menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        List<WebElement> menuItems = driver.findElements(By.cssSelector("check-box-menu-item"));
        int i = menuItems.size();
        assertMenuItemClick(menuItems, --i,
                "Event source: Another one (an instance of JCheckBoxMenuItem)\n    New state: selected\n");
        WebElement currRadioButton = menuItems.get(i);
        AssertJUnit.assertEquals("true", currRadioButton.getAttribute("selected"));

        new Actions(driver).moveToElement(menu).click().perform();
        assertMenuItemClick(menuItems, --i,
                "Event source: A check box menu item (an instance of JCheckBoxMenuItem)\n    New state: selected\n");
        currRadioButton = menuItems.get(i);
        AssertJUnit.assertEquals("true", currRadioButton.getAttribute("selected"));
    }

    public void radioButtonMenuItems() throws Throwable {
        driver = new JavaDriver();
        WebElement menu = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("A Menu", menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return driver.findElements(By.cssSelector("radio-button-menu-item")).size() > 0;
            }
        });
        List<WebElement> menuItems = driver.findElements(By.cssSelector("radio-button-menu-item"));
        int i = 0;
        assertMenuItem(menuItems, i++, "A radio button menu item", "", "82");
        assertMenuItem(menuItems, i++, "Another one", "", "79");

        /*
         * NOTE: Clicking on the menu again to ensure that MenuManager sets the
         * state of this menu to closed. Without this call the state of the menu
         * remains open and this test or the next test which clicks on the same
         * menu and perform any actions or request attributes will fail. This
         * behavior is noticed on OSX
         */
        new Actions(driver).moveToElement(menu).click().perform();
    }

    public void radioButtonMenuItemClick() throws Throwable {
        driver = new JavaDriver();
        WebElement menu = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("A Menu", menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        List<WebElement> menuItems = driver.findElements(By.cssSelector("radio-button-menu-item"));
        int i = menuItems.size();
        assertMenuItemClick(menuItems, --i, "Another one (an instance of JRadioButtonMenuItem)\n");
        WebElement currRadioButton = menuItems.get(i);
        AssertJUnit.assertEquals("true", currRadioButton.getAttribute("selected"));
        new Actions(driver).moveToElement(menu).click().perform();
        assertMenuItemClick(menuItems, --i, "A radio button menu item (an instance of JRadioButtonMenuItem)\n");
        currRadioButton = menuItems.get(i);
        new Actions(driver).moveToElement(menu).click().perform();
        AssertJUnit.assertEquals("true", currRadioButton.getAttribute("selected"));
    }

    public void subMenu() throws Throwable {
        driver = new JavaDriver();
        WebElement menu = driver.findElement(By.cssSelector("menu"));

        AssertJUnit.assertEquals("A Menu", menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu"));
        int i = 2;
        assertMenuItem(menuItems, i++, "A submenu", "", "83");

        /*
         * NOTE: Clicking on the menu again to ensure that MenuManager sets the
         * state of this menu to closed. Without this call the state of the menu
         * remains open and this test or the next test which clicks on the same
         * menu and perform any actions or request attributes will fail. This
         * behavior is noticed on OSX
         */
        new Actions(driver).moveToElement(menu).click().perform();
    }

    public void subMenuClicks() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertEquals(2, menus.size());

        new Actions(driver).moveToElement(menus.get(0)).click().perform();
        menus = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertEquals(3, menus.size());

        new Actions(driver).moveToElement(menus.get(2)).click().perform();

        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                List<WebElement> menus = driver.findElements(By.cssSelector("menu-item"));
                return menus.size() == 5;
            }
        });

        menus = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(5, menus.size());

        int i = 3;
        assertMenuItem(menus, i++, "An item in the submenu", "", "0");
        assertMenuItem(menus, i++, "Another item", "", "0");

        i = 3;
        assertMenuItemClick(menus, i++, "An item in the submenu (an instance of JMenuItem)\n");

    }

    public void subMenuClicks2() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertEquals(2, menus.size());

        new Actions(driver).moveToElement(menus.get(0)).click().perform();
        menus = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertEquals(3, menus.size());

        new Actions(driver).moveToElement(menus.get(2)).click().perform();

        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                List<WebElement> menus = driver.findElements(By.cssSelector("menu-item"));
                return menus.size() == 5;
            }
        });

        menus = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(5, menus.size());

        int i = 3;
        assertMenuItem(menus, i++, "An item in the submenu", "", "0");
        assertMenuItem(menus, i++, "Another item", "", "0");

        i = 4;
        assertMenuItemClick(menus, i++, "Another item (an instance of JMenuItem)\n");

    }

    private void assertMenuItem(List<WebElement> menuItems, int i, String text, String iconFileName, String mnemonic) {
        WebElement menuItem = menuItems.get(i);
        AssertJUnit.assertEquals(text, menuItem.getText());
        if ("".equals(iconFileName))
            AssertJUnit.assertNull(menuItem.getAttribute("icon"));
        else
            AssertJUnit.assertTrue("Icon mismatch", menuItem.getAttribute("icon").endsWith(iconFileName));
        AssertJUnit.assertEquals("Mnemonic mismatch", mnemonic, menuItem.getAttribute("mnemonic"));
    }

    private void assertMenuItemClick(List<WebElement> menuItems, int i, String textAfterClick) throws Throwable {
        new Actions(driver).moveToElement(menuItems.get(i)).click().perform();
        WebElement textArea = driver.findElement(By.cssSelector("text-area"));
        AssertJUnit.assertTrue("Error:Expected to end with:\n" + textAfterClick + "\nActual:\n" + textArea.getText().trim(),
                textArea.getText().trim().endsWith(textAfterClick.trim()));

    }

    public void menuItemShortcuts() throws Throwable {
        // This test for testing keyboard shortcuts on menu items need to be
        // fixed after the bug fix in send keys.
        driver = new JavaDriver();
        WebElement textArea = driver.findElement(By.cssSelector("text-area"));
        textArea.click();

        textArea.sendKeys(Keys.chord(Keys.ALT, "1"));
        AssertJUnit.assertTrue(
                "Error:Expected to end with:\n" + "Event source: A text-only menu item (an instance of JMenuItem)\n" + "\nActual:\n"
                        + textArea.getText().trim(),
                textArea.getText().trim().endsWith("Event source: A text-only menu item (an instance of JMenuItem)\n".trim()));
        textArea.sendKeys(Keys.chord(Keys.ALT, "2"));
        AssertJUnit.assertTrue(
                "Error:Expected to end with:\n" + "Event source: A text-only menu item (an instance of JMenuItem)\n" + "\nActual:\n"
                        + textArea.getText().trim(),
                textArea.getText().trim().endsWith("Event source: An item in the submenu (an instance of JMenuItem)\n".trim()));
    }

}
