package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TextFieldListViewSampleApp extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ListViewTextField");
        primaryStage.setScene(new Scene(new TextFieldListViewSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
