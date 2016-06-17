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
package net.sourceforge.marathon.javafxrecorder.component;

import java.util.LinkedList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXMenuItem extends RFXComponent {

    private IJSONRecorder recorder;
    private MenuBar menuBar;
    private Node ownerNode;
    private JSONOMapConfig oMapConfig;

    public RFXMenuItem(IJSONRecorder recorder, JSONOMapConfig oMapConfig) {
        super(null, oMapConfig, null, recorder);
        this.recorder = recorder;
        this.oMapConfig = oMapConfig;
    }

    public void record(ActionEvent event) {
        MenuItem source = (MenuItem) event.getSource();
        String tagForMenu = getTagForMenu(source);
        String menuPath = getSelectedMenuPath((MenuItem) source);
        if (!(ownerNode instanceof ChoiceBox<?>))
            recorder.recordSelectMenu(new RFXUnknownComponent(ownerNode, oMapConfig, null, recorder), tagForMenu, menuPath);
    }

    private String getTagForMenu(MenuItem source) {
        LinkedList<MenuItem> menuItems = new LinkedList<>();
        while (source != null) {
            menuItems.addFirst(source);
            source = source.getParentMenu();
        }
        if (menuItems.getFirst() instanceof Menu) {
            if (menuItems.size() >= 2) {
                ownerNode = menuItems.get(1).getParentPopup().getOwnerNode();
                return isMenuBar(ownerNode) ? "#menu" : "#contextmenu";
            }
        } else {
            ownerNode = menuItems.getFirst().getParentPopup().getOwnerNode();
            return "#contextmenu";
        }
        return null;
    }

    private boolean isMenuBar(Node ownerNode) {
        Node parent = ownerNode;
        while (parent != null) {
            if (parent instanceof MenuBar) {
                this.menuBar = (MenuBar) parent;
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    public String getSelectedMenuPath(MenuItem menuItem) {
        LinkedList<String> pathText = new LinkedList<>();
        while (menuItem != null) {
            String textForMenu = getTextForMenuItem(menuItem);
            pathText.addFirst(escapeSpecialCharacters(textForMenu));
            menuItem = menuItem.getParentMenu();
        }
        return buildSelectedMenuPath(pathText);
    }

    public String getTextForMenuItem(MenuItem menuItem) {
        Menu parentMenu = menuItem.getParentMenu();
        if (parentMenu == null) {
            if (menuBar != null) {
                ObservableList<Menu> menus = menuBar.getMenus();
                return parentMenuText(menus, menus.indexOf(menuItem));
            }
            String text = menuItem.getText();
            if (text == null || "".equals(text))
                return getTextFromIcon(menuItem, -1);
            return text;
        }
        return getTextForMenuItem(menuItem, parentMenu);
    }

    private String buildSelectedMenuPath(LinkedList<String> pathText) {
        StringBuilder sb = new StringBuilder();
        pathText.stream().forEach((itemText) -> sb.append(itemText).append(">>"));
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    protected String escapeSpecialCharacters(String name) {
        return name.replaceAll(">", "\\\\>");
    }
}
