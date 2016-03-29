package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TextFiledScrollSample extends Sample {

    public TextFiledScrollSample() {
        VBox root = new VBox();
        ObservableList<Node> children = root.getChildren();
        for (int i = 0; i < 20; i++) {
            Label label = new Label("TextFiled " + i);
            children.add(label);
            TextField e = new TextField();
            children.add(e);
            children.add(new Button("Click Me"));
        }
        getChildren().add(root);
    }
}
