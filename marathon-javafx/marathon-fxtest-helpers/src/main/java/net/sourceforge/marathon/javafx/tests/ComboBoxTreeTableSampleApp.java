package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;

public class ComboBoxTreeTableSampleApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TreeTable View Sample");
        primaryStage.setScene(new Scene(new ComboBoxTreeTableSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
        TreeTableView<?> treeTableView = (TreeTableView<?>) primaryStage.getScene().getRoot().lookup(".tree-table-view");
        treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}