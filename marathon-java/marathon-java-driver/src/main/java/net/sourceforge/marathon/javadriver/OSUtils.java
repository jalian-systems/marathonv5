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
package net.sourceforge.marathon.javadriver;

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import net.sourceforge.marathon.javaagent.KeysMap;
import net.sourceforge.marathon.json.JSONArray;

public class OSUtils {

    public static final Logger LOGGER = Logger.getLogger(OSUtils.class.getName());

    public static Keys getMenuKey() {
        int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (keyMask == Event.CTRL_MASK) {
            return Keys.CONTROL;
        }
        if (keyMask == Event.META_MASK) {
            return Keys.META;
        }
        if (keyMask == Event.ALT_MASK) {
            return Keys.ALT;
        }
        throw new WebDriverException("Unable to find the keymask... not control or meta?");
    }

    public static CharSequence getKeysFor(WebElement e, String keysFor) {
        String sKeyStroke = e.getAttribute("keystrokeFor-" + keysFor);
        if (sKeyStroke == null) {
            throw new WebDriverException("Unable to find keystroke for " + keysFor);
        }
        JSONArray a = new JSONArray(sKeyStroke);
        for (int i = 0; i < a.length() - 1; i++) {
            try {
                return getKeys(keysFor, a.getString(i));
            } catch (Throwable t) {
            }
        }
        return getKeys(keysFor, a.getString(a.length() - 1));
    }

    private static CharSequence getKeys(String keysFor, String sKeyStroke) {
        KeyStroke ks = KeyStroke.getKeyStroke(sKeyStroke);
        if (ks == null) {
            throw new WebDriverException("Unable to parse keystroke for " + keysFor + " trying to parse " + sKeyStroke);
        }
        StringBuilder sb = new StringBuilder();
        int modifiers = ks.getModifiers();
        if ((modifiers & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
            sb.append(Keys.CONTROL);
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK) {
            sb.append(Keys.ALT);
        }
        if ((modifiers & InputEvent.META_DOWN_MASK) == InputEvent.META_DOWN_MASK) {
            sb.append(Keys.META);
        }
        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
            sb.append(Keys.SHIFT);
        }
        int keyCode = ks.getKeyCode();
        if (keyCode == KeyEvent.VK_UNDEFINED) {
            sb.append(ks.getKeyChar());
        } else {
            sb.append(keyCharFromKeyCode(keyCode, keysFor));
        }
        sb.append(Keys.NULL);
        return sb.toString();
    }

    private static CharSequence keyCharFromKeyCode(int keyCode, String keysFor) {
        if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
            return "" + (char) (keyCode - KeyEvent.VK_A + 'a');
        }
        KeysMap entry = KeysMap.findMap(keyCode);
        if (entry != null) {
            return entry.getKeys();
        }
        if (keyCode <= 0x7F) {
            return "" + (char) keyCode;
        }
        throw new WebDriverException("Unable to find Keys entry for keycode " + keyCode + " for action " + keysFor);
    }

}
