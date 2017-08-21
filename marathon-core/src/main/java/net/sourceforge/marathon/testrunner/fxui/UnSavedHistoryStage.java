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

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.display.IInputHanler;
import net.sourceforge.marathon.fx.display.MarathonInputStage;
import net.sourceforge.marathon.runtime.api.Preferences;

public class UnSavedHistoryStage extends RunHistoryStage {

    public static final Logger LOGGER = Logger.getLogger(UnSavedHistoryStage.class.getName());

    private Button favouriteButton = FXUIUtils.createButton("favourite", "Mark as favourite", false, "Move to favourites...");
    private Button manageFavouritesButton = FXUIUtils.createButton("", "Manage favourites", true, "Manage favourites...");
    private Button removeButton = FXUIUtils.createButton("removeHistory", "Remove selected", false, "Remove");
    private Button removeAllButton = FXUIUtils.createButton("clearUnSavedHistory", "Clear All", false, "Clear all");

    public UnSavedHistoryStage(RunHistoryInfo runHistoryInfo) {
        super("Unsaved History", runHistoryInfo, true, "History of test runs", null);
        initComponents();
    }

    private void initComponents() {
        historyView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newVlaue) -> {
            int selectedIndex = historyView.getSelectionModel().getSelectedIndex();
            removeButton.setDisable(selectedIndex == -1);
            favouriteButton.setDisable(selectedIndex == -1);
        });

        favouriteButton.setOnAction((e) -> onSave());
        favouriteButton.setMaxWidth(Double.MAX_VALUE);
        manageFavouritesButton.setOnAction((e) -> onManageFavourites());
        manageFavouritesButton.setMaxWidth(Double.MAX_VALUE);
        removeButton.setOnAction((e) -> onRemove());
        removeButton.setMaxWidth(Double.MAX_VALUE);
        removeAllButton.setDisable(historyView.getItems().size() == 0);
        removeAllButton.setOnAction((e) -> onRemoveAll());
        removeAllButton.setMaxWidth(Double.MAX_VALUE);
    }

    private void onManageFavourites() {
        FavouriteHistoryStage favouriteHistoryStage = new FavouriteHistoryStage(new RunHistoryInfo("favourites"));
        favouriteHistoryStage.getStage().showAndWait();
        ObservableList<JSONObject> items = historyView.getItems();
        items.clear();
        items.addAll(runHistoryInfo.getTests());
    }

    private void onSave() {
        MarathonInputStage testNameStage = new MarathonInputStage("Test name", "Save the current test run",
                FXUIUtils.getIcon("testrunner")) {

            @Override protected String validateInput(String name) {
                String errorMessage = null;
                if (name.length() == 0 || name.trim().isEmpty()) {
                    errorMessage = "Enter a valid name";
                }
                return errorMessage;
            }

            @Override protected String getInputFiledLabelText() {
                return "Enter a test name: ";
            }

            @Override protected void setDefaultButton() {
                okButton.setDefaultButton(true);
            }
        };
        TestNameHandler testNameHandler = new TestNameHandler();
        testNameStage.setInputHandler(testNameHandler);
        testNameStage.getStage().showAndWait();
        if (testNameHandler.getTestName() == null)
            return;
        JSONObject jsonObject = removeAndGetTest("unsaved");
        if (jsonObject != null) {
            JSONArray history = TestRunnerHistory.getInstance().getHistory("favourites");
            String testName = testNameHandler.getTestName();
            jsonObject.put("name", testName);
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
        TestRunnerHistory.getInstance().remove("unsaved");
        historyView.getItems().clear();
    }

    private void onRemove() {
        removeAndGetTest("unsaved");
        TestRunnerHistory.getInstance().save();
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

    @Override protected String getRemeberedCount() {
        return Preferences.instance().getValue("testrunner", "remember-count", 10) + "";
    }

    @Override protected void addButtonsToVerticalButtonBar() {
        verticalButtonBar.getChildren().addAll(favouriteButton, manageFavouritesButton, removeButton, removeAllButton);
    }

    @Override protected void saveRememberedCount() {
        Preferences.instance().setValue("testrunner", "remember-count", Integer.parseInt(countField.getText()));
    }
}
