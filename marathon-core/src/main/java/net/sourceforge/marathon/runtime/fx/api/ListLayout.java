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
package net.sourceforge.marathon.runtime.fx.api;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.FileSelectionInfo;
import net.sourceforge.marathon.fx.api.FileSelectionStage;
import net.sourceforge.marathon.fx.api.IFileSelectionInfoHandler;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.api.UpDownHandler;
import net.sourceforge.marathon.runtime.api.MPFUtils;

public abstract class ListLayout implements IPropertiesLayout {

    public static final Logger LOGGER = Logger.getLogger(ListLayout.class.getName());

    public class ClassPathElement {

        private File classPath;

        public ClassPathElement(File classPath) {
            this.classPath = classPath;
        }

        public File getClassPath() {
            return classPath;
        }

        @Override
        public String toString() {
            String fileName = classPath.getName();
            if (classPath.getParent() != null) {
                fileName = fileName + " - " + classPath.getParent();
            }
            return fileName;
        }
    }

    final class BrowseActionHandler implements EventHandler<ActionEvent> {

        private FileSelectionStage fileSelectionStage;

        public BrowseActionHandler(FileSelectionInfo fileSelectionInfo) {
            fileSelectionStage = new FileSelectionStage(fileSelectionInfo);
        }

        @Override
        public void handle(ActionEvent event) {
            fileSelectionStage.setFileSelectionInfoHandler(new IFileSelectionInfoHandler() {
                @Override
                public void handleSelectedfiles(FileSelectionInfo fileSelectionInfo) {
                    String fileString = fileSelectionInfo.getSelectedFileName();
                    if (fileString == null) {
                        return;
                    }
                    String[] selectedFiles = fileString.split(File.pathSeparator);
                    addToList(selectedFiles);
                }
            });
        }
    }

    private ObservableList<ClassPathElement> classPathListItems = FXCollections.observableArrayList();
    private Button upButton;
    private Button downButton;
    private Button addJarsButton;
    private Button addFoldersButton;
    private Button deleteButton;
    private ListView<ClassPathElement> classPathListView;
    protected ModalDialog<?> parent;
    private boolean replaceProjectDir;
    protected VBox listViewBox;

    public ListLayout(ModalDialog<?> parent) {
        this(parent, false);
    }

    private void addToList(String[] selectedFiles) {
        for (String selectedFile : selectedFiles) {
            classPathListItems.add(new ClassPathElement(new File(selectedFile)));
        }
    }

    public ListLayout(ModalDialog<?> parent, boolean replaceProjectDir) {
        this.parent = parent;
        this.replaceProjectDir = replaceProjectDir;
    }

    @Override
    public Node getContent() {
        HBox hBox = new HBox();
        hBox.setId("ListLayout");
        hBox.getStyleClass().add("path-list");
        hBox.getChildren().addAll(createListView(), createVerticalButtonBar());
        return hBox;
    }

    public abstract boolean isAddArchivesNeeded();

    public abstract boolean isAddFoldersNeeded();

    public abstract boolean isAddClassesNeeded();

    public abstract boolean isSingleSelection();

    protected boolean isTraversalNeeded() {
        return true;
    }

    @Override
    public void setProperties(Properties props) {
        String cp = props.getProperty(getPropertyKey(), "");
        if (cp.length() == 0) {
            return;
        }
        String[] elements = cp.split(";");
        for (String element : elements) {
            if (replaceProjectDir) {
                classPathListItems.add(new ClassPathElement(new File(MPFUtils.decodeProjectDir(element, props))));
            } else {
                classPathListItems.add(new ClassPathElement(new File(element)));
            }
        }
    }

    public abstract String getPropertyKey();

    @Override
    public void getProperties(Properties props) {
        props.setProperty(getPropertyKey(), getClassPath(props));
    }

    protected String getClassPath(Properties props) {
        StringBuffer cp = new StringBuffer("");
        int size = classPathListItems.size();
        if (size == 0) {
            return cp.toString();
        }
        for (int i = 0; i < size; i++) {
            ClassPathElement elementAt = classPathListItems.get(i);
            File classPath = elementAt.getClassPath();
            if (classPath instanceof File) {
                if (replaceProjectDir) {
                    cp.append(MPFUtils.encodeProjectDir(classPath, props));
                } else {
                    cp.append(classPath.toString());
                }
            } else {
                cp.append(elementAt);
            }
            if (i != size - 1) {
                cp.append(";");
            }
        }
        return cp.toString();
    }

