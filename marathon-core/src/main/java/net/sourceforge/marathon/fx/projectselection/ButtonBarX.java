package net.sourceforge.marathon.fx.projectselection;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ButtonBarX extends HBox {

    public ButtonBarX() {
        super(5);
        setAlignment(Pos.CENTER_RIGHT);
    }

    @Override protected void layoutChildren() {
        double minPrefWidth = calculatePrefChildWidth();
        setMinWidth(minPrefWidth * (getChildren().size() + 1));
        for (Node n : getChildren())
            if (n instanceof Button)
                ((Button) n).setMinWidth(minPrefWidth);
        super.layoutChildren();
    }

    private double calculatePrefChildWidth() {
        double minPrefWidth = 0;
        for (Node n : getChildren())
            minPrefWidth = Math.max(minPrefWidth, n.prefWidth(-1));
        return minPrefWidth;
    }

    public ObservableList<Node> getButtons() {
        return getChildren();
    }

}
