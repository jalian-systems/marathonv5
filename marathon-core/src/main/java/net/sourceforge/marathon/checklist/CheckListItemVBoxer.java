package net.sourceforge.marathon.checklist;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.checklist.CheckList.CheckListItem;

public abstract class CheckListItemVBoxer {

    private VBox vbox;
    static CheckListItemVBoxer selectedItem;

    protected abstract VBox createVBox(boolean selectable, boolean editable);

    public VBox getVbox(boolean selectable, boolean editable) {
        if (vbox == null) {
            vbox = createVBox(selectable, editable);
        }
        if (selectable) {
            setMouseListener(vbox);
        }
        return vbox;
    }

    protected void setMouseListener(Node node) {
        node.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
            e.consume();
            if (selectedItem != null) {
                selectedItem.deselect();
            }
            selectedItem = CheckListItemVBoxer.this;
            CheckListItemVBoxer.this.select();
        });
    }

    public void select() {
        vbox.setStyle("-fx-border-color: blue;" + "-fx-border-width: 3");
    }

    public void deselect() {
        vbox.setStyle("-fx-border-color: white;");
    }

    abstract public CheckListItem getItem();

}
