package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.cell.ComboBoxListCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXComboBoxListCellElemnt extends JavaFXElement {

    public JavaFXComboBoxListCellElemnt(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        ComboBoxListCell cell = (ComboBoxListCell) getComponent();
        return cell.getConverter().toString(cell.getItem());
    }

}
