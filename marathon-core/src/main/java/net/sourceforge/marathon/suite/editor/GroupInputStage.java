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
package net.sourceforge.marathon.suite.editor;

import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.runtime.api.OSUtils;

public class GroupInputStage extends ModalDialog<GroupInputInfo> {

    public static final Logger LOGGER = Logger.getLogger(GroupInputStage.class.getName());

    private TextField name = new TextField();;
    private Button okButton;
    private IGroupInputHanler inputHandler;
    private GroupType type;

    public GroupInputStage(GroupType type) {
        super("New " + type.fileType() + " File", "Create a new " + type.fileType().toLowerCase() + " to store tests.",
                type.dockIcon());
        this.type = type;
    }

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(true);
    }

    @Override
    protected Parent getContentPane() {
        BorderPane borderPane = new BorderPane();

        FormPane formPane = new FormPane("group-editor-input-stage", 2);
        formPane.addFormField(type.fileType() + ": ", name);
        borderPane.setCenter(formPane);
        borderPane.setBottom(createButtonBar());
        return borderPane;
    }

    private Node createButtonBar() {
        ButtonBarX buttonBar = new ButtonBarX();
        okButton = FXUIUtils.createButton("ok", "Create " + type.fileType().toLowerCase() + " file", false, "OK");
        okButton.setOnAction(e -> onOK());
        okButton.disableProperty().bind(Bindings.isEmpty(name.textProperty()));
        Button cancelButton = FXUIUtils.createButton("cancel", "Cancel the " + type.fileType().toLowerCase() + " file creation",
                true, "Cancel");
        cancelButton.setOnAction(e -> dispose());
        buttonBar.getButtons().addAll(okButton, cancelButton);
        return buttonBar;
    }

    public void onOK() {
        GroupInputInfo info = new GroupInputInfo(OSUtils.createUniqueFile(name.getText(), type.ext(), type.dir()), name.getText());
        inputHandler.handleInput(info);
        dispose();
    }

    @Override
    protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }

    public void setInputHandler(IGroupInputHanler inputHandler) {
        this.inputHandler = inputHandler;
    }

}
