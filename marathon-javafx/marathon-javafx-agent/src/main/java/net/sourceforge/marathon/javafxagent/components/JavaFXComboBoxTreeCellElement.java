package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.cell.ComboBoxTreeCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXComboBoxTreeCellElement extends JavaFXElement {

    public JavaFXComboBoxTreeCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        ComboBoxTreeCell cell = (ComboBoxTreeCell) getComponent();
        return cell.getConverter().toString(cell.getItem());
    }
}
