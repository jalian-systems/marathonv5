/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.fx.display;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;

public abstract class MarathonInputStage extends ModalDialog<String> {

    private IInputHanler inputHandler;
    private TextField inputField = new TextField();
    private Label errorMsgLabel = new Label("");
    protected Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private ButtonBar buttonBar = new ButtonBar();

    public MarathonInputStage(String title, String subTitle, Node icon) {
        super(title, subTitle, icon);
        initComponents();
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private void initComponents() {
        inputField.textProperty().addListener((observable, oldValue, newValue) -> {
            String errorMessage = validateInput(inputField.getText());
            if (errorMessage != null) {
                errorMsgLabel.setText(errorMessage);
                okButton.setDisable(true);
                errorMsgLabel.setVisible(true);
            } else {
                okButton.setDisable(false);
                errorMsgLabel.setVisible(false);
            }
        });

        errorMsgLabel.setId("ErrorMessageLabel");
        errorMsgLabel.setGraphic(FXUIUtils.getIcon("error"));
        errorMsgLabel.setVisible(false);

        okButton.setOnAction((e) -> onOK());
        okButton.setDisable(true);
        cancelButton.setOnAction((e) -> onCancel());

        buttonBar.getButtons().addAll(okButton, cancelButton);
    }

    @Override protected Parent getContentPane() {
        VBox root = new VBox();
        root.setId("MarathonInputStage");
        root.getStyleClass().add("marathon-input");

        VBox container = new VBox();
        FormPane inputFieldform = new FormPane("marathon-input-form", 2);
        inputFieldform.addFormField(getInputFiledLabelText(), inputField);
        container.getChildren().addAll(inputFieldform, errorMsgLabel);
        root.getChildren().addAll(container, buttonBar);
        return root;
    }

    protected void onCancel() {
        dispose();
    }

    public void setValue(String value) {
        inputField.setText(value);
    }

    private void onOK() {
        inputHandler.handleInput(inputField.getText());
        dispose();
    }

    public void setInputHandler(IInputHanler inputHandler) {
        this.inputHandler = inputHandler;
    }

    protected abstract String validateInput(String text);

    protected abstract String getInputFiledLabelText();
}
