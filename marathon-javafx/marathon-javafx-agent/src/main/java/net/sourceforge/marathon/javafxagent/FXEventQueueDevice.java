package net.sourceforge.marathon.javafxagent;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

public class FXEventQueueDevice implements IDevice {

    public static class DeviceState {
        private boolean shiftPressed = false;
        private boolean ctrlPressed = false;
        private boolean altPressed = false;
        private boolean metaPressed = false;
        private boolean button1Pressed = false;
        private boolean button2Pressed = false;
        private boolean button3Pressed = false;
        private Node node;
        private double y;
        private double x;
        private Node dragSource;

        public DeviceState() {
        }

        public boolean isShiftPressed() {
            return shiftPressed;
        }

        public boolean isCtrlPressed() {
            return ctrlPressed;
        }

        public boolean isAltPressed() {
            return altPressed;
        }

        public boolean isMetaPressed() {
            return metaPressed;
        }

        public void toggleKeyState(JavaAgentKeys key) {
            storeIfEqualsShift(key);
            storeIfEqualsCtrl(key, true);
            storeIfEqualsAlt(key, true);
            storeIfEqualsMeta(key, true);
        }

        private void storeIfEqualsShift(JavaAgentKeys key) {
            if (key.equals(JavaAgentKeys.SHIFT)) {
                shiftPressed = !shiftPressed;
            }
        }

        private void storeIfEqualsCtrl(JavaAgentKeys key, boolean keyState) {
            if (key.equals(JavaAgentKeys.CONTROL)) {
                ctrlPressed = !ctrlPressed;
            }
        }

        private void storeIfEqualsAlt(JavaAgentKeys key, boolean keyState) {
            if (key.equals(JavaAgentKeys.ALT)) {
                altPressed = !altPressed;
            }
        }

        private void storeIfEqualsMeta(JavaAgentKeys key, boolean keyState) {
            if (key.equals(JavaAgentKeys.META)) {
                metaPressed = !metaPressed;
            }
        }

        @SuppressWarnings("deprecation") public int getModifierEx() {
            int modifiersEx = 0;
            if (shiftPressed)
                modifiersEx |= KeyCode.SHIFT.impl_getCode();
            if (ctrlPressed)
                modifiersEx |= KeyCode.CONTROL.impl_getCode();
            if (altPressed)
                modifiersEx |= KeyCode.ALT.impl_getCode();
            if (metaPressed)
                modifiersEx |= KeyCode.META.impl_getCode();
            return modifiersEx;
        }

        private boolean isModifier(JavaAgentKeys keys) {
            return keys == JavaAgentKeys.CONTROL || keys == JavaAgentKeys.ALT || keys == JavaAgentKeys.META
                    || keys == JavaAgentKeys.SHIFT;
        }

        private void storeMouseDown(MouseButton button) {
            if (button == MouseButton.PRIMARY)
                button1Pressed = true;
            if (button == MouseButton.MIDDLE)
                button2Pressed = true;
            if (button == MouseButton.SECONDARY)
                button3Pressed = true;
        }

        private void storeMouseUp(MouseButton button) {
            if (button == MouseButton.PRIMARY)
                button1Pressed = false;
            if (button == MouseButton.MIDDLE)
                button2Pressed = false;
            if (button == MouseButton.SECONDARY)
                button3Pressed = false;
        }

        public MouseButton getButtons() {
            if (button1Pressed)
                return MouseButton.PRIMARY;
            if (button2Pressed)
                return MouseButton.MIDDLE;
            if (button3Pressed)
                return MouseButton.SECONDARY;
            return MouseButton.NONE;
        }

        public MouseButton getButtonMask() {
            if (button1Pressed)
                return MouseButton.PRIMARY;
            if (button2Pressed)
                return MouseButton.MIDDLE;
            if (button3Pressed)
                return MouseButton.SECONDARY;
            return MouseButton.NONE;
        }

        public void setNode(Node node) {
            if (this.node != node) {
                x = 0;
                y = 0;
            }
            this.node = node;
        }

        public Node getNode() {
            return this.node;
        }

