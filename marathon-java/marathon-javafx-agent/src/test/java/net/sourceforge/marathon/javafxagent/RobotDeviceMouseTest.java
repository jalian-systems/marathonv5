package net.sourceforge.marathon.javafxagent;

import org.testng.annotations.Test;

import net.sourceforge.marathon.javafxagent.Device.Type;

@Test public class RobotDeviceMouseTest extends DeviceMouseTest {

    public RobotDeviceMouseTest() {
        super(Type.ROBOT);
    }

}
