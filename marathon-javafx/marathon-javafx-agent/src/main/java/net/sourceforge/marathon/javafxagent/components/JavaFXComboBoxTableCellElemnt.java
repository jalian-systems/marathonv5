package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.cell.ComboBoxTableCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXComboBoxTableCellElemnt extends JavaFXElement {

    public JavaFXComboBoxTableCellElemnt(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        ComboBoxTableCell cell = (ComboBoxTableCell) node;
        return cell.getConverter().toString(cell.getItem());
    }
}
