package net.sourceforge.marathon.javadriver.menu;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.NoSuchWindowException;
import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import components.MenuLayoutDemo;

@Test public class JMenuHorizontalTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws InterruptedException, InvocationTargetException, NoSuchWindowException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JMenuHorizontalTest.class.getSimpleName());
                frame.setName("frame-" + JMenuHorizontalTest.class.getSimpleName());
                MenuLayoutDemo demo = new MenuLayoutDemo();
                frame.setContentPane(demo.createMenuBar());
                frame.pack();
                frame.setVisible(true);
                frame.setAlwaysOnTop(true);
                frame.requestFocus();
            }
        });
    }

    @AfterMethod public void disposeDriver() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        if (driver != null)
            driver.quit();
    }

    public void getTextOnMenu() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));

        AssertJUnit.assertEquals(3, menus.size());

        int i = 0;
        assertMenu(menus, i++, "Menu 1");
        assertMenu(menus, i++, "Menu 2");
        assertMenu(menus, i++, "Menu 3");
    }

    private void assertMenu(List<WebElement> menus, int i, String menuText) {
        WebElement menu = menus.get(i);
        AssertJUnit.assertEquals(menuText, menu.getText());
    }

    public void clicksOnMenus() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));
        int i = 0;
        clicksOnMenu(menus, i++, "Menu 1");
        clicksOnMenu(menus, i++, "Menu 2");
        clicksOnMenu(menus, i++, "Menu 3");
    }

    private void clicksOnMenu(List<WebElement> menus, int index, String menuText) {
        WebElement menu = menus.get(index);
        AssertJUnit.assertEquals(menuText, menu.getText());
        new Actions(driver).moveToElement(menu).click().perform();

        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return driver.findElements(By.cssSelector("menu-item")).size() == 3;
            }
        });
        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(3, menuItems.size());
        int i = 0;
        assertMenuItem(menuItems, i++, "Menu item #1 in " + menuText);
        assertMenuItem(menuItems, i++, "Menu item #2 in " + menuText);
        assertMenuItem(menuItems, i++, "Menu item #3 in " + menuText);

        /*
         * NOTE: Clicking on the menu again to ensure that MenuManager sets the
         * state of this menu to closed. Without this call the state of the menu
         * remains open and this test or the next test which clicks on the same
         * menu and perform any actions or request attributes will fail. This
         * behavior is noticed on OSX
         */
        new Actions(driver).moveToElement(menu).click().perform();
    }

    private void assertMenuItem(List<WebElement> menuItems, int i, String text) {
        WebElement menuItem = menuItems.get(i);
        AssertJUnit.assertEquals(text, menuItem.getText());
    }

    public void clickOnMenuItemsInMenu() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));

        AssertJUnit.assertEquals(3, menus.size());
        for (WebElement menu : menus) {
            assertClicksOnMenuItemsIn(menu);
        }

    }

    private void assertClicksOnMenuItemsIn(WebElement menu) throws Throwable {
        menu.click();
        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(3, menuItems.size());
        menu.click();
        for (WebElement menuItem : menuItems) {
            performClickOnMenuItemInMenu(menu, menuItem, 3, 0);
        }
    }

    private void performClickOnMenuItemInMenu(WebElement menu, WebElement menuItem, int itemsB4Click, int itemsAfterClick)
            throws InterruptedException {
        menu.click();
        List<WebElement> menuItemsBeforeClick = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(itemsB4Click, menuItemsBeforeClick.size());
        menuItem.click();
        List<WebElement> menuItemsAfterClick = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(itemsAfterClick, menuItemsAfterClick.size());
    }

    public void subMenu() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));
        int i = 0;
        assertSubMenu(menus, i++);
        assertSubMenu(menus, i++);
        // assertSubMenu(menus, i++);
    }

    private void assertSubMenu(List<WebElement> menus, int i) throws InterruptedException {
        WebElement menu = menus.get(i);
        menu.click();
        List<WebElement> includeSubMenus = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertEquals(4, includeSubMenus.size());
        WebElement subMenu = includeSubMenus.get(3);
        AssertJUnit.assertEquals("Submenu", subMenu.getText());

        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(3, menuItems.size());

        subMenu.click();
        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return driver.findElements(By.cssSelector("menu-item")).size() == 5;
            }
        });
        menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(5, menuItems.size());
        menuItems.get(4).click();
    }

    public void menuItemsInSubMenu() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));

        AssertJUnit.assertEquals(3, menus.size());
        assertOnMenuItemsInSubMenu(menus.get(0));
        assertOnMenuItemsInSubMenu(menus.get(1));
        // assertOnMenuItemsInSubMenu(menus.get(2));
    }

    private void assertOnMenuItemsInSubMenu(WebElement menu) throws InterruptedException {
        menu.click();
        List<WebElement> includeSubMenus = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertEquals(4, includeSubMenus.size());
        WebElement subMenu = includeSubMenus.get(3);
        AssertJUnit.assertEquals("Submenu", subMenu.getText());

        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(3, menuItems.size());

        subMenu.click();
        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return driver.findElements(By.cssSelector("menu-item")).size() == 5;
            }
        });
        menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(5, menuItems.size());
        AssertJUnit.assertEquals("Submenu item #1", menuItems.get(3).getText());
        AssertJUnit.assertEquals("Submenu item #2", menuItems.get(4).getText());
        menu.click();
    }

    public void clicksOnMenuItemsInSubMenu() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> menus = driver.findElements(By.cssSelector("menu"));

        AssertJUnit.assertEquals(3, menus.size());
        assertClicksOnMenuItemsInSubMenu(menus.get(0));
        assertClicksOnMenuItemsInSubMenu(menus.get(1));
        // assertClicksOnMenuItemsInSubMenu(menus.get(2));

    }

    private void assertClicksOnMenuItemsInSubMenu(WebElement menu) throws Throwable {
        menu.click();
        List<WebElement> includeSubMenus = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertEquals(4, includeSubMenus.size());
        WebElement subMenu = includeSubMenus.get(3);
        AssertJUnit.assertEquals("Submenu", subMenu.getText());

        List<WebElement> menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(3, menuItems.size());
        menu.click();

        assertClicks(menu, subMenu, 3);
    }

    private void assertClicks(WebElement menu, WebElement subMenu, final int initialMenuItemsSize) throws InterruptedException {
        List<WebElement> menuItems;
        menu.click();
        subMenu.click();
        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                return driver.findElements(By.cssSelector("menu-item")).size() == initialMenuItemsSize + 2;
            }
        });
        menuItems = driver.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(initialMenuItemsSize + 2, menuItems.size());
        menu.click();
        for (int i = initialMenuItemsSize; i < menuItems.size(); i++) {
            menu.click();
            subMenu.click();
            menuItems.get(i).click();
            List<WebElement> menuItemsAfterClick = driver.findElements(By.cssSelector("menu-item"));
            AssertJUnit.assertEquals(0, menuItemsAfterClick.size());
        }
    }
}
