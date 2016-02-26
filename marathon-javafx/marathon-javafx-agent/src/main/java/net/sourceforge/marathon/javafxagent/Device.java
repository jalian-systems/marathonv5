package net.sourceforge.marathon.javafxagent;

import java.util.logging.Logger;

public abstract class Device {

    public enum Type {
        EVENT_QUEUE, ROBOT
    }

    public static IDevice getDevice() {
        return new FXEventQueueDevice();
    }

    public static IDevice getDevice(Type t) {
        Logger.getLogger(Device.class.getName()).info("Creating a " + t + " device");
        return t == Type.EVENT_QUEUE ? new FXEventQueueDevice() : new FXRobotDevice();
    }

}
