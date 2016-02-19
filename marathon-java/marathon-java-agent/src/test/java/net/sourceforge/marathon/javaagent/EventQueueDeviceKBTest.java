package net.sourceforge.marathon.javaagent;

import net.sourceforge.marathon.javaagent.Device.Type;

import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test public class EventQueueDeviceKBTest extends DeviceKBTest {

    public EventQueueDeviceKBTest() {
        super(Type.EVENT_QUEUE);
    }

    public void whetherMenusAreAccessible() throws Throwable {
        if (Platform.getCurrent() == Platform.MAC && System.getProperty("java.version", "").matches("1.[78].*")) {
            throw new SkipException("Menu mneomonics are not handled on Mac under Java 1.7+");
        }
        exitItemCalled = false;
        driver.sendKeys(textField, JavaAgentKeys.chord(JavaAgentKeys.ALT, "f"));
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return menu.isPopupMenuVisible();
            }
        };
        driver.sendKeys(textField, JavaAgentKeys.ENTER);
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return exitItemCalled;
            }
        };
        AssertJUnit.assertEquals(true, exitItemCalled);
    }

}
