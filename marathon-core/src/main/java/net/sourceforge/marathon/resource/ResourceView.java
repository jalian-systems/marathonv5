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
package net.sourceforge.marathon.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sourceforge.marathon.display.MarathonFileChooserInfo;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.resource.navigator.FileResource;
import net.sourceforge.marathon.resource.navigator.FolderResource;

public class ResourceView extends TreeView<Resource> implements IResourceChangeListener {
    private IResourceActionHandler handler;
    private Operation clipboardOperation;
    private ContextMenu contextMenu = new ContextMenu();
    private IResourceActionSource source;
    private ArrayList<TreeItem<Resource>> draggedItems;

    public enum Operation {
        COPY, CUT;

        Operation() {
        }
    };

    private final class TextFieldTreeCellImpl extends TreeCell<Resource> {

        private TextField textField;
        private Timeline dragTimeline;

        public TextFieldTreeCellImpl() {
            addEventHandler(KeyEvent.KEY_RELEASED, (event) -> {
                if (event.getCode() == KeyCode.F2) {
                    startEdit();
                }
            });
            addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
                TreeItem<Resource> treeItem = getTreeItem();
                if (e.getButton() != MouseButton.PRIMARY || e.isShiftDown() || e.isControlDown() || e.isMetaDown()
                        || e.getClickCount() != 2) {
                    return;
                }
                if (e.getClickCount() == 2 && treeItem != null && treeItem.isLeaf() && treeItem.getValue().canOpen()) {
                    e.consume();
                    if (isEditing()) {
                        cancelEdit();
                    }
                    if (e.isAltDown()) {
                        handler.openWithSystem(source, treeItem.getValue());
                    } else {
                        handler.open(source, treeItem.getValue());
                    }
                }
            });
            setOnDragDetected((e) -> {
                ObservableList<TreeItem<Resource>> selectedItems = getTreeView().getSelectionModel().getSelectedItems();
                ClipboardContent content = new ClipboardContent();
                for (TreeItem<Resource> treeItem : selectedItems) {
                    treeItem.getValue().copy(content);
                }
                TransferMode[] mode = TransferMode.COPY_OR_MOVE;
                if (org.openqa.selenium.Platform.getCurrent().is(org.openqa.selenium.Platform.MAC)) {
                    if (e.isAltDown()) {
                        mode = new TransferMode[] { TransferMode.COPY };
                    } else {
                        mode = new TransferMode[] { TransferMode.MOVE };
                    }
                }
                Dragboard db = startDragAndDrop(mode);
                db.setContent(content);
                draggedItems = new ArrayList<>(selectedItems);
            });
            setOnDragEntered((e) -> {
                setStyle("-fx-background-color:aliceblue;");
                dragTimeline = new Timeline(new KeyFrame(Duration.millis(1500), ae -> {
                    if (dragTimeline != null && getItem() != null && !getItem().isExpanded() && !getItem().isLeaf()) {
                        Timeline flasher = new Timeline(new KeyFrame(Duration.seconds(0.1), x -> {
                            setStyle("-fx-background-color:yellow;");
                        }), new KeyFrame(Duration.seconds(0.2), x -> {
                            setStyle("");
                        }));
                        flasher.setCycleCount(5);
                        flasher.play();
                        flasher.setOnFinished(x -> {
                            if (dragTimeline != null) {
                                getItem().setExpanded(true);
                            }
                            setStyle("");
                        });
                    }
                }));
                dragTimeline.play();

            });
            setOnDragExited((e) -> {
                dragTimeline.stop();
                dragTimeline = null;
                setStyle("");
            });
            setOnDragDropped((e) -> {
                Resource resource = getItem();
                Dragboard dragboard = e.getDragboard();
                Operation operation = Operation.COPY;
                if (e.getTransferMode() == TransferMode.MOVE) {
                    operation = Operation.CUT;
                }
                resource.pasteInto(dragboard, operation);
                e.setDropCompleted(true);
            });
            setOnDragOver((e) -> {
                Resource resource = getItem();
                if (resource != null && resource.droppable(e.getDragboard())) {
                    e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            });
            setOnDragDone((e) -> {
                if (e.getTransferMode() == TransferMode.MOVE) {
                    completeMove();
                }
            });
        }

