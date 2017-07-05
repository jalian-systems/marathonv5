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
package net.sourceforge.marathon.javaagent;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DnDConstants;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

public class EventQueueDevice extends Device {

    public static final Logger LOGGER = Logger.getLogger(EventQueueDevice.class.getName());

    public class DeviceState {
        private boolean button1Pressed = false;
        private boolean button2Pressed = false;
        private boolean button3Pressed = false;
        private Component component;
        private int y;
        private int x;
        private Component dragSource;

        Map<JavaAgentKeys, Boolean> keyStates = new HashMap<JavaAgentKeys, Boolean>();

        public DeviceState() {
            keyStates.put(JavaAgentKeys.SHIFT, false);
            keyStates.put(JavaAgentKeys.CONTROL, false);
            keyStates.put(JavaAgentKeys.ALT, false);
            keyStates.put(JavaAgentKeys.META, false);
        }

        public void toggleKeyState(Component component, JavaAgentKeys key) {
            Boolean pressed = !keyStates.get(key);
            keyStates.put(key, pressed);
            if (pressed) {
                pressKey(component, key);
            } else {
                releaseKey(component, key);
            }
        }

        public void setKeyStatePressed(Component component, JavaAgentKeys key) {
            keyStates.put(key, true);
        }

        public void setKeyStateReleased(Component component, JavaAgentKeys key) {
            keyStates.put(key, false);
        }

        private boolean isModifier(CharSequence keys) {
            return keys == JavaAgentKeys.CONTROL || keys == JavaAgentKeys.ALT || keys == JavaAgentKeys.META
                    || keys == JavaAgentKeys.SHIFT;
        }

        private void resetModifierState(Component component) {
            for (Entry<JavaAgentKeys, Boolean> keyState : keyStates.entrySet()) {
                if (keyState.getValue()) {
                    toggleKeyState(component, keyState.getKey());
                }
            }
        }

        public boolean isShiftPressed() {
            return keyStates.get(JavaAgentKeys.SHIFT);
        }

        public boolean isCtrlPressed() {
            return keyStates.get(JavaAgentKeys.CONTROL);
        }

        public boolean isAltPressed() {
            return keyStates.get(JavaAgentKeys.ALT);
        }

        public boolean isMetaPressed() {
            return keyStates.get(JavaAgentKeys.META);
        }

        public int getModifierEx() {
            int modifiersEx = 0;
            if (isShiftPressed()) {
                modifiersEx |= InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK;
            }
            if (isCtrlPressed()) {
                modifiersEx |= InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK;
            }
            if (isAltPressed()) {
                modifiersEx |= InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK;
            }
            if (isMetaPressed()) {
                modifiersEx |= InputEvent.META_DOWN_MASK | InputEvent.META_MASK;
            }
            return modifiersEx;
        }

        private void storeMouseDown(int button) {
            if (button == MouseEvent.BUTTON1) {
                button1Pressed = true;
            }
            if (button == MouseEvent.BUTTON2) {
                button2Pressed = true;
            }
            if (button == MouseEvent.BUTTON3) {
                button3Pressed = true;
            }
        }

        private void storeMouseUp(int button) {
            if (button == MouseEvent.BUTTON1) {
                button1Pressed = false;
            }
            if (button == MouseEvent.BUTTON2) {
                button2Pressed = false;
            }
            if (button == MouseEvent.BUTTON3) {
                button3Pressed = false;
            }
        }

        public int getButtons() {
            if (button1Pressed) {
                return MouseEvent.BUTTON1;
            }
            if (button2Pressed) {
                return MouseEvent.BUTTON2;
            }
            if (button3Pressed) {
                return MouseEvent.BUTTON3;
            }
            return 0;
        }

        public int getButtonMask() {
            if (button1Pressed) {
                return InputEvent.BUTTON1_DOWN_MASK;
            }
            if (button2Pressed) {
                return InputEvent.BUTTON2_DOWN_MASK;
            }
            if (button3Pressed) {
                return InputEvent.BUTTON3_DOWN_MASK;
            }
            return 0;
        }

