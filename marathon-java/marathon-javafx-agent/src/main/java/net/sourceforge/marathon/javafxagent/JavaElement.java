package net.sourceforge.marathon.javafxagent;

import java.awt.Component;

import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JavaElement extends AbstractJavaElement {

    public JavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }
}
