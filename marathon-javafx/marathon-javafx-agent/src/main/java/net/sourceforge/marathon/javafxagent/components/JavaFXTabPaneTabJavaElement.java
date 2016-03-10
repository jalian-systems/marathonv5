package net.sourceforge.marathon.javafxagent.components;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import net.sourceforge.marathon.javafxagent.JavaFXElement;

public class JavaFXTabPaneTabJavaElement extends JavaFXElement {

    private JavaFXElement parent;
    private int tabIndex;

    public JavaFXTabPaneTabJavaElement(JavaFXElement parent, int tabIndex) {
        super(parent);
        this.parent = parent;
        this.tabIndex = tabIndex;
    }

    public JavaFXElement getParent() {
        return parent;
    }

    @Override public String _getText() {
        TabPane tabPane = (TabPane) parent.getComponent();
        return getTextForTab(tabPane, tabPane.getTabs().get(tabIndex));
    }

    @Override public Point2D _getMidpoint() {
        StackPane tabRegion = getTabRegion();
        Bounds boundsInParent = tabRegion.getBoundsInParent();
        double x = boundsInParent.getWidth() / 2;
        double y = boundsInParent.getHeight() / 2;
        return tabRegion.localToParent(x, y);
    }

    private StackPane getTabRegion() {
        TabPane n = (TabPane) parent.getComponent();
        StackPane node = (StackPane) n.lookup(".headers-region");
        return (StackPane) node.getChildren().get(tabIndex);
    }

}
