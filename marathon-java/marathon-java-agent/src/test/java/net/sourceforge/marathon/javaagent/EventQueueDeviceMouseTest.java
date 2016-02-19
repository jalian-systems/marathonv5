package net.sourceforge.marathon.javaagent;

import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.Device.Type;

@Test public class EventQueueDeviceMouseTest extends DeviceMouseTest {

    public EventQueueDeviceMouseTest() {
        super(Type.EVENT_QUEUE);
    }

}
