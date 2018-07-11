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
package net.sourceforge.marathon.runtime;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.ValidationUtil;
import net.sourceforge.marathon.runtime.fx.api.FileSelectionHandler;
import net.sourceforge.marathon.runtime.fx.api.IFileSelectedAction;
import net.sourceforge.marathon.runtime.fx.api.IPropertiesLayout;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;

public class MainLayout implements ISubPropertiesLayout, IFileSelectedAction, IPropertiesLayout {

    public static final Logger LOGGER = Logger.getLogger(MainLayout.class.getName());

    private ModalDialog<?> parent;
    private TextField classNameField = new TextField();
    private TextArea programArgumentsField = new TextArea();
    private TextArea vmArgumentsField = new TextArea();
    private TextField workingDirField = new TextField() {
        @Override
        public void requestFocus() {
        };
    };
    private TextField javaHomeField = new TextField() {
        @Override
        public void requestFocus() {
        };
    };
    private Button workindDirBrowse = FXUIUtils.createButton("browse", "Browse working dir", true, "Browse");
    private Button javaHomeBrowse = FXUIUtils.createButton("browse", "Browse java home", true, "Browse");

    public MainLayout(ModalDialog<?> parent) {
        this.parent = parent;
        iniComponents();
    }

    @Override
    public Node getContent() {
        FormPane form = new FormPane("main-layout", 3);
        // @formatter:off
            form.addFormField("Class Name: ", classNameField)
                .addFormField("Program Arguments: ", programArgumentsField)
                .addFormField("VM Arguments: ", vmArgumentsField)
                .addFormField("Working Directory: ", workingDirField, workindDirBrowse)
                .addFormField("Java Home: ", javaHomeField, javaHomeBrowse);
        // @formatter:on
        return form;

    }

    private void iniComponents() {
        workingDirField.setEditable(false);
        FileSelectionHandler workingDirHandler = new FileSelectionHandler(this, null, parent, workingDirField,
                "Select Working Directory");
        workingDirHandler.setMode(FileSelectionHandler.DIRECTORY_CHOOSER);
        workindDirBrowse.setOnAction(workingDirHandler);

        javaHomeField.setEditable(false);
        FileSelectionHandler javaHomeHandler = new FileSelectionHandler(this, null, parent, javaHomeField,
                "Select Java Home Folder");
        javaHomeHandler.setMode(FileSelectionHandler.DIRECTORY_CHOOSER);
        javaHomeBrowse.setOnAction(javaHomeHandler);

    }

    @Override
    public String getName() {
        return "Main";
    }

    @Override
    public Node getIcon() {
        return FXUIUtils.getIcon("main_obj");
    }

    @Override
    public void getProperties(Properties props) {
        props.setProperty(Constants.PROP_APPLICATION_MAINCLASS, classNameField.getText());
        props.setProperty(Constants.PROP_APPLICATION_ARGUMENTS, programArgumentsField.getText().trim());
        props.setProperty(Constants.PROP_APPLICATION_VM_ARGUMENTS, vmArgumentsField.getText().trim());
        props.setProperty(Constants.PROP_APPLICATION_JAVA_HOME, javaHomeField.getText());
        props.setProperty(Constants.PROP_APPLICATION_WORKING_DIR, workingDirField.getText());
    }

    @Override
    public void setProperties(Properties props) {
        classNameField.setText(props.getProperty(Constants.PROP_APPLICATION_MAINCLASS, ""));
        programArgumentsField.setText(props.getProperty(Constants.PROP_APPLICATION_ARGUMENTS, ""));
        vmArgumentsField.setText(props.getProperty(Constants.PROP_APPLICATION_VM_ARGUMENTS, ""));
        javaHomeField.setText(props.getProperty(Constants.PROP_APPLICATION_JAVA_HOME, ""));
        workingDirField.setText(props.getProperty(Constants.PROP_APPLICATION_WORKING_DIR, ""));
    }

    @Override
    public boolean isValidInput(boolean showAlert) {
        if (classNameField.getText() == null || classNameField.getText().equals("")) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(parent.getStage(), "Main class can't be empty", "Main Class", AlertType.ERROR);
            }
            Platform.runLater(() -> classNameField.requestFocus());
            return false;
        }
        if (!ValidationUtil.isValidClassName(classNameField.getText())) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(parent.getStage(), "Invalid class name given for main class", "Main Class",
                        AlertType.ERROR);
            }
            Platform.runLater(() -> classNameField.requestFocus());
            return false;
        }
        if (classNameField.getText().indexOf('.') == -1) {
            if (showAlert) {
                Optional<ButtonType> r = FXUIUtils.showConfirmDialog(parent.getStage(),
                        "There is no package given for the main class. You need to give fully qualified class name. Do you want to continue?",
                        "Main Class", AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO);
                if (r.get() != ButtonType.YES) {
                    Platform.runLater(() -> classNameField.requestFocus());
                    return false;
                }
            }
            if (!showAlert) {
                Platform.runLater(() -> classNameField.requestFocus());
                return false;
            }
        }
        if (programArgumentsField.getText().indexOf('\n') != -1 || programArgumentsField.getText().indexOf('\r') != -1) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(parent.getStage(), "Can not have new lines in Program Arguments", "Program Arguments",
                        AlertType.ERROR);
            }
            Platform.runLater(() -> programArgumentsField.requestFocus());
            return false;
        }
        if (vmArgumentsField.getText().indexOf('\n') != -1 || vmArgumentsField.getText().indexOf('\r') != -1) {
            if (showAlert) {
                FXUIUtils.showMessageDialog(parent.getStage(), "Can not have new lines in VM Arguments", "VM Arguments",
                        AlertType.ERROR);
            }
            Platform.runLater(() -> vmArgumentsField.requestFocus());
            return false;
        }
        return true;
    }

    @Override
    public void filesSelected(List<File> selectedFiles, Object cookie) {
        if (selectedFiles != null && selectedFiles.size() != 0) {
            ((TextField) cookie).setText(selectedFiles.get(0).getAbsolutePath());
        }
    }
}
