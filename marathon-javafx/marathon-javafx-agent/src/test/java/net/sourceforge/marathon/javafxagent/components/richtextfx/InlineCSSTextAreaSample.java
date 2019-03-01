package net.sourceforge.marathon.javafxagent.components.richtextfx;

import org.fxmisc.richtext.InlineCssTextArea;

import ensemble.Sample;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class InlineCSSTextAreaSample extends Sample {

    public InlineCSSTextAreaSample() {
        InlineCssTextArea area = new InlineCssTextArea();
        area.setId("inlineCssTextArea");
        area.setMaxSize(250, 250);
        VBox root = new VBox();
        root.getChildren().addAll(area, new Button("Click Me!!"));
        getChildren().add(root);
    }
}