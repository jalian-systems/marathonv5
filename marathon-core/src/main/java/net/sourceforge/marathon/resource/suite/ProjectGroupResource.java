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

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import junit.framework.Test;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.ResourceView.Operation;
import net.sourceforge.marathon.resource.RootResource;
import net.sourceforge.marathon.runtime.api.IConsole;

public class ProjectGroupResource extends Resource implements RootResource {

    private boolean loaded = false;
    private GroupType type;

    public ProjectGroupResource(GroupType type) {
        this.type = type;
        setExpanded(true);
    }

    @Override public String getName() {
        return type.dockName();
    }

    @Override public ObservableList<TreeItem<Resource>> getChildren() {
        ObservableList<TreeItem<Resource>> original = super.getChildren();
        if (loaded) {
            return original;
        }
        loadGroups();
        return original;
    }

    @Override public boolean isLeaf() {
        return false;
    }

    @Override public List<Resource> findNodes(Resource resource, List<Resource> found) {
        getChildren().forEach((c) -> c.getValue().findNodes(resource, found));
        return found;
    }

    public void loadGroups() {
        loaded = true;
        ObservableList<Resource> children = FXCollections.observableArrayList();
        List<Group> groups = Group.getGroups(type);
        for (Group group : groups) {
            children.add(new GroupResource(type, group));
        }
        super.getChildren().setAll(children);
    }

    @Override public void refresh() {
        List<TreeItem<Resource>> expanded = super.getChildren().stream().filter((r) -> r.isExpanded()).collect(Collectors.toList());
        loadGroups();
        for (TreeItem<Resource> treeItem : expanded) {
            ArrayList<Resource> found = new ArrayList<>();
            findNodes(treeItem.getValue(), found);
            for (Resource resource : found) {
                if (!resource.isLeaf()) {
                    resource.setExpanded(true);
                }
            }
        }
    }

    @Override public void updated(Resource resource) {
        refresh();
    }

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        TestCreator tc = new TestCreator(acceptChecklist, console);
        return tc.getAllTestsForGroups(type);
    }

    @Override public boolean canRename() {
        return false;
    }

    @Override public boolean canDelete() {
        return false;
    }

    @Override public boolean canHide() {
        return false;
    }

    @Override public boolean canPlaySingle() {
        return false;
    }

    @Override public boolean canOpen() {
        return false;
    }

    @Override public boolean canRun() {
        return true;
    }

    @Override public Resource rename(String text) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public boolean copy(Map<DataFormat, Object> content) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public void paste(Clipboard clipboard, Operation clipboardOperation) {
    }

    @Override public void pasteInto(Clipboard clipboard, Operation operation) {
    }

    @Override public boolean droppable(Dragboard dragboard) {
        return false;
    }

    @Override public Optional<ButtonType> delete(Optional<ButtonType> option) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public Path getFilePath() {
        // TODO Auto-generated method stub
        return null;
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

    @Override public void moved(Resource from, Resource to) {
    }

    @Override public void copied(Resource from, Resource to) {
    }
}
