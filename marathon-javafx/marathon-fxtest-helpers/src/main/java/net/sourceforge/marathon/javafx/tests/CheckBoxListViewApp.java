package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CheckBoxListViewApp extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("LisViewCheckBox");
        primaryStage.setScene(new Scene(new CheckBoxListViewSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
