package net.sourceforge.marathon.javafxagent.components;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JSpinner;

import net.sourceforge.marathon.javafxagent.AbstractJavaElement;
import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.IJavaElement;
import net.sourceforge.marathon.javafxagent.JavaAgentException;
import net.sourceforge.marathon.javafxagent.JavaElementFactory;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JSpinnerJavaElement extends AbstractJavaElement {

    public JSpinnerJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public boolean marathon_select(String value) {
        JComponent spinnerEditor = ((JSpinner) component).getEditor();
        if (spinnerEditor == null)
            throw new JavaAgentException("Null value returned by getEditor() on spinner", null);
        IJavaElement ele = JavaElementFactory.createElement(spinnerEditor, driver, window);
        ele.marathon_select(value);
        try {
            ((JSpinner) component).commitEdit();
        } catch (Throwable t) {
        }
        return true;
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor"))
            return Arrays.asList(JavaElementFactory.createElement(getEditor(), getDriver(), getWindow()));
        return super.getByPseudoElement(selector, params);
    }

    @Override public String _getText() {
        IJavaElement editor = JavaElementFactory.createElement(getEditor(), getDriver(), getWindow());
        return editor.getText();
    }

    private Component getEditor() {
        JComponent editorComponent = ((JSpinner) component).getEditor();
        if (editorComponent == null)
            throw new JavaAgentException("Null value returned by getEditor() on spinner", null);
        if (editorComponent instanceof JSpinner.DefaultEditor) {
            editorComponent = ((JSpinner.DefaultEditor) editorComponent).getTextField();
        }
        return editorComponent;
    }
}
