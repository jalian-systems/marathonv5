package net.sourceforge.marathon.javafxagent.components.richtextfx;

import org.fxmisc.richtext.CodeArea;

import ensemble.Sample;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class CodeAreaSample extends Sample {

    public CodeAreaSample() {
        CodeArea codeArea = new CodeArea();
        codeArea.setId("codeArea");
        codeArea.setMaxSize(250, 250);
        VBox root = new VBox();
        root.getChildren().addAll(codeArea, new Button("Click Me!!"));
        getChildren().add(root);
    }
}