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
