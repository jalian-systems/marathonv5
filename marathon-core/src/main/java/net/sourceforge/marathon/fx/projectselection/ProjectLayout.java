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
package net.sourceforge.marathon.fx.projectselection;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.fx.api.FileSelectionHandler;
import net.sourceforge.marathon.runtime.fx.api.IFileSelectedAction;
import net.sourceforge.marathon.runtime.fx.api.IPropertiesLayout;

public class ProjectLayout implements IPropertiesLayout, IFileSelectedAction {

    public static final Logger LOGGER = Logger.getLogger(ProjectLayout.class.getName());

    private TextField projectNameField = new TextField();
    private TextField issuesTrackerPattern = new TextField();
    private TextField testManagementPattern = new TextField();
    private TextArea descriptionArea = new TextArea();
    private TextField dirField = new TextField() {
        @Override
        public void requestFocus() {
        };
    };
    private Button browseButton = FXUIUtils.createButton("browse", "Browse project", true, "Browse");
    private ModalDialog<?> parent;

    public static String projectDir = "";
    private String testDir;
    private String fixtureDir;
    private String moduleDir;
    private String checklistDir;

    public ProjectLayout(ModalDialog<?> parent) {
        this.parent = parent;
        initComponents();
    }

    @Override
    public Node getContent() {
        FormPane form = new FormPane("project-layout", 3);
        issuesTrackerPattern.setPromptText("https://bugzilla.mozilla.org/show_bug.cgi?id=%s");
        testManagementPattern.setPromptText("http://mantis.testlink.org/view.php?id=%s");
        // @formatter:off
        form.addFormField("Name: ", projectNameField)
            .addFormField("Directory: ", dirField, browseButton)
            .addFormField("Description: ", descriptionArea)
            .addFormField("Issue Tracker Pattern: ", issuesTrackerPattern)
            .addFormField("Test Management Pattern: ", testManagementPattern);
        // @formatter:on
        return form;
    }

    private void initComponents() {
        dirField.setEditable(false);
        dirField.setFocusTraversable(false);
        dirField.textProperty().addListener((observable, oldValue, newValue) -> projectDir = dirField.getText());

        FileSelectionHandler fileSelectionHandler = new FileSelectionHandler(this, null, parent, null, "Select Project Directory");
        fileSelectionHandler.setMode(FileSelectionHandler.DIRECTORY_CHOOSER);
        browseButton.setOnAction(fileSelectionHandler);
    }

    @Override
    public String getName() {
        return "Project";
    }

    @Override
    public Node getIcon() {
        return FXUIUtils.getIcon("prj_obj");
    }

    @Override
    public void getProperties(Properties props) {
        System.setProperty(Constants.PROP_PROJECT_NAME, projectNameField.getText());
        props.setProperty(Constants.PROP_PROJECT_NAME, projectNameField.getText());
        props.setProperty(Constants.PROP_PROJECT_DIR, dirField.getText().replace(File.separatorChar, '/'));
        props.setProperty(Constants.PROP_PROJECT_DESCRIPTION, descriptionArea.getText());
        props.setProperty(Constants.PROP_ISSUE_PATTERN, issuesTrackerPattern.getText());
        props.setProperty(Constants.PROP_TMS_PATTERN, testManagementPattern.getText());
        System.setProperty(Constants.PROP_ISSUE_PATTERN, issuesTrackerPattern.getText());
        System.setProperty(Constants.PROP_TMS_PATTERN, testManagementPattern.getText());

        if (testDir != null) {
            props.setProperty(Constants.PROP_TEST_DIR, testDir);
        }
        if (fixtureDir != null) {
            props.setProperty(Constants.PROP_FIXTURE_DIR, fixtureDir);
        }
        if (moduleDir != null) {
            props.setProperty(Constants.PROP_MODULE_DIRS, moduleDir);
        }
        if (checklistDir != null) {
            props.setProperty(Constants.PROP_CHECKLIST_DIR, checklistDir);
        }
    }

    @Override
    public void setProperties(Properties props) {
        // Also store the directory props and give them back
        testDir = props.getProperty(Constants.PROP_TEST_DIR);
        fixtureDir = props.getProperty(Constants.PROP_FIXTURE_DIR);
        moduleDir = props.getProperty(Constants.PROP_MODULE_DIRS);
        checklistDir = props.getProperty(Constants.PROP_CHECKLIST_DIR);
        projectNameField.setText(props.getProperty(Constants.PROP_PROJECT_NAME, ""));
        String pdir = props.getProperty(Constants.PROP_PROJECT_DIR, "").replace('/', File.separatorChar);
        projectDir = pdir;
        dirField.setText(pdir);
        if (!dirField.getText().equals("")) {
            browseButton.setDisable(true);
        }
        descriptionArea.setText(props.getProperty(Constants.PROP_PROJECT_DESCRIPTION, ""));
        issuesTrackerPattern.setText(props.getProperty(Constants.PROP_ISSUE_PATTERN, ""));
        testManagementPattern.setText(props.getProperty(Constants.PROP_TMS_PATTERN, ""));
    }

    @Override
    public boolean isValidInput(boolean showAlert) {
        if (projectNameField.getText() == null || projectNameField.getText().equals("")) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(parent.getStage(), "Project name can't be empty", "Project Name", AlertType.ERROR);
            }
            Platform.runLater(() -> projectNameField.requestFocus());
            return false;
        }
        if (dirField.getText() == null || dirField.getText().equals("")) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(parent.getStage(), "Project directory can't be empty", "Project Directory",
                        AlertType.ERROR);
            }
            Platform.runLater(() -> browseButton.requestFocus());
            return false;
        }
        return true;
    }

    @Override
    public void filesSelected(List<File> selectedFiles, Object cookie) {
        if (selectedFiles != null && selectedFiles.size() != 0) {
            dirField.setText(selectedFiles.get(0).getAbsolutePath());
        }
    }

}
