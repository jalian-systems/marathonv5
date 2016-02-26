package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RComponentFactory {
    private JSONOMapConfig omapConfig;

    private static class InstanceCheckFinder implements IRComponentFinder {
        private Class<? extends Node> componentKlass;
        private Class<? extends RComponent> rComponentKlass;

        public InstanceCheckFinder(Class<? extends Node> componentKlass, Class<? extends RComponent> javaElementKlass) {
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
        @Override public Class<? extends RComponent> get(Node component) {
            if (componentKlass.isInstance(component))
                return rComponentKlass;
            return null;
        }
    }

    private static LinkedList<IRComponentFinder> entries = new LinkedList<IRComponentFinder>();

    static {
    }

    public static void add(Class<? extends Node> componentKlass, Class<? extends RComponent> rComponentKlass) {
        add(new InstanceCheckFinder(componentKlass, rComponentKlass));
    }

    public static void add(IRComponentFinder f) {
        entries.addFirst(f);
    }

    public static void reset() {
        entries.clear();
        add(Node.class, RUnknownComponent.class);
    }

    static {
        reset();
    }

    public RComponentFactory(JSONOMapConfig objectMapConfiguration) {
        this.omapConfig = objectMapConfiguration;
    }

    public RComponent findRComponent(Node parent, Point2D point, IJSONRecorder recorder) {
        return findRawRComponent(getComponent(parent, point), point, recorder);
    }

    public RComponent findRawRComponent(Node source, Point2D point, IJSONRecorder recorder) {
        for (IRComponentFinder entry : entries) {
            Class<? extends RComponent> k = entry.get(source);
            if (k == null)
                continue;
            try {
                Constructor<? extends RComponent> cons = k.getConstructor(Node.class, JSONOMapConfig.class, Point2D.class,
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
