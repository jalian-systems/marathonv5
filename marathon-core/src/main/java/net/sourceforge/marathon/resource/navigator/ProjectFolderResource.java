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
package net.sourceforge.marathon.resource.navigator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.event.Event;
import junit.framework.Test;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.ResourceView.ResourceModificationEvent;
import net.sourceforge.marathon.resource.RootResource;
import net.sourceforge.marathon.resource.Watcher;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.ProjectFile;

public final class ProjectFolderResource extends FolderResource implements RootResource {

    public static final Logger LOGGER = Logger.getLogger(ProjectFolderResource.class.getName());

    private String name;

    public ProjectFolderResource(Watcher watcher) {
        super(Constants.getMarathonProjectDirectory(), watcher);
        setExpanded(true);
        setName();
    }

    public void setName() {
        try {
            name = ProjectFile.getProjectProperty(Constants.PROP_PROJECT_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Resource rename(String text) {
        try {
            ProjectFile.updateProjectProperty(Constants.PROP_PROJECT_NAME, text);
            name = text;
            Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.UPDATE, this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void setIcon() {
        setGraphic(FXUIUtils.getIcon("prj_obj"));
    }

    @Override
    public void hide() {
    }

    @Override
    public void updated(Resource resource) {
        Path projectFilePath = super.getFilePath().resolve(ProjectFile.PROJECT_FILE);
        if (projectFilePath.equals(resource.getFilePath())) {
            setName();
            Event.fireEvent(this, new TreeModificationEvent<Resource>(valueChangedEvent(), this, this));
        }
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public boolean canRename() {
        return true;
    }

    @Override
    public boolean canRun() {
        return true;
    }

    @Override
    public boolean canHide() {
        return false;
    }

    @Override
    public void moved(Resource from, Resource to) {
    }

    @Override
    public void copied(Resource from, Resource to) {
    }

    @Override
    public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        TestCreator testCreator = new TestCreator(acceptChecklist, console);
        return testCreator.getTest("AllTests");
    }
}