    private VBox createListView() {
        listViewBox = new VBox(5);
        classPathListView = new ListView<ClassPathElement>(classPathListItems);
        classPathListView.setPrefHeight(Node.BASELINE_OFFSET_SAME_AS_HEIGHT);
        classPathListView.setId("ClassPathList");
        classPathListView.setCellFactory((e) -> {
            ClassPathCell classPathCell = new ClassPathCell();
            classPathCell.setId("ClassPathCell");
            return classPathCell;
        });

        if (!isSingleSelection()) {
            classPathListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        classPathListView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            MultipleSelectionModel<ClassPathElement> selectionModel = classPathListView.getSelectionModel();
            int itemCount = classPathListItems.size();
            int selectedIndex = selectionModel.getSelectedIndex();
            setButtonState(deleteButton, selectedIndex != -1);
            boolean enable = selectedIndex != 0 && selectedIndex != -1 && itemCount > 1;
            setButtonState(upButton, enable);
            enable = selectedIndex != itemCount - 1 && selectedIndex != -1 && itemCount > 1;
            setButtonState(downButton, enable);
        });
        listViewBox.getChildren().add(classPathListView);
        HBox.setHgrow(listViewBox, Priority.ALWAYS);
        VBox.setVgrow(classPathListView, Priority.ALWAYS);
        return listViewBox;
    }

    private void setButtonState(Button button, boolean enable) {
        if (button != null) {
            button.setDisable(!enable);
        }
    }

    private VBox createVerticalButtonBar() {
        VBox vBox = new VBox();
        vBox.setId("VerticalButtonBar");
        if (isTraversalNeeded()) {
            upButton = FXUIUtils.createButton("up", "Move selection up", true, "Up");
            setButtonState(upButton, false);
            upButton.setOnAction(new UpDownHandler(classPathListView, true));
            upButton.setMaxWidth(Double.MAX_VALUE);
            downButton = FXUIUtils.createButton("down", "Move selection down", true, "Down");
            setButtonState(downButton, false);
            downButton.setOnAction(new UpDownHandler(classPathListView, false));
            downButton.setMaxWidth(Double.MAX_VALUE);
            vBox.getChildren().addAll(upButton, downButton);
        }

        if (isAddArchivesNeeded()) {
            addJarsButton = FXUIUtils.createButton("addjar", "Add JAR/ZIP files to class path", true, "Add Archives...");
            addJarsButton.setOnAction(new BrowseActionHandler(
                    new FileSelectionInfo("Select Zip/Jar files", "Java Archives", new String[] { "*.jar", "*.zip" },
                            "Add Zip/Jar files to the application classpath", FXUIUtils.getIcon("addjar"))));
            vBox.getChildren().add(addJarsButton);
        }

        if (isAddFoldersNeeded()) {
            addFoldersButton = FXUIUtils.createButton("addfolder", "Add folders to class path", true, "Add Folders...");
            addFoldersButton.setOnAction(new BrowseActionHandler(new FileSelectionInfo("Select Folders", null, null,
                    "Add class files from folders to classpath", FXUIUtils.getIcon("addfolder"))));
            addFoldersButton.setMaxWidth(Double.MAX_VALUE);
            vBox.getChildren().add(addFoldersButton);
        }

        deleteButton = FXUIUtils.createButton("remove", "Delete selection", true, "Remove");
        setButtonState(deleteButton, false);
        deleteButton.setOnAction((e) -> {
            ObservableList<ClassPathElement> selectedItems = classPathListView.getSelectionModel().getSelectedItems();
            if (selectedItems != null) {
                classPathListItems.removeAll(selectedItems);
                boolean enable = classPathListItems.size() != 0;
                setButtonState(deleteButton, enable);
                setButtonState(upButton, enable);
                setButtonState(downButton, enable);
                classPathListView.getSelectionModel().select(-1);
            }
        });
        deleteButton.setMaxWidth(Double.MAX_VALUE);
        vBox.getChildren().addAll(deleteButton);
        return vBox;
    }

    public class ClassPathCell extends ListCell<ClassPathElement> {

        @Override
        protected void updateItem(ClassPathElement item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                File file = item.getClassPath();
                if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip")) {
                    setGraphic(FXUIUtils.getIcon("jar_obj"));
                } else {
                    setGraphic(FXUIUtils.getIcon("fldr_obj"));
                }
                String fileName = file.getName();
                if (file.getParent() != null) {
                    fileName = fileName + " - " + file.getParent();
                }
                setText(fileName);
            } else {
                setGraphic(null);
                setText(null);
            }
        }
    }
}
