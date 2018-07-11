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

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgentException;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JMenuItemJavaElement extends AbstractJavaElement {

    public static final Logger LOGGER = Logger.getLogger(JMenuItemJavaElement.class.getName());

    public JMenuItemJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public boolean marathon_select(String value) {
        if (value != null && !"".equals(value)) {
            return setSelectedPath(value.split("\\>\\>"));
        }
        return true;
    }

    private boolean setSelectedPath(String[] items) {
        if (items.length == 1) {
            click();
            return true;
        }
        if (component instanceof JMenu) {
            if (!((JMenu) component).isPopupMenuVisible()) {
                click();
                return false;
            }
        } else {
            throw new JavaAgentException("Component is not a JMenu", null);
        }
        Component current = component;
        for (int i = 1; i < items.length; i++) {
            current = findMenuElement(current, items[i]);
            if (current == null) {
                return false;
            }
            if (current instanceof JMenu) {
                if (!((JMenu) current).isPopupMenuVisible()) {
                    JavaElementFactory.createElement(current, driver, window).click();
                    return false;
                }
                continue;
            }
            if (i != items.length - 1) {
                throw new JavaAgentException("A Jmenuitem found in the middle of menu path?", null);
            }
            JavaElementFactory.createElement(current, driver, window).click();
        }
        return true;
    }

    private Component findMenuElement(Component parent, String text) {
        if (parent instanceof JMenu) {
            Component[] components = ((JMenu) parent).getMenuComponents();
            for (Component component : components) {
                IJavaElement ije = JavaElementFactory.createElement(component, driver, window);
                if (text.equals(getText(ije.getText(), component, components))) {
                    return component;
                }
            }
        } else {
            throw new JavaAgentException("Can't find menu elements from a menuitem that is not a menu", null);
        }
        return null;
    }

    public static String getText(String original, Component current, Component[] components) {
        String itemText = original;
        int suffixIndex = 0;
        for (int i = 0; i < components.length; i++) {
            if (components[i] == current) {
                return itemText;
            }
            if (!(components[i] instanceof AbstractButton)) {
                continue;
            }
            AbstractButton menuItem = (AbstractButton) components[i];
            String menuItemText = menuItem.getText();
            if ("".equals(menuItemText) || menuItemText == null) {
                menuItemText = getTextFromIcon((JMenuItem) components[i]);
            }
            if (menuItemText.equals(original)) {
                itemText = String.format("%s(%d)", original, ++suffixIndex);
            }
        }
        return itemText;
    }

    @Override
    public String _getText() {
        JMenuItem current = (JMenuItem) component;
        return getItemText(current);
    }

    public static String getItemText(JMenuItem current) {
        String text = current.getText();
        if (text == null || "".equals(text)) {
            return getTextFromIcon(current);
        }
        return text;
    }

    private static String getTextFromIcon(JMenuItem mi) {
        Icon icon = mi.getIcon();
        if (icon != null && icon instanceof ImageIcon) {
            String description = ((ImageIcon) icon).getDescription();
            return getNameFromImageDescription(description);
        }
        return null;
    }

    private static String getNameFromImageDescription(String description) {
        try {
            String name = new URL(description).getPath();
            if (name.lastIndexOf('/') != -1) {
                name = name.substring(name.lastIndexOf('/') + 1);
            }
            if (name.lastIndexOf('.') != -1) {
                name = name.substring(0, name.lastIndexOf('.'));
            }
            return name;
        } catch (MalformedURLException e) {
            return description;
        }
    }
}
