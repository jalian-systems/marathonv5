package net.sourceforge.marathon.javafxagent;

import java.util.logging.Logger;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

public abstract class Device implements IDevice {

    public enum Type {
        EVENT_QUEUE, ROBOT
    }

    public static Device getDevice() {
        return new FXEventQueueDevice();
    }

    public static Device getDevice(Type t) {
        Logger.getLogger(Device.class.getName()).info("Creating a " + t + " device");
        return t == Type.EVENT_QUEUE ? new FXEventQueueDevice() : new FXRobotDevice();
    }

    public static Node getActiveComponent(Node component) {
        return component;
    }

    public static Node getActiveComponent(Node component, int x, int y) {
        return component;
    }

    protected void ensureVisible(Node component, final Rectangle2D d) {
    }

}
