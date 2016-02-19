package net.sourceforge.marathon.javaagent;

import java.awt.Component;

import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JavaElement extends AbstractJavaElement {

    public JavaElement(Component component, JavaAgent driver, JWindow window) {
        super(component, driver, window);
    }
}
