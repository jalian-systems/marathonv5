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
package net.sourceforge.marathon.javaagent.components;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.PopupMenuDemoX;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;

@Test public class JMenuItemJavaElement2Test extends JavaElementTest {
    private IJavaAgent driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JMenuItemJavaElement2Test.class.getSimpleName());
                frame.setName("frame-" + JMenuItemJavaElement2Test.class.getSimpleName());
                PopupMenuDemoX demo = new PopupMenuDemoX();
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
        JavaElementFactory.add(JMenuItem.class, JMenuItemJavaElement.class);
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        EventQueueWait.waitTillInvisibled(frame);
    }

    public void selectPopupMenuItem1() throws InterruptedException {
        driver = new JavaAgent();
        driver.setImplicitWait(30000);
        IJavaElement textArea = driver.findElementByTagName("text-area");
        textArea.click(2, 1, 10, 10);
        AssertJUnit.assertEquals(1, driver.getWindowHandles().size());
        List<IJavaElement> popups = driver.findElementsByTagName("popup-menu");
        AssertJUnit.assertEquals(1, popups.size());
        IJavaElement popup = popups.get(0);
        List<IJavaElement> menus = popup.findElementsByTagName("menu");
        IJavaElement menu = null;
        for (IJavaElement je : menus) {
            if ("A Popup Menu".equals(je.getAttribute("text"))) {
                menu = je;
                break;
            }
        }
        AssertJUnit.assertEquals("A Popup Menu", menu.getAttribute("text"));
        marathon_select(menu, "A Popup Menu>>A sub menu item");
        assertMenuItemClick("A sub menu item (an instance of JMenuItem)");
    }

    public void selectPopupMenuItem2() throws InterruptedException {
        driver = new JavaAgent();
        driver.setImplicitWait(30000);
        IJavaElement textArea = driver.findElementByTagName("text-area");
        textArea.click(2, 1, 10, 10);
        AssertJUnit.assertEquals(1, driver.getWindowHandles().size());
        List<IJavaElement> popups = driver.findElementsByTagName("popup-menu");
        AssertJUnit.assertEquals(1, popups.size());
        IJavaElement popup = popups.get(0);
        List<IJavaElement> menus = popup.findElementsByTagName("menu");
        IJavaElement menu = null;
        for (IJavaElement je : menus) {
            if ("A Popup Menu".equals(je.getAttribute("text"))) {
                menu = je;
                break;
            }
        }
        AssertJUnit.assertEquals("A Popup Menu", menu.getAttribute("text"));
        marathon_select(menu, "A Popup Menu>>Another sub menu item");
        assertMenuItemClick("Another sub menu item (an instance of JMenuItem)");
    }

    public void selectPopupMenuItem3() throws InterruptedException {
        driver = new JavaAgent();
        driver.setImplicitWait(30000);
        IJavaElement textArea = driver.findElementByTagName("text-area");
        textArea.click(2, 1, 10, 10);
        AssertJUnit.assertEquals(1, driver.getWindowHandles().size());
        List<IJavaElement> popups = driver.findElementsByTagName("popup-menu");
        AssertJUnit.assertEquals(1, popups.size());
        IJavaElement popup = popups.get(0);
        List<IJavaElement> menuItems = popup.findElementsByTagName("menu-item");
        IJavaElement menuItem = null;
        for (IJavaElement je : menuItems) {
            if ("A popup menu item".equals(je.getAttribute("text"))) {
                menuItem = je;
                break;
            }
        }
        AssertJUnit.assertEquals("A popup menu item", menuItem.getAttribute("text"));
        marathon_select(menuItem, "A popup menu item");
        assertMenuItemClick("A popup menu item (an instance of JMenuItem)");
    }

    public void selectPopupMenuItem4() throws InterruptedException {
        driver = new JavaAgent();
        driver.setImplicitWait(30000);
        IJavaElement textArea = driver.findElementByTagName("text-area");
        textArea.click(2, 1, 10, 10);
        AssertJUnit.assertEquals(1, driver.getWindowHandles().size());
        List<IJavaElement> popups = driver.findElementsByTagName("popup-menu");
        AssertJUnit.assertEquals(1, popups.size());
        IJavaElement popup = popups.get(0);
        List<IJavaElement> menuItems = popup.findElementsByTagName("menu-item");
        IJavaElement menuItem = null;
        for (IJavaElement je : menuItems) {
            if ("Another popup menu item".equals(je.getAttribute("text"))) {
                menuItem = je;
                break;
            }
        }
        AssertJUnit.assertEquals("Another popup menu item", menuItem.getAttribute("text"));
        marathon_select(menuItem, "Another popup menu item");
        assertMenuItemClick("Another popup menu item (an instance of JMenuItem)");
    }

    private void assertMenuItemClick(String textAfterClick) {
        IJavaElement textArea = driver.findElementByTagName("text-area");
        AssertJUnit.assertTrue("Error:Expected to end with:\n" + textAfterClick + "\nActual:\n" + textArea.getText().trim(),
                textArea.getText().trim().endsWith(textAfterClick.trim()));
    }
}
