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
package net.sourceforge.marathon.fx.projectselection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sourceforge.marathon.api.GuiceInjector;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Constants;

public class ProjectSelection extends ModalDialog<ProjectInfo> {

    private HBox banner = new HBox();
    private Text bannerTitle = new Text("Create and manage configuration");
    private Region toolBarSpacer = new Region();
    private Separator separator = new Separator();
    private Label tableLabel = new Label("Select a project");
    private TableView<ProjectInfo> projectInfotable = new TableView<ProjectInfo>();
    private ContextMenu contextMenu = new ContextMenu();
    private Button newButton = FXUIUtils.createButton("new", "New project", true, "New");
    private Button editButton = FXUIUtils.createButton("edit", "Edit project", true, "Edit");
    private Button browseButton = FXUIUtils.createButton("browse", "Browse project", true, "Browse");
    private Button deleteButton = FXUIUtils.createButton("delete", "Delete project", false, "Delete");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private Button selectButton = FXUIUtils.createButton("ok", "Select project", true, "Select");;
    private ButtonBar buttonBar = new ButtonBar();

    private ObservableList<ProjectInfo> projects;
    private INewProjectHandler newProjectHandler;
    private IEditProjectHandler editProjectHandler;
    private List<List<String>> frameworks;

    public ProjectSelection(ObservableList<ProjectInfo> projects, List<List<String>> frameworks) {
        super("Select a Project");
        this.projects = projects;
        this.frameworks = frameworks;
        initComponents();
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.setX(20.0);
        stage.setY(20.0);
    }

    @Override protected Parent getContentPane() {
        VBox content = new VBox();
        content.setId("ProjectSelectionParent");
        content.getStyleClass().add("project-selection");
        content.getChildren().addAll(banner, separator, tableLabel, projectInfotable, buttonBar);
        return content;
    }

