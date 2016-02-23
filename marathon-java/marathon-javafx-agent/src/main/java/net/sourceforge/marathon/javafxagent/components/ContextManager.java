package net.sourceforge.marathon.javafxagent.components;

import java.util.LinkedList;

import javafx.scene.Node;

public class ContextManager {

    private static class InstanceCheck implements IContextChecker {

        private Class<? extends Node> componentClass;

        public InstanceCheck(Class<? extends Node> klass) {
            componentClass = klass;
        }

        @Override public boolean isContext(Node c) {
            return componentClass.isInstance(c);
        }

    }

    private static LinkedList<IContextChecker> containers = new LinkedList<IContextChecker>();

    static {
        add(new IContextChecker() {
            @Override public boolean isContext(Node c) {
                return c.getScene().getRoot() == c;
            }
        });
    }

    public static void add(Class<? extends Node> klass) {
        add(new InstanceCheck(klass));
    }

    public static void add(IContextChecker e) {
        containers.addFirst(e);
    }

    public static boolean isContext(Node c) {
        for (IContextChecker checker : containers) {
            if (checker.isContext(c))
                return true;
        }
        return false;
    }

}
