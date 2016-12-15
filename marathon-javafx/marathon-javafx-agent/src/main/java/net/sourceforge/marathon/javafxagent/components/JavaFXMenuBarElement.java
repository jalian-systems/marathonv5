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
package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXMenuBarElement extends JavaFXElement {

    public JavaFXMenuBarElement(MenuBar node, IJavaFXAgent driver, JFXWindow window) {
        super(node, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        MenuBar menuBar = (MenuBar) node;
        ObservableList<Menu> menus = menuBar.getMenus();
        String[] items = value.split("\\>\\>");
        Menu parentMenu = getParentMenu(menus, items[0]);
        List<MenuItem> menuItems = new ArrayList<>();
        for (int i = 1; i < items.length; i++) {
            getChidernMenuItem(parentMenu, items[i], menuItems);
        }
        parentMenu.fire();
        menuItems.stream().forEach((menu) -> {
            if (menu instanceof CheckMenuItem) {
                CheckMenuItem checkMenuItem = (CheckMenuItem) menu;
                checkMenuItem.setSelected(!checkMenuItem.isSelected());
            } else if (menu instanceof RadioMenuItem) {
                RadioMenuItem radioMenuItem = (RadioMenuItem) menu;
                radioMenuItem.setSelected(!isSelected());
            }
            menu.fire();
        });
        return true;
    }

    private void getChidernMenuItem(Menu parentMenu, String items, List<MenuItem> menuItems) {
        ObservableList<MenuItem> children = parentMenu.getItems();
        for (MenuItem menuItem : children) {
            if (menuItem instanceof Menu) {
                getChidernMenuItem((Menu) menuItem, items, menuItems);
            }
            if (!(menuItem instanceof SeparatorMenuItem) && getTextForMenuItem(menuItem, menuItem.getParentMenu()).equals(items)) {
                menuItems.add(menuItem);
                break;
            }
        }
    }

    private Menu getParentMenu(ObservableList<Menu> menus, String menuText) {
        for (Menu menu : menus) {
            if (parentMenuText(menus, menus.indexOf(menu)).equals(menuText)) {
                return menu;
            }
        }
        return null;
    }
}
