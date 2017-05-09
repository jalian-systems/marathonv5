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

import com.sun.javafx.PlatformUtil;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class OSFXUtils {

    public static final Logger LOGGER = Logger.getLogger(OSFXUtils.class.getName());

    public static final String MOUSE_MENU_MASK = getMenuMask();

    public static String mouseEventGetModifiersExText(MouseEvent event) {
        StringBuffer sb = new StringBuffer();

        if (event.isControlDown()) {
            sb.append("Ctrl+");
        }
        if (event.isMetaDown()) {
            sb.append("Meta+");
        }
        if (event.isAltDown()) {
            sb.append("Alt+");
        }
        if (event.isShiftDown()) {
            sb.append("Shift+");
        }
        if (event.isPrimaryButtonDown()) {
            sb.append("Button1+");
        }
        if (event.isMiddleButtonDown()) {
            sb.append("Button2+");
        }
        if (event.isSecondaryButtonDown()) {
            sb.append("Button3+");
        }
        String text = sb.toString();
        if (text.equals("")) {
            return text;
        }
        return text.substring(0, text.length() - 1);
    }

    private static String getMenuMask() {
        if (PlatformUtil.isMac()) {
            return "Meta";
        }
        return "Ctrl";
    }

    public static String keyEventGetKeyText(KeyCode keycode) {
        if (keycode == KeyCode.TAB) {
            return "Tab";
        }
        if (keycode == KeyCode.CONTROL) {
            return "Ctrl";
        }
        if (keycode == KeyCode.ALT) {
            return "Alt";
        }
        if (keycode == KeyCode.SHIFT) {
            return "Shift";
        }
        if (keycode == KeyCode.META) {
            return "Meta";
        }
        if (keycode == KeyCode.SPACE) {
            return "Space";
        }
        if (keycode == KeyCode.BACK_SPACE) {
            return "Backspace";
        }
        if (keycode == KeyCode.HOME) {
            return "Home";
        }
        if (keycode == KeyCode.END) {
            return "End";
        }
        if (keycode == KeyCode.DELETE) {
            return "Delete";
        }
        if (keycode == KeyCode.PAGE_UP) {
            return "Pageup";
        }
        if (keycode == KeyCode.PAGE_DOWN) {
            return "Pagedown";
        }
        if (keycode == KeyCode.UP) {
            return "Up";
        }
        if (keycode == KeyCode.DOWN) {
            return "Down";
        }
        if (keycode == KeyCode.LEFT) {
            return "Left";
        }
        if (keycode == KeyCode.RIGHT) {
            return "Right";
        }
        if (keycode == KeyCode.ENTER) {
            return "Enter";
        }
        return keycode.getName();
    }

    public static boolean isModifiers(KeyEvent e) {
        if (e.isAltDown() || e.isControlDown() || e.isMetaDown() || e.isShiftDown()) {
            return true;
        }
        return false;
    }

    public static String ketEventGetModifiersExText(KeyEvent event) {
        StringBuffer sb = new StringBuffer();

        if (event.isControlDown()) {
            sb.append("Ctrl+");
        }
        if (event.isMetaDown()) {
            sb.append("Meta+");
        }
        if (event.isAltDown()) {
            sb.append("Alt+");
        }
        if (event.isShiftDown()) {
            sb.append("Shift+");
        }
        String text = sb.toString();
        if (text.equals("")) {
            return text;
        }
        return text.substring(0, text.length() - 1);
    }
}
