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
package net.sourceforge.marathon.checklist;

import java.util.logging.Logger;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;

public class NewChekListInputStage extends ModalDialog<String> {

    public static final Logger LOGGER = Logger.getLogger(NewChekListInputStage.class.getName());

    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private TextField nameField = new TextField();
    private TextArea descriptionField = new TextArea();
    private ButtonBarX buttonBar = new ButtonBarX();
    private boolean ok = false;

    public NewChekListInputStage() {
        super("New CheckList", "Create a new check list", FXUIUtils.getIcon("newCheckList"));
        initComponents();
    }

    @Override
    public Parent getContentPane() {
        BorderPane borderPane = new BorderPane();
        FormPane formPane = new FormPane("new-checkList-input-stage", 2);
        //@formatter:off
        formPane.addFormField("Name", nameField)
                .addFormField("Description", descriptionField);
        //formatter:on
        borderPane.setCenter(formPane);
        borderPane.setBottom(buttonBar);
        return borderPane;
    }

    private void initComponents() {
        buttonBar.getButtons().addAll(okButton, cancelButton);
        okButton.setOnAction((e) -> onOk());
        cancelButton.setOnAction((e) -> onCancle());
    }

    private void onCancle() {
        dispose();
    }

    public void onOk() {
        dispose();
        ok = true;
    }

    public boolean isOk() {
        return ok;
    }
    
    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }

    public String getScript() {
            //@formatter:off
              String script= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
                              "<java version=\"1.8.0_102\" class=\"java.beans.XMLDecoder\">\n"+
                                  "<object class=\"net.sourceforge.marathon.checklist.CheckList\">\n"+
                                      "<void property=\"description\">\n"+
                                          "<string>"+descriptionField.getText()+"</string>\n"+
                                              "</void>\n"+
                                       "<void property=\"name\">\n"+
                                           "<string>"+nameField.getText() +"</string>\n"+
                                               "</void>\n"+
                                   "</object>\n"+
                                "</java>\n";
              //@formatter:on
        return script;
    }
}
