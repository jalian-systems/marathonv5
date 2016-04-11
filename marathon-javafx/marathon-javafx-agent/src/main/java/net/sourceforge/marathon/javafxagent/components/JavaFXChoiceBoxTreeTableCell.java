package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXChoiceBoxTreeTableCell extends JavaFXElement {

    public JavaFXChoiceBoxTreeTableCell(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        ChoiceBoxTreeTableCell cell = (ChoiceBoxTreeTableCell) node;
        @SuppressWarnings("rawtypes")
        StringConverter converter = cell.getConverter();
        if (converter != null)
            return converter.toString(cell.getItem());
        return cell.getItem().toString();
    }
}
