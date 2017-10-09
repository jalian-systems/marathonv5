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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.io.Files;

import net.sourceforge.marathon.runtime.api.Constants;

public class Issue {

    public static final Logger LOGGER = Logger.getLogger(Issue.class.getName());

    private JSONArray issues;

    public Issue() {
        this.issues = createIssues();
    }

    private void save() {
        File issueFile = new File(Constants.getMarathonProjectDirectory(), "issues.json");
        try {
            Files.write(issues.toString(2).getBytes(), issueFile);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray createIssues() {
        File historyFile = new File(Constants.getMarathonProjectDirectory(), "issues.json");
        if (historyFile.exists()) {
            try {
                return new JSONArray(Files.asCharSource(historyFile, Charset.forName("utf-8")).read());
            } catch (JSONException e) {
            } catch (IOException e) {
            }
        }
        return new JSONArray();
    }

    public JSONArray getIssues() {
        return issues;
    }

    public void save(List<JSONObject> addedIssue, List<JSONObject> unAddedIssue) {
        issues = new JSONArray();
        addedIssue.stream().forEach((issue) -> issues.put(issue));
        unAddedIssue.stream().forEach((issue) -> issues.put(issue));
        save();
    }
}
