package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class TextAreaSample extends Sample {

    public TextAreaSample() {
        TextArea textArea = new TextArea();
        textArea.setMaxSize(250, 250);
        VBox root = new VBox();
        root.getChildren().addAll(textArea, new Button("Click Me!!"));
        getChildren().add(root);
    }
}