        public void setComponent(Component component) {
            if (this.component != component) {
                x = 0;
                y = 0;
            }
            this.component = component;
        }

        public Component getComponent() {
            if (component == null) {
                Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
                if (activeWindow != null) {
                    return activeWindow.getFocusOwner();
                }
                Window[] windows = Window.getWindows();
                if (windows.length > 0) {
                    if (windows[0].getFocusOwner() != null) {
                        return windows[0].getFocusOwner();
                    }
                    return windows[0];
                }
            }
            return component;
        }

        private void setMousePosition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setDragSource(Component dragSource) {
            this.dragSource = dragSource;
        }

        public Component getDragSource() {
            return dragSource;
        }

        public boolean isDnDCopyPressed() {
            if (Platform.getCurrent().is(Platform.MAC)) {
                return isAltPressed();
            } else {
                return isCtrlPressed();
            }
        }

        @Override public String toString() {
            return "DeviceState [shiftPressed=" + isShiftPressed() + ", ctrlPressed=" + isCtrlPressed() + ", altPressed="
                    + isAltPressed() + ", metaPressed=" + isMetaPressed() + ", button1Pressed=" + button1Pressed
                    + ", button2Pressed=" + button2Pressed + ", button3Pressed=" + button3Pressed + ", y=" + y + ", x=" + x + "]";
        }

    }

    private DeviceState deviceState = new DeviceState();

