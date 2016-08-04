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
import java.util.ArrayList;
import java.util.List;

import javafx.stage.Stage;
import net.sourceforge.marathon.runtime.api.Constants;

public class NewProjectHandler implements INewProjectHandler {

    private Stage parent;

    public NewProjectHandler(Stage parent) {
        this.parent = parent;
    }

    @Override public ProjectInfo createNewProject() {
        List<File> projects = new ArrayList<>();
        MPFConfigurationInfo mpfConfigurationInfo = new MPFConfigurationInfo("Configure - (New Project)");
        MPFConfigurationStage mpfConfigurationStage = new MPFConfigurationStage(parent, mpfConfigurationInfo) {
            @Override public void onSave() {
                if (validInupt()) {
                    projects.add(mpfConfigurationInfo.saveProjectFile(layouts));
                    dispose();
                }
            }
        };
        mpfConfigurationStage.getStage().showAndWait();
        if (projects.size() == 0) {
            return null;
        }
        File file = projects.get(0);
        return new ProjectInfo(System.getProperty(Constants.PROP_PROJECT_NAME),
                System.getProperty(Constants.PROP_PROJECT_DESCRIPTION), file.getAbsolutePath(),
                System.getProperty(Constants.PROP_PROJECT_FRAMEWORK));
    }

}
