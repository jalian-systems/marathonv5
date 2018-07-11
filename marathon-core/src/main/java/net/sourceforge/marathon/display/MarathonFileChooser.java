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
package net.sourceforge.marathon.display;

import java.io.File;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.display.AddPropertiesView;
import net.sourceforge.marathon.fx.display.IInputHanler;
import net.sourceforge.marathon.fx.display.MarathonInputStage;
import net.sourceforge.marathon.fx.display.TestPropertiesInfo;
import net.sourceforge.marathon.fx.projectselection.FormPane;

public class MarathonFileChooser extends ModalDialog<MarathonFileChooserInfo> {

    public static final Logger LOGGER = Logger.getLogger(MarathonFileChooser.class.getName());

    private ToolBar toolBar = new ToolBar();
    private Button newFolderButton = FXUIUtils.createButton("addfolder", "Create new empty folder", false);
    private VBox folderView = new VBox();
    private TreeView<File> parentTreeView = new TreeView<>();
    private ListView<File> childrenListView = new ListView<>();
    private SplitPane splitPane = new SplitPane();
    private VBox centerPane = new VBox();
    private AddPropertiesView propertiesView;
    private TextField fileNameBox = new TextField();
    private ButtonBarX buttonBar = new ButtonBarX();
    private Button saveButton = FXUIUtils.createButton("save", "Save", false, "Save");
    private Button createButton = FXUIUtils.createButton("create", "Create File", false, "Create");
    private Button cancelButton = FXUIUtils.createButton("cancel", "cancel", true, "Cancel");

    private MarathonFileChooserInfo fileChooserInfo;
    private IFileChooserHandler fileChooserHandler;
    private boolean doesAllowChildren;

    public MarathonFileChooser(MarathonFileChooserInfo fileChooserInfo, String subTitle, Node icon) {
        super(fileChooserInfo.getTitle(), subTitle, icon);
        this.fileChooserInfo = fileChooserInfo;
        this.fileChooserHandler = fileChooserInfo.getFileChooserHandler();
        this.doesAllowChildren = fileChooserInfo.doesAllowChidren();
        initComponents();
    }

    private void initComponents() {
        initListView();

        if (doesAllowChildren) {
            newFolderButton.setOnAction((e) -> onNewFolder());
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            toolBar.getItems().addAll(region, newFolderButton);
            initTreeView();
            folderView.getChildren().addAll(toolBar, parentTreeView);
            splitPane.setDividerPositions(0.4);
            splitPane.getItems().addAll(folderView, childrenListView);
        }

        cancelButton.setOnAction((e) -> onCancel());
        if (fileChooserInfo.isFileCreation()) {
            createButton.setOnAction((e) -> onSave());
            buttonBar.getButtons().addAll(createButton, cancelButton);
        } else {
            saveButton.setOnAction((e) -> onSave());
            buttonBar.getButtons().addAll(saveButton, cancelButton);
        }
        fileNameBox.textProperty().addListener((observable, oldvalue, newvalue) -> {
            String text = fileNameBox.getText();
            saveButton.setDisable(text == null || "".equals(text));
            createButton.setDisable(text == null || "".equals(text));
        });
        FormPane form = new FormPane("marathon-module-form", 2);
        form.addFormField("File Name: ", fileNameBox);
        Separator separator = new Separator();
        HBox.setHgrow(separator, Priority.ALWAYS);
        centerPane.getChildren().addAll(separator, form);
    }

    @Override
    protected Parent getContentPane() {
        BorderPane root = new BorderPane();

        root.getStyleClass().add("MarathonFileChooser");
        root.setId("marathon-file-chooser");
        if (doesAllowChildren) {
            if (!fileChooserInfo.isFileCreation()) {
                propertiesView = new AddPropertiesView(new TestPropertiesInfo(fileChooserInfo.getFileToSave()));
                TitledPane titledPane = new TitledPane("Properties", propertiesView);
                centerPane.getChildren().addAll(splitPane, titledPane);
                root.setPrefWidth(540);
                root.setPrefHeight(580);
            } else {
                root.setPrefWidth(540);
                root.setPrefHeight(380);
                centerPane.getChildren().addAll(splitPane);
            }
        } else {
            root.setPrefWidth(540);
            root.setPrefHeight(380);
            centerPane.getChildren().add(childrenListView);
        }
        root.setCenter(centerPane);
        root.setBottom(buttonBar);
        return root;
    }

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private void onSave() {
        File parentFile;
        if (doesAllowChildren) {
            TreeItem<File> selectedItem = parentTreeView.getSelectionModel().getSelectedItem();
            parentFile = selectedItem.getValue();
        } else {
            parentFile = fileChooserInfo.getRoot();
        }
        File file = new File(parentFile, fileNameBox.getText());
        fileChooserHandler.handle(file);
        if (propertiesView != null) {
            fileChooserInfo.setProperties(propertiesView);
        }
        dispose();
    }

