package net.sourceforge.marathon.javafxagent;

import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.Platform;
import net.sourceforge.marathon.javafxagent.Device.Type;
import net.sourceforge.marathon.javafxagent.IDevice.Buttons;

@Test public class RobotDeviceKBTest extends DeviceKBTest {

    public RobotDeviceKBTest() {
        super(Type.ROBOT);
    }

    @BeforeMethod public void clickOnTextField() {
        driver.click(textField, Buttons.LEFT, 1, 0, 0);
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
        driver.sendKeys(exitItem, JavaAgentKeys.ENTER);
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return exitItemCalled;
            }
        };
        AssertJUnit.assertEquals(true, exitItemCalled);
    }

}
