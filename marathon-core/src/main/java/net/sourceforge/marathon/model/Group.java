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
package net.sourceforge.marathon.model;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.inject.BindingAnnotation;

import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import net.sourceforge.marathon.editor.IEditorProvider.EditorType;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.model.GroupEntry.GroupEntryType;
import net.sourceforge.marathon.runtime.api.Constants;

public class Group {

    public static final Logger LOGGER = Logger.getLogger(Group.SuitesPanel.class.getName());

    @BindingAnnotation @Retention(RUNTIME) public @interface SuitesPanel {
    }

    @BindingAnnotation @Retention(RUNTIME) public @interface FeaturesPanel {
    }

    @BindingAnnotation @Retention(RUNTIME) public @interface StoriesPanel {
    }

    @BindingAnnotation @Retention(RUNTIME) public @interface IssuesPanel {
    }

    public enum GroupType {
        SUITE("Suite", Constants.getSuiteDirectory(), ".suite", "Suites", "Show All Suites", "tsuite", EditorType.SUITE) {
            @Override public boolean droppable(List<File> files, Path self) {
                if (files.size() == 0) {
                    return false;
                }
                for (File file : files) {
                    if (!Constants.isSuiteFile(file) && !Constants.isTestFile(file) && !Constants.isFeatureFile(file)
                            && !Constants.isStoryFile(file) && !Constants.isIssueFile(file)) {
                        return false;
                    }
                    if (file.toPath().equals(self)) {
                        return false;
                    }
                    if (Constants.isSuiteFile(file)) {
                        try {
                            Group suite = Group.findByFile(SUITE, file.toPath());
                            if (suite.containsRecursive(self)) {
                                return false;
                            }
                        } catch (IOException e) {
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override public String fileCommentHeader() {
        // @formatter:off
                return  
                    "# You can assign one or more tests to a suite. The suite file\n" +
                    "# is in JSON format.\n" +
                    "# Example:\n" +
                    "# {\n" + 
                    "#   \"tests\": [\n" + 
                    "#     {\n" + 
                    "#       \"name\": \"<test-name>\",\n" +
                    "#       \"type\": \"TEST\"\n" + 
                    "#     },\n" + 
                    "#     {\n" + 
                    "#       \"name\": \"<suite-file>\",\n" +
                    "#       \"type\": \"SUITE\"\n" + 
                    "#     },\n" + 
                    "#     {\n" + 
                    "#       \"name\": \"<feature-file>\",\n" +
                    "#       \"type\": \"FEATURE\"\n" + 
                    "#     },\n" + 
                    "#     {\n" + 
                    "#       \"name\": \"<story-file>\",\n" +
                    "#       \"type\": \"STORY\"\n" + 
                    "#     },\n" + 
                    "#     {\n" + 
                    "#       \"name\": \"<issue-file>\",\n" +
                    "#       \"type\": \"ISSUE\"\n" + 
                    "#     }\n" + 
                    "#   ],\n" + 
                    "#   \"name\": \"<suite-name>\"\n" +
                    "# }\n" + 
                    "# Once you assign tests, features, stories, issues or other stories to a suite\n" +
                    "# you can run them from the navigator or from command line batch mode.\n" +
                    "#\n" +      
                    "# marathonite -batch <project-directory> +<suite-name|issue-file>\n\n";
                // @formatter:on
            }

        },
        FEATURE("Feature", Constants.getFeatureDirectory(), ".feature", "Features", "Show All Features", "tfeature",
                EditorType.FEATURE) {
            @Override public boolean droppable(List<File> files, Path self) {
                if (files.size() == 0) {
                    return false;
                }
                for (File file : files) {
                    if (!file.isFile() || !Constants.isTestFile(file)) {
                        return false;
                    }
                }
                return true;
            }

            @Override public String fileCommentHeader() {
        // @formatter:off
                return
                    "# You can assign one or more tests to a feature. The feature file\n" +
                    "# is in JSON format.\n" +
                    "# Example:\n" +
                    "#    {\n" +
                    "#      \"tests\": [\n" +
                    "#        {\n" +
                    "#          \"name\": \"<name-of-the-test>\",\n" +
                    "#          \"type\": \"TEST\"\n" +
                    "#        },\n" +
                    "#        {\n" +
                    "#          \"name\": \"<name-of-the-test-2>\",\n" +
                    "#          \"type\": \"TEST\"\n" +
                    "#        }\n" +
                    "#      ],\n" +
                    "#      \"name\": \"<Name of the Feature>\"\n" +
                    "#    }\n" +
                    "#\n" +
                    "# Once you assign tests to a feature you can run them from the navigator\n" +
                    "# or from command line batch mode.\n" +
                    "#\n" +
                    "# marathonite -batch <project-directory> @<feature-name|feature-file>\n\n";
                // @formatter:on
            }

        },
        STORY("Story", Constants.getStoryDirectory(), ".story", "Stories", "Show All Stories", "tstory", EditorType.STORY) {
            @Override public boolean droppable(List<File> files, Path self) {
                if (files.size() == 0) {
                    return false;
                }
                for (File file : files) {
                    if (!file.isFile() || !Constants.isTestFile(file)) {
                        return false;
                    }
                }
                return true;
            }

            @Override public String fileCommentHeader() {
        // @formatter:off
                return
                    "# You can assign one or more tests to a story. The story file\n" +
                    "# is in JSON format.\n" +
                    "# Example:\n" +
                    "#    {\n" +
                    "#      \"tests\": [\n" +
                    "#        {\n" +
                    "#          \"name\": \"<name-of-the-test>\",\n" +
                    "#          \"type\": \"TEST\"\n" +
                    "#        },\n" +
                    "#        {\n" +
                    "#          \"name\": \"<name-of-the-test-2>\",\n" +
                    "#          \"type\": \"TEST\"\n" +
                    "#        }\n" +
                    "#      ],\n" +
                    "#      \"name\": \"<Name of the Story>\"\n" +
                    "#    }\n" +
                    "#\n" +
                    "# Once you assign tests to a story you can run them from the navigator\n" +
                    "# or from command line batch mode.\n" +
                    "#\n" +
                    "# marathonite -batch <project-directory> #<story-name|story-file>\n\n";
                // @formatter:on
            }

        },
        ISSUE("Issue", Constants.getIssueDirectory(), ".issue", "Issues", "Show All Issues", "tissue", EditorType.ISSUE) {
            @Override public boolean droppable(List<File> files, Path self) {
                if (files.size() == 0) {
                    return false;
                }
                for (File file : files) {
                    if (!file.isFile() || !Constants.isTestFile(file)) {
                        return false;
                    }
                }
                return true;
            }

            @Override public String fileCommentHeader() {
        // @formatter:off
                return
                    "# You can assign one or more tests to a issue. The issue file\n" +
                    "# is in JSON format.\n" +
                    "# Example:\n" +
                    "#    {\n" +
                    "#      \"tests\": [\n" +
                    "#        {\n" +
                    "#          \"name\": \"<name-of-the-test>\",\n" +
                    "#          \"type\": \"TEST\"\n" +
                    "#        },\n" +
                    "#        {\n" +
                    "#          \"name\": \"<name-of-the-test-2>\",\n" +
                    "#          \"type\": \"TEST\"\n" +
                    "#        }\n" +
                    "#      ],\n" +
                    "#      \"name\": \"<Issue ID>\"\n" +
                    "#    }\n" +
                    "#\n" +
                    "# Once you assign tests to a issue you can run them from the navigator\n" +
                    "# or from command line batch mode.\n" +
                    "#\n" +
                    "# marathonite -batch <project-directory> !<issue-id|issue-file>\n\n";
                // @formatter:on
            }

        };

        private String fileType;
        private File dir;
        private String ext;
        private String dockName;
        private String dockDescription;
        private String dockIcon;
        private EditorType editorType;

        GroupType(String fileType, File dir, String ext, String dockName, String dockDescription, String dockIcon,
                EditorType editorType) {
            this.fileType = fileType;
            this.dir = dir;
            this.ext = ext;
            this.dockName = dockName;
            this.dockDescription = dockDescription;
            this.dockIcon = dockIcon;
            this.editorType = editorType;
        }

        public String fileType() {
            return fileType;
        }

        public File dir() {
            return dir;
        }

        public String ext() {
            return ext;
        }

        public abstract boolean droppable(List<File> files, Path self);

        public String dockDescription() {
            return dockDescription;
        }

        public String dockName() {
            return dockName;
        }

        public Node dockIcon() {
            return FXUIUtils.getIcon(dockIcon);
        }

        public EditorType editorType() {
            return editorType;
        }

        public abstract String fileCommentHeader();

    }

    private String name;
    private List<GroupEntry> entries = new ArrayList<>();

    private Path path;
    private JSONObject data = new JSONObject();

    private Group(File file) throws IOException {
        this.path = file.getCanonicalFile().toPath();
        String text = new String(Files.readAllBytes(this.path), Charset.defaultCharset());
        init(new JSONObject(text));
    }

    protected boolean containsRecursive(Path self) throws IOException {
        for (GroupEntry entry : entries) {
            if (entry.getFilePath().equals(self))
                return true;
            if (entry.getType() == GroupEntryType.SUITE) {
                Group entrySuite = Group.findByFile(GroupType.SUITE, entry.getFilePath());
                if (entrySuite.containsRecursive(self))
                    return true;
            }
        }
        return false;
    }

    private Group(String name) {
        this.name = name;
    }

    public void init(JSONObject jsonObject) {
        name = jsonObject.getString("name");
        JSONArray tests = jsonObject.getJSONArray("tests");
        for (int i = 0; i < tests.length(); i++) {
            JSONObject o = tests.getJSONObject(i);
            String sType = o.getString("type");
            String sName = o.getString("name");
            GroupEntry.GroupEntryType type = GroupEntry.GroupEntryType.valueOf(sType);
            try {
                if (type == GroupEntryType.TEST) {
                    entries.add(new GroupTestEntry(sName));
                } else if (type == GroupEntryType.FOLDER) {
                    entries.add(new GroupFolderEntry(sName));
                } else {
                    entries.add(new GroupGroupEntry(type, sName));
                }
            } catch (IOException e) {
                Logger.getLogger(Group.class.getName()).warning(e.getMessage());
            }
        }
        if (jsonObject.has("properties")) {
            data = jsonObject.getJSONObject("properties");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<GroupEntry> getEntries() {
        return entries;
    }

    public String toJSONString() {
        JSONObject o = new JSONObject();
        o.put("name", name);
        JSONArray a = new JSONArray();
        for (GroupEntry test : entries) {
            a.put(test.toJSON());
        }
        o.put("tests", a);
        if (data.keySet().size() > 0) {
            o.put("properties", data);
        }
        return o.toString(2);
    }

    public static List<Group> getGroups(GroupType type) {
        List<Group> groups = new ArrayList<>();
        File dir = type.dir();
        File[] list = dir.listFiles((f, n) -> n.endsWith(type.ext()));
        if (list != null) {
            for (File file : list) {
                try {
                    groups.add(new Group(file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return groups;
    }

    @Override public String toString() {
        return getName();
    }

    public Path getPath() {
        return path;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (path == null ? 0 : path.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Group other = (Group) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }

    public static Group findByFile(GroupType type, Path file) throws IOException {
        List<Group> groups = getGroups(type);
        for (Group group : groups) {
            if (group.getPath().equals(file)) {
                return group;
            }
        }
        return null;
    }

    public static Group createGroup(GroupType type, Path path, String name) {
        List<Group> groups = getGroups(type);
        for (Group g : groups) {
            if (g.getName().equals(name)) {
                Optional<ButtonType> option = FXUIUtils.showConfirmDialog(null,
                        type.fileType() + " `" + g.getName() + "` name is already used.", "Duplicate " + type.fileType() + " Name",
                        AlertType.CONFIRMATION);
                if (!option.isPresent() || option.get() == ButtonType.CANCEL) {
                    return null;
                }
            }
        }
        Group group = new Group(name);
        try {
            Files.write(path, (type.fileCommentHeader() + group.toJSONString()).getBytes());
            return new Group(path.toFile());
        } catch (IOException e) {
            return null;
        }
    }

    public static Group createGroup(Path path) throws IOException {
        return new Group(path.toFile());
    }

    public static void delete(GroupType type, Group group) throws IOException {
        Group found = Group.findByFile(type, group.getPath());
        if (found == null) {
            FXUIUtils.showMessageDialog(null,
                    type.fileType() + " `" + group.getName() + "`(" + group.getPath().toString() + ") not found.",
                    "Unknown " + type.fileType(), AlertType.ERROR);
        }
        Files.delete(found.getPath());
    }

    public static void refresh(Group group) {
        String text = null;
        try {
            text = new String(Files.readAllBytes(group.getPath()));
            group.init(new JSONObject(text));
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        } catch (JSONException e) {
            Logger.getLogger(Group.class.getName()).warning("Group not in JSON format.");
        }
    }

    public static void updateFile(Group group) throws IOException {
        Files.write(group.getPath(), group.toJSONString().getBytes());
    }

    public <T> void setProperty(String p, T v) {
        data.put(p, v);
    }

    @SuppressWarnings("unchecked") public <T> T getProperty(String p, T def) {
        if (!data.has(p)) {
            return def;
        }
        T v = (T) data.get(p);
        return v == null ? def : v;
    }

    public boolean hasTest(Path testPath) {
        if (testPath == null) {
            return false;
        }
        return entries.stream().anyMatch((e) -> e.getFilePath().equals(testPath));
    }

    public static Group findByName(GroupType type, String groupName) {
        List<Group> groups = getGroups(type);
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

}
