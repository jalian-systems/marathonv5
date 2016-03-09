package net.sourceforge.marathon.javafxagent;

import java.lang.reflect.Constructor;
import java.util.LinkedList;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTextInputControlElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXToggleButtonElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXTreeViewElement;

public class JavaFXElementFactory {

    public static IJavaFXElement createElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        IJavaFXElement found = window.findElementFromMap(component);
        if (found != null) {
            return found;
        }
        Class<? extends IJavaFXElement> klass = get(component);
        if (klass == null)
            return new JavaFXElement(component, driver, window);
        try {
            Constructor<? extends IJavaFXElement> constructor = klass.getConstructor(Node.class, IJavaFXAgent.class,
                    JFXWindow.class);
            IJavaFXElement newInstance = constructor.newInstance(component, driver, window);
            return newInstance;
        } catch (Exception e) {
            throw new RuntimeException("createElement failed", e);
        }
    }

    private static class InstanceCheckFinder implements IJavaElementFinder {
        private Class<? extends Node> componentKlass;
        private Class<? extends IJavaFXElement> javaElementKlass;

        public InstanceCheckFinder(Class<? extends Node> componentKlass, Class<? extends IJavaFXElement> javaElementKlass) {
            this.componentKlass = componentKlass;
            this.javaElementKlass = javaElementKlass;
        }

        @Override public Class<? extends IJavaFXElement> get(Node component) {
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
        add(CheckBox.class, JavaFXCheckBoxElement.class);
        add(ToggleButton.class, JavaFXToggleButtonElement.class);
        add(TreeView.class, JavaFXTreeViewElement.class);
    }

    public static Class<? extends IJavaFXElement> get(Node component) {
        for (IJavaElementFinder entry : entries) {
            Class<? extends IJavaFXElement> k = entry.get(component);
            if (k != null)
                return k;
        }
        return null;
    }

    public static void add(Class<? extends Node> component, Class<? extends IJavaFXElement> javaelement) {
        add(new InstanceCheckFinder(component, javaelement));
    }

    public static void add(IJavaElementFinder e) {
        entries.addFirst(e);
    }
}
