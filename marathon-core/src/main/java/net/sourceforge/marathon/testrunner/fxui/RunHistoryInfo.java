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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RunHistoryInfo {

    public static final Logger LOGGER = Logger.getLogger(RunHistoryInfo.class.getName());

    private String section;

    public RunHistoryInfo(String section) {
        this.section = section;
    }

    private ObservableList<JSONObject> populate(JSONArray unsavedHistory) {
        ObservableList<JSONObject> tests = FXCollections.observableArrayList();
        for (int i = 0; i < unsavedHistory.length(); i++) {
            JSONObject testJSON = unsavedHistory.getJSONObject(i);
            if (section.equals("unsaved")) {
                tests.add(0, testJSON);
            } else {
                tests.add(testJSON);
            }
        }
        return tests;
    }

    public ObservableList<JSONObject> getTests() {
        return populate(TestRunnerHistory.getInstance().getHistory(section));
    }
}
