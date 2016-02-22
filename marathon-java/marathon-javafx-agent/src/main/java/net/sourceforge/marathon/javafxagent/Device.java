package net.sourceforge.marathon.javafxagent;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public abstract class Device implements IDevice {

    public enum Type {
        EVENT_QUEUE, ROBOT
    }

    public static Device getDevice() {
        return new EventQueueDevice();
    }

    public static Device getDevice(Type t) {
        Logger.getLogger(Device.class.getName()).info("Creating a " + t + " device");
        return t == Type.EVENT_QUEUE ? new EventQueueDevice() : new RobotDevice();
    }

    public static Component getActiveComponent(Component component) {
        final Component comp = component;
        Dimension d = EventQueueWait.exec(new Callable<Dimension>() {
            @Override public Dimension call() {
                return comp.getSize();
            }
        });
        int x = d.width / 2;
        int y = d.height / 2;
        return Device.getActiveComponent(component, x, y);
    }

    public static Component getActiveComponent(Component component, int x, int y) {
        Component componentx = SwingUtilities.getDeepestComponentAt(component, x, y);
        if (componentx == null)
            return component;
        return componentx;
    }

    protected void ensureVisible(Component component, final Rectangle d) {
        final JComponent parent = getSwingParent(component);
        if (parent == null)
            return;
        if (parent != component) {
            Point point = SwingUtilities.convertPoint(component, d.x, d.y, parent);
            d.x = point.x;
            d.y = point.y;
        }
        EventQueueWait.exec(new Runnable() {
            @Override public void run() {
                parent.scrollRectToVisible(d);
            }
        });
    }

    private JComponent getSwingParent(Component component) {
        while (component != null && !(component instanceof JComponent))
            component = component.getParent();
        return (JComponent) component;
    }

}
