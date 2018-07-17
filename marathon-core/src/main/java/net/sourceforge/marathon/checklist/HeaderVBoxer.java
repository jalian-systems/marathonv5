package net.sourceforge.marathon.checklist;

import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.checklist.CheckList.Header;

public class HeaderVBoxer extends CheckListItemVBoxer {

    private Header item;

    public HeaderVBoxer(Header item) {
        this.item = item;
    }

    @Override
    protected VBox createVBox(boolean selectable, boolean editable) {
        VBox headerBox = new VBox();
        HBox hBox = new HBox();
        Separator separator = new Separator();
        HBox.setHgrow(separator, Priority.ALWAYS);
        separator.setStyle("-fx-padding: 8 0 0 3;");
        hBox.getChildren().addAll(new Label(item.getLabel()), separator);
        headerBox.getChildren().add(hBox);
        return headerBox;
    }

    @Override
    public Header getItem() {
        return item;
    }

}
