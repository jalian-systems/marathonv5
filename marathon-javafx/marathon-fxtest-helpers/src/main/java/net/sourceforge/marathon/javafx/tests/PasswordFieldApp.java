package net.sourceforge.marathon.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PasswordFieldApp extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("PasswordField Sample");
        primaryStage.setScene(new Scene(new PasswordFieldSample()));
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
