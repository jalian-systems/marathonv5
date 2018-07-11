package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TextAreaApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Text Area Sample");
        primaryStage.setScene(new Scene(new TextAreaSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
