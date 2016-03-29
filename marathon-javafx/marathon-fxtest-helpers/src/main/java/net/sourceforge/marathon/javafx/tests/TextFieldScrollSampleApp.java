package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class TextFieldScrollSampleApp extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Text Fileds");
        primaryStage.setScene(new Scene(new ScrollPane(new TextFiledScrollSample()), 250, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
