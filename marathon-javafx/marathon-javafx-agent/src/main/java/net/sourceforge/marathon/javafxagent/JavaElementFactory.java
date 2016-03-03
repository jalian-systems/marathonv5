package net.sourceforge.marathon.javafxagent;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javafxagent.components.JavaFXTextInputControlElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTreeViewElement;

public class JavaElementFactory {

    public static IJavaElement createElement(Node component, IJavaAgent driver, JWindow window) {
        IJavaElement found = window.findElementFromMap(component);
        if (found != null) {
            return found;
        }
        Class<? extends IJavaElement> klass = get(component);
        if (klass == null)
            return new JavaFXElement(component, driver, window);
        try {
            Constructor<? extends IJavaElement> constructor = klass.getConstructor(Node.class, IJavaAgent.class, JWindow.class);
            IJavaElement newInstance = constructor.newInstance(component, driver, window);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("createElement failed", e);
        }
    }

    private static class InstanceCheckFinder implements IJavaElementFinder {
        private Class<? extends Node> componentKlass;
        private Class<? extends IJavaElement> javaElementKlass;

        public InstanceCheckFinder(Class<? extends Node> componentKlass, Class<? extends IJavaElement> javaElementKlass) {
            this.componentKlass = componentKlass;
            this.javaElementKlass = javaElementKlass;
        }

        @Override public Class<? extends IJavaElement> get(Node component) {
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
        add(Node.class, JavaFXElement.class);
        add(TextInputControl.class, JavaFXTextInputControlElement.class);
        add(TreeView.class, JavaFXTreeViewElement.class);
    }

    public static Class<? extends IJavaElement> get(Node component) {
        for (IJavaElementFinder entry : entries) {
            Class<? extends IJavaElement> k = entry.get(component);
            if (k != null)
                return k;
        }
        return null;
    }

    public static void add(Class<? extends Node> component, Class<? extends IJavaElement> javaelement) {
        add(new InstanceCheckFinder(component, javaelement));
    }

    public static void add(IJavaElementFinder e) {
        entries.addFirst(e);
    }
}