        private void setMousePosition(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public void setDragSource(Node dragSource) {
            this.dragSource = dragSource;
        }

        public Node getDragSource() {
            return dragSource;
        }

        public boolean isDnDCopyPressed() {
            return isAltPressed();
        }

        @Override public String toString() {
            return "DeviceState [shiftPressed=" + shiftPressed + ", ctrlPressed=" + ctrlPressed + ", altPressed=" + altPressed
                    + ", metaPressed=" + metaPressed + ", button1Pressed=" + button1Pressed + ", button2Pressed=" + button2Pressed
                    + ", button3Pressed=" + button3Pressed + ", y=" + y + ", x=" + x + "]";
        }

    }

    private static FXEventQueueDevice instance;

    private DeviceState deviceState = new DeviceState();

    public FXEventQueueDevice() {
        FXEventQueueDevice.instance = this;
    }

    @Override public void sendKeys(Node node, CharSequence... keysToSend) {
        for (CharSequence seq : keysToSend) {
            for (int i = 0; i < seq.length(); i++) {
                sendKey(node, seq.charAt(i));
            }
        }
        EventQueueWait.empty();
    }

    private void sendKey(Node node, char c) {
        node = getActiveNode(node);
        JavaAgentKeys keys = JavaAgentKeys.getKeyFromUnicode(c);
        if (keys == null) {
            dispatchKeyEvent(node, null, KeyEvent.KEY_PRESSED, c);
            if (deviceState.getModifierEx() == 0)
                dispatchKeyEvent(node, null, KeyEvent.KEY_TYPED, c);
            dispatchKeyEvent(node, null, KeyEvent.KEY_RELEASED, c);
            return;
        }
        if (keys == JavaAgentKeys.NULL)
            resetModifierState(node);
        else if (deviceState.isModifier(keys)) {
            pressKey(node, keys);
        } else {
            pressKey(node, keys);
            if (keys == JavaAgentKeys.SPACE)
                dispatchKeyEvent(node, null, KeyEvent.KEY_TYPED, ' ');
            releaseKey(node, keys);
        }
    }

    @SuppressWarnings("deprecation") private void dispatchKeyEvent(final Node node, JavaAgentKeys keyToPress,
            EventType<KeyEvent> eventType, char c) {
        if (keyToPress == null) {
            KeyboardMap kbMap = new KeyboardMap(c);
            KeyCode keyCode = KeyCode.getKeyCode((kbMap.getChar() + "").toUpperCase());
            if (eventType.getName().equals("KEY_TYPED"))
                keyCode = KeyCode.UNDEFINED;
            int modifiersEx = deviceState.getModifierEx();
            char char1 = kbMap.getChar();
            if (modifiersEx == 0) {
                modifiersEx = kbMap.getModifiersEx();
                dispatchEvent(new KeyEvent(eventType, char1 + "", char1 + "", keyCode, deviceState.isShiftPressed(),
                        deviceState.isCtrlPressed(), deviceState.isAltPressed(), deviceState.isMetaPressed()), node);
            } else {
                dispatchEvent(new KeyEvent(eventType, char1 + "", char1 + "", keyCode, deviceState.isShiftPressed(),
                        deviceState.isCtrlPressed(), deviceState.isAltPressed(), deviceState.isMetaPressed()), node);
            }
            EventQueueWait.empty();
            return;
        }
        final KeysMap keysMap = KeysMap.findMap(keyToPress);
        if (keysMap == null)
            return;
        deviceState.toggleKeyState(keyToPress);
        dispatchEvent(new KeyEvent(eventType, KeyCode.UNDEFINED.impl_getChar(), KeyCode.UNDEFINED.impl_getChar(), keysMap.getCode(),
                deviceState.isShiftPressed(), deviceState.isCtrlPressed(), deviceState.isAltPressed(), deviceState.isMetaPressed()),
                node);
        EventQueueWait.empty();
    }

