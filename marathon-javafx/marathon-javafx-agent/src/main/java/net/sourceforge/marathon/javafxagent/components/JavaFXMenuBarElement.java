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
            if (parentMenuText(menus, menus.indexOf(menu)).equals(menuText))
                return menu;
        }
        return null;
    }
}
