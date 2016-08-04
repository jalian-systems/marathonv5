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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

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

public final class ProjectFolderResource extends FolderResource implements RootResource {
    private String name;

    public ProjectFolderResource(Watcher watcher) {
        super(Constants.getMarathonProjectDirectory(), watcher);
        setExpanded(true);
        setName();
    }

    public void setName() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(Constants.getMarathonProjectDirectory(), Constants.PROJECT_FILE)));
            name = props.getProperty(Constants.PROP_PROJECT_NAME);
        } catch (IOException e) {
            e.printStackTrace();
            name = "";
        }
    }

    @Override public String getName() {
        return name;
    }

    @Override public Resource rename(String text) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(new File(Constants.getMarathonProjectDirectory(), Constants.PROJECT_FILE)));
            props.setProperty(Constants.PROP_PROJECT_NAME, text);
            props.store(new FileOutputStream(new File(Constants.getMarathonProjectDirectory(), Constants.PROJECT_FILE)), "");
            name = text;
            Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.UPDATE, this));
        } catch (IOException e) {
        }
        return this;
    }

    @Override public void setIcon() {
        setGraphic(FXUIUtils.getIcon("prj_obj"));
    }

    @Override public void hide() {
    }

    @Override public void updated(Resource resource) {
        Path projectFilePath = super.getFilePath().resolve(Constants.PROJECT_FILE);
        if (projectFilePath.equals(resource.getFilePath())) {
            setName();
            Event.fireEvent(this, new TreeModificationEvent<Resource>(valueChangedEvent(), this, this));
        }
    }

    @Override public boolean canDelete() {
        return false;
    }

    @Override public boolean canRename() {
        return true;
    }

    @Override public boolean canRun() {
        return true;
    }

    @Override public boolean canHide() {
        return false;
    }

    @Override public void moved(Resource from, Resource to) {
    }

    @Override public void copied(Resource from, Resource to) {
    }

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        TestCreator testCreator = new TestCreator(acceptChecklist, console);
        return testCreator.getTest("AllTests");
    }
}
