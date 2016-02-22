package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JTreeEditingContainerJavaElement extends AbstractJavaElement {
    public JTreeEditingContainerJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        clear();
        sendKeys(value, JavaAgentKeys.ENTER);
        return true;
    }
}
