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

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import net.sourceforge.marathon.json.JSONObject;

public class HistoryUpDownHandler implements EventHandler<ActionEvent> {

    public static final Logger LOGGER = Logger.getLogger(HistoryUpDownHandler.class.getName());

    private ListView<JSONObject> historyView;
    private boolean shouldMoveUp;

    public HistoryUpDownHandler(ListView<JSONObject> historyView, boolean shouldMoveUp) {
        this.historyView = historyView;
        this.shouldMoveUp = shouldMoveUp;
    }

    @Override
    public void handle(ActionEvent event) {
        MultipleSelectionModel<JSONObject> selectionModel = historyView.getSelectionModel();
        ObservableList<JSONObject> items = historyView.getItems();
        int selectedIndex = selectionModel.getSelectedIndex();
        JSONObject selectedItem = selectionModel.getSelectedItem();
        items.remove(selectedItem);
        if (shouldMoveUp) {
            items.add(selectedIndex - 1, selectedItem);
        } else {
            items.add(selectedIndex + 1, selectedItem);
        }
        selectionModel.select(selectedItem);
        TestRunnerHistory.getInstance().rewrite("favourites", items);
    }
}
