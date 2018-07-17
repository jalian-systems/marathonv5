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

import components.MenuDemo;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;

@Test
public class JMenuItemJavaElementTest extends JavaElementTest {
    private IJavaAgent driver;
    protected JFrame frame;
    private List<IJavaElement> menus;
    private IJavaElement AMenu;

    @BeforeMethod
    public void showDialog() throws Throwable {
        JavaElementFactory.add(JMenuItem.class, JMenuItemJavaElement.class);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame(JMenuItemJavaElementTest.class.getSimpleName());
                frame.setName("frame-" + JMenuItemJavaElementTest.class.getSimpleName());
                MenuDemo demo = new MenuDemo();
                frame.setJMenuBar(demo.createMenuBar());
                frame.setContentPane(demo.createContentPane());
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        MenuSelectionManager.defaultManager().clearSelectedPath();
        driver = new JavaAgent();
        menus = driver.findElementsByTagName("menu");
        AMenu = menus.get(0);

    }

    @AfterMethod
    public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void selectMenuItem() throws InterruptedException {
        driver.setImplicitWait(30000);
        marathon_select(AMenu, "A Menu>>A text-only menu item");
        assertMenuItemClick("A text-only menu item (an instance of JMenuItem)");
        marathon_select(AMenu, "A Menu>>Both text and icon");
        assertMenuItemClick("Both text and icon (an instance of JMenuItem)");
    }

    public void selectMenuItemHavingOnlyIcon() throws InterruptedException {
        driver.setImplicitWait(30000);
        marathon_select(AMenu, "A Menu>>middle");
        assertMenuItemClick("(an instance of JMenuItem)");
    }

    public void select_radio_and_checkbox_menu_items() throws InterruptedException {
        driver.setImplicitWait(30000);
        marathon_select(AMenu, "A Menu>>A radio button menu item");
        assertMenuItemClick("A radio button menu item (an instance of JRadioButtonMenuItem)");
    }

    public void selectSubMenu() throws InterruptedException {
        driver.setImplicitWait(30000);
        marathon_select(AMenu, "A Menu>>A submenu>>An item in the submenu");
        assertMenuItemClick("An item in the submenu (an instance of JMenuItem)");
        marathon_select(AMenu, "A Menu>>A submenu>>Another item");
        assertMenuItemClick("Another item (an instance of JMenuItem)");
    }

    public void findElements() throws InterruptedException {
        driver.setImplicitWait(30000);
        driver.switchTo().window("JMenuItemJavaElementTest");
        IJavaElement element = driver
                .findElementByCssSelector("[type='JMenu'][actionCommand='A Menu'][visible='true'][showing='true']");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals("A Menu", element.getText());

    }

    public void duplicateMenuItem() throws InterruptedException {
        driver.setImplicitWait(30000);
        marathon_select(AMenu, "A Menu>>Another one");
        assertMenuItemClick("Another one (an instance of JRadioButtonMenuItem)");

        marathon_select(AMenu, "A Menu>>Another one(1)");
        assertMenuItemClick("Another one (an instance of JCheckBoxMenuItem)\n    New state: selected");
    }

    private void assertMenuItemClick(String textAfterClick) {
        IJavaElement textArea = driver.findElementByTagName("text-area");
        AssertJUnit.assertTrue("Error:Expected to end with:\n" + textAfterClick + "\nActual:\n" + textArea.getText().trim(),
                textArea.getText().trim().endsWith(textAfterClick.trim()));
    }

}
