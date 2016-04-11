package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxListCell;

public class ComboBoxListViewSample extends Sample {

    private final ObservableList items = FXCollections.observableArrayList("Option 1", "Option 2", "Option 3", "Option 4",
            "Option 5", "Option 6");

    public ComboBoxListViewSample() {
        ListView<Object> listView = new ListView<>();
        listView.getItems().addAll(items);
        listView.setEditable(true);
        listView.setCellFactory(ComboBoxListCell.forListView(items));
        getChildren().add(listView);
    }

}
