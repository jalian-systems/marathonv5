package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.stage.Stage;

public class TreeTableCellSampleApp extends Application {

    @Override public void start(Stage primaryStage) {
        primaryStage.setTitle("TreeTable View Cell Sample");
        primaryStage.setScene(new Scene(new TreeTableSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
        TreeTableView<?> treeTableView = (TreeTableView<?>) primaryStage.getScene().getRoot().lookup(".tree-table-view");
        TreeTableViewSelectionModel<?> selectionModel = treeTableView.getSelectionModel();
        selectionModel.setCellSelectionEnabled(true);
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}