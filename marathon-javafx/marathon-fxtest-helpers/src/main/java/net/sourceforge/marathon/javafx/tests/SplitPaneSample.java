package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

public class SplitPaneSample extends Sample {

    public SplitPaneSample() {
        SplitPane sp = new SplitPane();
        sp.setPrefSize(250, 250);

        StackPane sp1 = new StackPane();
        sp1.getChildren().add(new Button("Button One"));
        final StackPane sp2 = new StackPane();
        sp2.getChildren().add(new Button("Button Two"));
        final StackPane sp3 = new StackPane();
        sp3.getChildren().add(new Button("Button Three"));
        sp.getItems().addAll(sp1, sp2, sp3);
        sp.setDividerPositions(0.3f, 0.6f, 0.9f);
        getChildren().add(sp);
    }

}
