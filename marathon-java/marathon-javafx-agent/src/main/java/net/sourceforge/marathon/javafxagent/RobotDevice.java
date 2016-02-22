package net.sourceforge.marathon.javafxagent;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class RobotDevice extends Device {

    private Robot robot;

    public RobotDevice() {
        try {
            this.robot = new Robot();
            robotXsetAutoWaitForIdle(true);
            robotXsetAutoDelay();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public class DeviceState {
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
            if (pressed)
                pressKey(component, key);
            else
                releaseKey(component, key);
        }

        private boolean isModifier(CharSequence key) {
            return key == JavaAgentKeys.CONTROL || key == JavaAgentKeys.ALT || key == JavaAgentKeys.META
                    || key == JavaAgentKeys.SHIFT;
        }

        private void resetModifierState(Component component) {
            for (Entry<JavaAgentKeys, Boolean> keyState : keyStates.entrySet()) {
                if (keyState.getValue())
                    toggleKeyState(component, keyState.getKey());
            }
        }
    }

    private DeviceState deviceState = new DeviceState();

    @Override public void sendKeys(Component component, CharSequence... keysToSend) {
        component = Device.getActiveComponent(component);
        EventQueueWait.requestFocus(component);
        for (CharSequence seq : keysToSend) {
            for (int i = 0; i < seq.length(); i++)
                sendKey(component, seq.charAt(i));
        }
    }

    private void sendKey(Component component, char c) {
        JavaAgentKeys keys = JavaAgentKeys.getKeyFromUnicode(c);
        if (keys == null) {
            dispatchNormalCharacter(component, c);
            return;
        }
        if (keys == JavaAgentKeys.NULL) {
            deviceState.resetModifierState(component);
        } else if (deviceState.isModifier(keys)) {
            deviceState.toggleKeyState(component, keys);
        } else {
            pressKey(component, keys);
            releaseKey(component, keys);
        }
    }

    private void dispatchNormalCharacter(Component component, char c) {
        KeyboardMap kbMap = new KeyboardMap(c);
        List<CharSequence[]> keysList = kbMap.getKeys();
        if (keysList == null) {
            return;
        }
        for (CharSequence[] keys : keysList) {
            // Generate Key Press
            for (int i = 0; i < keys.length; i++) {
                CharSequence key = keys[i];
                if (deviceState.isModifier(key)) {
                    pressKey(component, (JavaAgentKeys) key);
                } else {
                    robotXkeyPress(Integer.parseInt(key.toString()));
                }
            }
            // Generate Key Release
            for (int i = keys.length - 1; i >= 0; i--) {
                CharSequence key = keys[i];
                if (deviceState.isModifier(key)) {
                    releaseKey(component, (JavaAgentKeys) key);
                } else {
                    robotXkeyRelease(Integer.parseInt(key.toString()));
                }
            }
        }
    }

    @Override public void pressKey(Component component, JavaAgentKeys keyToPress) {
        KeysMap keysMap = KeysMap.findMap(keyToPress);
        int keycode = keysMap.getCode();
        robotXkeyPress(keycode);
    }

    @Override public void releaseKey(Component component, JavaAgentKeys keyToRelease) {
        KeysMap keysMap = KeysMap.findMap(keyToRelease);
        int keycode = keysMap.getCode();
        robotXkeyRelease(keycode);
    }

    @Override public void buttonDown(Component component, Buttons button, int xoffset, int yoffset) {
        moveto(component, xoffset, yoffset);
        robotXmousePress(InputEvent.BUTTON1_MASK);
    }

    @Override public void buttonUp(Component component, Buttons button, int xoffset, int yoffset) {
        robotXmouseRelease(InputEvent.BUTTON1_MASK);
    }

    @Override public void moveto(final Component component) {
        Dimension d = EventQueueWait.exec(new Callable<Dimension>() {
            @Override public Dimension call() {
                return component.getSize();
            }
        });
        moveto(component, d.width / 2, d.height / 2);
    }

    @Override public void moveto(final Component component, int xoffset, int yoffset) {
        Point compLocation = EventQueueWait.exec(new Callable<Point>() {
            @Override public Point call() {
                return component.getLocationOnScreen();
            }
        });
        robotXmouseMove(compLocation.x + xoffset, compLocation.y + yoffset);
    }

    @Override public void click(Component component, Buttons button, int clickCount, int xoffset, int yoffset) {
        ensureVisible(component, new Rectangle(xoffset, yoffset, 50, 50));
        int b = InputEvent.BUTTON1_MASK;
        if (button.getButton() == 0)
            b = InputEvent.BUTTON1_MASK;
        else if (button.getButton() == 1)
            b = InputEvent.BUTTON2_MASK;
        else if (button.getButton() == 2)
            b = InputEvent.BUTTON3_MASK;
        Point compLocation = component.getLocationOnScreen();
        int x = compLocation.x + xoffset;
        int y = compLocation.y + yoffset;
        robotXmouseMove(x, y);
        for (int i = 0; i < clickCount; i++) {
            robotXmousePress(b);
            robotXmouseRelease(b);
        }
    }

    static Map<Integer, String> keyCodeToString = new HashMap<Integer, String>();
    static Map<Integer, String> buttonToString = new HashMap<Integer, String>();
    static {
        Field[] declaredFields = KeyEvent.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().startsWith("VK_"))
                try {
                    keyCodeToString.put(field.getInt(null), field.getName().substring(3));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        buttonToString.put(InputEvent.BUTTON1_MASK, "BUTTON1");
        buttonToString.put(InputEvent.BUTTON2_MASK, "BUTTON2");
        buttonToString.put(InputEvent.BUTTON3_MASK, "BUTTON3");
    }

    private void robotXmouseMove(int x, int y) {
        Logger.getLogger(RobotDevice.class.getName()).info("robot.mouseMove(" + x + "," + y + ")");
        try {
            robot.mouseMove(x, y);
        } catch (IllegalThreadStateException e) {
            try {
                // Invoked in EDT.
                // Sleep a while and hope for the best.
                Thread.sleep(50);
            } catch (InterruptedException e1) {
            }
        }
    }

    private void robotXmousePress(int b) {
        Logger.getLogger(RobotDevice.class.getName()).info("robot.mousePress(" + buttonToString.get(b) + ")");
        try {
            robot.mousePress(b);
        } catch (IllegalThreadStateException e) {
            try {
                // Invoked in EDT.
                // Sleep a while and hope for the best.
                Thread.sleep(50);
            } catch (InterruptedException e1) {
            }
        }
    }

    private void robotXmouseRelease(int b) {
        Logger.getLogger(RobotDevice.class.getName()).info("robot.mouseRelease(" + buttonToString.get(b) + ")");
        try {
            robot.mouseRelease(b);
        } catch (IllegalThreadStateException e) {
            try {
                // Invoked in EDT.
                // Sleep a while and hope for the best.
                Thread.sleep(50);
            } catch (InterruptedException e1) {
            }
        }
    }

    private void robotXsetAutoWaitForIdle(boolean b) {
        Logger.getLogger(RobotDevice.class.getName()).info("robot.setAutoWaitForIdle(" + b + ")");
        robot.setAutoWaitForIdle(b);
    }

    private void robotXkeyRelease(int keyCode) {
        String s = keyCodeToString.get(keyCode);
        if (s != null)
            Logger.getLogger(RobotDevice.class.getName()).info("robot.keyReleases(" + s + ")");
        else
            Logger.getLogger(RobotDevice.class.getName()).info("robot.keyReleases(" + keyCode + ")");
        try {
            robot.keyRelease(keyCode);
        } catch (IllegalThreadStateException e) {
            try {
                // Invoked in EDT.
                // Sleep a while and hope for the best.
                Thread.sleep(50);
            } catch (InterruptedException e1) {
            }
        }
    }

    private void robotXkeyPress(int keyCode) {
        String s = keyCodeToString.get(keyCode);
        if (s != null)
            Logger.getLogger(RobotDevice.class.getName()).info("robot.keyPress(" + s + ")");
        else
            Logger.getLogger(RobotDevice.class.getName()).info("robot.keyPress(" + keyCode + ")");
        try {
            robot.keyPress(keyCode);
        } catch (IllegalThreadStateException e) {
            try {
                // Invoked in EDT.
                // Sleep a while and hope for the best.
                Thread.sleep(50);
            } catch (InterruptedException e1) {
            }
        }
    }

    private void robotXsetAutoDelay() {
        int delay = 50;
        if (Platform.getCurrent().is(Platform.LINUX))
            delay = 50;
        delay = Integer.getInteger("marathon.robot.delay", delay);
        if (delay == 0)
            return;
        Logger.getLogger(RobotDevice.class.getName()).info("robot.setAutoDelay(" + delay + ")");
        robot.setAutoDelay(delay);
    }

}
