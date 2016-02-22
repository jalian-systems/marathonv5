package net.sourceforge.marathon.javafxagent;

import org.testng.annotations.Test;

import net.sourceforge.marathon.javafxagent.Device.Type;

@Test public class EventQueueDeviceMouseTest extends DeviceMouseTest {

    public EventQueueDeviceMouseTest() {
        super(Type.EVENT_QUEUE);
    }

}