    private void initComponents() {
        banner.setId("ProjectSelectionBanner");
        bannerTitle.setId("BannerText");
        VBox titleBox = new VBox();
        titleBox.getChildren().add(bannerTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(toolBarSpacer, Priority.ALWAYS);
        VBox logoBox = new VBox();
        logoBox.getChildren().add(FXUIUtils.getImage("banner"));
        logoBox.setAlignment(Pos.CENTER_RIGHT);
        banner.getChildren().addAll(titleBox, toolBarSpacer, logoBox);

        tableLabel.setId("ProjectTableLabel");
        tableLabel.setLabelFor(projectInfotable);

        projectInfotable.setItems(projects);
        projectInfotable.setId("ProjectInfoTable");
        projectInfotable.setRowFactory(new Callback<TableView<ProjectInfo>, TableRow<ProjectInfo>>() {
            @Override public TableRow<ProjectInfo> call(TableView<ProjectInfo> param) {
                return new ProjectInfoTableRow();
            }
        });
        VBox.setVgrow(projectInfotable, Priority.ALWAYS);

        projectInfotable.setRowFactory(e -> {
            TableRow<ProjectInfo> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !tableRow.isEmpty()) {
                    onSelect(projectInfotable.getSelectionModel().getSelectedItem());
                }
            });
            return tableRow;
        });

        TableColumn<ProjectInfo, String> projectNameColumn = new TableColumn<>("Name");
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        projectNameColumn.prefWidthProperty().bind(projectInfotable.widthProperty().multiply(0.15));

        TableColumn<ProjectInfo, String> projectfolderColumn = new TableColumn<>("Destination");
        projectfolderColumn.setCellValueFactory(new PropertyValueFactory<>("folder"));
        projectfolderColumn.prefWidthProperty().bind(projectInfotable.widthProperty().multiply(0.691));

        TableColumn<ProjectInfo, String> frameWorkColumn = new TableColumn<>("FrameWork");
        frameWorkColumn.setCellValueFactory(new PropertyValueFactory<>("frameWork"));
        frameWorkColumn.prefWidthProperty().bind(projectInfotable.widthProperty().multiply(0.15));

        projectInfotable.getColumns().add(projectNameColumn);
        projectInfotable.getColumns().add(frameWorkColumn);
        projectInfotable.getColumns().add(projectfolderColumn);
        loadFileNames();
        projectInfotable.getSelectionModel().selectedIndexProperty().addListener((listener) -> {
            updateButtonState();
        });

        newButton.setOnAction((e) -> {
            Bounds bounds = newButton.localToScreen(newButton.getBoundsInLocal());
            contextMenu.show(newButton, bounds.getMinX() + newButton.getWidth() / 2, bounds.getMinY() + newButton.getHeight() / 2);
        });
        contextMenu.getItems().addAll(getAvailableFrameworks());
        browseButton.setOnAction((e) -> {
            ProjectInfo selectedItem = projectInfotable.getSelectionModel().getSelectedItem();
            File initailDirectory = null;
            if (selectedItem != null) {
                initailDirectory = new File(selectedItem.getFolder()).getParentFile();
            }
            File projectFile = FXUIUtils.showDirectoryChooser(null, initailDirectory, getStage());
            if (projectFile != null) {
                if (isValidProjectDirectory(projectFile)) {
                    loadProperties(projectFile);
                    ProjectInfo projectInfo = projectExists(projectFile.getAbsolutePath());
                    if (projectInfo == null) {
                        projectInfo = new ProjectInfo(System.getProperty(Constants.PROP_PROJECT_NAME),
                                System.getProperty(Constants.PROP_PROJECT_DESCRIPTION), projectFile.getAbsolutePath(),
                                System.getProperty(Constants.PROP_PROJECT_FRAMEWORK));
                        projects.add(0, projectInfo);
                    }
                    projectInfotable.getSelectionModel().select(projects.indexOf(projectInfo));
                } else {
                    FXUIUtils.showMessageDialog(getStage(), "Not a valid Marathon Project Directory", "", AlertType.INFORMATION);
                }
            }
        });
        deleteButton.setOnAction((e) -> onDelete());
        editButton.setOnAction((e) -> onEdit(projectInfotable.getSelectionModel().getSelectedItem()));
        cancelButton.setOnAction((e) -> onCancel());
        selectButton.setOnAction((e) -> onSelect(projectInfotable.getSelectionModel().getSelectedItem()));

        buttonBar.setId("ProjectSelectionButtonbar");
        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
        buttonBar.getButtons().addAll(newButton, browseButton, deleteButton, editButton, cancelButton, selectButton);
        updateButtonState();
    }

    private List<MenuItem> getAvailableFrameworks() {
        List<MenuItem> items = new ArrayList<>();
        for (List<String> framework : frameworks) {
            items.add(getFrameworkMenuItem(framework.get(0), framework.get(1)));
        }
        System.out.println("ProjectSelection.getAvailableFrameworks(" + GuiceInjector.get() + ")");
        return items;
    }

    private void onDelete() {
        System.out.println("ProjectSelection.onDelete()");
        ProjectInfo selectedItem = projectInfotable.getSelectionModel().getSelectedItem();
        projects.remove(selectedItem);
        removeFromPreferences(selectedItem);

    }

    private void removeFromPreferences(ProjectInfo selectedItem) {
        Preferences p = Preferences.userNodeForPackage(this.getClass());
        String[] keys;
        try {
            keys = p.keys();
            for (int i = 0; i < keys.length; i++) {
                String key = "dirName" + i;
                String fName = p.get(key, null);
                if (fName.equals(selectedItem.getFolder())) {
                    p.remove(key);
                    break;
                }
            }
        } catch (BackingStoreException e) {
            return;
        }
    }

    private ProjectInfo projectExists(String projectFolder) {
        for (ProjectInfo project : projects) {
            if (projectFolder.equals(project.getFolder())) {
                return project;
            }
        }
        return null;
    }

    protected void onCancel() {
        dispose();
    }

    private void loadProperties(File projectFile) {
        Properties properties = getProperties(projectFile);
        System.setProperty(Constants.PROP_PROJECT_NAME, properties.getProperty(Constants.PROP_PROJECT_NAME));
        System.setProperty(Constants.PROP_PROJECT_DESCRIPTION, properties.getProperty(Constants.PROP_PROJECT_DESCRIPTION));
        System.setProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL, properties.getProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL));
        System.setProperty(Constants.PROP_PROJECT_FRAMEWORK, Constants.getFramework());
    }

    private String getProperty(Properties properties, String key) {
        return properties.getProperty(key);
    }

    private Properties getProperties(File projectFile) {
        Properties properties = new Properties();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(projectFile.getAbsolutePath(), Constants.PROJECT_FILE));
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return properties;
    }

    public MenuItem getFrameworkMenuItem(String name, final String framework) {
        MenuItem projectMenuItem = new MenuItem(name);
        projectMenuItem.addEventHandler(ActionEvent.ACTION, (e) -> {
            System.setProperty(Constants.PROP_PROJECT_FRAMEWORK, framework);
            onNew();
        });
        return projectMenuItem;
    }

    private void updateButtonState() {
        selectButton.setDisable(projectInfotable.getSelectionModel().getSelectedIndex() == -1);
        editButton.setDisable(projectInfotable.getSelectionModel().getSelectedIndex() == -1);
        deleteButton.setDisable(projectInfotable.getSelectionModel().getSelectedIndex() == -1);
    }

    protected void onSelect(ProjectInfo selected) {
        storeFileNames();
    }

    private void onEdit(ProjectInfo selected) {
        boolean isProjectEdited = editProjectHandler.editProject(selected);
        if (!isProjectEdited) {
            return;
        } else {
            projectInfotable.refresh();
        }
    }

    private void onNew() {
        ProjectInfo newProject = newProjectHandler.createNewProject();
        if (newProject != null) {
            projects.add(0, newProject);
            projectInfotable.getSelectionModel().select(projects.indexOf(newProject));
        }
    }

    public void setNewProjectHandler(INewProjectHandler newProjectHandler) {
        this.newProjectHandler = newProjectHandler;
    }

    public void setEditProjectHandler(IEditProjectHandler editProjectHandler) {
        this.editProjectHandler = editProjectHandler;
    }

    private void storeFileNames() {
        Preferences p = Preferences.userNodeForPackage(this.getClass());
        try {
            p.clear();
            p.flush();
            p = Preferences.userNodeForPackage(this.getClass());
            int itemCount = projectInfotable.getItems().size();
            int selected = projectInfotable.getSelectionModel().getSelectedIndex();
            ProjectInfo pi = projectInfotable.getItems().get(selected);
            p.put("dirName0", pi.getFolder());
            for (int i = 0, j = 1; i < itemCount; i++) {
                if (i != selected) {
                    p.put("dirName" + j++, projectInfotable.getItems().get(i).getFolder());
                }
            }
        } catch (BackingStoreException e) {
            return;
        }
    }

    private void loadFileNames() {
        Preferences p = Preferences.userNodeForPackage(this.getClass());
        try {
            String[] keys = p.keys();
            for (int i = 0; i < keys.length; i++) {
                String fName = p.get("dirName" + i, null);
                if (fName == null) {
                    continue;
                }
                File file = new File(fName);
                if (isValidProjectDirectory(file)) {
                    System.setProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL,
                            getProperty(getProperties(file), Constants.PROP_PROJECT_LAUNCHER_MODEL));
                    projects.add(new ProjectInfo(getProperty(getProperties(file), Constants.PROP_PROJECT_NAME),
                            getProperty(getProperties(file), Constants.PROP_PROJECT_DESCRIPTION), file.getAbsolutePath(),
                            Constants.getFramework()));
                }
            }
            if (projects.size() > 0) {
                projectInfotable.getSelectionModel().select(0);
            }
        } catch (BackingStoreException e) {
            return;
        }
    }

    private boolean isValidProjectDirectory(File file) {
        return file.exists() && file.isDirectory() && new File(file, Constants.PROJECT_FILE).exists();
    }

    @Override protected void setDefaultButton() {
        selectButton.setDefaultButton(true);
    }

    public class ProjectInfoTableRow extends TableRow<ProjectInfo> {

        @Override protected void updateItem(ProjectInfo item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                String description = item.getDescription();
                if (description != null && !description.equals("")) {
                    setTooltip(new Tooltip(description));
                }
            }
        }

    }
}
