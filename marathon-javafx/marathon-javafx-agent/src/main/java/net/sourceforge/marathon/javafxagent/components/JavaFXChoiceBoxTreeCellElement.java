package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.cell.ChoiceBoxTreeCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXChoiceBoxTreeCellElement extends JavaFXElement {

    public JavaFXChoiceBoxTreeCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Override public String _getValue() {
        ChoiceBoxTreeCell cell = (ChoiceBoxTreeCell) getComponent();
        StringConverter converter = cell.getConverter();
        if (converter != null)
            return converter.toString(cell.getItem());
        return cell.getItem().toString();
    }
}
