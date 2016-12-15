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

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import net.sourceforge.marathon.javaagent.Device.Type;

public abstract class DeviceTest {

    public static abstract class WaitWithoutException extends Wait {

        private static final int OVERRIDDEN_TIMEOUT = 5000;

        public WaitWithoutException(String message) {
            super(message);
        }

        @Override public void wait(String message, long timeoutInMilliseconds, long intervalInMilliseconds) {
            timeoutInMilliseconds = OVERRIDDEN_TIMEOUT;
            long lastTimeInMillis = System.currentTimeMillis();
            while (true) {
                try {
                    super.wait(message, timeoutInMilliseconds, intervalInMilliseconds);
                    return;
                } catch (WaitTimedOutException e) {
                    Logger.getLogger(DeviceTest.class.getName()).warning(e.getMessage());
                    break;
                } catch (Throwable e) {
                    Logger.getLogger(DeviceTest.class.getName()).warning(e.getClass().getName() + ": " + e.getMessage());
                    long currentTimeMillis = System.currentTimeMillis();
                    timeoutInMilliseconds = 5000 - (currentTimeMillis - lastTimeInMillis);
                    if (timeoutInMilliseconds <= 0) {
                        break;
                    }
                }
            }
        }
    }

    public static class KeyStrokeHolder {
        private List<KeyStroke> keystrokes = new ArrayList<KeyStroke>();

        public synchronized void add(KeyStroke keyStrokeForEvent) {
            keystrokes.add(keyStrokeForEvent);
        }

        public synchronized void clear() {
            keystrokes.clear();
        }

        public synchronized int size() {
            return keystrokes.size();
        }

        @Override public synchronized String toString() {
            return keystrokes.toString();
        }
    }

    protected JDialog dialog;
    protected JTextField textField;
    protected JButton button;
    protected IDevice driver;
    protected KeyStrokeHolder kss = new KeyStrokeHolder();
    protected JMenu menu;
    protected JMenuItem exitItem;
    protected JCheckBox checkBox;
    protected Box boxPanel;
    private TextField awtTextField;
    private Type type;
    protected volatile boolean altDown;
    protected volatile boolean controlDown;
    protected volatile boolean metaDown;
    protected volatile boolean shiftDown;
    protected volatile boolean altGraphDown;
    protected volatile boolean textFieldClicked;
    protected volatile boolean popupTriggerClicked;
    protected volatile boolean popupTriggerPressed;
    protected volatile boolean popupTriggerReleased;
    protected volatile boolean exitItemCalled;
    protected volatile int clickedButton;
    protected volatile boolean buttonClicked;
    protected volatile int pressedButton;
    protected volatile boolean buttonPressed;
    protected volatile boolean buttonReleased;
    protected volatile int maxClickCount;
    protected StringBuilder mouseText = new StringBuilder();
    protected StringBuilder tfMouseText = new StringBuilder();

    public DeviceTest(Device.Type type) {
        this.type = type;
    }

