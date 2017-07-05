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
package net.sourceforge.marathon.testrunner.fxui;

import java.util.logging.Logger;

import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.display.IInputHanler;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.testrunner.fxui.TestTreeItem.State;

public abstract class RunHistoryStage extends ModalDialog<RunHistoryInfo> {

    public static final Logger LOGGER = Logger.getLogger(RunHistoryStage.class.getName());

    protected RunHistoryInfo runHistoryInfo;
    private HBox historyPane = new HBox(5);
    protected ListView<JSONObject> historyView = new ListView<>();
    private FormPane form = new FormPane("run-count-form", 2);
    protected TextField countField = new TextField();
    protected VBox verticalButtonBar = new VBox();
    private ButtonBar buttonBar = new ButtonBar();
    private Button doneButton = FXUIUtils.createButton("ok", null, true, "Done");
    private boolean countNeeded;

    public RunHistoryStage(String title, RunHistoryInfo runHistoryInfo, boolean countNeeded, String subTitle, Node icon) {
        super(title, subTitle, icon);
        this.runHistoryInfo = runHistoryInfo;
        this.countNeeded = countNeeded;
        initComponents();
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override protected Parent getContentPane() {
        addButtonsToVerticalButtonBar();
        BorderPane content = new BorderPane();
        content.getStyleClass().add("run-history-stage");
        content.setId("RunHistoryStage");
        content.setCenter(historyPane);
        content.setBottom(buttonBar);
        return content;
    }

    private void initComponents() {
        VBox.setVgrow(historyView, Priority.ALWAYS);
        historyView.setItems(FXCollections.observableArrayList(runHistoryInfo.getTests()));
        historyView.setCellFactory(new Callback<ListView<JSONObject>, ListCell<JSONObject>>() {
            @Override public ListCell<JSONObject> call(ListView<JSONObject> param) {
                return new HistoryStateCell();
            }
        });

        VBox historyBox = new VBox(5);
        HBox.setHgrow(historyBox, Priority.ALWAYS);

        countField.setText(getRemeberedCount());
        if (countNeeded) {
            form.addFormField("Max count of remembered runs: ", countField);
        }
        historyBox.getChildren().addAll(new Label("Select test", FXUIUtils.getIcon("params")), historyView, form);

        verticalButtonBar.setId("vertical-buttonbar");
        historyPane.setId("history-pane");
        historyPane.getChildren().addAll(historyBox, verticalButtonBar);

        doneButton.setOnAction((e) -> onOK());
        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
        buttonBar.getButtons().addAll(doneButton);
    }

    protected abstract String getRemeberedCount();

    protected abstract void addButtonsToVerticalButtonBar();

    protected abstract void saveRememberedCount();

    private void onOK() {
        saveRememberedCount();
        TestRunnerHistory.getInstance().save();
        dispose();
    }

    @Override protected void setDefaultButton() {
        doneButton.setDefaultButton(true);
    }

    public class HistoryStateCell extends ListCell<JSONObject> {
        @Override protected void updateItem(JSONObject item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setGraphic(getIcon(State.valueOf(item.getString("state"))));
                setText(item.getString("name") + " (" + item.get("run-on") + ")");
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }

    private Node getIcon(State state) {
        Node icon = null;
        if (state == State.ERROR) {
            icon = FXUIUtils.getIcon("testerror");
        } else if (state == State.FAILURE) {
            icon = FXUIUtils.getIcon("testfail");
        } else if (state == State.SUCCESS) {
            icon = FXUIUtils.getIcon("tsuiteok");
        }
        return icon;
    }

    final class TestNameHandler implements IInputHanler {
        private String testName;

        @Override public void handleInput(String name) {
            this.testName = name;
        }

        public String getTestName() {
            return testName;
        }
    }
}
