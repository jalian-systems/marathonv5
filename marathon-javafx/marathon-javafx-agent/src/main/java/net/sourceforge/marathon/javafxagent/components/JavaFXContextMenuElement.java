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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXContextMenuElement extends JavaFXElement {

    private ContextMenu contextMenu;

    public JavaFXContextMenuElement(ContextMenu contextMenu, IJavaFXAgent driver, JFXWindow window) {
        super(null, driver, window);
        this.contextMenu = contextMenu;
    }

    @Override public boolean marathon_select(String value) {
        String[] items = value.split("\\>\\>");
        ObservableList<MenuItem> children = contextMenu.getItems();
        List<MenuItem> menuItems = new ArrayList<>();
        for (String item : items) {
            getChidernMenuItem(children, item, menuItems);
        }
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
        contextMenu.hide();
        return true;
    }

    private void getChidernMenuItem(ObservableList<MenuItem> children, String items, List<MenuItem> menuItems) {
        for (MenuItem menuItem : children) {
            if (menuItem instanceof Menu) {
                getChidernMenuItem(((Menu) menuItem).getItems(), items, menuItems);
            }
            if (!(menuItem instanceof SeparatorMenuItem) && getTextForMenuItem(menuItem).equals(items)) {
                menuItems.add(menuItem);
                break;
            }
        }
    }

    public String getTextForMenuItem(MenuItem menuItem) {
        Menu parentMenu = menuItem.getParentMenu();
        if (parentMenu == null) {
            String text = menuItem.getText();
            if (text == null || "".equals(text)) {
                return getTextFromIcon(menuItem, -1);
            }
            return text;
        }
        return getTextForMenuItem(menuItem, parentMenu);
    }
}
