package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXComponentFactory {
    private JSONOMapConfig omapConfig;

    private static class InstanceCheckFinder implements IRComponentFinder {
        private Class<? extends Node> componentKlass;
        private Class<? extends RFXComponent> rComponentKlass;

        public InstanceCheckFinder(Class<? extends Node> componentKlass, Class<? extends RFXComponent> javaElementKlass) {
            this.componentKlass = componentKlass;
            this.rComponentKlass = javaElementKlass;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * net.sourceforge.marathon.javaagent.IJavaElementFinder#get(java.awt
         * .Node)
         */
        @Override public Class<? extends RFXComponent> get(Node component) {
            if (componentKlass.isInstance(component))
                return rComponentKlass;
            return null;
        }
    }

    private static LinkedList<IRComponentFinder> entries = new LinkedList<IRComponentFinder>();

    static {
    }

    public static void add(Class<? extends Node> componentKlass, Class<? extends RFXComponent> rComponentKlass) {
        add(new InstanceCheckFinder(componentKlass, rComponentKlass));
    }

    public static void add(IRComponentFinder f) {
        entries.addFirst(f);
    }

    public static void reset() {
        entries.clear();
        add(Node.class, RFXUnknownComponent.class);
        add(TextInputControl.class, RFXTextInputControl.class);
    }

    static {
        reset();
    }

    public RFXComponentFactory(JSONOMapConfig objectMapConfiguration) {
        this.omapConfig = objectMapConfiguration;
    }

    public RFXComponent findRComponent(Node parent, Point2D point, IJSONRecorder recorder) {
        return findRawRComponent(getComponent(parent, point), point, recorder);
    }

    public RFXComponent findRawRComponent(Node source, Point2D point, IJSONRecorder recorder) {
        for (IRComponentFinder entry : entries) {
            Class<? extends RFXComponent> k = entry.get(source);
            if (k == null)
                continue;
            try {
                Constructor<? extends RFXComponent> cons = k.getConstructor(Node.class, JSONOMapConfig.class, Point2D.class,
                        IJSONRecorder.class);
                return cons.newInstance(source, omapConfig, point, recorder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Node getComponent(Node component, Point2D location) {
        return component;
    }

}
