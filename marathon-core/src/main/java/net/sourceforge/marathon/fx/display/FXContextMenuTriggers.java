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
package net.sourceforge.marathon.fx.display;

import java.util.logging.Logger;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Preferences;

public class FXContextMenuTriggers {

    public static final Logger LOGGER = Logger.getLogger(FXContextMenuTriggers.class.getName());

    private static String menuModifiers;
    private static String contextMenuKey;
    private static String contextMenuKeyModifiers;

    static {
        Preferences prefs = Preferences.instance();
        System.setProperty(Constants.PROP_RECORDER_MOUSETRIGGER,
                prefs.getSection("marathon").optString(Constants.PREF_RECORDER_MOUSE_TRIGGER));
        System.setProperty(Constants.PROP_RECORDER_KEYTRIGGER,
                prefs.getSection("marathon").optString(Constants.PREF_RECORDER_KEYBOARD_TRIGGER));
        FXContextMenuTriggers.setContextMenuModifiers();
        FXContextMenuTriggers.setContextMenuKey();
    }

    public static void setContextMenuModifiers() {
        String menuModifiersTxt = System.getProperty(Constants.PROP_RECORDER_MOUSETRIGGER);
        if (menuModifiersTxt == null || menuModifiersTxt.equals("")) {
            menuModifiers = OSFXUtils.MOUSE_MENU_MASK + "+Button3";
            return;
        }
        setMenuModifiersFromText(menuModifiersTxt);
    }

    private static void setMenuModifiersFromText(String menuModifiersTxt) {
        String button = menuModifiersTxt.substring(menuModifiersTxt.lastIndexOf('+') + 1);
        if (menuModifiersTxt.contains("+")) {
            String modifiers = menuModifiersTxt.substring(0, menuModifiersTxt.lastIndexOf('+'));
            KeyCombination kc = KeyCombination.valueOf(modifiers + "+A");
            menuModifiers = getDownModifierMask(kc);
            if (button.equals("Button1")) {
                menuModifiers += "Button1";
            } else if (button.equals("Button2")) {
                menuModifiers += "Button2";
            } else if (button.equals("Button3")) {
                menuModifiers += "Button3";
            } else {
                throw new RuntimeException("Unknow button " + button + " in setting mouse trigger");
            }
            return;
        }
        if (button.equals("Button1")) {
            menuModifiers = "Button1";
        } else if (button.equals("Button2")) {
            menuModifiers = "Button2";
        } else if (button.equals("Button3")) {
            menuModifiers = "Button3";
        } else {
            throw new RuntimeException("Unknow button " + button + " in setting mouse trigger");
        }
    }

    public static String getContextMenuModifiers() {
        return menuModifiers;
    }

    public static String getContextMenuKeyModifiers() {
        return contextMenuKeyModifiers;
    }

    public static String getContextMenuKeyCode() {
        return contextMenuKey;
    }

    public static void setContextMenuKey() {
        String keyText = System.getProperty(Constants.PROP_RECORDER_KEYTRIGGER);
        if (keyText == null || keyText.equals("")) {
            contextMenuKey = "F8";
            contextMenuKeyModifiers = OSFXUtils.MOUSE_MENU_MASK;
        } else {
            String downModifierMask = getDownModifierMask(KeyCombination.valueOf(keyText));
            if (downModifierMask.contains("+") && downModifierMask.lastIndexOf("+") == downModifierMask.length() - 1) {
                downModifierMask = downModifierMask.substring(0, downModifierMask.lastIndexOf("+"));
            }
            contextMenuKeyModifiers = downModifierMask;
            String menuKey = keyText.substring(keyText.lastIndexOf('+') + 1);
            contextMenuKey = KeyCode.valueOf(menuKey).getName();
        }
    }

    private static String getDownModifierMask(KeyCombination kc) {
        StringBuilder contextMenuKeyModifiers = new StringBuilder();
        if (kc.getControl() == ModifierValue.DOWN) {
            contextMenuKeyModifiers.append("Ctrl+");
        }
        if (kc.getAlt() == ModifierValue.DOWN) {
            contextMenuKeyModifiers.append("Alt+");
        }
        if (kc.getMeta() == ModifierValue.DOWN) {
            contextMenuKeyModifiers.append("Meta+");
        }
        if (kc.getShift() == ModifierValue.DOWN) {
            contextMenuKeyModifiers.append("Shift+");
        }
        return contextMenuKeyModifiers.toString();
    }

}
