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
package net.sourceforge.marathon.screencapture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class AnnotateScreenCapture extends ModalDialog<String> {

    private ButtonBar buttonBar = new ButtonBar();
    private Button doneButton = FXUIUtils.createButton("ok", "Done", true, "Done");
    private Button saveButton = FXUIUtils.createButton("save", "Save the Screen Capture", true, "Save");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private Button removeButton = FXUIUtils.createButton("remove", "Remove", true, "Remove");
    private boolean edit;
    private ImagePanel imagePanel;
    private boolean isSaved = false;

    public AnnotateScreenCapture(File is, boolean edit) throws FileNotFoundException, IOException {
        super("Annotate Screen Capture");
        this.edit = edit;
        imagePanel = new ImagePanel(is, edit);
        initComponents();
    }

    private void initComponents() {
        if (edit) {
            buttonBar.getButtons().addAll(saveButton, cancelButton, removeButton);
        } else {
            buttonBar.getButtons().add(doneButton);
        }

        removeButton.setOnAction(e -> onRemove());
        saveButton.setOnAction((e) -> onSave());
        doneButton.setOnAction((e) -> dispose());
        cancelButton.setOnAction((e) -> dispose());
    }

    private void onRemove() {
        imagePanel.removeAnnotation();
    }

    private void onSave() {
        isSaved = true;
        dispose();
    }

    @Override protected Parent getContentPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(imagePanel);
        borderPane.setBottom(buttonBar);
        return borderPane;

    }

    @Override protected void setDefaultButton() {
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void saveToFile(File captureFile) {
        imagePanel.save(captureFile);
    }

}
