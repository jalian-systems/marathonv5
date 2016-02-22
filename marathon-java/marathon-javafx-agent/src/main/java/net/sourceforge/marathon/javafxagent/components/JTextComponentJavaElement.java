package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;

import javax.swing.text.JTextComponent;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JTextComponentJavaElement extends AbstractJavaElement {

    public JTextComponentJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        JTextComponent tc = (JTextComponent) component;
        tc.requestFocus();
        Boolean clientProperty = (Boolean) tc.getClientProperty("marathon.celleditor");
        if (clientProperty != null && clientProperty) {
            clear();
            sendKeys(value, JavaAgentKeys.ENTER);
        } else {
            tc.setText(value);
        }
        return true;
    }
}
