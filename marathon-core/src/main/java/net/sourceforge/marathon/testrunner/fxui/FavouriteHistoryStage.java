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

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.control.Button;
import net.sourceforge.marathon.fx.api.FXUIUtils;

public class FavouriteHistoryStage extends RunHistoryStage {

    private Button upButton = FXUIUtils.createButton("up", "Move up", false, "Up");
    private Button downButton = FXUIUtils.createButton("down", "Move down", false, "Down");
    private Button unSaveButton = FXUIUtils.createButton("", "Move to unsaved", false, "Unsave");
    private Button removeButton = FXUIUtils.createButton("removeHistory", "Remove selected", false, "Remove");
    private Button removeAllButton = FXUIUtils.createButton("clearSavedHistory", "Clear All", false, "Clear all");

    public FavouriteHistoryStage(RunHistoryInfo runHistoryInfo) {
        super("Favourites Run History", runHistoryInfo, false);
        this.runHistoryInfo = runHistoryInfo;
        initComponents();
    }

    private void initComponents() {
        historyView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newVlaue) -> {
            int selectedIndex = historyView.getSelectionModel().getSelectedIndex();
            int itemCount = historyView.getItems().size();
            boolean enable = selectedIndex != 0 && selectedIndex != -1 && itemCount > 1;
            upButton.setDisable(!enable);
            enable = selectedIndex != itemCount - 1 && selectedIndex != -1 && itemCount > 1;
            downButton.setDisable(!enable);
            removeButton.setDisable(selectedIndex == -1);
            unSaveButton.setDisable(selectedIndex == -1);
        });

        upButton.setOnAction(new HistoryUpDownHandler(historyView, true));
        upButton.setMaxWidth(Double.MAX_VALUE);
        downButton.setOnAction(new HistoryUpDownHandler(historyView, false));
        downButton.setMaxWidth(Double.MAX_VALUE);
        unSaveButton.setOnAction((e) -> onUnsave());
        unSaveButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction((e) -> onRemove());
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeAllButton.setDisable(historyView.getItems().size() == 0);
        removeAllButton.setOnAction((e) -> onRemoveAll());
        removeAllButton.setMaxWidth(Double.MAX_VALUE);
    }

    private void onUnsave() {
        JSONObject jsonObject = removeAndGetTest("favourites");
        if (jsonObject != null) {
            JSONArray history = TestRunnerHistory.getInstance().getHistory("unsaved");
            history.put(jsonObject);
            TestRunnerHistory.getInstance().save();
        }
    }

    private JSONObject removeAndGetTest(String section) {
        JSONArray history = TestRunnerHistory.getInstance().getHistory(section);
        for (int i = 0; i < history.length(); i++) {
            JSONObject test = history.getJSONObject(i);
            JSONObject selectedItem = historyView.getSelectionModel().getSelectedItem();
            if (test.getString("name").equals(selectedItem.getString("name"))) {
                historyView.getItems().remove(historyView.getSelectionModel().getSelectedItem());
                history.remove(i);
                return test;
            }
        }
        return null;
    }

    private void onRemoveAll() {
        TestRunnerHistory.getInstance().remove("favourites");
        historyView.getItems().clear();
    }

    private void onRemove() {
        removeAndGetTest("favourites");
        TestRunnerHistory.getInstance().save();
    }

    @Override protected String getRemeberedCount() {
        return "";
    }

    @Override protected void addButtonsToVerticalButtonBar() {
        verticalButtonBar.getChildren().addAll(upButton, downButton, unSaveButton, removeButton, removeAllButton);
    }

    @Override protected void saveRememberedCount() {
    }
}
