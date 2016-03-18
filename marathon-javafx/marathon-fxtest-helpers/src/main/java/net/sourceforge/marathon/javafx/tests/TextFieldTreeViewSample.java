package net.sourceforge.marathon.javafx.tests;

import java.util.Arrays;

import ensemble.Sample;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;

public class TextFieldTreeViewSample extends Sample {

    public TextFieldTreeViewSample() {
        final TreeItem<String> treeRoot = new TreeItem<String>("Root node");
        treeRoot.getChildren().addAll(Arrays.asList(new TreeItem<String>("Child Node 1"), new TreeItem<String>("Child Node 2"),
                new TreeItem<String>("Child Node 3")));

        treeRoot.getChildren().get(2).getChildren().addAll(Arrays.asList(new TreeItem<String>("Child Node 4"),
                new TreeItem<String>("Child Node 5"), new TreeItem<String>("Child Node 6"), new TreeItem<String>("Child Node 7"),
                new TreeItem<String>("Child Node 8"), new TreeItem<String>("Child Node 9"), new TreeItem<String>("Child Node 10"),
                new TreeItem<String>("Child Node 11"), new TreeItem<String>("Child Node 12")));

        final TreeView treeView = new TreeView();
        treeView.setShowRoot(true);
        treeView.setRoot(treeRoot);
        treeRoot.setExpanded(true);
        treeView.setEditable(true);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView());

        getChildren().add(treeView);
    }

}