    @BeforeMethod public void showDialog() throws Throwable {
        driver = Device.getDevice(type);
        altDown = false;
        controlDown = false;
        metaDown = false;
        shiftDown = false;
        altGraphDown = false;
        textFieldClicked = false;
        popupTriggerClicked = false;
        popupTriggerPressed = false;
        popupTriggerReleased = false;
        exitItemCalled = false;
        buttonClicked = false;
        buttonPressed = false;
        buttonReleased = false;
        clickedButton = -1;
        pressedButton = -1;
        maxClickCount = -1;

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                dialog = new JDialog();
                JMenuBar menuBar = new JMenuBar();
                menu = new JMenu("File");
                menu.setMnemonic(KeyEvent.VK_F);
                menuBar.add(menu);
                exitItem = new JMenuItem("Exit");
                exitItem.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        exitItemCalled = true;
                    }
                });
                menu.add(exitItem);
                dialog.setJMenuBar(menuBar);
                boxPanel = new Box(BoxLayout.Y_AXIS);
                boxPanel.setName("box-panel");
                textField = new JTextField("");
                textField.setName("text");
                textField.addKeyListener(new KeyAdapter() {
                    @Override public void keyPressed(KeyEvent e) {
                        kss.add(KeyStroke.getKeyStrokeForEvent(e));
                    }

                    @Override public void keyReleased(KeyEvent e) {
                        kss.add(KeyStroke.getKeyStrokeForEvent(e));
                    }

                    @Override public void keyTyped(KeyEvent e) {
                        kss.add(KeyStroke.getKeyStrokeForEvent(e));
                    }
                });
                textField.addMouseListener(new MouseListener() {
                    @Override public void mousePressed(MouseEvent e) {
                        tfMouseText.append(getModifiersExText(e.getModifiersEx()) + "pressed ");
                    }

                    @Override public void mouseReleased(MouseEvent e) {
                        tfMouseText.append(getModifiersExText(e.getModifiersEx()) + "released ");
                    }

                    @Override public void mouseEntered(MouseEvent e) {
                        tfMouseText.append(getModifiersExText(e.getModifiersEx()) + "entered ");
                    }

                    @Override public void mouseExited(MouseEvent e) {
                        tfMouseText.append(getModifiersExText(e.getModifiersEx()) + "exited ");
                    }

                    @Override public void mouseClicked(MouseEvent e) {
                        textFieldClicked = true;
                        tfMouseText.append(getModifiersExText(e.getModifiersEx()) + "clicked ");
                    }
                });
                boxPanel.add(textField);
                button = new JButton("Click Me!!");
                button.setName("click-me");
                button.addMouseListener(new MouseListener() {
                    @Override public void mouseClicked(MouseEvent e) {
                        String s = "clicked";
                        if (e.isPopupTrigger()) {
                            s = "contextclicked";
                        }
                        mouseText.append(getModifiersExText(e.getModifiersEx()) + s + "(" + e.getClickCount() + ") ");
                        buttonClicked = true;
                        popupTriggerClicked = e.isPopupTrigger();
                        clickedButton = e.getButton();
                        altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
                        controlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                        metaDown = (e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0;
                        shiftDown = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
                        altGraphDown = (e.getModifiersEx() & InputEvent.ALT_GRAPH_DOWN_MASK) != 0;
                        maxClickCount = e.getClickCount();
                    }

                    @Override public void mousePressed(MouseEvent e) {
                        buttonClicked = false;
                        buttonPressed = true;
                        pressedButton = e.getButton();
                        popupTriggerPressed = e.isPopupTrigger();
                        altDown = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
                        controlDown = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
                        metaDown = (e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0;
                        shiftDown = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
                        altGraphDown = (e.getModifiersEx() & InputEvent.ALT_GRAPH_DOWN_MASK) != 0;
                        mouseText.append(getModifiersExText(e.getModifiersEx()) + "pressed ");
                    }

                    @Override public void mouseReleased(MouseEvent e) {
                        buttonReleased = true;
                        popupTriggerReleased = e.isPopupTrigger();
                        mouseText.append(getModifiersExText(e.getModifiersEx()) + "released ");
                    }

                    @Override public void mouseEntered(MouseEvent e) {
                        mouseText.append(getModifiersExText(e.getModifiersEx()) + "entered ");
                    }

                    @Override public void mouseExited(MouseEvent e) {
                        mouseText.append(getModifiersExText(e.getModifiersEx()) + "exited ");
                    }
                });
                boxPanel.add(button);
                checkBox = new JCheckBox("Check Me!!");
                checkBox.setName("check-me");
                boxPanel.add(checkBox);
                awtTextField = new TextField();
                awtTextField.setName("awt-text");
                boxPanel.add(awtTextField);
                dialog.setContentPane(boxPanel);
                dialog.pack();
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
            }
        });
        EventQueueWait.empty();
        EventQueueWait.requestFocus(textField);
        EventQueueWait.empty();
    }

    @AfterMethod public void disposeDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                dialog.dispose();
            }
        });
        EventQueueWait.empty();
    }

    protected String getModifiersExText(int modifiers) {

        StringBuffer buf = new StringBuffer();
        if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
            buf.append("Meta");
            buf.append("+");
        }
        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
            buf.append("Ctrl");
            buf.append("+");
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
            buf.append("Alt");
            buf.append("+");
        }
        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
            buf.append("Shift");
            buf.append("+");
        }
        if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
            buf.append("Alt Graph");
            buf.append("+");
        }
        if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0) {
            buf.append("Button1");
            buf.append("+");
        }
        if ((modifiers & InputEvent.BUTTON2_DOWN_MASK) != 0) {
            buf.append("Button2");
            buf.append("+");
        }
        if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0) {
            buf.append("Button3");
            buf.append("+");
        }
        return buf.toString();
    }
}
