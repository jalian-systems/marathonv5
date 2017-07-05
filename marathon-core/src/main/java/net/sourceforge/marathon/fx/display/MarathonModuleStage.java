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

import java.util.logging.Logger;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.display.ModuleInfo.ModuleDirElement;
import net.sourceforge.marathon.fx.projectselection.FormPane;

public class MarathonModuleStage extends ModalDialog<ModuleInfo> {

    public static final Logger LOGGER = Logger.getLogger(MarathonModuleStage.class.getName());

    private ComboBox<ModuleDirElement> moduleDirComboBox = new ComboBox<>();
    private ComboBox<ModuleInfo.ModuleFileElement> moduleFileComboBox = new ComboBox<>();
    private TextField moduleNameField = new TextField();
    private TextArea descriptionArea = new TextArea();
    private Label errorMessageLabel = new Label("");
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private ButtonBar buttonBar = new ButtonBar();
    private String errorMessage;
    private ModuleInfo moduleInfo;
    private IModuleFunctionHandler moduleFunctionHandler;

    public MarathonModuleStage(ModuleInfo moduleInfo) {
        super(moduleInfo.getTitle(), null, null);
        this.moduleInfo = moduleInfo;
        initComponents();
    }

    @Override protected Parent getContentPane() {
        VBox content = new VBox();
        content.getStyleClass().add("marathon-module");
        FormPane form = new FormPane("marathon-module-form", 2);
        if (moduleInfo.isNeedModuleFile()) {
        // @formatter:off
        form.addFormField("Module function name: ", moduleNameField)
            .addFormField("Description: ", descriptionArea)
            .addFormField("Module Directory: ", moduleDirComboBox)
            .addFormField("Module File: ", moduleFileComboBox);
        // @formatter:on
        } else {
         // @formatter:off
            form.addFormField("Module function name: ", moduleNameField)
                .addFormField("Description: ", descriptionArea);
         // @formatter:on
        }

        content.getChildren().addAll(form, errorMessageLabel);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(content);
        borderPane.setBottom(buttonBar);
        return borderPane;
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private void initComponents() {
        moduleNameField.setPrefColumnCount(20);
        moduleNameField.textProperty().addListener((observable, oldValue, newValue) -> validateModuleName());

        descriptionArea.setPrefColumnCount(20);
        descriptionArea.setPrefRowCount(4);

        if (moduleInfo.isNeedModuleFile()) {
            moduleDirComboBox.setItems(FXCollections.observableArrayList(moduleInfo.getModuleDirElements()));
            moduleDirComboBox.getSelectionModel().selectedItemProperty().addListener((e) -> {
                moduleInfo.populateFiles(moduleDirComboBox.getSelectionModel().getSelectedItem());
            });

            if (moduleDirComboBox.getItems().size() > 0) {
                moduleDirComboBox.getSelectionModel().select(0);
            }
            moduleFileComboBox.setItems(moduleInfo.getModuleFileElements());
            moduleFileComboBox.setEditable(true);
            TextField editor = moduleFileComboBox.getEditor();
            editor.textProperty().addListener((observable, oldValue, newValue) -> validateModuleName());
        }

        errorMessageLabel.setGraphic(FXUIUtils.getIcon("error"));
        errorMessageLabel.setVisible(false);

        buttonBar.setId("ModuleButtonBar");
        okButton.setOnAction((e) -> onOK());
        okButton.setDisable(true);
        cancelButton.setOnAction((e) -> onCancel());
        buttonBar.getButtons().addAll(okButton, cancelButton);
    }

    protected void onCancel() {
        dispose();
    }

    private void onOK() {
        moduleInfo.setModuleFunctionName(moduleNameField.getText());
        moduleInfo.setModuleFunctionDescription(descriptionArea.getText());
        if (moduleInfo.isNeedModuleFile()) {
            moduleInfo.setModuleDirElement(moduleDirComboBox.getSelectionModel().getSelectedItem());
            moduleInfo.setModuleFile(moduleFileComboBox.getEditor().getText());
        }
        moduleFunctionHandler.handleModule(moduleInfo);
        dispose();
    }

    public void setModuleFunctionHandler(IModuleFunctionHandler moduleFunctionHandler) {
        this.moduleFunctionHandler = moduleFunctionHandler;
    }

    private void validateModuleName() {
        if (isValidModuleName(moduleNameField.getText())) {
            okButton.setDisable(false);
            errorMessageLabel.setVisible(false);
        } else {
            okButton.setDisable(true);
            errorMessageLabel.setVisible(true);
        }
        errorMessageLabel.setText(errorMessage);
    }

    private boolean isValidModuleName(String moduleName) {
        errorMessage = "";
        if (moduleName.length() == 0) {
            errorMessage = "Module name cannot be empty";
            return false;
        }
        if (moduleName.equals("test")) {
            errorMessage = "Module name cannot be test";
            return false;
        }
        if (isNumber(moduleName)) {
            errorMessage = "Module name cannot be a number";
            return false;
        }
        if (moduleName.contains(" ")) {
            errorMessage = "Module name cannot contain spaces";
            return false;
        }
        if (Pattern.matches("[\\\\\\W]*", moduleName)) {
            errorMessage = "Module name can contain only alpha-numeric characters.";
            return false;
        }
        if (!Pattern.matches("[^[_]|^[[a-z]|[A-Z]]].*", moduleName)) {
            errorMessage = "Module name should begin only with a alphabet or an underscore.";
            return false;
        }
        if (!Pattern.matches("[^[_]|^[[a-z]|[A-Z]]][\\\\\\w]*", moduleName)) {
            errorMessage = "Module name should not contain symbols.";
            return false;
        }
        if (moduleInfo.isNeedModuleFile()) {
            String selectedItem = moduleFileComboBox.getEditor().getText();
            if (selectedItem == null || selectedItem.length() == 0) {
                errorMessage = "File name should be provided";
                return false;
            }
        }
        return true;
    }

    private boolean isNumber(String string) {
        return Pattern.matches("^\\d+$", string);
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }
}
