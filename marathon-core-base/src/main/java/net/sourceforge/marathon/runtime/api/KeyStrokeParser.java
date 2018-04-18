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
package net.sourceforge.marathon.runtime.api;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

public class KeyStrokeParser {

    public static final Logger LOGGER = Logger.getLogger(KeyStrokeParser.class.getName());

    private static Map<Object, Object> keyCodes;

    static {
        initKeyCodes();
    }

    private char keyChar;
    private KeyStroke keyStroke;

    public KeyStrokeParser(String sequence) {
        parseSequence(sequence);
    }

    private void parseSequence(String sequence) {
        if (sequence.length() == 1) {
            keyChar = sequence.charAt(0);
            keyStroke = KeyStroke.getKeyStroke(keyChar);
            return;
        }
        int modifiers = 0;
        int key = 0;
        StringTokenizer toke = new StringTokenizer(sequence, "+");
        while (toke.hasMoreTokens()) {
            String keyText = toke.nextToken();
            Integer keycode = (Integer) keyCodes.get(keyText);
            if (keycode == null && keyText.equals("Meta")) {
                keycode = (Integer) keyCodes.get("Meta");
            }
            if (keycode == null && keyText.equals("Meta")) {
                keycode = (Integer) keyCodes.get("Meta");
            }
            if (keycode == null) {
                throw new RuntimeException("don't know what key is represented by " + sequence);
            }
            if (toke.hasMoreTokens()) {
                modifiers |= getModifier(keycode.intValue());
            } else {
                key = keycode.intValue();
            }
        }
        keyStroke = KeyStroke.getKeyStroke(key, modifiers);
        if (modifiers == 0 && key < 128) {
            keyChar = (char) key;
        } else {
            keyChar = keyStroke.getKeyChar();
        }
    }

    private int getModifier(int keycode) {
        switch (keycode) {
        case KeyEvent.VK_SHIFT:
            return InputEvent.SHIFT_MASK;
        case KeyEvent.VK_CONTROL:
            return InputEvent.CTRL_MASK;
        case KeyEvent.VK_ALT:
            return InputEvent.ALT_MASK;
        case KeyEvent.VK_META:
            return InputEvent.META_MASK;
        default:
            throw new RuntimeException(OSUtils.keyEventGetKeyText(keycode) + " is not a valid modifier");
        }
    }

    private synchronized static void initKeyCodes() {
        keyCodes = new HashMap<Object, Object>();
        Field[] fields = KeyEvent.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (fieldName.startsWith("VK_") && !fieldName.startsWith("VK_KP")) {
                int keyCode = 0;
                try {
                    keyCode = field.getInt(null);
                } catch (IllegalArgumentException e) {
                    throw new Error("could not read key codes from VM!");
                } catch (IllegalAccessException e) {
                    throw new Error("could not read key codes from VM!");
                }
                keyCodes.put(OSUtils.keyEventGetKeyText(keyCode), Integer.valueOf(keyCode));
                keyCodes.put(Integer.valueOf(keyCode), OSUtils.keyEventGetKeyText(keyCode));
                keyCodes.put(fieldName, Integer.valueOf(keyCode));
            }
        }
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public static String getKeyModifierText(int modifiers) {
        if (modifiers == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
            sb.append("Ctrl+");
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
            sb.append("Alt+");
        }
        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
            sb.append("Shift+");
        }
        if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
            sb.append("Meta+");
        }
        return sb.toString();
    }

    public static String getTextForKeyChar(char keyChar) {
        int keycode;
        switch (keyChar) {
        case ' ':
            keycode = KeyEvent.VK_SPACE;
            break;
        case '\b':
            keycode = KeyEvent.VK_BACK_SPACE;
            break;
        case '\t':
            keycode = KeyEvent.VK_TAB;
            break;
        case '\n':
            keycode = KeyEvent.VK_ENTER;
            break;
        case '\u0018':
            keycode = KeyEvent.VK_CANCEL;
            break;
        case '\u001b':
            keycode = KeyEvent.VK_ESCAPE;
            break;
        case '\u007f':
            keycode = KeyEvent.VK_DELETE;
            break;
        default:
            return "" + keyChar;
        }
        return (String) keyCodes.get(Integer.valueOf(keycode));
    }

}
