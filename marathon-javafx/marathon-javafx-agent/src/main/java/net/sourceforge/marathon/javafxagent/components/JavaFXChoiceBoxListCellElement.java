package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXChoiceBoxListCellElement extends JavaFXElement {

    public JavaFXChoiceBoxListCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Override public String _getValue() {
        ChoiceBoxListCell cell = (ChoiceBoxListCell) node;
        StringConverter converter = cell.getConverter();
        if (converter != null)
            return converter.toString(cell.getItem());
        return cell.getItem().toString();
    }

}