    public EventQueueDevice() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.Device#sendKeys(java.awt.Component,
     * java.lang.CharSequence)
     */
    @Override public void sendKeys(Component component, CharSequence... keysToSend) {
        for (CharSequence seq : keysToSend) {
            for (int i = 0; i < seq.length(); i++) {
                sendKey(component, seq.charAt(i));
            }
        }
        EventQueueWait.empty();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.Device#pressKey(net.sourceforge.
     * marathon .javaagent.Keys)
     */
    @Override public void pressKey(Component component, JavaAgentKeys keyToPress) {
        if (keyToPress == JavaAgentKeys.NULL) {
            deviceState.resetModifierState(component);
        } else {
            if (deviceState.isModifier(keyToPress)) {
                deviceState.setKeyStatePressed(component, keyToPress);
            }
            dispatchKeyEvent(component, keyToPress, KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED);
        }
        EventQueueWait.empty();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.Device#releaseKey(net.sourceforge.
     * marathon.javaagent.Keys)
     */
    @Override public void releaseKey(Component component, JavaAgentKeys keyToRelease) {
        if (deviceState.isModifier(keyToRelease)) {
            deviceState.setKeyStateReleased(component, keyToRelease);
        }
        dispatchKeyEvent(component, keyToRelease, KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED);
        EventQueueWait.empty();
    }

    @Override public void moveto(final Component component) {
        try {
            Dimension d = EventQueueWait.exec(new Callable<Dimension>() {
                @Override public Dimension call() {
                    return component.getSize();
                }
            });
            moveto(component, d.width / 2, d.height / 2);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception while getting component size", e);
        }
        EventQueueWait.empty();
    }

    @Override public void moveto(Component component, int xOffset, int yOffset) {
        int buttons = deviceState.getButtons();
        if (component != deviceState.getComponent()) {
            if (deviceState.getComponent() != null) {
                dispatchEvent(new MouseEvent(deviceState.getComponent(), MouseEvent.MOUSE_EXITED, System.currentTimeMillis(),
                        deviceState.getModifierEx(), deviceState.x, deviceState.y, 0, false, buttons));
            }
            dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(),
                    deviceState.getModifierEx(), xOffset, yOffset, 0, false, buttons));
        }
        Component source = component;
        int id = MouseEvent.MOUSE_MOVED;
        Point p = new Point(xOffset, yOffset);
        if (buttons != 0) {
            id = MouseEvent.MOUSE_DRAGGED;
            source = deviceState.getDragSource();
            if (source != component) {
                p = SwingUtilities.convertPoint(component, xOffset, yOffset, source);
            }
        }
        int modifierEx = deviceState.getModifierEx() | deviceState.getButtonMask();
        MouseEvent mouseEvent = new MouseEvent(source, id, System.currentTimeMillis(), modifierEx, p.x, p.y, 0, false, buttons);
        dispatchEvent(mouseEvent);
        EventQueueWait.empty();
        deviceState.setComponent(component);
        deviceState.setMousePosition(xOffset, yOffset);
    }

    @Override public void buttonDown(Component component, Buttons button, int xoffset, int yoffset) {
        dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
                deviceState.getModifierEx() | InputEvent.BUTTON1_DOWN_MASK, xoffset, yoffset, 1, false, MouseEvent.BUTTON1));
        deviceState.storeMouseDown(MouseEvent.BUTTON1);
        deviceState.setDragSource(component);
        EventQueueWait.empty();
    }

    @Override public void buttonUp(Component component, Buttons button, int xoffset, int yoffset) {
        if (button.getButton() == 0) {
            if (handleDnD(component, xoffset, yoffset)) {
                EventQueueWait.empty();
                return;
            }
        }
        dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), deviceState.getModifierEx(),
                xoffset, yoffset, 1, false, MouseEvent.BUTTON1));
        EventQueueWait.empty();
        if (deviceState.getComponent() == component) {
            dispatchMouseEvent(component, button.getButton() == 2, 1, MouseEvent.BUTTON1, xoffset, yoffset);
        }
        deviceState.storeMouseUp(MouseEvent.BUTTON1);
        deviceState.setDragSource(null);
        EventQueueWait.empty();
    }

    private Boolean handleDnD(Component component, int xoffset, int yoffset) {
        int dropAction;
        if (deviceState.isDnDCopyPressed()) {
            dropAction = DnDConstants.ACTION_COPY;
        } else {
            dropAction = DnDConstants.ACTION_MOVE;
        }
        Logger.getLogger(EventQueueDevice.class.getName()).info("Performing Drop");
        DnDHandler dnd = new DnDHandler(deviceState.getDragSource(), component, xoffset, yoffset, dropAction);
        return dnd.performDrop();
    }

    private void sendKey(Component component, char c) {
        component = Device.getActiveComponent(component);
        JavaAgentKeys keys = JavaAgentKeys.getKeyFromUnicode(c);
        if (keys == null) {
            dispatchNormal(component, c);
        } else if (keys == JavaAgentKeys.NULL) {
            deviceState.resetModifierState(component);
        } else if (deviceState.isModifier(keys)) {
            deviceState.toggleKeyState(component, keys);
        } else {
            pressKey(component, keys);
            if (keys == JavaAgentKeys.SPACE) {
                dispatchKeyEvent(component, keys, KeyEvent.KEY_TYPED, ' ');
            }
            if (keys == JavaAgentKeys.ENTER) {
                dispatchKeyEvent(component, keys, KeyEvent.KEY_TYPED, '\n');
            }
            releaseKey(component, keys);
        }
    }

    private void dispatchNormal(Component component, char c) {
        KeyboardMap kbMap = new KeyboardMap(c);
        List<CharSequence[]> keysList = kbMap.getKeys();
        if (keysList == null) {
            // Generate Key Typed
            dispatchEvent(new KeyEvent(component, KeyEvent.KEY_TYPED, System.currentTimeMillis(), deviceState.getModifierEx(),
                    KeyEvent.VK_UNDEFINED, c));
            return;
        }
        for (CharSequence[] keys : keysList) {
            // Generate Key Press
            for (CharSequence key : keys) {
                if (deviceState.isModifier(key)) {
                    pressKey(component, (JavaAgentKeys) key);
                } else {
                    int keyCode = Integer.parseInt(key.toString());
                    dispatchEvent(new KeyEvent(component, KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
                            deviceState.getModifierEx(), keyCode, c));
                }
            }
            // Generate Key Typed
            dispatchEvent(new KeyEvent(component, KeyEvent.KEY_TYPED, System.currentTimeMillis(), deviceState.getModifierEx(),
                    KeyEvent.VK_UNDEFINED, c));
            // Generate Key Release
            for (int i = keys.length - 1; i >= 0; i--) {
                CharSequence key = keys[i];
                if (deviceState.isModifier(key)) {
                    releaseKey(component, (JavaAgentKeys) key);
                } else {
                    int keyCode = Integer.parseInt(key.toString());
                    dispatchEvent(new KeyEvent(component, KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
                            deviceState.getModifierEx(), keyCode, c));
                }
            }
        }
    }

    private void dispatchKeyEvent(final Component component, JavaAgentKeys keyToPress, int id, char c) {
        try {
            Rectangle d = EventQueueWait.exec(new Callable<Rectangle>() {
                @Override public Rectangle call() {
                    return component.getBounds();
                }
            });
            ensureVisible(component.getParent(), d);
            EventQueueWait.call_noexc(component, "requestFocusInWindow");
        } catch (Exception e) {
            throw new RuntimeException("getBounds failed for " + component.getClass().getName(), e);
        }
        final KeysMap keysMap = KeysMap.findMap(keyToPress);
        if (keysMap == null) {
            return;
        }
        int m = deviceState.getModifierEx();
        int keyCode = keysMap.getCode();
        if (id == KeyEvent.KEY_TYPED) {
            keyCode = KeyEvent.VK_UNDEFINED;
        }
        dispatchEvent(new KeyEvent(component, id, System.currentTimeMillis(), m, keyCode, c));
        EventQueueWait.empty();
    }

    private void dispatchEvent(final AWTEvent event) {
        LOGGER.info(event.toString());
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                ((Component) event.getSource()).dispatchEvent(event);
            }
        });
    }

    private void dispatchMouseEvent(Component component, boolean popupTrigger, int clickCount, int buttons, int x, int y) {
        ensureVisible(component, new Rectangle(x, y, 50, 50));
        EventQueueWait.call_noexc(component, "requestFocusInWindow");
        int modifierEx = deviceState.getModifierEx();
        if (component != deviceState.getComponent()) {
            if (deviceState.getComponent() != null) {
                dispatchEvent(new MouseEvent(deviceState.getComponent(), MouseEvent.MOUSE_EXITED, System.currentTimeMillis(),
                        modifierEx, deviceState.x, deviceState.y, 0, popupTrigger, buttons));
            }
            dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), modifierEx, x, y, 0,
                    popupTrigger, buttons));
        }
        for (int n = 1; n <= clickCount; n++) {
            int buttonMask = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON1_MASK;
            if (buttons == 3) {
                buttonMask = InputEvent.BUTTON3_DOWN_MASK | InputEvent.BUTTON3_MASK;
            }
            dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), modifierEx | buttonMask,
                    x, y, n, popupTrigger, buttons));
            buttonMask = InputEvent.BUTTON1_MASK;
            if (buttons == 3) {
                buttonMask = InputEvent.BUTTON3_MASK;
            }
            dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), modifierEx | buttonMask,
                    x, y, n, false, buttons));
            dispatchEvent(new MouseEvent(component, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), modifierEx | buttonMask,
                    x, y, n, false, buttons));
        }
    }

    @Override public void click(Component component, Buttons button, int clickCount, int xoffset, int yoffset) {
        int b = MouseEvent.BUTTON1;
        if (button.getButton() == 0) {
            b = MouseEvent.BUTTON1;
        } else if (button.getButton() == 1) {
            b = MouseEvent.BUTTON2;
        } else if (button.getButton() == 2) {
            b = MouseEvent.BUTTON3;
        }
        dispatchMouseEvent(component, button.getButton() == 2, clickCount, b, xoffset, yoffset);
        EventQueueWait.empty();
        deviceState.setComponent(component);
    }

    @Override public String toString() {
        return "EventQueueDevice [deviceState=" + deviceState + "]";
    }

}
