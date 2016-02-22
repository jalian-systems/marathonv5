package net.sourceforge.marathon.javafxagent;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.util.LinkedList;

import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JavaElementFactory {

    public static IJavaElement createElement(Component component, IJavaAgent driver, JWindow window) {
        IJavaElement found = window.findElementFromMap(component);
        if (found != null) {
            return found;
        }
        Class<? extends IJavaElement> klass = get(component);
        if (klass == null)
            return new JavaElement(component, driver, window);
        try {
            Constructor<? extends IJavaElement> constructor = klass.getConstructor(Component.class, IJavaAgent.class, JWindow.class);
            IJavaElement newInstance = constructor.newInstance(component, driver, window);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("createElement failed", e);
        }
    }

    private static class InstanceCheckFinder implements IJavaElementFinder {
        private Class<? extends Component> componentKlass;
        private Class<? extends IJavaElement> javaElementKlass;

        public InstanceCheckFinder(Class<? extends Component> componentKlass, Class<? extends IJavaElement> javaElementKlass) {
            this.componentKlass = componentKlass;
            this.javaElementKlass = javaElementKlass;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * net.sourceforge.marathon.javaagent.IJavaElementFinder#get(java.awt
         * .Component)
         */
        @Override public Class<? extends IJavaElement> get(Component component) {
            if (componentKlass.isInstance(component))
                return javaElementKlass;
            return null;
        }
    }

    private static LinkedList<IJavaElementFinder> entries = new LinkedList<IJavaElementFinder>();

    static {
        reset();
    }

    public static void reset() {
        add(Component.class, JavaElement.class);
    }

    public static Class<? extends IJavaElement> get(Component component) {
        for (IJavaElementFinder entry : entries) {
            Class<? extends IJavaElement> k = entry.get(component);
            if (k != null)
                return k;
        }
        return null;
    }

    public static void add(Class<? extends Component> component, Class<? extends IJavaElement> javaelement) {
        add(new InstanceCheckFinder(component, javaelement));
    }

    public static void add(IJavaElementFinder e) {
        entries.addFirst(e);
    }
}
