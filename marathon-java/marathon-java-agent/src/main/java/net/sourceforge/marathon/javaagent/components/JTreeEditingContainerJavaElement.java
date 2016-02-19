package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaAgentKeys;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JTreeEditingContainerJavaElement extends AbstractJavaElement {
    public JTreeEditingContainerJavaElement(Component component, JavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        clear();
        sendKeys(value, JavaAgentKeys.ENTER);
        return true;
    }
}
