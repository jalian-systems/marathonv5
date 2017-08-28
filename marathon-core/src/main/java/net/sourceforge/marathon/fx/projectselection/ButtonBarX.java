package net.sourceforge.marathon.fx.projectselection;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;

public class ButtonBarX extends TilePane {

    public ButtonBarX() {
        super(Orientation.HORIZONTAL, 5, 0);
        setMinWidth(TilePane.USE_PREF_SIZE);
        setAlignment(Pos.CENTER_RIGHT);
    }

    @Override protected void layoutChildren() {
        ObservableList<Node> children = this.getChildren();
        for (Node node : children)
            if (node instanceof Button)
                ((Button) node).setMaxWidth(Double.MAX_VALUE);
        setPrefColumns(children.size());
        super.layoutChildren();
    }

    public ObservableList<Node> getButtons() {
        return getChildren();
    }

}
