/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.runtime.BrowserTab.BrowserPreference;

public class AddPreferenceStage extends ModalDialog<BrowserPreference> {

    public static final Logger LOGGER = Logger.getLogger(AddPreferenceStage.class.getName());

    private TextField nameField = new TextField();
    private ComboBox<String> typeComboBox = new ComboBox<String>();
    private TextField stringValueField = new TextField();
    private ComboBox<String> booleanValueField = new ComboBox<>(FXCollections.observableArrayList("true", "false"));
    private TextField integerValueField = new TextField();
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private ButtonBarX buttonBar = new ButtonBarX();

    private BrowserPreference selected = null;

    public AddPreferenceStage() {
        super("Add a preference", "Add a new preference for browser", FXUIUtils.getIcon("new"));
        initComponents();
    }

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public Parent getContentPane() {
        VBox content = new VBox();
        content.getStyleClass().add("add-preference-stage");
        content.setId("addPreferenceStage");
        FormPane form = new FormPane("add-preference-stage-form", 3);
        integerValueField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    integerValueField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        StackPane pane = new StackPane(stringValueField, integerValueField, booleanValueField);
        pane.setAlignment(Pos.CENTER_LEFT);
        //@formatter:off
        form.addFormField("Property name:", nameField)
            .addFormField("Method:", typeComboBox, new Region())
            .addFormField("Value:", pane);
        //@formatter:on
        GridPane.setHgrow(typeComboBox, Priority.NEVER);
        VBox.setVgrow(form, Priority.ALWAYS);
        content.getChildren().addAll(form, buttonBar);
        return content;
    }

    private void initComponents() {
        nameField.setPrefColumnCount(25);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> checkEnableOk());

        integerValueField.setVisible(false);
        stringValueField.setVisible(true);
        booleanValueField.setVisible(false);
        typeComboBox.setItems(FXCollections.observableArrayList("String", "Integer", "Boolean"));
        typeComboBox.getSelectionModel().select("String");
        typeComboBox.setOnAction((e) -> {
            String type = typeComboBox.getSelectionModel().getSelectedItem();
            integerValueField.setVisible(type.equals("Integer"));
            stringValueField.setVisible(type.equals("String"));
            booleanValueField.setVisible(type.equals("Boolean"));
            checkEnableOk();
        });

        stringValueField.textProperty().addListener((observable, oldValue, newValue) -> checkEnableOk());
        integerValueField.textProperty().addListener((observable, oldValue, newValue) -> checkEnableOk());
        booleanValueField.setOnAction((e) -> {
            checkEnableOk();
        });
        okButton.setDisable(true);
        okButton.setOnAction((e) -> onOK());
        cancelButton.setOnAction((e) -> onCancel());
        buttonBar.setId("buttonBar");
        buttonBar.getButtons().addAll(okButton, cancelButton);
    }

    private void onOK() {
        String type = typeComboBox.getSelectionModel().getSelectedItem().toLowerCase();
        String value = type.equals("string") ? stringValueField.getText()
                : (type.equals("boolean") ? booleanValueField.getSelectionModel().getSelectedItem().toLowerCase()
                        : integerValueField.getText());
        selected = new BrowserPreference(nameField.getText(), type, value);
        dispose();
    }

    public BrowserPreference getSelected() {
        return selected;
    }

    protected void onCancel() {
        dispose();
    }

    public void checkEnableOk() {
        boolean disable = false;
        if (nameField.getText().equals("")) {
            disable = true;
        } else {
            String type = typeComboBox.getSelectionModel().getSelectedItem().toLowerCase();
            if (type.equals("string") && stringValueField.getText().equals(""))
                disable = true;
            else if (type.equals("boolean") && booleanValueField.getSelectionModel().getSelectedItem() == null)
                disable = true;
            else if (type.equals("integer") && integerValueField.getText().equals(""))
                disable = true;
        }
        okButton.setDisable(disable);
    }

    @Override
    protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }
}
