package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ComboBoxTreeViewApp extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Combo Box TreeView");
        primaryStage.setScene(new Scene(new ComboBoxTreeViewSample()));
        primaryStage.sizeToScene();
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
