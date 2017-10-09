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

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.editor.IContentChangeListener;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;

public class CheckListView extends BorderPane implements IContentChangeListener {

    public static final Logger LOGGER = Logger.getLogger(CheckListView.class.getName());

    private CheckListFormNode checkListFormNode;
    private boolean dirty = false;
    ArrayList<IContentChangeListener> contentChangeListeners = new ArrayList<IContentChangeListener>();
    private ScrollPane pane;
    private VBox verticalButtonBar;

    public CheckListView(boolean selectable) {
        initComponents(selectable);
    }

    public CheckListView(CheckListFormNode checkListFormNode) {
        this(checkListFormNode.isSelectable());
        setCheckListNode(checkListFormNode);
        this.checkListFormNode.addContentChangeListener(this);
    }

    private void initComponents(boolean selectable) {
        initVerticalButtonBar();
        pane = new ScrollPane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        setCenter(pane);
        if (selectable) {
            setRight(verticalButtonBar);
            verticalButtonBar.setStyle("-fx-padding: 5px");
            verticalButtonBar.setDisable(true);
        }
        VBox titleBox = new VBox();
        Label titleLabel = new Label("Editing CheckList", FXUIUtils.getIcon("newCheckList"));
        titleLabel.getStyleClass().add("modaldialog-title");
        titleBox.getChildren().add(titleLabel);
        titleBox.getChildren().add(new Separator());
        setTop(titleBox);
    }

    public void setCheckListNode(Node node) {
        pane.setContent(node);
        pane.setFitToWidth(true);
        pane.setFitToHeight(true);
        this.checkListFormNode = (CheckListFormNode) node;
        verticalButtonBar.setDisable(false);
        checkListFormNode.addContentChangeListener(this);
        HBox.setHgrow(checkListFormNode, Priority.ALWAYS);
    }

    private void initVerticalButtonBar() {
        verticalButtonBar = new VBox();
        Button upButton = FXUIUtils.createButton("up", "Move selection up", true, "Up");
        upButton.setMaxWidth(Double.MAX_VALUE);
        upButton.setOnAction((e) -> {
            checkListFormNode.moveUpSelected();
            if (checkListFormNode.isDirty()) {
                fireContentChanged();
            }
        });

        Button deleteButton = FXUIUtils.createButton("remove", "Delete selection", true, "Remove");
        deleteButton.setOnAction((e) -> {
            checkListFormNode.deleteSelected();
            if (checkListFormNode.isDirty()) {
                fireContentChanged();
            }
        });

        Button downButton = FXUIUtils.createButton("down", "Move selection down", true, "Down");
        downButton.setOnAction((e) -> {
            checkListFormNode.moveDownSelected();
            if (checkListFormNode.isDirty()) {
                fireContentChanged();
            }
        });
        downButton.setMaxWidth(Double.MAX_VALUE);

        verticalButtonBar.getChildren().addAll(upButton, deleteButton, downButton);
    }

    public void onHeader() {
        TextInputDialog input = new TextInputDialog();
        input.setTitle("New Header");
        input.setContentText("Label: ");
        input.setHeaderText("Please enter header text.");
        Optional<String> result = input.showAndWait();
        result.ifPresent(name -> {
            checkListFormNode.addHeader(name);
            fireContentChanged();
        });
    }

    public void onChecklist() {
        TextInputDialog input = new TextInputDialog();
        input.setTitle("New Checklist Item");
        input.setContentText("Label: ");
        input.setHeaderText("Please enter item label.");
        Optional<String> result = input.showAndWait();
        result.ifPresent(name -> {
            checkListFormNode.addCheckListItem(name);
            fireContentChanged();
        });
    }

    public void onTextArea() {
        TextInputDialog input = new TextInputDialog();
        input.setTitle("New Textbox");
        input.setContentText("Label: ");
        input.setHeaderText("Please enter text box label.");
        Optional<String> result = input.showAndWait();
        result.ifPresent(name -> {
            checkListFormNode.addTextArea(name);
            fireContentChanged();
        });
    }

    public void addContentChangeListener(IContentChangeListener l) {
        contentChangeListeners.add(l);
    }

    private void fireContentChanged() {
        dirty = true;
        for (IContentChangeListener l : contentChangeListeners) {
            l.contentChanged();
        }
    }

    @Override public void contentChanged() {
        fireContentChanged();
    }

    public boolean isDitry() {
        return dirty;
    }

    public static class InputStage extends ModalDialog<String> {

        private String labelText;
        private TextField inputTextField = new TextField();
        private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
        private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
        private ButtonBarX buttonBar = new ButtonBarX();

        public InputStage(String title, String labelText) {
            super(title, null, FXUIUtils.getIcon("newCheckList"));
            this.labelText = labelText;
            initComponents();
        }

        @Override protected void initialize(Stage stage) {
            super.initialize(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
        }

        private void initComponents() {
            inputTextField.setPrefColumnCount(25);
            inputTextField.textProperty().addListener((observable, oldValue, newValue) -> checkEnableOk());

            okButton.setDisable(true);
            okButton.setOnAction((e) -> onOK());
            cancelButton.setOnAction((e) -> onCancel());
            buttonBar.getButtons().addAll(okButton, cancelButton);
        }

        @Override protected Parent getContentPane() {
            BorderPane borderPane = new BorderPane();
            borderPane.setId("check-list-input-stage");
            FormPane form = new FormPane("check-list-input-stage-form", 2);
            form.addFormField(labelText, inputTextField);
            borderPane.setCenter(form);
            borderPane.setBottom(buttonBar);
            return borderPane;
        }

        private void checkEnableOk() {
            if (inputTextField.getText().trim().equals("")) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        }

        private void onOK() {
            setReturnValue(inputTextField.getText());
            dispose();
        }

        private void onCancel() {
            setReturnValue(null);
            dispose();
        }

        @Override protected void setDefaultButton() {
            okButton.setDefaultButton(true);
        }
    }

}
