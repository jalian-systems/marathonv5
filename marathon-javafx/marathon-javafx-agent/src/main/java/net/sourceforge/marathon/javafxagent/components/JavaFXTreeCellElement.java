package net.sourceforge.marathon.javafxagent.components;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTreeCellElement extends JavaFXElement {

    public JavaFXTreeCellElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public String _getValue() {
        TreeCell<?> cell = (TreeCell<?>) getComponent();
        Node graphic = cell.getGraphic();
        JavaFXElement cellElement = (JavaFXElement) JavaFXElementFactory.createElement(graphic, driver, window);
        if (graphic != null && cellElement != null)
            return cellElement._getValue();
        return super._getValue();
    }
}
