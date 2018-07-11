package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TextFieldTreeViewApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Text Field TreeView");
        primaryStage.setScene(new Scene(new TextFieldTreeViewSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
