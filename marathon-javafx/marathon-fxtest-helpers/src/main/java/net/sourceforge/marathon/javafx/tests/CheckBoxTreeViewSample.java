package net.sourceforge.marathon.javafx.tests;

import java.util.Arrays;

import ensemble.Sample;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;

public class CheckBoxTreeViewSample extends Sample {

    public CheckBoxTreeViewSample() {
        final CheckBoxTreeItem<String> treeRoot = new CheckBoxTreeItem<String>("Root node");
        treeRoot.getChildren().addAll(Arrays.asList(new CheckBoxTreeItem<String>("Child Node 1"),
                new CheckBoxTreeItem<String>("Child Node 2"), new CheckBoxTreeItem<String>("Child Node 3")));

        treeRoot.getChildren().get(2).getChildren()
                .addAll(Arrays.asList(new CheckBoxTreeItem<String>("Child Node 4"), new CheckBoxTreeItem<String>("Child Node 5"),
                        new CheckBoxTreeItem<String>("Child Node 6"), new CheckBoxTreeItem<String>("Child Node 7"),
                        new TreeItem<String>("Child Node 8"), new CheckBoxTreeItem<String>("Child Node 9"),
                        new CheckBoxTreeItem<String>("Child Node 10"), new CheckBoxTreeItem<String>("Child Node 11"),
                        new CheckBoxTreeItem<String>("Child Node 12")));

        final TreeView treeView = new TreeView();
        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
        treeView.setShowRoot(true);
        treeView.setRoot(treeRoot);
        treeRoot.setExpanded(true);

        getChildren().add(treeView);
    }
}
