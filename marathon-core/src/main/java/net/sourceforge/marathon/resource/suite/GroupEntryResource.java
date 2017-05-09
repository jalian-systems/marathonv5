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
package net.sourceforge.marathon.resource.suite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.event.Event;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import junit.framework.Test;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.GroupEntry;
import net.sourceforge.marathon.model.GroupEntry.GroupEntryType;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.ResourceView.Operation;
import net.sourceforge.marathon.resource.ResourceView.ResourceModificationEvent;
import net.sourceforge.marathon.runtime.api.IConsole;

public class GroupEntryResource extends Resource {

    public static final Logger LOGGER = Logger.getLogger(GroupEntryResource.class.getName());

    private GroupEntry entry;

    public GroupEntryResource(GroupEntry entry) {
        this.entry = entry;
        setGraphic(entry.getIcon());
    }

    @Override public String getName() {
        return entry.getName();
    }

    @Override public boolean canRun() {
        return true;
    }

    @Override public boolean canPlaySingle() {
        return entry.canPlaySingle();
    }

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        return entry.getTest(acceptChecklist, console);
    }

    @Override public Path getFilePath() {
        return entry.getFilePath();
    }

    public GroupEntry getEntry() {
        return entry;
    }

    @Override public List<Resource> findNodes(Resource resource, List<Resource> found) {
        if (getFilePath().equals(resource.getFilePath())) {
            found.add(this);
        }
        return found;
    }

    @Override public void updated(Resource resource) {
        Event.fireEvent(this, new TreeModificationEvent<Resource>(valueChangedEvent(), this, this));
    }

    @Override public Resource rename(String text) {
        entry.rename(text);
        Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.UPDATE, this));
        return this;
    }

    @Override public boolean canRename() {
        return entry.getType() != GroupEntryType.FOLDER;
    }

    @Override public boolean canOpen() {
        return entry.getType() != GroupEntryType.FOLDER;
    }

    @Override public boolean copy(Map<DataFormat, Object> content) {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) content.get(DataFormat.FILES);
        if (files == null) {
            files = new ArrayList<>();
            content.put(DataFormat.FILES, files);
        }
        files.add(entry.getFilePath().toFile());

        return true;
    }

    @Override public void paste(Clipboard clipboard, Operation operation) {
        int index = getParent().getChildren().indexOf(this);
        ((GroupResource) getParent()).paste(index + 1, clipboard, operation);
    }

    @Override public boolean droppable(Dragboard dragboard) {
        return ((GroupResource) getParent()).droppable(dragboard);
    }

    @Override public void pasteInto(Clipboard clipboard, Operation operation) {
        int index = getParent().getChildren().indexOf(this);
        ((GroupResource) getParent()).paste(index + 1, clipboard, operation);
    }

    @Override public Optional<ButtonType> delete(Optional<ButtonType> option) {
        if (!option.isPresent() || option.get() != FXUIUtils.YES_ALL) {
            option = FXUIUtils.showConfirmDialog(null, "Do you want to delete the entry `" + entry.getName() + "`?", "Confirm",
                    AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO, FXUIUtils.YES_ALL, ButtonType.CANCEL);
        }
        if (option.isPresent() && (option.get() == ButtonType.YES || option.get() == FXUIUtils.YES_ALL)) {
            GroupResource parent = (GroupResource) getParent();
            parent.deleteEntry(this);
            try {
                Group.updateFile(parent.getSuite());
                Event.fireEvent(parent, new ResourceModificationEvent(ResourceModificationEvent.UPDATE, parent));
            } catch (IOException e) {
                e.printStackTrace();
                return option;
            }
        }
        return option;
    }

    @Override public boolean canDelete() {
        return true;
    }

    @Override public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override public boolean canHide() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public void hide() {
        // TODO Auto-generated method stub

    }

    @Override public MenuItem[] getUnhideMenuItem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public void deleted() {
        // TODO Auto-generated method stub

    }

    @Override public void moved() {
        GroupResource suiteResource = (GroupResource) getParent();
        if (suiteResource != null) {
            suiteResource.getSuite().getEntries().remove(suiteResource.getChildren().indexOf(this));
            suiteResource.getChildren().remove(this);
            try {
                Group.updateFile(suiteResource.getSuite());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Event.fireEvent(suiteResource, new ResourceModificationEvent(ResourceModificationEvent.UPDATE, suiteResource));
        }
    }
}
