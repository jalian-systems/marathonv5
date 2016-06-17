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

import net.sourceforge.marathon.javaagent.Device.Type;
import net.sourceforge.marathon.javaagent.IDevice.Buttons;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test public class EventQueueDeviceKBTest extends DeviceKBTest {

    public EventQueueDeviceKBTest() {
        super(Type.EVENT_QUEUE);
    }

    public void whetherMenusAreAccessible() throws Throwable {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override public void eventDispatched(AWTEvent event) {
                System.err.println(event);
            }
        }, AWTEvent.KEY_EVENT_MASK);
        if (Platform.getCurrent() == Platform.MAC && System.getProperty("java.version", "").matches("1.[78].*")) {
            throw new SkipException("Menu mneomonics are not handled on Mac under Java 1.7+");
        }
        exitItemCalled = false;
        driver.sendKeys(textField, JavaAgentKeys.chord(JavaAgentKeys.ALT, "f"));
        new WaitWithoutException("Waiting for exit item to be shown") {
            @Override public boolean until() {
                return exitItem.isShowing();
            }
        };
        driver.click(exitItem, Buttons.LEFT, 1, 0, 0);
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return exitItemCalled;
            }
        };
        AssertJUnit.assertEquals(true, exitItemCalled);
    }

    public void sendingEnterToMenuItemDoesntWork() throws Throwable {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override public void eventDispatched(AWTEvent event) {
                System.err.println(event);
            }
        }, AWTEvent.KEY_EVENT_MASK);
        if (Platform.getCurrent() == Platform.MAC && System.getProperty("java.version", "").matches("1.[78].*")) {
            throw new SkipException("Menu mneomonics are not handled on Mac under Java 1.7+");
        }
        if (Platform.getCurrent().is(Platform.WINDOWS)) {
            throw new SkipException("Sending ENTER to menuitem is not working on Windows");
        }
        exitItemCalled = false;
        driver.sendKeys(textField, JavaAgentKeys.chord(JavaAgentKeys.ALT, "f"));
        new WaitWithoutException("Waiting for exit item to be shown") {
            @Override public boolean until() {
                return exitItem.isShowing();
            }
        };
        driver.sendKeys(exitItem, JavaAgentKeys.ENTER);
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return exitItemCalled;
            }
        };
        AssertJUnit.assertEquals(true, exitItemCalled);
    }

}
