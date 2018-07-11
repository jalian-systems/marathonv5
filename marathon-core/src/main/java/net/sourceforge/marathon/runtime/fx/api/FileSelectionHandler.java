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
package net.sourceforge.marathon.runtime.fx.api;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class FileSelectionHandler implements EventHandler<ActionEvent> {

    public static final Logger LOGGER = Logger.getLogger(FileSelectionHandler.class.getName());

    public static final int FILE_CHOOSER = 0;
    public static final int DIRECTORY_CHOOSER = 1;
    public static final int FILE_SAVE_CHOOSER = 2;
    private int mode = FILE_CHOOSER;
    private ModalDialog<?> parent;
    private IFileSelectedAction fsl;
    private ExtensionFilter filter;
    private Object cookie;
    private String title;
    private File previousDir;

    public FileSelectionHandler(IFileSelectedAction fsl, FileChooser.ExtensionFilter filter, ModalDialog<?> parent, Object cookie,
            String title) {
        this.fsl = fsl;
        this.filter = filter;
        this.parent = parent;
        this.cookie = cookie;
        this.previousDir = new File(System.getProperty("user.home"));
        this.title = title;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setPreviousDir(File previousDir) {
        if (previousDir.exists())
            this.previousDir = previousDir;
    }

    @Override
    public void handle(ActionEvent event) {
        if (mode == FILE_CHOOSER) {
            List<File> selectedFiles = FXUIUtils.showOpenMultipleFileChooser(title, previousDir,
                    parent != null ? parent.getStage() : null, filter);
            if (selectedFiles != null && selectedFiles.size() > 0) {
                this.previousDir = selectedFiles.get(0).getParentFile();
                fsl.filesSelected(selectedFiles, cookie);
            }
        } else if (mode == FILE_SAVE_CHOOSER) {
            File selectedFile = FXUIUtils.showSaveFileChooser(title, previousDir, parent != null ? parent.getStage() : null,
                    filter);
            if (selectedFile != null) {
                this.previousDir = selectedFile.getParentFile();
                fsl.filesSelected(Arrays.asList(selectedFile), cookie);
            }
        } else if (mode == DIRECTORY_CHOOSER) {
            File selectedDirectory = FXUIUtils.showDirectoryChooser(title, previousDir, parent != null ? parent.getStage() : null);
            List<File> selectedDirs = new ArrayList<>();
            if (selectedDirectory != null) {
                selectedDirs.add(selectedDirectory);
                this.previousDir = selectedDirectory.getParentFile();
            }
            fsl.filesSelected(selectedDirs, cookie);
        }
    }

    public void setFilter(ExtensionFilter filter) {
        this.filter = filter;
    }

}
