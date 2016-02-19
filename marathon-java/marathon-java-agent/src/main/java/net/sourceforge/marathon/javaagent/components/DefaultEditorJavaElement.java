package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;

import javax.swing.JSpinner.DefaultEditor;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class DefaultEditorJavaElement extends AbstractJavaElement {

    public DefaultEditorJavaElement(Component component, JavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        ((DefaultEditor) component).getTextField().setText(value);
        return true;
    }
}
