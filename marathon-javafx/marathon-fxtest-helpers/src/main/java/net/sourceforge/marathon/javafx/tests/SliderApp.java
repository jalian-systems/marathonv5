package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SliderApp extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Slider Sample");
        primaryStage.setScene(new Scene(new SliderSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
