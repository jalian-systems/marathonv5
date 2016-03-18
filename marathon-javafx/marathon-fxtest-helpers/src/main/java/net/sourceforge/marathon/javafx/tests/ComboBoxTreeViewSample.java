package net.sourceforge.marathon.javafx.tests;

import java.util.Arrays;

import ensemble.Sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.ComboBoxTreeCell;

public class ComboBoxTreeViewSample extends Sample {

    private final ObservableList items = FXCollections.observableArrayList("Option 1", "Option 2", "Option 3", "Option 4",
            "Option 5", "Option 6");

    public ComboBoxTreeViewSample() {
        final TreeItem<String> treeRoot = new TreeItem<String>("Root node");
        treeRoot.getChildren().addAll(Arrays.asList(new TreeItem<String>("Option 1"), new TreeItem<String>("Option 2"),
                new TreeItem<String>("Option 3")));

        treeRoot.getChildren().get(2).getChildren().addAll(Arrays.asList(new TreeItem<String>("Option 4"),
                new TreeItem<String>("Option 5"), new TreeItem<String>("Option 6")));

        final TreeView treeView = new TreeView();
        treeView.setShowRoot(true);
        treeView.setRoot(treeRoot);
        treeRoot.setExpanded(true);
        treeView.setEditable(true);
        treeView.setCellFactory(ComboBoxTreeCell.forTreeView(items));

        getChildren().add(treeView);
    }
}
