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

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class AddPropertiesStage extends ModalDialog<TestPropertiesInfo> {

    private Button okButton = FXUIUtils.createButton("ok", "Add issue", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private ButtonBar buttonBar = new ButtonBar();
    private TestPropertiesInfo issueInfo;
    private AddPropertiesView root;

    public AddPropertiesStage(TestPropertiesInfo issueInfo) {
        super("Add Properties", "Set properties to the test script", FXUIUtils.getIcon("properties"));
        this.issueInfo = issueInfo;
    }

    @Override protected Parent getContentPane() {
        root = new AddPropertiesView(issueInfo);
        okButton.setOnAction((e) -> onOK());
        cancelButton.setOnAction((e) -> dispose());
        buttonBar.getButtons().addAll(okButton, cancelButton);
        root.setBottom(buttonBar);
        root.setPrefHeight(400);
        root.setPrefWidth(500);
        return root;
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }

    private void onOK() {
        root.write(null);
        dispose();
    }

}
