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
import java.util.logging.Logger;

import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;
import net.sourceforge.marathon.resource.Project;
import net.sourceforge.marathon.runtime.api.Constants;

public class TestPropertiesInfo {

    public static final Logger LOGGER = Logger.getLogger(TestPropertiesInfo.class.getName());

    private String path;
    private String name;
    private String description;
    private String severity;
    private String id;
    private File testCase;

    public TestPropertiesInfo(File testCase) {
        if (testCase != null) {
            this.testCase = testCase;
            File testDirectory = new File(System.getProperty(Constants.PROP_TEST_DIR));
            path = testDirectory.toPath().relativize(testCase.toPath()).toString();
            path.replace(File.separatorChar, '/');
            this.name = Project.getTestName(testCase);
            this.description = Project.getTestDescription(testCase);
            this.severity = Project.getTestSeverity(testCase);
            this.id = Project.getTestID(testCase);
        }
    }

    public String getRawName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getSeverity() {
        return severity;
    }

    public String getId() {
        return id;
    }

    public void setRawName(String rawName) {
        this.name = rawName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void writeToJSON(JSONObject issue) {
        JSONArray tests;
        if (!issue.has("tests")) {
            tests = new JSONArray();
        } else {
            tests = issue.getJSONArray("tests");
        }
        removeTestIfPresent(issue);
        tests.put(toJSON());
        issue.put("tests", tests);
    }

    private void removeTestIfPresent(JSONObject issue) {
        JSONArray tests = issue.getJSONArray("tests");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject testObj = tests.getJSONObject(i);
            String name = testObj.getString("path");
            if (name.equals(path)) {
                tests.remove(i);
            }
        }
    }

    private JSONObject toJSON() {
        JSONObject test = new JSONObject();
        test.put("path", path);
        return test;
    }

    public File save() {
        return save(testCase);
    }

    public File save(File file) {
        Project.setTestName(name, file);
        Project.setTestDescription(description, file);
        Project.setTestSeverity(severity, file);
        Project.setTestID(id, file);
        return file;
    }

    public File getTestCase() {
        return testCase;
    }
}
