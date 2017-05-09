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

import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;

public class ProjectInfo {
    
    public static final Logger LOGGER = Logger.getLogger(ProjectInfo.class.getName());

    private final SimpleStringProperty name;
    private final SimpleStringProperty folder;
    private SimpleStringProperty frameWork;
    private String description;

    public ProjectInfo(String name, String description, String folder, String frameWork) {
        super();
        this.name = new SimpleStringProperty(name);
        this.description = description;
        this.folder = new SimpleStringProperty(folder);
        this.frameWork = new SimpleStringProperty(frameWork);
    }

    public String getName() {
        return name.get();
    }

    public String getFolder() {
        return folder.get();
    }

    public String getFrameWork() {
        return frameWork.get();
    }

    public void setName(String projectName) {
        name.set(projectName);
    }

    public void setFolder(String projectFolder) {
        folder.set(projectFolder);
    }

    public void setFrameWork(String projectFrameWork) {
        frameWork.set(projectFrameWork);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
