package net.sourceforge.marathon.javafxagent.components.richtextfx;

import org.fxmisc.richtext.StyleClassedTextArea;

import ensemble.Sample;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class StyleClassedTextAreaSample extends Sample {

    public StyleClassedTextAreaSample() {
        StyleClassedTextArea codeArea = new StyleClassedTextArea();
        codeArea.setId("styleClassedTextArea");
        codeArea.setMaxSize(250, 250);
        VBox root = new VBox();
        root.getChildren().addAll(codeArea, new Button("Click Me!!"));
        getChildren().add(root);
    }
}