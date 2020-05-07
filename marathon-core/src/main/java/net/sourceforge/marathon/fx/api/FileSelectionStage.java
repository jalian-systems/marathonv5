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
package net.sourceforge.marathon.fx.api;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.projectselection.ProjectLayout;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.fx.api.FileSelectionHandler;
import net.sourceforge.marathon.runtime.fx.api.IFileSelectedAction;

public class FileSelectionStage extends ModalDialog<FileSelectionInfo> implements IFileSelectedAction {

    public static final Logger LOGGER = Logger.getLogger(FileSelectionStage.class.getName());

    private TextField dirField;
    private Button okButton;
    private FileSelectionInfo fileSelectionInfo;
    private IFileSelectionInfoHandler fileSelectionInfoHandler;

    public FileSelectionStage(FileSelectionInfo fileSelectionInfo) {
        super(fileSelectionInfo.getTitle(), fileSelectionInfo.getSubTitle(), fileSelectionInfo.getIcon());
        this.fileSelectionInfo = fileSelectionInfo;
    }

    @Override
    public Parent getContentPane() {
        VBox root = new VBox();
        root.setId("FileSelectionStage");
        root.getStyleClass().add("file-selection");
        root.getChildren().addAll(createBrowserField(), createButtonBar());
        return root;
    }

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private HBox createBrowserField() {
        HBox browseFieldBox = new HBox(5);
        dirField = new TextField();
        dirField.setId("DirectoryField");
        dirField.textProperty().addListener((observable, oldValue, newValue) -> updateOKButton());
        HBox.setHgrow(dirField, Priority.ALWAYS);
        Button browseButton = FXUIUtils.createButton("browse", "Browse directory", true, "Browse");
        FileSelectionHandler browserListener;
        String fileType = fileSelectionInfo.getFileType();
        if (fileType != null) {
            browserListener = new FileSelectionHandler(this,
                    new ExtensionFilter(fileType, Arrays.asList(fileSelectionInfo.getExtensionFilters())), this, null,
                    fileSelectionInfo.getTitle());
        } else {
            browserListener = new FileSelectionHandler(this, null, this, null, fileSelectionInfo.getTitle());
            browserListener.setMode(FileSelectionHandler.DIRECTORY_CHOOSER);
        }
        browserListener.setPreviousDir(new File(System.getProperty(Constants.PROP_PROJECT_DIR, ProjectLayout.projectDir)));
        browseButton.setOnAction(browserListener);
        Label label = createLabel("Name: ");
        label.setMinWidth(Region.USE_PREF_SIZE);
        label.setId("FileSelectedLabel");
        browseFieldBox.getChildren().addAll(label, dirField, browseButton);
        VBox.setMargin(browseFieldBox, new Insets(5, 5, 5, 5));
        return browseFieldBox;
    }

    private void updateOKButton() {
        if (dirField.getText().equals("")) {
            okButton.setDisable(true);
        } else {
            okButton.setDisable(false);
        }
    }

    private ButtonBarX createButtonBar() {
        ButtonBarX buttonBar = new ButtonBarX();
        buttonBar.setId("FileSelectionButtonBar");
        okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
        okButton.setDisable(true);
        okButton.setOnAction((e) -> {
            onOK();
        });
        Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
        cancelButton.setOnAction((e) -> {
            onCancel();
        });
        buttonBar.getButtons().addAll(okButton, cancelButton);
        return buttonBar;
    }

    protected void onCancel() {
        dispose();
    }

    private void onOK() {
        fileSelectionInfo.setFileName(dirField.getText());
        if (fileSelectionInfoHandler != null) {
            fileSelectionInfoHandler.handleSelectedfiles(fileSelectionInfo);
        }
        dispose();
    }

    private Label createLabel(String labelText) {
        return new Label(labelText);
    }

    @Override
    public void filesSelected(List<File> selectedFiles, Object cookie) {
        if (selectedFiles != null && selectedFiles.size() != 0) {
            StringBuffer fileList = new StringBuffer();
            for (int i = 0; i < selectedFiles.size() - 1; i++) {
                fileList.append(selectedFiles.get(i)).append(File.pathSeparator);
            }
            fileList.append(selectedFiles.get(selectedFiles.size() - 1));
            dirField.setText(fileList.toString());
        }
    }

    public void setFileSelectionInfoHandler(IFileSelectionInfoHandler fileSelectionInfoHandler) {
        Stage stage = getStage();
        dirField.setText("");
        this.fileSelectionInfoHandler = fileSelectionInfoHandler;
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.show();
                }
            });
        } else {
            stage.show();
        }
    }

    @Override
    protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }

}
