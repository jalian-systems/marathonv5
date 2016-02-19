package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;

import javax.swing.text.JTextComponent;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaAgentKeys;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JTextComponentJavaElement extends AbstractJavaElement {

    public JTextComponentJavaElement(Component component, JavaAgent driver, JWindow window) {
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
