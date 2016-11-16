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

import javafx.scene.Node;

public class FileSelectionInfo {

    private String title;
    private String fileType;
    private String[] extensionFilters;
    private String selectedFileName;
    private String subTitle;
    private Node icon;

    public FileSelectionInfo(String title, String fileType, String[] extensionFilters, String subTitle, Node icon) {
        this.title = title;
        this.fileType = fileType;
        this.extensionFilters = extensionFilters;
        this.subTitle = subTitle;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public Node getIcon() {
        return icon;
    }
    
    public String getSubTitle() {
        return subTitle;
    }
    
    public String getFileType() {
        return fileType;
    }

    public String[] getExtensionFilters() {
        return extensionFilters;
    }

    public void setFileName(String selectedFileName) {
        this.selectedFileName = selectedFileName;
    }

    public String getSelectedFileName() {
        return selectedFileName;
    }
}
