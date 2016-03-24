package net.sourceforge.marathon.javafx.tests;

import ensemble.Sample;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

public class HTMLEditorSample extends Sample {

    public HTMLEditorSample() {
        final VBox root = new VBox();
        root.setPadding(new Insets(8, 8, 8, 8));
        root.setSpacing(5);
        root.setAlignment(Pos.BOTTOM_LEFT);

        final GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(10);

        final ChoiceBox sendTo = new ChoiceBox(FXCollections.observableArrayList("To:", "Cc:", "Bcc:"));

        sendTo.setPrefWidth(100);
        GridPane.setConstraints(sendTo, 0, 0);
        grid.getChildren().add(sendTo);

        final TextField tbTo = new TextField();
        tbTo.setPrefWidth(400);
        GridPane.setConstraints(tbTo, 1, 0);
        grid.getChildren().add(tbTo);

        final Label subjectLabel = new Label("Subject:");
        GridPane.setConstraints(subjectLabel, 0, 1);
        grid.getChildren().add(subjectLabel);

        final TextField tbSubject = new TextField();
        tbTo.setPrefWidth(400);
        GridPane.setConstraints(tbSubject, 1, 1);
        grid.getChildren().add(tbSubject);

        root.getChildren().add(grid);

        Platform.runLater(() -> {
            final HTMLEditor htmlEditor = new HTMLEditor();
            htmlEditor.setPrefHeight(370);
            root.getChildren().addAll(htmlEditor, new Button("Send"));
        });

        final Label htmlLabel = new Label();
        htmlLabel.setWrapText(true);
        getChildren().add(root);
    }

}