    private void dispatchEvent(final Event event, final Node node) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Event.fireEvent(node, event);
            }
        });
    }

    private void dispatchEvent(MouseEvent mouseEvent) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Event.fireEvent(mouseEvent.getTarget(), mouseEvent);
            }
        });
    }

    public static Node getActiveNode(Node node) {
        return node;
    }

    @Override public void pressKey(Node node, JavaAgentKeys keyToPress) {
        if (keyToPress == JavaAgentKeys.NULL)
            resetModifierState(node);
        else
            dispatchKeyEvent(node, keyToPress, KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED.charAt(0));
        EventQueueWait.empty();

    }

    @Override public void releaseKey(Node node, JavaAgentKeys keyToRelease) {
        dispatchKeyEvent(node, keyToRelease, KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED.charAt(0));
        EventQueueWait.empty();
    }

    @Override public void buttonDown(Node node, Buttons button, double xoffset, double yoffset) {
        MouseButton mb = button.getMouseButton();
        dispatchEvent(createMouseEvent(MouseEvent.MOUSE_PRESSED, xoffset, yoffset, 0, 0, mb, 1, deviceState.shiftPressed,
                deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed, true, false, false, false, false, false,
                node));
        deviceState.storeMouseDown(mb);
        deviceState.setDragSource(node);
        EventQueueWait.empty();
    }

    @Override public void buttonUp(Node node, Buttons button, double xoffset, double yoffset) {
        MouseButton mb = button.getMouseButton();
        dispatchEvent(createMouseEvent(MouseEvent.MOUSE_RELEASED, xoffset, yoffset, 0, 0, mb, 1, deviceState.shiftPressed,
                deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed, true, false, false, false, false, false,
                node));
        deviceState.storeMouseUp(mb);
        deviceState.setDragSource(null);
        EventQueueWait.empty();
    }

    @Override public void moveto(Node node) {
        if (node instanceof Control)
            moveto(node, ((Control) node).getWidth() / 2, ((Control) node).getHeight() / 2);
    }

    @Override public void moveto(Node node, double xoffset, double yoffset) {
        MouseButton buttons = deviceState.getButtons();
        if (node != deviceState.getNode()) {
            if (deviceState.getNode() != null)
                dispatchEvent(createMouseEvent(MouseEvent.MOUSE_PRESSED, xoffset, yoffset, 0, 0, buttons, 0,
                        deviceState.shiftPressed, deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed,
                        buttons == MouseButton.PRIMARY, buttons == MouseButton.MIDDLE, buttons == MouseButton.SECONDARY, false,
                        false, false, node));
            dispatchEvent(createMouseEvent(MouseEvent.MOUSE_ENTERED, xoffset, yoffset, 0, 0, buttons, 0, deviceState.shiftPressed,
                    deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed, buttons == MouseButton.PRIMARY,
                    buttons == MouseButton.MIDDLE, buttons == MouseButton.SECONDARY, false, false, false, node));
        }
        Node source = node;
        EventType<MouseEvent> id = MouseEvent.MOUSE_MOVED;
        if (buttons != MouseButton.NONE) {
            id = MouseEvent.MOUSE_DRAGGED;
            source = deviceState.getDragSource();
        }
        MouseButton modifierEx = deviceState.getButtonMask();
        dispatchEvent(createMouseEvent(id, xoffset, yoffset, 0, 0, buttons, 0, deviceState.shiftPressed, deviceState.ctrlPressed,
                deviceState.altPressed, deviceState.metaPressed, modifierEx == MouseButton.PRIMARY,
                modifierEx == MouseButton.MIDDLE, modifierEx == MouseButton.SECONDARY, false, false, false, source));
        EventQueueWait.empty();
        deviceState.setNode(node);
        deviceState.setMousePosition(xoffset, yoffset);
    }

    @Override public void click(Node node, Buttons button, int clickCount, double xoffset, double yoffset) {
        MouseButton b = button.getMouseButton();
        dispatchMouseEvent(node, button == Buttons.RIGHT, clickCount, b, xoffset, yoffset);
        EventQueueWait.empty();
        deviceState.setNode(node);
    }

    private void dispatchMouseEvent(Node node, boolean popupTrigger, int clickCount, MouseButton buttons, double x, double y) {
        Point2D screenXY = node.localToScreen(new Point2D(x, y));
        if (node != deviceState.getNode()) {
            if (deviceState.getNode() != null)
                dispatchEvent(createMouseEvent(MouseEvent.MOUSE_EXITED, x, y, screenXY.getX(), screenXY.getY(), buttons, clickCount,
                        deviceState.shiftPressed, deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed, false,
                        false, false, false, popupTrigger, false, node));
            dispatchEvent(createMouseEvent(MouseEvent.MOUSE_ENTERED, x, y, screenXY.getX(), screenXY.getY(), buttons, clickCount,
                    deviceState.shiftPressed, deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed, false,
                    false, false, false, popupTrigger, false, node));
        }
        for (int n = 1; n <= clickCount; n++) {
            dispatchEvent(createMouseEvent(MouseEvent.MOUSE_PRESSED, x, y, screenXY.getX(), screenXY.getY(), buttons, n,
                    deviceState.shiftPressed, deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed,
                    buttons == MouseButton.PRIMARY, buttons == MouseButton.MIDDLE, buttons == MouseButton.SECONDARY, false,
                    popupTrigger, false, node));
            dispatchEvent(createMouseEvent(MouseEvent.MOUSE_RELEASED, x, y, screenXY.getX(), screenXY.getY(), buttons, n,
                    deviceState.shiftPressed, deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed,
                    buttons == MouseButton.PRIMARY, buttons == MouseButton.MIDDLE, buttons == MouseButton.SECONDARY, false,
                    popupTrigger, false, node));
            dispatchEvent(createMouseEvent(MouseEvent.MOUSE_CLICKED, x, y, screenXY.getX(), screenXY.getY(), buttons, n,
                    deviceState.shiftPressed, deviceState.ctrlPressed, deviceState.altPressed, deviceState.metaPressed,
                    buttons == MouseButton.PRIMARY, buttons == MouseButton.MIDDLE, buttons == MouseButton.SECONDARY, false,
                    popupTrigger, false, node));
        }
    }

    private void resetModifierState(Node node) {
        if (deviceState.isShiftPressed())
            releaseKey(node, JavaAgentKeys.SHIFT);
        if (deviceState.isAltPressed())
            releaseKey(node, JavaAgentKeys.ALT);
        if (deviceState.isCtrlPressed())
            releaseKey(node, JavaAgentKeys.CONTROL);
        if (deviceState.isMetaPressed())
            releaseKey(node, JavaAgentKeys.META);
    }

    public static FXEventQueueDevice getInstance() {
        return instance;
    }

    private Node getTarget_source, getTarget_target;
    private double getTarget_x, getTarget_y;

    private Node getTarget(Node source, double x, double y) {
        if (getTarget_source == source && getTarget_x == x && getTarget_y == y) {
            return getTarget_target;
        }
        List<Node> hits = new ArrayList<>();
        if (!(source instanceof Parent))
            return source;
        ObservableList<Node> children = ((Parent) source).getChildrenUnmodifiable();
        for (Node child : children) {
            checkHit(child, x, y, hits, "");
        }
        Node target = hits.size() > 0 ? hits.get(hits.size() - 1) : source;
        getTarget_source = source;
        getTarget_target = target;
        getTarget_x = x;
        getTarget_y = y;
        return target;
    }

    private void checkHit(Node child, double x, double y, List<Node> hits, String indent) {
        Bounds boundsInParent = child.getBoundsInParent();
        if (boundsInParent.contains(x, y)) {
            hits.add(child);
            if (!(child instanceof Parent))
                return;
            ObservableList<Node> childrenUnmodifiable = ((Parent) child).getChildrenUnmodifiable();
            for (Node node : childrenUnmodifiable) {
                checkHit(node, x, y, hits, "    " + indent);
            }
        }
    }

    private MouseEvent createMouseEvent(EventType<? extends MouseEvent> eventType, double x, double y, double screenX,
            double screenY, MouseButton button, int clickCount, boolean shiftDown, boolean controlDown, boolean altDown,
            boolean metaDown, boolean primaryButtonDown, boolean middleButtonDown, boolean secondaryButtonDown, boolean synthesized,
            boolean popupTrigger, boolean stillSincePress, Node source) {
        Node target = getTarget(source, x, y);
        Point2D sceneXY = source.localToScene(new Point2D(x, y));
        PickResult pickResult = new PickResult(target, sceneXY.getX(), sceneXY.getY());
        return new MouseEvent(source, target, eventType, x, y, screenX, screenY, button, clickCount, shiftDown, controlDown,
                altDown, metaDown, primaryButtonDown, middleButtonDown, secondaryButtonDown, synthesized, popupTrigger,
                stillSincePress, pickResult);
    }

}