    private void onNewFolder() {
        MarathonInputStage testNameStage = new MarathonInputStage("Folder name", "Create a new folder",
                FXUIUtils.getIcon("fldr_closed")) {

            @Override
            protected String validateInput(String name) {
                String errorMessage = null;
                if (name.length() == 0 || name.trim().isEmpty()) {
                    errorMessage = "Enter a valid name";
                }
                return errorMessage;
            }

            @Override
            protected String getInputFiledLabelText() {
                return "Enter a folder name: ";
            }

            @Override
            protected void setDefaultButton() {
                okButton.setDefaultButton(true);
            }
        };
        FolderNameHandler testNameHandler = new FolderNameHandler();
        testNameStage.setInputHandler(testNameHandler);
        testNameStage.getStage().showAndWait();
        TreeItem<File> selectedItem = parentTreeView.getSelectionModel().getSelectedItem();
        String testName = testNameHandler.getTestName();
        if (testName == null || "".equals(testName)) {
            return;
        }
        File file = new File(selectedItem.getValue(), testName);
        if (file.exists()) {
            FXUIUtils.showMessageDialog(getStage(), "Folder with name '" + testName + "' already exists.", "File exists",
                    AlertType.INFORMATION);
        } else {
            selectedItem.getChildren().add(new TreeItem<File>(file));
            if (!file.mkdir()) {
                FXUIUtils.showMessageDialog(getStage(), "Couldn't create folder with name '" + testName + "'", "Creation Fail",
                        AlertType.ERROR);
            }
        }
    }

    final class FolderNameHandler implements IInputHanler {
        private String folderName;

        @Override
        public void handleInput(String name) {
            this.folderName = name;
        }

        public String getTestName() {
            return folderName;
        }
    }

    private void initListView() {
        if (!doesAllowChildren) {
            fillUpChildren(fileChooserInfo.getRoot());
        }
        childrenListView.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
            @Override
            public ListCell<File> call(ListView<File> param) {
                return new ChildrenFileCell();
            }
        });
        childrenListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (fileChooserInfo.isFileCreation()) {
                return;
            }
            File selectedItem = childrenListView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                fileNameBox.clear();
            }
        });
    }

    private void initTreeView() {
        parentTreeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            @Override
            public TreeCell<File> call(TreeView<File> param) {
                return new ParentFileCell();
            }
        });
        parentTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newVlaue) -> {
            TreeItem<File> selectedItem = parentTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                newFolderButton.setDisable(false);
                fileNameBox.setEditable(true);
                File selectedFile = selectedItem.getValue();
                fillUpChildren(selectedFile);
            } else {
                fileNameBox.setEditable(false);
                newFolderButton.setDisable(true);
                childrenListView.getItems().clear();
            }
        });
        File root = fileChooserInfo.getRoot();
        TreeItem<File> rootItem = new TreeItem<>(root);
        parentTreeView.setRoot(rootItem);
        rootItem.setExpanded(true);
        parentTreeView.getSelectionModel().select(0);
        populateChildren(root, rootItem);
    }

    private void populateChildren(File root, TreeItem<File> rootItem) {
        ObservableList<TreeItem<File>> children = rootItem.getChildren();
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.getName().contains(".") && file.isDirectory()) {
                    TreeItem<File> item = new TreeItem<File>(file);
                    children.add(item);
                    populateChildren(file, item);
                }
            }
        }
    }

    private void fillUpChildren(File rootFile) {
        File[] files = rootFile.listFiles();
        ObservableList<File> items = childrenListView.getItems();
        items.clear();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    items.add(file);
                }
            }
        }
    }

    private void onCancel() {
        fileChooserHandler.handle(null);
        dispose();
    }

    @Override
    protected void setDefaultButton() {
        if (fileChooserInfo.isFileCreation()) {
            createButton.setDefaultButton(true);
        } else {
            saveButton.setDefaultButton(true);
        }
    }

    public class ChildrenFileCell extends ListCell<File> {

        public ChildrenFileCell() {
            addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
                if (fileChooserInfo.isFileCreation()) {
                    return;
                }
                if (e.getButton() != MouseButton.PRIMARY || e.getClickCount() != 1) {
                    return;
                }
                File selectedItem = childrenListView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    fileNameBox.clear();
                    fileNameBox.setText(selectedItem.getName());
                } else {
                    fileNameBox.clear();
                }

            });
        }

        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item.getName());
                setGraphic(FXUIUtils.getIcon("file"));
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }

    public class ParentFileCell extends TreeCell<File> {
        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                setText(item.getName());
                setIcon();
            } else {
                setText(null);
                setGraphic(null);
            }
        }

        public void setIcon() {
            Node folderOpen = FXUIUtils.getIcon("fldr_obj");
            Node folderClose = FXUIUtils.getIcon("fldr_closed");
            setGraphic(folderClose);
            boolean expanded = getTreeItem().isExpanded();
            if (expanded) {
                setGraphic(folderOpen);
            } else {
                setGraphic(folderClose);
            }
        }
    }
}
