package net.sourceforge.marathon.javafxagent.components;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXCheckBoxTreeTableCell extends JavaFXElement {

    public JavaFXCheckBoxTreeTableCell(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        CheckBoxTreeTableCell cell = (CheckBoxTreeTableCell) node;
        @SuppressWarnings("unchecked")
        ObservableValue<Boolean> call = (ObservableValue<Boolean>) cell.getSelectedStateCallback().call(cell.getItem());
        int selection = call.getValue() ? 2 : 0;
        return JavaFXCheckBoxElement.states[selection];
    }
}
