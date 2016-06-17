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
package net.sourceforge.marathon.javaagent;

import static java.awt.event.KeyEvent.*;

public enum KeysMap {

    // @formatter:off
    NULL        (JavaAgentKeys.NULL, 0),
    Cancel      (JavaAgentKeys.CANCEL, VK_CANCEL),
    Help        (JavaAgentKeys.HELP, VK_HELP),
    Backspace  (JavaAgentKeys.BACK_SPACE, VK_BACK_SPACE),
    Tab         (JavaAgentKeys.TAB, VK_TAB),
    Clear       (JavaAgentKeys.CLEAR, VK_CLEAR),
    Return      (JavaAgentKeys.RETURN, VK_ENTER),
    Enter       (JavaAgentKeys.ENTER, VK_ENTER),
    Pause       (JavaAgentKeys.PAUSE, VK_PAUSE),
    Escape      (JavaAgentKeys.ESCAPE, VK_ESCAPE),
    Space       (JavaAgentKeys.SPACE, VK_SPACE),
    Pageup     (JavaAgentKeys.PAGE_UP, VK_PAGE_UP),
    Pagedown   (JavaAgentKeys.PAGE_DOWN, VK_PAGE_DOWN),
    End         (JavaAgentKeys.END, VK_END),
    Home        (JavaAgentKeys.HOME, VK_HOME),
    Left        (JavaAgentKeys.LEFT, VK_LEFT),
    Up          (JavaAgentKeys.UP, VK_UP),
    Right       (JavaAgentKeys.RIGHT, VK_RIGHT),
    Down        (JavaAgentKeys.DOWN, VK_DOWN),
    Insert      (JavaAgentKeys.INSERT, VK_INSERT),
    Delete      (JavaAgentKeys.DELETE, VK_DELETE),
    Semicolon   (JavaAgentKeys.SEMICOLON, VK_SEMICOLON),
    Equals      (JavaAgentKeys.EQUALS, VK_EQUALS),

    SHIFT       (JavaAgentKeys.SHIFT, VK_SHIFT),
    LEFT_SHIFT  (JavaAgentKeys.LEFT_SHIFT, VK_SHIFT),
    CONTROL     (JavaAgentKeys.CONTROL, VK_CONTROL),
    LEFT_CONTROL(JavaAgentKeys.LEFT_CONTROL, VK_CONTROL),
    ALT         (JavaAgentKeys.ALT, VK_ALT),
    LEFT_ALT    (JavaAgentKeys.LEFT_ALT, VK_ALT),

    ARROW_LEFT  (JavaAgentKeys.ARROW_LEFT, VK_KP_LEFT),
    ARROW_UP    (JavaAgentKeys.ARROW_UP, VK_KP_UP),
    ARROW_RIGHT (JavaAgentKeys.ARROW_RIGHT, VK_KP_RIGHT),
    ARROW_DOWN  (JavaAgentKeys.ARROW_DOWN, VK_KP_DOWN),

    NUMPAD0     (JavaAgentKeys.NUMPAD0, VK_NUMPAD0),
    NUMPAD1     (JavaAgentKeys.NUMPAD1, VK_NUMPAD1),
    NUMPAD2     (JavaAgentKeys.NUMPAD2, VK_NUMPAD2),
    NUMPAD3     (JavaAgentKeys.NUMPAD3, VK_NUMPAD3),
    NUMPAD4     (JavaAgentKeys.NUMPAD4, VK_NUMPAD4),
    NUMPAD5     (JavaAgentKeys.NUMPAD5, VK_NUMPAD5),
    NUMPAD6     (JavaAgentKeys.NUMPAD6, VK_NUMPAD6),
    NUMPAD7     (JavaAgentKeys.NUMPAD7, VK_NUMPAD7),
    NUMPAD8     (JavaAgentKeys.NUMPAD8, VK_NUMPAD8),
    NUMPAD9     (JavaAgentKeys.NUMPAD9, VK_NUMPAD9),
    MULTIPLY    (JavaAgentKeys.MULTIPLY, VK_MULTIPLY),
    ADD         (JavaAgentKeys.ADD, VK_ADD),
    SEPARATOR   (JavaAgentKeys.SEPARATOR, VK_SEPARATOR),
    SUBTRACT    (JavaAgentKeys.SUBTRACT, VK_SUBTRACT),
    DECIMAL     (JavaAgentKeys.DECIMAL, VK_DECIMAL),
    DIVIDE      (JavaAgentKeys.DIVIDE, VK_DIVIDE),

    F1          (JavaAgentKeys.F1, VK_F1),
    F2          (JavaAgentKeys.F2, VK_F2),
    F3          (JavaAgentKeys.F3, VK_F3),
    F4          (JavaAgentKeys.F4, VK_F4),
    F5          (JavaAgentKeys.F5, VK_F5),
    F6          (JavaAgentKeys.F6, VK_F6),
    F7          (JavaAgentKeys.F7, VK_F7),
    F8          (JavaAgentKeys.F8, VK_F8),
    F9          (JavaAgentKeys.F9, VK_F9),
    F10         (JavaAgentKeys.F10, VK_F10),
    F11         (JavaAgentKeys.F11, VK_F11),
    F12         (JavaAgentKeys.F12, VK_F12),

    META        (JavaAgentKeys.META, VK_META),
    COMMAND     (JavaAgentKeys.COMMAND, VK_META);

    // @formatter:on
    private final JavaAgentKeys keys;
    private final int code;

    KeysMap(JavaAgentKeys keys, int code) {
        this.keys = keys;
        this.code = code;
    }

    public JavaAgentKeys getKeys() {
        return keys;
    }

    public int getCode() {
        return code;
    }

    public static KeysMap findMap(JavaAgentKeys k) {
        for (KeysMap km : values())
            if (km.getKeys().equals(k))
                return km;
        return null;
    }

    public static KeysMap findMap(int k) {
        for (KeysMap km : values())
            if (km.getCode() == k)
                return km;
        return null;
    }
}
