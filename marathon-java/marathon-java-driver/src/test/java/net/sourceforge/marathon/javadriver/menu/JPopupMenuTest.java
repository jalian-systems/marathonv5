/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javadriver.menu;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.PopupMenuDemo;

@Test public class JPopupMenuTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JPopupMenuTest.class.getSimpleName());
                frame.setName("frame-" + JPopupMenuTest.class.getSimpleName());
                PopupMenuDemo demo = new PopupMenuDemo();
                frame.setJMenuBar(demo.createMenuBar());
                frame.setContentPane(demo.createContentPane());

                // Create and set up the popup menu.
                demo.createPopupMenu();

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
        EventQueueWait.waitTillInvisibled(frame);
    }

    public void getText() throws Throwable {
        driver = new JavaDriver();
        WebElement panel = driver.findElement(By.cssSelector("text-area"));
        AssertJUnit.assertNotNull(panel);
        new Actions(driver).contextClick(panel).perform();
        WebElement menu = driver.findElement(By.cssSelector("popup-menu"));
        AssertJUnit.assertNotNull(menu);
        List<WebElement> items = menu.findElements(By.cssSelector("menu-item"));
        AssertJUnit.assertEquals(2, items.size());
        AssertJUnit.assertEquals("A popup menu item", items.get(0).getText());
        AssertJUnit.assertEquals("Another popup menu item", items.get(1).getText());
        items.get(1).click();
        AssertJUnit.assertEquals(true, panel.getText().contains("Another popup menu"));
    }
}
