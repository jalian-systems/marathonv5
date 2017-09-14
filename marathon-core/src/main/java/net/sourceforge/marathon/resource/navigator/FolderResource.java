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
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import junit.framework.Test;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.junit.TestCreator;
import net.sourceforge.marathon.resource.Resource;
import net.sourceforge.marathon.resource.ResourceView;
import net.sourceforge.marathon.resource.ResourceView.Operation;
import net.sourceforge.marathon.resource.ResourceView.ResourceModificationEvent;
import net.sourceforge.marathon.resource.Watcher;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;
import net.sourceforge.marathon.runtime.api.Preferences;

public class FolderResource extends Resource {

    public static final Logger LOGGER = Logger.getLogger(FolderResource.class.getName());

    Path path;
    private boolean loaded;
    private Watcher watcher;

    private static ObservableList<String> hiddenFiles = FXCollections.observableArrayList();
    static {
        JSONArray files = Preferences.instance().getSection("navigator").optJSONArray("hidden-files");
        if (files != null) {
            for (int i = 0; i < files.length(); i++) {
                hiddenFiles.add(files.getString(i));
            }
        }
        hiddenFiles.addListener(new ListChangeListener<String>() {
            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
                JSONObject section = Preferences.instance().getSection("navigator");
                section.put("hidden-files", new JSONArray(hiddenFiles));
                Preferences.instance().save("navigator");
            }
        });
    }

    private static Predicate<File> predicate = (file) -> {
        Path filePath = file.toPath().toAbsolutePath();
        String path = Constants.getProjectPath().relativize(filePath).toString();
        return !hiddenFiles.contains(path);
    };

    private static Comparator<File> comparator = (o1, o2) -> {
        if (o1.isDirectory() && o2.isDirectory() || o1.isFile() && o2.isFile()) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
        if (o1.isDirectory()) {
            return -1;
        }
        return 1;
    };

    public FolderResource(File folder, Watcher watcher) {
        this.watcher = watcher;
        this.path = folder.toPath().toAbsolutePath();
        this.watcher.register(this, this.path);
        setIcon();
    }

    public void setIcon() {
        Node folderOpen = FXUIUtils.getIcon("fldr_obj");
        Node folderClose = FXUIUtils.getIcon("fldr_closed");
        setGraphic(folderClose);
        expandedProperty().addListener((event, o, n) -> {
            if (n) {
                setGraphic(folderOpen);
            } else {
                setGraphic(folderClose);
            }
        });
    }

    @Override public ObservableList<TreeItem<Resource>> getChildren() {
        ObservableList<TreeItem<Resource>> children = super.getChildren();
        if (loaded) {
            return children;
        }
        refresh();
        return children;
    }

    @Override public boolean isLeaf() {
        return false;
    }

    @Override public String getName() {
        return path.toFile().getName();
    }

    @Override public boolean isTestFile() {
        return Constants.isTestFile(path.toFile());
    }

    @Override public Resource rename(String text) {
        try {
            Path moved = Files.move(path, path.resolveSibling(text));
            FileResource to = new FileResource(moved.toFile());
            Event.fireEvent(this, new ResourceModificationEvent(ResourceModificationEvent.MOVED, this, to));
            return to;
        } catch (IOException e) {
            FXUIUtils.showMessageDialog(null, "Unable to rename folder: " + e.getMessage(), e.getClass().getName(),
                    AlertType.ERROR);
            return null;
        }
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
        if (getValue() != null) {
            getValue().pasteInto(clipboard, operation);
        }
    }

    @Override public void pasteInto(Clipboard clipboard, Operation operation) {
        if (clipboard.hasFiles()) {
            List<File> files = clipboard.getFiles();
            List<Path> paths = new ArrayList<>();
            for (File file : files) {
                paths.add(file.toPath().toAbsolutePath());
            }
            Collections.sort(paths);
            Path lastCopiedPath = null;
            for (Path path : paths) {
                try {
                    if (lastCopiedPath == null || !path.startsWith(lastCopiedPath)) {
                        Path newPath = Copy.copy(path, this.path, operation);
                        if (newPath == null) {
                            continue;
                        }
                        Resource to;
                        if (Files.isDirectory(newPath)) {
                            to = new FolderResource(newPath.toFile(), watcher);
                        } else {
                            to = new FileResource(newPath.toFile());
                        }
                        lastCopiedPath = path;
                        Resource from;
                        if (path.toFile().isDirectory()) {
                            from = new FolderResource(path.toFile(), watcher);
                        } else {
                            from = new FileResource(path.toFile());
                        }
                        Event.fireEvent(this, new ResourceView.ResourceModificationEvent(
                                operation == Operation.CUT ? ResourceModificationEvent.MOVED : ResourceModificationEvent.COPIED,
                                from, to));
                    }
                } catch (IOException e) {
                    FXUIUtils.showMessageDialog(null, "Error in copying files.", e.getMessage(), AlertType.ERROR);
                }
            }
        }
        Platform.runLater(() -> refresh());
    }

    @Override public Optional<ButtonType> delete(Optional<ButtonType> option) {
        if (!option.isPresent() || option.get() != FXUIUtils.YES_ALL) {
            option = FXUIUtils.showConfirmDialog(null, "Do you want to delete the folder `" + path + "` and all its children?",
                    "Confirm", AlertType.CONFIRMATION, ButtonType.YES, ButtonType.NO, FXUIUtils.YES_ALL, ButtonType.CANCEL);
        }
        if (option.isPresent() && (option.get() == ButtonType.YES || option.get() == FXUIUtils.YES_ALL)) {
            if (Files.exists(path)) {
                try {
                    File file = path.toFile();
                    File[] listFiles = file.listFiles();
                    option = Copy.delete(path, option);
                    if (listFiles.length > 0)
                        for (File f : listFiles) {
                            Event.fireEvent(this,
                                    new ResourceModificationEvent(ResourceModificationEvent.DELETE, new FileResource(f)));
                        }
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

    public void create(Path name) {
        Path newEntry = path.resolve(name);
        Resource resource;
        if (Files.isDirectory(newEntry)) {
            resource = new FolderResource(newEntry.toFile(), watcher);
        } else {
            resource = new FileResource(newEntry.toFile());
        }
        ObservableList<TreeItem<Resource>> children = getChildren();
        if (contains(children, resource) == null) {
            refresh();
        }
    }

    public void delete(Path name) {
        Path newEntry = path.resolve(name);
        Optional<TreeItem<Resource>> found = getChildren().stream().filter((r) -> newEntry.equals(r.getValue().getFilePath()))
                .findFirst();
        if (found.isPresent()) {
            getChildren().remove(found);
        }
    }

    @Override public void refresh() {
        ObservableList<TreeItem<Resource>> children = FXCollections.observableArrayList();
        File[] files = path.toFile().listFiles(new FileFilter() {
            @Override public boolean accept(File pathname) {
                return predicate.test(pathname);
            }
        });
        if (files != null) {
            Arrays.sort(files, comparator);
            for (File file : files) {
                if (file.isDirectory()) {
                    children.add(new FolderResource(file, watcher));
                } else {
                    children.add(new FileResource(file));
                }
            }
            ObservableList<TreeItem<Resource>> current = super.getChildren();
            List<Resource> expanded = current.stream().filter((ti) -> ti.isExpanded()).map((ti) -> ti.getValue())
                    .collect(Collectors.toList());
            loaded = true;
            super.getChildren().setAll(children);
            for (Resource resource : expanded) {
                TreeItem<Resource> ti = contains(current, resource);
                if (ti != null) {
                    ti.setExpanded(true);
                }
            }
            Iterator<TreeItem<Resource>> iterator = current.iterator();
            while (iterator.hasNext()) {
                iterator.next().getValue().refresh();
            }
        }
    }

    private TreeItem<Resource> contains(ObservableList<TreeItem<Resource>> l, Resource r) {
        for (TreeItem<Resource> treeItem : l) {
            Resource other = treeItem.getValue();
            if (other.getFilePath().getFileName().equals(r.getFilePath().getFileName())) {
                return treeItem;
            }
        }
        return null;
    }

    @Override public void hide() {
        FolderResource folderResource = (FolderResource) getParent();
        if (folderResource != null) {
            folderResource.hideChild(this);
        }
    }

    public void hideChild(Resource resource) {
        hiddenFiles.add(Constants.getProjectPath().relativize(resource.getFilePath()).toString());
    }

    @Override public Path getFilePath() {
        return path;
    }

    @Override public MenuItem[] getUnhideMenuItem() {
        return hiddenFiles.stream().map((file) -> {
            MenuItem menuItem = new MenuItem(file, new ImageView());
            menuItem.setOnAction((event) -> hiddenFiles.remove(file));
            return menuItem;
        }).collect(Collectors.toList()).toArray(new MenuItem[hiddenFiles.size()]);
    }

    @Override public List<Resource> findNodes(Resource resource, List<Resource> found) {
        if (path.equals(resource.getFilePath())) {
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

    @Override public Test getTest(boolean acceptChecklist, IConsole console) throws IOException {
        if (path.toString().endsWith("ExploratoryTests")) {
            return null;
        }
        TestCreator testCreator = new TestCreator(acceptChecklist, console);
        return testCreator.getTest(path.toFile(), null);
    }

    @Override public boolean canOpen() {
        return false;
    }

    @Override public boolean canPlaySingle() {
        return false;
    }

    @Override public boolean canRename() {
        return true;
    }

    @Override public boolean canRun() {
        return isTestFile();
    }

    @Override public boolean canDelete() {
        return true;
    }

    @Override public boolean canHide() {
        return true;
    }

    @Override public void moved() {
        if (getParent() != null) {
            getParent().getChildren().remove(this);
        }
    }
}
