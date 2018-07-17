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

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.checklist.CheckListForm.CheckListElement;
import net.sourceforge.marathon.checklist.CheckListFormNode.Mode;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class MarathonCheckListStage extends ModalDialog<CheckListForm> {

    public static final Logger LOGGER = Logger.getLogger(MarathonCheckListStage.class.getName());

    private CheckListForm checkListInfo;
    private ListView<CheckListElement> checkListView;
    private ObservableList<CheckListElement> checkListElements;
    private IInsertCheckListHandler insertCheckListHandler;

    private SplitPane splitPane = new SplitPane();
    private BorderPane leftPane = new BorderPane();
    private BorderPane rightPane = new BorderPane();
    private ButtonBarX buttonBar = new ButtonBarX();
    private Button doneButton;

    public MarathonCheckListStage(CheckListForm checkListInfo) {
        super(checkListInfo.getTitle(), null, FXUIUtils.getIcon("newCheckList"));
        this.checkListInfo = checkListInfo;
        initComponents();
    }

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    protected Parent getContentPane() {
        BorderPane pane = new BorderPane();
        pane.setCenter(splitPane);
        pane.setBottom(buttonBar);
        pane.setPrefSize(750, 600);
        return pane;
    }

    private void initComponents() {
        initCheckList();
        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.25);

        boolean insert = checkListInfo.isInsert();
        if (insert) {
            doneButton = FXUIUtils.createButton("insert", "Insert", true, "Insert");
            doneButton.setOnAction((e) -> onInsert());
            doneButton.setDisable(true);
        } else {
            doneButton = FXUIUtils.createButton("ok", "Done", true, "Done");
            doneButton.setOnAction((e) -> onDone());
        }
        buttonBar.getButtons().addAll(doneButton);
        if (insert) {
            Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
            cancelButton.setOnAction((e) -> onCancel());
            buttonBar.getButtons().add(cancelButton);
        }
        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
    }

    private void initCheckList() {
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().add(new Text("Check Lists"));
        toolBar.setMinWidth(Region.USE_PREF_SIZE);
        leftPane.setTop(toolBar);
        checkListElements = checkListInfo.getCheckListElements();
        checkListView = new ListView<CheckListForm.CheckListElement>(checkListElements);
        checkListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            CheckListElement selectedItem = checkListView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                doneButton.setDisable(true);
                return;
            }
            Node checkListForm = getChecklistFormNode(selectedItem, Mode.DISPLAY);
            if (checkListForm == null) {
                doneButton.setDisable(true);
                return;
            }
            doneButton.setDisable(false);
            ScrollPane sp = new ScrollPane(checkListForm);
            sp.setFitToWidth(true);
            sp.setPadding(new Insets(0, 0, 0, 10));
            rightPane.setCenter(sp);
        });
        leftPane.setCenter(checkListView);
    }

    private CheckListFormNode getChecklistFormNode(CheckListElement selectedItem, Mode mode) {
        try {
            return new CheckListFormNode(CheckList.read(selectedItem.getFile()), mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onDone() {
        dispose();
    }

    private void onInsert() {
        dispose();
        Platform.runLater(() -> {
            insertCheckListHandler.insert(checkListView.getSelectionModel().getSelectedItem());
        });
    }

    public void setInsertCheckListHandler(IInsertCheckListHandler insertCheckListHandler) {
        this.insertCheckListHandler = insertCheckListHandler;
    }

    @Override
    protected void setDefaultButton() {
    }

    protected void onCancel() {
        dispose();
    }
}
