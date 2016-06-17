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
package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.MenuDemo;
import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.javaagent.Device;
import net.sourceforge.marathon.javaagent.IDevice;
import net.sourceforge.marathon.javaagent.IDevice.Buttons;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

@Test public class RMenuItemTest extends RComponentTest {

    protected JFrame frame;
    private List<Component> menus;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                MenuSelectionManager.defaultManager().clearSelectedPath();
                frame = new JFrame(RMenuItemTest.class.getSimpleName());
                frame.setName("frame-" + RMenuItemTest.class.getSimpleName());
                MenuDemo demo = new MenuDemo();
                frame.setJMenuBar(demo.createMenuBar());
                frame.setContentPane(demo.createContentPane());
                frame.pack();
                frame.setVisible(true);
            }
        });
        menus = ComponentUtils.findComponents(JMenu.class, frame);
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void selectMenuWithNoItems() {
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                JMenu AnotherMenu = (JMenu) menus.get(1);
                AnotherMenu.doClick();
                RMenuItem rmenu = new RMenuItem(AnotherMenu, null, null, lr);
                rmenu.mouseButton1Pressed(null);
            }
        });
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select_menu", call.getFunction());
        AssertJUnit.assertEquals("Another Menu", call.getState());
    }

    public void selectMenuItem() {
        final LoggingRecorder lr = new LoggingRecorder();
        final List<JMenuItem> items = new ArrayList<JMenuItem>();
        siw(new Runnable() {
            @Override public void run() {
                JMenu AMenu = (JMenu) menus.get(0);
                AMenu.doClick();
                items.addAll(getMenuComponents(AMenu));
            }
        });
        items.get(1).addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                RMenuItem rMenuItem = new RMenuItem(items.get(1), null, null, lr);
                rMenuItem.mouseButton1Pressed(e);
            }
        });
        IDevice d = Device.getDevice();
        d.click(items.get(1), Buttons.LEFT, 1, 5, 5);
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select_menu", call.getFunction());
        AssertJUnit.assertEquals("A Menu>>Both text and icon", call.getState());
    }

    public void selectMenuItemWithOnlyIcon() {
        final LoggingRecorder lr = new LoggingRecorder();
        final List<JMenuItem> items = new ArrayList<JMenuItem>();
        siw(new Runnable() {
            @Override public void run() {
                JMenu AMenu = (JMenu) menus.get(0);
                AMenu.doClick();
                items.addAll(getMenuComponents(AMenu));
            }
        });
        items.get(2).addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                RMenuItem rMenuItem = new RMenuItem(items.get(2), null, null, lr);
                rMenuItem.mouseButton1Pressed(e);
            }
        });
        IDevice d = Device.getDevice();
        d.click(items.get(2), Buttons.LEFT, 1, 5, 5);
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select_menu", call.getFunction());
        AssertJUnit.assertEquals("A Menu>>middle", call.getState());
    }

    public void selectSubMenuItem() {
        final LoggingRecorder lr = new LoggingRecorder();
        final List<JMenuItem> items = new ArrayList<JMenuItem>();
        final List<JMenuItem> subMenuItems = new ArrayList<JMenuItem>();
        siw(new Runnable() {

            @Override public void run() {
                JMenu AMenu = (JMenu) menus.get(0);
                AMenu.doClick();
                items.addAll(getMenuComponents(AMenu));
                JMenu subMenu = (JMenu) items.get(7);
                subMenu.doClick();
                subMenuItems.addAll(getMenuComponents(subMenu));
            }
        });
        subMenuItems.get(1).addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                RMenuItem rMenuItem = new RMenuItem(subMenuItems.get(1), null, null, lr);
                rMenuItem.mouseButton1Pressed(e);
            }
        });
        IDevice d = Device.getDevice();
        d.click(subMenuItems.get(1), Buttons.LEFT, 1, 5, 5);
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select_menu", call.getFunction());
        AssertJUnit.assertEquals("A Menu>>A submenu>>Another item", call.getState());
    }

    public void duplicateMenuItem() throws InterruptedException {
        final LoggingRecorder lr = new LoggingRecorder();
        final List<JMenuItem> items = new ArrayList<JMenuItem>();
        siw(new Runnable() {
            @Override public void run() {
                JMenu AMenu = (JMenu) menus.get(0);
                AMenu.doClick();
                items.addAll(getMenuComponents(AMenu));
            }
        });
        items.get(4).addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                RMenuItem rMenuItem = new RMenuItem(items.get(4), null, null, lr);
                rMenuItem.mouseButton1Pressed(e);
            }
        });
        IDevice d = Device.getDevice();
        d.click(items.get(4), Buttons.LEFT, 1, 5, 5);
        Call call = lr.getCall();
        AssertJUnit.assertEquals("select_menu", call.getFunction());
        AssertJUnit.assertEquals("A Menu>>Another one", call.getState());

        siw(new Runnable() {
            @Override public void run() {
                JMenu AMenu = (JMenu) menus.get(0);
                AMenu.doClick();
                items.addAll(getMenuComponents(AMenu));
            }
        });
        items.get(6).addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                RMenuItem rMenuItem2 = new RMenuItem(items.get(6), null, null, lr);
                rMenuItem2.mouseButton1Pressed(e);
            }
        });

        IDevice d2 = Device.getDevice();
        d2.click(items.get(6), Buttons.LEFT, 1, 5, 5);
        Call calll = lr.getCall();
        AssertJUnit.assertEquals("select_menu", calll.getFunction());
        AssertJUnit.assertEquals("A Menu>>Another one(1)", calll.getState());
    }

    private List<JMenuItem> getMenuComponents(JMenu AMenu) {
        Component[] components = AMenu.getMenuComponents();
        List<JMenuItem> items = new ArrayList<JMenuItem>();
        for (int j = 0; j < components.length; j++) {
            if (!(components[j] instanceof AbstractButton))
                continue;
            items.add((JMenuItem) components[j]);
        }
        return items;
    }

}