        @Override public void startEdit() {
            Resource resource = getItem();
            if (resource == null || !resource.canRename()) {
                return;
            }
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
            textField.requestFocus();
        }

        @Override public void cancelEdit() {
            super.cancelEdit();
            if (getItem() != null) {
                setText(getItem().getName());
            }
            if (getTreeItem() != null) {
                setGraphic(getTreeItem().getGraphic());
            }
        }

        @Override public void updateItem(Resource item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    String d = getTreeItem().getValue().getDescription();
                    if (d != null) {
                        setTooltip(new Tooltip(d));
                    }
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

                @Override public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        Resource value = getTreeItem().getValue();
                        Resource renamed = value.rename(textField.getText());
                        if (renamed != null) {
                            commitEdit(renamed);
                        } else {
                            cancelEdit();
                        }
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }

    }

    public static class ResourceModificationEvent extends Event {
        private static final long serialVersionUID = -8307438128922036441L;

        public static final EventType<ResourceModificationEvent> ANY = new EventType<>(Event.ANY, "ResourceModificationEvent");
        public static final EventType<ResourceModificationEvent> DELETE = new EventType<>(ANY, "ResourceDeleted");
        public static final EventType<ResourceModificationEvent> UPDATE = new EventType<>(ANY, "ResourceUpdated");
        public static final EventType<ResourceModificationEvent> MOVED = new EventType<>(ANY, "ResourceMoved");
        public static final EventType<ResourceModificationEvent> COPIED = new EventType<>(ANY, "ResourceCopied");

        private Resource to;
        private Resource from;

        private Resource resource;

        public ResourceModificationEvent(EventType<ResourceModificationEvent> eventType, Resource resource) {
            super(resource, resource, eventType);
            this.resource = resource;
        }

        public ResourceModificationEvent(EventType<ResourceModificationEvent> eventType, Resource from, Resource to) {
            super(from, to, eventType);
            this.from = from;
            this.to = to;
        }

        @Override public String toString() {
            return "ResourceModificationEvent [getTarget()=" + getTarget() + ", getEventType()=" + getEventType() + ", getSource()="
                    + getSource() + "]";
        }

        public Resource getResource() {
            return resource;
        }

        public Resource getFrom() {
            return from;
        }

        public Resource getTo() {
            return to;
        }
    }

    public ResourceView(IResourceActionSource source, Resource root, IResourceActionHandler handler,
            IResourceChangeListener listener) {
        this.source = source;
        this.handler = handler;
        setEditable(true);
        setRoot(root);
        getRoot().addEventHandler(ResourceModificationEvent.ANY, (event) -> {
            if (event.getEventType() == ResourceModificationEvent.DELETE) {
                listener.deleted(source, event.getResource());
            }
            if (event.getEventType() == ResourceModificationEvent.UPDATE) {
                listener.updated(source, event.getResource());
            }
            if (event.getEventType() == ResourceModificationEvent.MOVED) {
                listener.moved(source, event.getFrom(), event.getTo());
            }
            if (event.getEventType() == ResourceModificationEvent.COPIED) {
                listener.copied(source, event.getFrom(), event.getTo());
            }
        });
        setContextMenu(contextMenu);
        setContextMenu((Resource) null);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().selectedItemProperty().addListener((event, o, n) -> {
            if (n != null && n.getValue() != null) {
                setContextMenu(n != null ? n.getValue() : null);
            }
        });
        Callback<TreeView<Resource>, TreeCell<Resource>> value = new Callback<TreeView<Resource>, TreeCell<Resource>>() {
            @Override public TreeCell<Resource> call(TreeView<Resource> param) {
                return new TextFieldTreeCellImpl();
            }
        };
        setCellFactory(value);
    }

    private void expandTreeView(TreeItem<Resource> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<Resource> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    private void collapseTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(false);
            for (TreeItem<?> child : item.getChildren()) {
                collapseTreeView(child);
            }
        }
    }

    public void setContextMenu(Resource item) {
        contextMenu.getItems().clear();
        MenuItem m;
        ObservableList<TreeItem<Resource>> selectedItems = getSelectionModel().getSelectedItems();
        if (item != null && selectedItems.size() == 1 && (item instanceof FolderResource || item instanceof FileResource)) {
            m = new Menu("New");
            m.setDisable(item == null || selectedItems.size() != 1);
            contextMenu.getItems().add(m);
            ObservableList<MenuItem> items = ((Menu) m).getItems();
            m = FXUIUtils.createMenuItem("file", "New File", "");
            m.setOnAction((event) -> newFile(item));
            items.add(m);
            m = FXUIUtils.createMenuItem("fldr_closed", "New Folder", "");
            m.setOnAction((event) -> newFolder(item));
            items.add(m);
        }
        m = FXUIUtils.createMenuItem("open", "Open", "");
        m.setDisable(item == null || !item.canOpen() || selectedItems.size() != 1);
        m.setOnAction((event) -> handler.open(source, item));
        contextMenu.getItems().add(m);
        Menu mm = new Menu("Open With");
        m = FXUIUtils.createMenuItem("defaultEditor", "Default Editor", "");
        m.setDisable(item == null || !item.canOpen() || selectedItems.size() != 1);
        m.setOnAction((event) -> handler.open(source, item));
        mm.getItems().add(m);
        m = FXUIUtils.createMenuItem("textEditor", "Text Editor", "");
        m.setDisable(item == null || !item.canOpen() || selectedItems.size() != 1);
        m.setOnAction((event) -> handler.openAsText(source, item));
        mm.getItems().add(m);
        m = FXUIUtils.createMenuItem("systemEditor", "System Editor", "");
        m.setDisable(item == null || !item.canOpen() || selectedItems.size() != 1);
        m.setOnAction((event) -> handler.openWithSystem(source, item));
        mm.getItems().add(m);
        contextMenu.getItems().add(mm);
        contextMenu.getItems().add(new SeparatorMenuItem());
        m = FXUIUtils.createMenuItem("play", "Play", "");
        m.setDisable(item == null || !canRun(selectedItems));
        m.setOnAction((event) -> handler.play(source, selectedItems.stream().map(TreeItem::getValue).collect(Collectors.toList())));
        contextMenu.getItems().add(m);
        m = FXUIUtils.createMenuItem("slowPlay", "Slow Play", "");
        m.setDisable(item == null || !item.canPlaySingle() || selectedItems.size() != 1);
        m.setOnAction((event) -> handler.slowPlay(source, item));
        contextMenu.getItems().add(m);
        m = FXUIUtils.createMenuItem("debug", "Debug", "");
        m.setDisable(item == null || !item.canPlaySingle() || selectedItems.size() != 1);
        m.setOnAction((event) -> handler.debug(source, item));
        contextMenu.getItems().add(m);
        contextMenu.getItems().add(new SeparatorMenuItem());
        m = FXUIUtils.createMenuItem("cut", "Cut", "Shortcut+X");
        m.setDisable(item == null || selectedItems.size() < 1);
        m.setOnAction((event) -> cut(selectedItems));
        contextMenu.getItems().add(m);
        m = FXUIUtils.createMenuItem("copy", "Copy", "Shortcut+C");
        m.setDisable(item == null || selectedItems.size() < 1);
        m.setOnAction((event) -> copy(selectedItems));
        contextMenu.getItems().add(m);
        m = FXUIUtils.createMenuItem("paste", "Paste", "Shortcut+V");
        m.setDisable(item == null || selectedItems.size() != 1);
        m.setOnAction((event) -> {
            paste(item);
        });
        contextMenu.getItems().add(m);
        contextMenu.getItems().add(new SeparatorMenuItem());
        m = FXUIUtils.createMenuItem("delete", "Delete", "Delete");
        m.setDisable(item == null || selectedItems.size() < 1 || !canDelete(selectedItems));
        m.setOnAction((event) -> delete(selectedItems));
        contextMenu.getItems().add(m);
        m = FXUIUtils.createMenuItem("rename", "Rename", "");
        m.setDisable(item == null || !item.canRename() || selectedItems.size() != 1);
        m.setOnAction((event) -> edit(item));
        contextMenu.getItems().add(m);
        contextMenu.getItems().add(new SeparatorMenuItem());
        m = FXUIUtils.createMenuItem("expandAll", "Expand All", "");
        m.setOnAction((event) -> expandAll());
        contextMenu.getItems().add(m);
        m = FXUIUtils.createMenuItem("collapseAll", "Collapse All", "");
        m.setOnAction((event) -> collapseAll());
        contextMenu.getItems().add(m);
        contextMenu.getItems().add(new SeparatorMenuItem());
        m = FXUIUtils.createMenuItem("refresh", "Refresh", "F5");
        m.setOnAction((x) -> refreshView());
        contextMenu.getItems().add(m);
        if (item != null && item.canHide()) {
            m = FXUIUtils.createMenuItem("hide", "Hide", "");
            m.setDisable(!canHide(selectedItems));
            m.setOnAction((x) -> hide(selectedItems));
            contextMenu.getItems().add(m);
        }
        if (item != null) {
            MenuItem[] unhideMenuItems = item.getUnhideMenuItem();
            if (unhideMenuItems != null && unhideMenuItems.length > 0) {
                for (MenuItem menuItem : unhideMenuItems) {
                    EventHandler<ActionEvent> onAction = menuItem.getOnAction();
                    menuItem.setOnAction((event) -> {
                        onAction.handle(event);
                        Platform.runLater(() -> refreshView());
                    });
                }
                m = new Menu("Unhide", null, unhideMenuItems);
                contextMenu.getItems().add(m);
            }
        }
        if (item != null && item.hasProperties() && selectedItems.size() == 1) {
            m = FXUIUtils.createMenuItem("properties", "Properties...", "");
            m.setOnAction((event) -> handler.addProperties(source, item));
            contextMenu.getItems().add(m);
        }
    }

    private void newFolder(Resource resource) {
        Path filePath = resource.getFilePath();
        if (filePath == null) {
            return;
        }
        File file;
        if (resource instanceof FolderResource) {
            file = resource.getFilePath().toFile();
        } else {
            file = resource.getFilePath().getParent().toFile();
        }
        File newFile = FXUIUtils.showMarathonSaveFileChooser(new MarathonFileChooserInfo("Create new folder", file, true),
                "Create a folder with the given name", FXUIUtils.getIcon("fldr_obj"));
        if (newFile == null) {
            return;
        }
        if (newFile.exists()) {
            FXUIUtils.showMessageDialog(this.getScene().getWindow(), "Folder with name '" + newFile.getName() + "' already exists.",
                    "Folder exists", AlertType.INFORMATION);
        } else {
            newFile.mkdir();
        }
    }

    private void newFile(Resource resource) {
        Path filePath = resource.getFilePath();
        if (filePath == null) {
            return;
        }
        File file;
        if (resource instanceof FolderResource) {
            file = resource.getFilePath().toFile();
        } else {
            file = resource.getFilePath().getParent().toFile();
        }
        File newFile = FXUIUtils.showMarathonSaveFileChooser(new MarathonFileChooserInfo("Create new file", file, true),
                "Create a new file with the given name", FXUIUtils.getIcon("file_obj"));
        if (newFile == null) {
            return;
        }
        if (newFile.exists()) {
            FXUIUtils.showMessageDialog(this.getScene().getWindow(), "File with name '" + newFile.getName() + "' already exists.",
                    "File exists", AlertType.INFORMATION);
        } else {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                FXUIUtils.showExceptionMessage("Couldn't create file.", e);
            }
        }
    }

    private void hide(ObservableList<TreeItem<Resource>> selectedItems) {
        List<Resource> resources = selectedItems.stream().map((item) -> item.getValue()).collect(Collectors.toList());
        for (Resource r : resources) {
            r.hide();
        }
        refreshView();
    }

    private boolean canHide(ObservableList<TreeItem<Resource>> selectedItems) {
        return selectedItems.stream().map(TreeItem::getValue).filter(((Predicate<? super Resource>) Resource::canHide).negate())
                .count() <= 0;
    }

    private boolean canRun(ObservableList<TreeItem<Resource>> selectedItems) {
        return selectedItems.stream().map(TreeItem::getValue).filter(((Predicate<? super Resource>) Resource::canRun).negate())
                .count() <= 0;
    }

    private void refreshView() {
        ArrayList<Integer> selection = new ArrayList<>(getSelectionModel().getSelectedIndices());
        getSelectionModel().clearSelection();
        ((Resource) getRoot()).refresh();
        int[] selectedIndices = new int[selection.size() - 1];
        if (selection.size() > 1) {
            for (int i = 1; i < selection.size(); i++) {
                selectedIndices[i - 1] = selection.get(i);
            }
        }
        int selectedIndex = selection.size() > 0 ? selection.get(0) : -1;
        if (selectedIndex != -1) {
            getSelectionModel().selectIndices(selectedIndex, selectedIndices);
        }
    }

    private boolean canDelete(ObservableList<TreeItem<Resource>> selectedItems) {
        for (TreeItem<Resource> treeItem : selectedItems) {
            if (treeItem == null) {
                continue;
            }
            if (!treeItem.getValue().canDelete()) {
                return false;
            }
        }
        return true;
    }

    private void paste(Resource item) {
        item.paste(Clipboard.getSystemClipboard(), clipboardOperation);
        if (clipboardOperation == Operation.CUT) {
            completeMove();
        }
    }

    private void delete(ObservableList<TreeItem<Resource>> selectedItems) {
        Optional<ButtonType> option = Optional.empty();
        ArrayList<TreeItem<Resource>> items = new ArrayList<>(selectedItems);
        for (TreeItem<Resource> treeItem : items) {
            option = treeItem.getValue().delete(option);
            if (option.isPresent() && option.get() == ButtonType.CANCEL) {
                break;
            }
        }
    }

    private void cut(ObservableList<TreeItem<Resource>> selectedItems) {
        copy(selectedItems);
        clipboardOperation = Operation.CUT;
        draggedItems = new ArrayList<>(selectedItems);
    }

    private void copy(ObservableList<TreeItem<Resource>> selectedItems) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        Map<DataFormat, Object> content = new HashMap<>();
        for (TreeItem<Resource> treeItem : selectedItems) {
            Resource resource = treeItem.getValue();
            if (resource != null) {
                if (!resource.copy(content)) {
                    FXUIUtils.showMessageDialog(null, "Clipboard operation failed", "Unhandled resource selection",
                            AlertType.ERROR);
                }
            }
        }
        clipboard.setContent(content);
        clipboardOperation = Operation.COPY;
    }

    public void expandAll() {
        expandTreeView(getRoot());
    }

    public void collapseAll() {
        collapseTreeView(getRoot());
        getRoot().setExpanded(true);
    }

    public void cut() {
        cut(getSelectionModel().getSelectedItems());
    }

    public void copy() {
        copy(getSelectionModel().getSelectedItems());
    }

    public void paste() {
        paste(getSelectionModel().getSelectedItem().getValue());
    }

    @Override public void deleted(IResourceActionSource source, Resource resource) {
        List<Resource> found = new ArrayList<>();
        ((Resource) getRoot()).findNodes(resource, found);
        for (Resource r : found) {
            if (r != null) {
                r.deleted();
            }
        }
    }

    @Override public void updated(IResourceActionSource source, Resource resource) {
        ((RootResource) getRoot()).updated(resource);
    }

    @Override public void moved(IResourceActionSource source, Resource from, Resource to) {
        ((RootResource) getRoot()).moved(from, to);
    }

    @Override public void copied(IResourceActionSource source, Resource from, Resource to) {
        ((RootResource) getRoot()).copied(from, to);
    }

    private void completeMove() {
        if (draggedItems != null) {
            for (TreeItem<Resource> treeItem : draggedItems) {
                treeItem.getValue().moved();
            }
        }
        draggedItems = null;
    }
}
