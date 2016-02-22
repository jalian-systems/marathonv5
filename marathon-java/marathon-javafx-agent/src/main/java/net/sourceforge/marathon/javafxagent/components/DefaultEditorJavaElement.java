package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;

import javax.swing.JSpinner.DefaultEditor;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class DefaultEditorJavaElement extends AbstractJavaElement {

    public DefaultEditorJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        ((DefaultEditor) component).getTextField().setText(value);
        return true;
    }
}
