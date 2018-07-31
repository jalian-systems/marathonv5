package net.sourceforge.marathon.checklist;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.checklist.CheckList.FailureNote;

public class FailureNoteVBoxer extends CheckListItemVBoxer {

    private FailureNote item;
    private Label label;
    private RadioButton success;
    private RadioButton fail;
    private RadioButton notes;
    private TextArea textArea;

    public FailureNoteVBoxer(FailureNote item) {
        this.item = item;
        textArea = new TextArea();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected VBox createVBox(boolean selectable, boolean editable) {
        VBox checkListBox = new VBox();
        checkListBox.getChildren().addAll(createButtonBar(selectable, editable), createTextArea(selectable, editable));
        if (selectable) {
            setMouseListener(checkListBox);
            setMouseListener(success);
            setMouseListener(fail);
            setMouseListener(notes);
            setMouseListener(textArea);
            setMouseListener(label);
        }
        return checkListBox;
    }

    private ToolBar createButtonBar(boolean selectable, boolean editable) {
        ToolBar toolBar = new ToolBar();
        label = new Label(item.getLabel());
        ToggleGroup toggleGroup = new ToggleGroup();
        success = new RadioButton("Success");
        fail = new RadioButton("Fail");
        notes = new RadioButton("Notes");
        success.setDisable(!editable);
        fail.setDisable(!editable);
        notes.setDisable(!editable);
        success.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (success.isSelected()) {
                item.setSelected(1);
            }
            textArea.setDisable(success.isSelected());
        });
        fail.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (fail.isSelected()) {
                item.setSelected(3);
            }
            textArea.setDisable(success.isSelected());
        });
        notes.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (notes.isSelected()) {
                item.setSelected(2);
            }
            textArea.setDisable(success.isSelected());
        });
        int selected = item.getSelected();
        if (selected == 1) {
            success.setSelected(true);
        } else if (selected == 3) {
            fail.setSelected(true);
        } else if (selected == 2) {
            notes.setSelected(true);
        } else {
            success.setSelected(editable);
        }
        toggleGroup.getToggles().addAll(success, fail, notes);
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        toolBar.getItems().addAll(label, region, success, fail, notes);
        return toolBar;
    }

    private Node createTextArea(boolean selectable, boolean editable) {
        textArea.setPrefRowCount(4);
        textArea.setEditable(editable);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            item.setText(textArea.getText());
        });
        textArea.setText(item.getText());
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        return scrollPane;
    }

    @Override
    public FailureNote getItem() {
        return item;
    }
}
