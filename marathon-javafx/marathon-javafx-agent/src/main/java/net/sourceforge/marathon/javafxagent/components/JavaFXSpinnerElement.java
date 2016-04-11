package net.sourceforge.marathon.javafxagent.components;

import java.util.Arrays;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaAgentException;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXSpinnerElement extends JavaFXElement {

    public JavaFXSpinnerElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings("unchecked") @Override public boolean marathon_select(String value) {
        Spinner<?> spinner = (Spinner<?>) getComponent();
        if (!spinner.isEditable()) {
            @SuppressWarnings("rawtypes")
            SpinnerValueFactory factory = ((Spinner<?>) getComponent()).getValueFactory();
            Object convertedValue = factory.getConverter().fromString(value);
            factory.setValue(convertedValue);
            return true;
        }
        TextField spinnerEditor = spinner.getEditor();
        if (spinnerEditor == null)
            throw new JavaAgentException("Null value returned by getEditor() on spinner", null);
        IJavaFXElement ele = JavaFXElementFactory.createElement(spinnerEditor, driver, window);
        spinnerEditor.getProperties().put("marathon.celleditor", true);
        ele.marathon_select(value);
        return true;
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor"))
            return Arrays.asList(JavaFXElementFactory.createElement(getEditor(), driver, window));
        return super.getByPseudoElement(selector, params);
    }

    @Override public String _getText() {
        return getSpinnerText((Spinner<?>) getComponent());
    }

    private Node getEditor() {
        return ((Spinner<?>) getComponent()).getEditor();
    }

}
