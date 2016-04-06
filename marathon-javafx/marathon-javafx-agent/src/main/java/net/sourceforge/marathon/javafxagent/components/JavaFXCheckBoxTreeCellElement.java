package net.sourceforge.marathon.javafxagent.components;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTreeCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXCheckBoxTreeCellElement extends JavaFXElement {

    public JavaFXCheckBoxTreeCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        CheckBoxTreeCell cell = (CheckBoxTreeCell) getComponent();
        @SuppressWarnings("unchecked")
        ObservableValue<Boolean> call = (ObservableValue<Boolean>) cell.getSelectedStateCallback().call(cell.getTreeItem());
        int selection = call.getValue() ? 2 : 0;
        String cellText = cell.getText();
        if (cellText == null)
            cellText = "";
        String text = cellText + ":" + JavaFXCheckBoxElement.states[selection];
        return text;
    }
}
