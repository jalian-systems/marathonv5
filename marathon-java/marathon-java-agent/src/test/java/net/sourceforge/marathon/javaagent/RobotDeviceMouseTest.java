package net.sourceforge.marathon.javaagent;

import net.sourceforge.marathon.javaagent.Device.Type;

import org.testng.annotations.Test;

@Test public class RobotDeviceMouseTest extends DeviceMouseTest {

    public RobotDeviceMouseTest() {
        super(Type.ROBOT);
    }

}
