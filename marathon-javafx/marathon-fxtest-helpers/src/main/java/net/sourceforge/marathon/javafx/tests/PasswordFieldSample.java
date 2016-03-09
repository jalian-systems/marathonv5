package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class PasswordFieldSample extends Sample {

    public PasswordFieldSample() {
        PasswordField passwordFiled = new PasswordField();
        passwordFiled.setMaxSize(250, 250);
        VBox root = new VBox();
        root.getChildren().addAll(passwordFiled, new Button("Click Me!!"));
        getChildren().add(root);
    }
}
