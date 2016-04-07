package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TextInputControl;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTextInputControlElement extends JavaFXElement {

    public JavaFXTextInputControlElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Override public boolean marathon_select(String value) {
        TextInputControl tc = (TextInputControl) getComponent();
        Boolean isCellEditor = (Boolean) tc.getProperties().get("marathon.celleditor");
        tc.setText("");
        if (isCellEditor != null && isCellEditor) {
            super.sendKeys(value, JavaAgentKeys.ENTER);
            Cell cell = (Cell) tc.getProperties().get("marathon.cell");
            cell.commitEdit(value);
        } else {
            super.sendKeys(value);
        }
        return true;
    }

    @Override public String _getText() {
        return ((TextInputControl) getComponent()).getText();
    }
}
