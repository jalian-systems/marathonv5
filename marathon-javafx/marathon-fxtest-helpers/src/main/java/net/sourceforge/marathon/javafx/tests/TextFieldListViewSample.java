package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;

public class TextFieldListViewSample extends Sample {

    public TextFieldListViewSample() {
        final ListView<String> listView = new ListView<String>();
        listView.setItems(FXCollections.observableArrayList("Row 1", "Row 2", "Long Row 3", "Row 4", "Row 5", "Row 6", "Row 7",
                "Row 8", "Row 9", "Row 10", "Row 11", "Row 12", "Row 13", "Row 14", "Row 15", "Row 16", "Row 17", "Row 18",
                "Row 19", "Row 20"));
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        getChildren().add(listView);
    }
}
