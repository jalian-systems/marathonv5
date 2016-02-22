package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;
import java.util.LinkedList;

import javax.swing.JInternalFrame;

public class ContextManager {

    private static class InstanceCheck implements IContextChecker {

        private Class<? extends Component> componentClass;

        public InstanceCheck(Class<? extends Component> klass) {
            componentClass = klass;
        }

        @Override public boolean isContext(Component c) {
            return componentClass.isInstance(c);
        }

    }

    private static LinkedList<IContextChecker> containers = new LinkedList<IContextChecker>();

    static {
        add(JInternalFrame.class);
    }

    public static void add(Class<? extends Component> klass) {
        add(new InstanceCheck(klass));
    }

    public static void add(IContextChecker e) {
        containers.addFirst(e);
    }

    public static boolean isContext(Component c) {
        for (IContextChecker checker : containers) {
            if (checker.isContext(c))
                return true;
        }
        return false;
    }

}
