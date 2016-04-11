package net.sourceforge.marathon.javafxagent.components;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXCheckBoxTreeTableCell extends JavaFXElement {

    public JavaFXCheckBoxTreeTableCell(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public String _getValue() {
        CheckBoxTreeTableCell cell = (CheckBoxTreeTableCell) node;
        Callback selectedStateCallback = cell.getSelectedStateCallback();
        String cbText;
        if (selectedStateCallback != null) {
            ObservableValue<Boolean> call = (ObservableValue<Boolean>) selectedStateCallback.call(cell.getItem());
            int selection = call.getValue() ? 2 : 0;
            cbText = JavaFXCheckBoxElement.states[selection];
        } else {
            Node cb = cell.getGraphic();
            JavaFXElement comp = (JavaFXElement) JavaFXElementFactory.createElement(cb, driver, window);
            cbText = comp._getValue();

        }
        String cellText = cell.getText();
        if (cellText == null)
            cellText = "";
        String text = cellText + ":" + cbText;
        return text;
    }
}
