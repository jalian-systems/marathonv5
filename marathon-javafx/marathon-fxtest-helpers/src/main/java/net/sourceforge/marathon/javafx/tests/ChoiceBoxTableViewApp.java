package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChoiceBoxTableViewApp extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Choice Box TableView");
        primaryStage.setScene(new Scene(new ChoiceBoxTableViewSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
