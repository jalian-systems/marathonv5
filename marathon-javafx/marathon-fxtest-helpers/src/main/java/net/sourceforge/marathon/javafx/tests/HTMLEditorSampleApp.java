package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HTMLEditorSampleApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Message Composing");
        stage.setWidth(500);
        stage.setHeight(500);
        Scene scene = new Scene(new HTMLEditorSample());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}