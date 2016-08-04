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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.event.Event;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import junit.framework.Test;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.ResourceView.Operation;
import net.sourceforge.marathon.resource.ResourceView.ResourceModificationEvent;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;

public class FileResource extends Resource {

    Path path;

    public FileResource(File file) {
        this.path = file.toPath().toAbsolutePath();
        String graphic = "file";
        if (isTestFile()) {
            graphic = "test";
        } else if (isSuiteFile()) {
            graphic = "tsuite";
        } else if (isFeatureFile()) {
            graphic = "tfeature";
        } else if (isStoryFile()) {
            graphic = "tstory";
        } else if (isIssueFile()) {
            graphic = "tissue";
        }
        setGraphic(FXUIUtils.getIcon(graphic));
    }

    @Override public boolean isLeaf() {
        return true;
    }

    @Override public String getName() {
        return path.toFile().getName();
    }

    @Override public Resource rename(String text) {
        try {
            Path moved = Files.move(path, path.resolveSibling(text));
            FileResource to = new FileResource(moved.toFile());
            Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.MOVED, this, to));
            return to;
        } catch (IOException e) {
            FXUIUtils.showMessageDialog(null, "Unable to rename file: " + e.getMessage(), e.getClass().getName(), AlertType.ERROR);
            return null;
        }
    }

    @Override public boolean isTestFile() {
        return Constants.isTestFile(path.toFile());
    }

    @Override public boolean hasProperties() {
        return isTestFile();
    }

    @Override public boolean isSuiteFile() {
        return Constants.isSuiteFile(path.toFile());
    }

    @Override public boolean isFeatureFile() {
        return Constants.isFeatureFile(path.toFile());
    }

    @Override public boolean isStoryFile() {
        return Constants.isStoryFile(path.toFile());
    }

    @Override public boolean isIssueFile() {
        return Constants.isIssueFile(path.toFile());
    }

    @Override public boolean copy(Map<DataFormat, Object> content) {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) content.get(DataFormat.FILES);
        if (files == null) {
            files = new ArrayList<>();
            content.put(DataFormat.FILES, files);
        }
        files.add(path.toFile());
        return true;
    }

    @Override public void paste(Clipboard clipboard, Operation operation) {
        getParent().getValue().pasteInto(clipboard, operation);
    }

    @Override public void pasteInto(Clipboard clipboard, Operation operation) {
        getParent().getValue().pasteInto(clipboard, operation);
    }

    @Override public Optional<ButtonType> delete(Optional<ButtonType> option) {
        if (!option.isPresent() || option.get() != FXUIUtils.YES_ALL) {
            option = FXUIUtils.showConfirmDialog(null, "Do you want to delete `" + path + "`?", "Confirm", AlertType.CONFIRMATION,
                    ButtonType.YES, ButtonType.NO, FXUIUtils.YES_ALL, ButtonType.CANCEL);
        }
        if (option.isPresent() && (option.get() == ButtonType.YES || option.get() == FXUIUtils.YES_ALL)) {
            if (Files.exists(path)) {
                try {
                    Files.delete(path);
                    Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.DELETE, this));
                    getParent().getChildren().remove(this);
                } catch (IOException e) {
                    String message = String.format("Unable to delete: %s: %s%n", path, e);
                    FXUIUtils.showMessageDialog(null, message, "Unable to delete", AlertType.ERROR);
                }
            }
        }
        return option;
    }

    @Override public Path getFilePath() {
        return path;
    }

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        TestCreator testCreator = new TestCreator(acceptChecklist, console);
        if (isSuiteFile()) {
            return testCreator.getTest(Group.findByFile(GroupType.SUITE, path));
        }
        if (isFeatureFile()) {
            return testCreator.getTest(Group.findByFile(GroupType.FEATURE, path));
        }
        if (isStoryFile()) {
            return testCreator.getTest(Group.findByFile(GroupType.STORY, path));
        }
        if (isIssueFile()) {
            return testCreator.getTest(Group.findByFile(GroupType.ISSUE, path));
        }
        return testCreator.getTest(path.toFile(), null);
    }

    @Override public void hide() {
        FolderResource folderResource = (FolderResource) getParent();
        if (folderResource != null) {
            folderResource.hideChild(this);
        }
    }

    @Override public MenuItem[] getUnhideMenuItem() {
        return ((FolderResource) getParent()).getUnhideMenuItem();
    }

    @Override public void deleted() {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
        }
    }

    @Override public List<Resource> findNodes(Resource resource, List<Resource> found) {
        if (path.equals(resource.getFilePath())) {
            found.add(this);
        }
        return found;
    }

    @Override public void refresh() {
        // Nothing to do
    }

    @Override public boolean canRename() {
        return true;
    }

    @Override public boolean canRun() {
        return isTestFile() || isSuiteFile() || isFeatureFile() || isStoryFile() || isIssueFile();
    }

    @Override public boolean canOpen() {
        return true;
    }

    @Override public boolean canDelete() {
        return true;
    }

    @Override public boolean canHide() {
        return true;
    }

    @Override public boolean canPlaySingle() {
        return isTestFile();
    }

    @Override public void moved() {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
        }
    }
}
