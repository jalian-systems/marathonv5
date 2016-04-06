package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTableViewCellElement extends JavaFXElement {

    public JavaFXTableViewCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public String _getValue() {
        TableCell<?, ?> cell = (TableCell<?, ?>) node;
        Node graphic = cell.getGraphic();
        JavaFXElement component = (JavaFXElement) JavaFXElementFactory.createElement(graphic, driver, window);
        if (graphic != null && component != null) {
            if (graphic instanceof CheckBox) {
                String cellText = cell.getText() == null ? "" : cell.getText();
                return cellText + ":" + component._getValue();
            }
            return component._getValue();
        }
        return super._getValue();
    }
}
