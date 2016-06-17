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

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.logging.Logger;

import net.sourceforge.marathon.javaagent.Device.Type;
import net.sourceforge.marathon.javaagent.IDevice.Buttons;

import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test public abstract class DeviceMouseTest extends DeviceTest {

    public DeviceMouseTest(Type type) {
        super(type);
    }

    public void click() {
        try {
            buttonClicked = false;
            EventQueueWait.requestFocus(button);
            driver.click(button, Buttons.LEFT, 1, 0, 0);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return buttonClicked;
                }
            };
            AssertJUnit.assertTrue(buttonClicked);
            AssertJUnit.assertEquals(MouseEvent.BUTTON1, clickedButton);
            AssertJUnit.assertFalse(altDown);
            AssertJUnit.assertFalse(metaDown);
            AssertJUnit.assertFalse(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
            AssertJUnit.assertEquals(1, maxClickCount);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            moveCursorAway();
        }
    }

    public void clickWithAlt() throws Throwable {
        try {
            mouseText.setLength(0);
            EventQueueWait.requestFocus(button);
            driver.pressKey(button, JavaAgentKeys.ALT);
            driver.click(button, Buttons.LEFT, 1, 0, 0);
            driver.releaseKey(button, JavaAgentKeys.ALT);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return buttonClicked;
                }
            };
            AssertJUnit.assertTrue(buttonClicked);
            AssertJUnit.assertEquals(MouseEvent.BUTTON1, clickedButton);
            AssertJUnit.assertTrue(altDown);
            AssertJUnit.assertFalse(metaDown);
            AssertJUnit.assertFalse(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
            AssertJUnit.assertEquals(1, maxClickCount);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            moveCursorAway();
        }
    }

    public void clickWithAltControl() throws Throwable {
        try {
            mouseText.setLength(0);
            EventQueueWait.requestFocus(button);
            driver.pressKey(button, JavaAgentKeys.ALT);
            driver.pressKey(button, JavaAgentKeys.CONTROL);
            driver.click(button, Buttons.LEFT, 1, 0, 0);
            driver.releaseKey(button, JavaAgentKeys.CONTROL);
            driver.releaseKey(button, JavaAgentKeys.ALT);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return buttonClicked;
                }
            };
            AssertJUnit.assertTrue(buttonClicked);
            AssertJUnit.assertEquals(MouseEvent.BUTTON1, clickedButton);
            AssertJUnit.assertTrue(altDown);
            AssertJUnit.assertFalse(metaDown);
            AssertJUnit.assertTrue(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
            AssertJUnit.assertEquals(1, maxClickCount);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            moveCursorAway();
        }
    }

    public void doubleClick() {
        try {
            mouseText.setLength(0);
            buttonClicked = false;
            EventQueueWait.requestFocus(button);
            driver.click(button, Buttons.LEFT, 2, 0, 0);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return maxClickCount == 2;
                }
            };
            AssertJUnit.assertTrue(buttonClicked);
            AssertJUnit.assertEquals(MouseEvent.BUTTON1, clickedButton);
            AssertJUnit.assertFalse(altDown);
            AssertJUnit.assertFalse(metaDown);
            AssertJUnit.assertFalse(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
            AssertJUnit.assertEquals(2, maxClickCount);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            moveCursorAway();
        }
    }

    public void mouseDown() throws Throwable {
        try {
            mouseText.setLength(0);
            EventQueueWait.requestFocus(button);
            driver.buttonDown(button, Buttons.LEFT, 0, 0);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return buttonPressed;
                }
            };
            AssertJUnit.assertFalse(buttonClicked);
            AssertJUnit.assertTrue(buttonPressed);
            AssertJUnit.assertEquals(MouseEvent.BUTTON1, pressedButton);
            AssertJUnit.assertFalse(altDown);
            AssertJUnit.assertFalse(metaDown);
            AssertJUnit.assertFalse(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            driver.buttonUp(button, Buttons.LEFT, 0, 0);
            moveCursorAway();
        }
    }

    public void mouseUp() {
        try {
            mouseText.setLength(0);
            EventQueueWait.requestFocus(button);
            driver.buttonDown(button, Buttons.LEFT, 0, 0);
            driver.buttonUp(button, Buttons.LEFT, 0, 0);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return buttonClicked;
                }
            };
            AssertJUnit.assertTrue(buttonClicked);
            AssertJUnit.assertTrue(buttonPressed);
            AssertJUnit.assertTrue(buttonReleased);
            AssertJUnit.assertEquals(MouseEvent.BUTTON1, clickedButton);
            AssertJUnit.assertFalse(altDown);
            AssertJUnit.assertFalse(metaDown);
            AssertJUnit.assertFalse(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            moveCursorAway();
        }
    }

    public void mouseMoveCoordinatesLongLong() throws Throwable {
        try {
            EventQueueWait.requestFocus(button);
            final StringBuilder moveText = new StringBuilder();
            button.addMouseMotionListener(new MouseMotionListener() {
                @Override public void mouseDragged(MouseEvent e) {
                    moveText.append("dragged ");
                }

                @Override public void mouseMoved(MouseEvent e) {
                    moveText.append("moved(" + e.getX() + "," + e.getY() + ") ");
                }
            });
            driver.moveto(button, 2, 6);
            driver.moveto(button, 8, 8);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return moveText.toString().contains("moved(8,8)");
                }
            };
            moveText.setLength(0);
            driver.moveto(button, 3, 3);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return "moved(3,3)".equals(moveText.toString().trim());
                }
            };
            driver.moveto(button, 5, 10);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return "moved(3,3) moved(5,10)".equals(moveText.toString().trim());
                }
            };
            AssertJUnit.assertEquals("moved(3,3) moved(5,10)", moveText.toString().trim());
        } finally {
            moveCursorAway();
        }
    }

    public void mouseMoveCoordinatesLongLongWithButtonPress() throws Throwable {
        try {
            EventQueueWait.requestFocus(button);
            driver.buttonDown(button, Buttons.LEFT, 10, 8);
            final StringBuilder moveText = new StringBuilder();
            button.addMouseMotionListener(new MouseMotionListener() {
                @Override public void mouseDragged(MouseEvent e) {
                    moveText.append("dragged(" + e.getX() + "," + e.getY() + ") ");
                }

                @Override public void mouseMoved(MouseEvent e) {
                    moveText.append("moved(" + e.getX() + "," + e.getY() + ") ");
                }
            });
            driver.moveto(button, 3, 3);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return "dragged(3,3)".equals(moveText.toString().trim());
                }
            };
            driver.moveto(button, 5, 10);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return "dragged(3,3) dragged(5,10)".equals(moveText.toString().trim());
                }
            };
            AssertJUnit.assertEquals("dragged(3,3) dragged(5,10)", moveText.toString().trim());
        } finally {
            driver.buttonUp(button, Buttons.LEFT, 0, 0);
            moveCursorAway();
        }
    }

    public void contextClick() throws Throwable {
        try {
            buttonClicked = false;
            EventQueueWait.requestFocus(button);
            driver.click(button, Buttons.RIGHT, 1, 3, 3);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return buttonClicked;
                }
            };
            AssertJUnit.assertTrue(buttonClicked);
            AssertJUnit.assertEquals(MouseEvent.BUTTON3, clickedButton);
            AssertJUnit.assertFalse(popupTriggerClicked);
            AssertJUnit.assertTrue(popupTriggerPressed | popupTriggerReleased);
            AssertJUnit.assertFalse(altDown);
            AssertJUnit.assertTrue(metaDown);
            AssertJUnit.assertFalse(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
            AssertJUnit.assertEquals(1, maxClickCount);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            moveCursorAway();
        }
    }

    public void altContextClick() {
        try {
            buttonClicked = false;
            EventQueueWait.requestFocus(button);
            driver.pressKey(button, JavaAgentKeys.ALT);
            driver.click(button, Buttons.RIGHT, 1, 0, 0);
            driver.releaseKey(button, JavaAgentKeys.ALT);
            new DeviceTest.WaitWithoutException("Waiting for events to be processed") {
                @Override public boolean until() {
                    return buttonClicked;
                }
            };
            AssertJUnit.assertTrue(buttonClicked);
            AssertJUnit.assertEquals(MouseEvent.BUTTON3, clickedButton);
            AssertJUnit.assertFalse(popupTriggerClicked);
            AssertJUnit.assertTrue(altDown);
            AssertJUnit.assertFalse(metaDown);
            AssertJUnit.assertFalse(controlDown);
            AssertJUnit.assertFalse(shiftDown);
            AssertJUnit.assertFalse(altGraphDown);
            AssertJUnit.assertEquals(1, maxClickCount);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(mouseText.toString());
            moveCursorAway();
        }
    }

    public void clickOnTextfield() throws Throwable {
        try {
            textFieldClicked = false;
            driver.click(textField, Buttons.LEFT, 1, 1, 1);
            new DeviceTest.WaitWithoutException("Waiting for the text field to be clicked") {
                @Override public boolean until() {
                    return textFieldClicked;
                }
            };
            AssertJUnit.assertTrue(textFieldClicked);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(tfMouseText.toString());
        }
    }

    public void contextclickOnTextfield() throws Throwable {
        if (Platform.getCurrent().is(Platform.MAC))
            throw new SkipException("Context clicking a textfield on mac and java 1.7+ doesn't work and don't know why!!!");
        try {
            Thread.sleep(10000);
            textFieldClicked = false;
            driver.click(textField, Buttons.RIGHT, 1, 1, 1);
            new DeviceTest.WaitWithoutException("Waiting for the text field to be clicked") {
                @Override public boolean until() {
                    return textFieldClicked;
                }
            };
            AssertJUnit.assertTrue(textFieldClicked);
        } finally {
            Logger.getLogger(DeviceMouseTest.class.getName()).info(tfMouseText.toString());
        }
    }

    private void moveCursorAway() {
        textFieldClicked = false;
        driver.click(textField, Buttons.RIGHT, 1, 1, 1);
        driver.click(textField, Buttons.LEFT, 1, 1, 1);
        new DeviceTest.WaitWithoutException("Waiting for the text field to be clicked") {
            @Override public boolean until() {
                return textFieldClicked;
            }
        };
    }

}
