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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import com.google.common.io.Files;

import javafx.collections.ObservableList;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONException;
import net.sourceforge.marathon.json.JSONObject;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.Preferences;

public class TestRunnerHistory {

    public static final Logger LOGGER = Logger.getLogger(TestRunnerHistory.class.getName());

    private static TestRunnerHistory _instance = new TestRunnerHistory();
    private JSONObject history;

    private TestRunnerHistory() {
        this.history = createHistoryObject();
    }

    public static TestRunnerHistory getInstance() {
        return _instance;
    }

    public void save() {
        removeExceeding();
        File preferenceFile = new File(Constants.getMarathonProjectDirectory(), "runner.json");
        try {
            Files.write(history.toString(2).getBytes(), preferenceFile);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void removeExceeding() {
        JSONArray his = getHistory("unsaved");
        if (his.length() > Preferences.instance().getValue("testrunner", "remember-count", 10)) {
            his.remove(0);
        }
    }

    private JSONObject createHistoryObject() {
        File historyFile = new File(Constants.getMarathonProjectDirectory(), "runner.json");
        if (historyFile.exists()) {
            try {
                return new JSONObject(Files.asCharSource(historyFile, Charset.forName("utf-8")).read());
            } catch (JSONException e) {
            } catch (IOException e) {
            }
        }
        return new JSONObject();
    }

    public JSONArray getHistory(String section) {
        if (!history.has(section)) {
            history.put(section, new JSONArray());
        }
        return history.getJSONArray(section);
    }

    public void remove(String section) {
        history.put(section, new JSONArray());
        save();
    }

    public void rewrite(String section, ObservableList<JSONObject> items) {
        JSONArray value = new JSONArray();
        history.put(section, value);
        for (JSONObject jsonObject : items) {
            value.put(jsonObject);
        }
        save();
    }

}
