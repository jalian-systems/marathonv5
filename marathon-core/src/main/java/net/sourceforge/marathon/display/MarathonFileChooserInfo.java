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
package net.sourceforge.marathon.display;

import java.io.File;
import java.util.logging.Logger;

import net.sourceforge.marathon.fx.display.AddPropertiesView;

public class MarathonFileChooserInfo {

    public static final Logger LOGGER = Logger.getLogger(MarathonFileChooserInfo.class.getName());

    private String title;
    private boolean doesAllowChildren;
    private File root;
    private IFileChooserHandler fileChooserHandler;
    protected File savedFile;
    private File fileToSave;
    private boolean fileCreation = false;
    private AddPropertiesView propertiesView;

    public MarathonFileChooserInfo(String title, String filename, File root, boolean doesAllowChildern) {
        this.title = title;
        this.root = root;
        if (filename != null && !"".equals(filename)) {
            File f = new File(root, filename);
            if (f.exists()) {
                this.fileToSave = f;
            }
        }
        this.doesAllowChildren = doesAllowChildern;
        this.fileChooserHandler = (file) -> MarathonFileChooserInfo.this.savedFile = file;
    }

    public MarathonFileChooserInfo(String title, File file, boolean fileCreation) {
        this(title, null, file, true);
        this.fileCreation = fileCreation;
    }

    public String getTitle() {
        return title;
    }

    public boolean doesAllowChidren() {
        return doesAllowChildren;
    }

    public File getRoot() {
        return root;
    }

    public IFileChooserHandler getFileChooserHandler() {
        return fileChooserHandler;
    }

    public File getSavedFile() {
        return savedFile;
    }

    public boolean isFileCreation() {
        return fileCreation;
    }

    public void setProperties(AddPropertiesView propertiesView) {
        this.propertiesView = propertiesView;
    }

    public void fileCreated(File file) {
        if (propertiesView != null) {
            propertiesView.write(file);
        }
    }

    public File getFileToSave() {
        return fileToSave;
    }
}
