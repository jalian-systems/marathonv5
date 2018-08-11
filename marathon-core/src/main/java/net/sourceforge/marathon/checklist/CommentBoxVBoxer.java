package net.sourceforge.marathon.checklist;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.checklist.CheckList.CommentBox;

public class CommentBoxVBoxer extends CheckListItemVBoxer {

    private CommentBox item;
    private TextArea textArea;

    public CommentBoxVBoxer(CommentBox item) {
        this.item = item;
    }

    @Override
    protected VBox createVBox(boolean selectable, boolean editable) {
        VBox textAreaBox = new VBox();
        Label label = new Label(item.getLabel());
        textAreaBox.getChildren().addAll(label, createTextArea(selectable, editable));
        if (selectable) {
            setMouseListener(label);
            setMouseListener(textArea);
        }
        HBox.setHgrow(textAreaBox, Priority.ALWAYS);
        return textAreaBox;
    }

    private Node createTextArea(boolean selectable, boolean editable) {
        textArea = new TextArea();
        textArea.setPrefRowCount(4);
        textArea.setEditable(editable);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            item.setText(textArea.getText());
        });
        textArea.setText(item.getText());
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
    }

    @Override
    public CommentBox getItem() {
        return item;
    }
}
