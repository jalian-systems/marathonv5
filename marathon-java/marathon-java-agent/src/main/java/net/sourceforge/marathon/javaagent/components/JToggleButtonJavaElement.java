package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;

import javax.swing.JToggleButton;

import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElement;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JToggleButtonJavaElement extends JavaElement {

    public JToggleButtonJavaElement(Component component, JavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        boolean selected = Boolean.parseBoolean(value);
        boolean current = ((JToggleButton) component).isSelected();
        if (selected != current)
            click();
        return true;
    }

}
