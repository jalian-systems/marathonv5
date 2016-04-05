package net.sourceforge.marathon.javafxagent.components;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxListCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXCheckBoxListCellElement extends JavaFXElement {

    public JavaFXCheckBoxListCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        CheckBoxListCell cell = (CheckBoxListCell) getComponent();
        ObservableValue<Boolean> call = (ObservableValue<Boolean>) cell.getSelectedStateCallback().call(cell.getItem());
        int selection = call.getValue() ? 2 : 0;
        String text = cell.getText() + ":" + JavaFXCheckBoxElement.states[selection];
        return text;
    }

}
