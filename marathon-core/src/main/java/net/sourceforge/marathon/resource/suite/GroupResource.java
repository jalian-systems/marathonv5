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
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.junit.IHasFullname;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.model.GroupEntry;
import net.sourceforge.marathon.model.GroupEntry.GroupEntryType;
import net.sourceforge.marathon.model.GroupFolderEntry;
import net.sourceforge.marathon.model.GroupGroupEntry;
import net.sourceforge.marathon.model.GroupTestEntry;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.ResourceView.Operation;
import net.sourceforge.marathon.resource.ResourceView.ResourceModificationEvent;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;

public class GroupResource extends Resource {

    private Group group;
    private boolean loaded;
    private GroupType type;

    public GroupResource(GroupType type, Group group) {
        this.type = type;
        if (group == null) {
            throw new RuntimeException("Group can't be null");
        }
        this.group = group;
        setGraphic(type.dockIcon());
    }

    @Override public ObservableList<TreeItem<Resource>> getChildren() {
        ObservableList<TreeItem<Resource>> original = super.getChildren();
        if (loaded) {
            return original;
        }
        loadEntries();
        return original;
    }

    @Override public String getName() {
        if (group == null) {
            return "XXX";
        }
        return group.getName();
    }

    @Override public boolean isLeaf() {
        return false;
    }

    @Override public boolean canOpen() {
        return true;
    }

    @Override public Path getFilePath() {
        return group.getPath();
    }

    @Override public Test getTest(boolean acceptChecklist, IConsole console) {
        TestCreator tc;
        try {
            tc = new TestCreator(acceptChecklist, console);
            return tc.getTest(Group.findByFile(type, group.getPath()));
        } catch (IOException e) {
            FXUIUtils.showExceptionMessage("Failed to create test", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override public boolean canRun() {
        return true;
    }

    @Override public boolean canDelete() {
        return true;
    }

    @Override public Optional<ButtonType> delete(Optional<ButtonType> option) {
        if (!option.isPresent() || option.get() != FXUIUtils.YES_ALL) {
            option = FXUIUtils.showConfirmDialog(null, "Do you want to delete `" + group.getName() + "`?", "Confirm",
                    AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO, FXUIUtils.YES_ALL, ButtonType.CANCEL);
        }
        if (option.isPresent() && (option.get() == ButtonType.YES || option.get() == FXUIUtils.YES_ALL)) {
            try {
                Group.delete(type, group);
                Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.DELETE, this));
                getParent().getChildren().remove(this);
            } catch (Exception e) {
                e.printStackTrace();
                String message = String.format("Unable to delete: %s: %s%n", group.getName(), e);
                FXUIUtils.showMessageDialog(null, message, "Unable to delete", AlertType.ERROR);
            }
        }
        return option;
    }

    @Override public List<Resource> findNodes(Resource resource, List<Resource> found) {
        if (getFilePath().equals(resource.getFilePath())) {
            found.add(this);
        } else {
            getChildren().forEach((c) -> c.getValue().findNodes(resource, found));
        }
        return found;
    }

    @Override public void deleted() {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
        }
    }

    @Override public Resource rename(String text) {
        group.setName(text);
        try {
            Group.updateFile(group);
            Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.UPDATE, this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override public void updated(Resource resource) {
        try {
            group = Group.findByFile(type, group.getPath());
            loadEntries();
            Event.fireEvent(this, new TreeModificationEvent<Resource>(valueChangedEvent(), this, this));
        } catch (IOException e) {
            throw new RuntimeException("Could not read " + type.fileType() + " resource", e);
        }
    }

    public void loadEntries() {
        loaded = true;
        List<TreeItem<Resource>> entries = group.getEntries().stream().map((e) -> (TreeItem<Resource>) new GroupEntryResource(e))
                .collect(Collectors.toList());
        super.getChildren().setAll(entries);
    }

    @Override public void refresh() {
    }

    @Override public boolean canRename() {
        return true;
    }

    @Override public boolean copy(Map<DataFormat, Object> content) {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) content.get(DataFormat.FILES);
        if (files == null) {
            files = new ArrayList<>();
            content.put(DataFormat.FILES, files);
        }
        files.add(group.getPath().toFile());
        return true;
    }

    @Override public void paste(Clipboard clipboard, Operation operation) {
        paste(0, clipboard, operation);
    }

    @Override public boolean droppable(Dragboard dragboard) {
        if (dragboard.hasFiles()) {
            return type.droppable(dragboard.getFiles(), getFilePath());
        }
        return false;
    }

    public void paste(int index, Clipboard clipboard, Operation operation) {
        if (!clipboard.hasFiles()) {
            return;
        }
        List<File> files = clipboard.getFiles();
        ObservableList<TreeItem<Resource>> cs = getChildren();
        for (File file : files) {
            GroupEntry ge = null;
            try {
                if (Constants.isSuiteFile(file)) {
                    ge = new GroupGroupEntry(GroupEntryType.SUITE, file.toPath().toString());
                } else if (Constants.isFeatureFile(file)) {
                    ge = new GroupGroupEntry(GroupEntryType.FEATURE, file.toPath().toString());
                } else if (Constants.isStoryFile(file)) {
                    ge = new GroupGroupEntry(GroupEntryType.STORY, file.toPath().toString());
                } else if (Constants.isIssueFile(file)) {
                    ge = new GroupGroupEntry(GroupEntryType.ISSUE, file.toPath().toString());
                } else if (Constants.isTestFile(file)) {
                    ge = getTestEntry(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            cs.add(index, new GroupEntryResource(ge));
            group.getEntries().add(index, ge);
            index++;
        }
        try {
            Group.updateFile(group);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.UPDATE, this));
        return;
    }

    private GroupEntry getTestEntry(File file) throws IOException {
        TestCreator tc = new TestCreator(false, null);
        Test test = tc.getTest(file, null);
        String name = null;
        if (test instanceof IHasFullname) {
            name = ((IHasFullname) test).getFullName();
        } else if (test instanceof TestSuite) {
            name = ((TestSuite) test).getName();
        } else if (test instanceof TestCase) {
            name = ((TestCase) test).getName();
        }
        if (name.endsWith("AllTests")) {
            return new GroupFolderEntry(name);
        } else {
            return new GroupTestEntry(name);
        }
    }

    @Override public void pasteInto(Clipboard clipboard, Operation operation) {
        paste(0, clipboard, operation);
    }

    @Override public boolean canPlaySingle() {
        // TODO Auto-generated method stub
        return false;
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

    public void deleteEntry(GroupEntryResource entryResource) {
        group.getEntries().remove(getChildren().indexOf(entryResource));
        getChildren().remove(entryResource);
    }

    public Group getSuite() {
        return group;
    }

    @Override public String getDescription() {
        return group.getProperty("description", null);
    }
}
