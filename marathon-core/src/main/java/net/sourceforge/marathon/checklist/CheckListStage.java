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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class CheckListStage extends ModalDialog<CheckListFormNode> {

    public static final Logger LOGGER = Logger.getLogger(CheckListStage.class.getName());

    private CheckListFormNode checkListFormNode;
    private Button[] actionButtons;
    private boolean dirty = false;

    public CheckListStage(CheckListFormNode checkListFormNode) {
        super("", null, null);
        this.checkListFormNode = checkListFormNode;
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override protected Parent getContentPane() {
        BorderPane root = new BorderPane();
        root.setCenter(new CheckListView(checkListFormNode));
        root.setBottom(createButtonBar());
        return root;
    }

    private ButtonBarX createButtonBar() {
        ButtonBarX buttonBar = new ButtonBarX();
        buttonBar.getButtons().addAll(getActionButtons());
        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
        return buttonBar;
    }

    public void setActionButtons(Button[] actionButtons) {
        this.actionButtons = actionButtons;
    }

    public Button[] getActionButtons() {
        return actionButtons;
    }

    public boolean isDirty() {
        return dirty || checkListFormNode.isDirty();
    }

    @Override protected void setDefaultButton() {
        actionButtons[0].setDefaultButton(true);
    }
}
