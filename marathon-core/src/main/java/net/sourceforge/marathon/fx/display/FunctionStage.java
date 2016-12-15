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
package net.sourceforge.marathon.fx.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sourceforge.marathon.editor.IEditor;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.api.Argument;
import net.sourceforge.marathon.runtime.api.Function;
import net.sourceforge.marathon.runtime.api.Module;

public class FunctionStage extends ModalDialog<FunctionInfo> {

    public IFunctionArgumentHandler functionArgumentHandler;
    private FunctionInfo functionInfo;
    private IEditor documentArea;

    private SplitPane mainSplitPane = new SplitPane();
    private ButtonBar topButtonBar = new ButtonBar();
    private Button expandAllBtn = FXUIUtils.createButton("expandall", "Expand All");
    private Button collapseAllBtn = FXUIUtils.createButton("collapseall", "Collapse All");
    private Button refreshButton = FXUIUtils.createButton("refresh", "Refresh", true);
    private TreeView<Object> tree = new TreeView<>();
    private TreeItem<Object> functionItem;
    private VBox argumentPane;
    private CheckBox filterByName = new CheckBox("Filter by window name");
    private SplitPane functionSplitPane = new SplitPane();
    private ButtonBar buttonBar = new ButtonBar();
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");

    public FunctionStage(FunctionInfo functionInfo) {
        super("Insert Method", "Insert a method into the recorded script", FXUIUtils.getIcon("insertScript"));
        this.functionInfo = functionInfo;
        initComponents();
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override protected Parent getContentPane() {
        BorderPane content = new BorderPane();
        content.getStyleClass().add("function-stage");
        content.setId("functionStage");
        content.setCenter(mainSplitPane);
        content.setBottom(buttonBar);
        content.setPrefSize(700, 500);
        return content;
    }

    private void initComponents() {
        mainSplitPane.setDividerPositions(0.35);
        mainSplitPane.getItems().addAll(createTree(), functionSplitPane);

        expandAllBtn.setOnAction((e) -> expandAll());
        collapseAllBtn.setOnAction((e) -> collapseAll());
        refreshButton.setOnAction((e) -> refresh());
        topButtonBar.setId("topButtonBar");
        topButtonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
        topButtonBar.getButtons().addAll(expandAllBtn, collapseAllBtn, refreshButton);

        functionSplitPane.setDividerPositions(0.4);
        functionSplitPane.setOrientation(Orientation.VERTICAL);
        documentArea = functionInfo.getEditorProvider().get(false, 0, IEditorProvider.EditorType.OTHER, false);
        Platform.runLater(() -> {
            documentArea.setEditable(false);
            documentArea.setMode("ruby");
        });

        argumentPane = new VBox();
        ScrollPane scrollPane = new ScrollPane(argumentPane);
        scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        functionSplitPane.getItems().addAll(documentArea.getNode(), scrollPane);

        okButton.setOnAction(new OkHandler());
        okButton.setDisable(true);
        cancelButton.setOnAction((e) -> dispose());
        buttonBar.getButtons().addAll(okButton, cancelButton);
        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
    }

    private Node createTree() {
        VBox treeContentBox = new VBox();
        tree.setRoot(functionInfo.getRoot(true));
        tree.setShowRoot(false);
        tree.getSelectionModel().selectedItemProperty().addListener(new TreeViewSelectionChangeListener());
        tree.setCellFactory(new Callback<TreeView<Object>, TreeCell<Object>>() {
            @Override public TreeCell<Object> call(TreeView<Object> param) {
                return new FunctionTreeCell();
            }
        });
        filterByName.setOnAction((e) -> {
            tree.setRoot(functionInfo.refreshNode(filterByName.isSelected()));
            expandAll();
        });
        filterByName.setSelected(true);
        expandAll();
        treeContentBox.getChildren().addAll(topButtonBar, tree, filterByName);
        VBox.setVgrow(tree, Priority.ALWAYS);
        return treeContentBox;
    }

    private void expandAll() {
        List<TreeItem<Object>> parentItems = getParentItems();
        for (TreeItem<Object> parentItem : parentItems) {
            parentItem.setExpanded(true);
        }
    }

    private void collapseAll() {
        List<TreeItem<Object>> parentItems = getParentItems();
        for (TreeItem<Object> parentItem : parentItems) {
            parentItem.setExpanded(false);
        }
    }

    private List<TreeItem<Object>> getParentItems() {
        TreeItem<Object> root = tree.getRoot();
        if (tree.isShowRoot()) {
            root.setExpanded(true);
        }
        List<TreeItem<Object>> parentItems = new ArrayList<TreeItem<Object>>();
        addAllParentTreeItem(root, parentItems);
        Collections.reverse(parentItems);
        return parentItems;
    }

    private void addAllParentTreeItem(TreeItem<Object> root, List<TreeItem<Object>> parentItems) {
        ObservableList<TreeItem<Object>> children = root.getChildren();
        for (TreeItem<Object> treeItem : children) {
            if (treeItem.getChildren().size() != 0) {
                parentItems.add(treeItem);
            } else {
                addAllParentTreeItem(treeItem, parentItems);
            }
        }
    }

    private void refresh() {
        functionInfo.refreshModuleFunctions();
        TreeItem<Object> root = functionInfo.getRoot(filterByName.isSelected());
        tree.setRoot(root);
        tree.refresh();
        expandAll();
        argumentPane.getChildren().clear();
    }

    public void setFunctionArgumentHandler(IFunctionArgumentHandler functionArgumentHandler) {
        this.functionArgumentHandler = functionArgumentHandler;
    }

    public class FunctionTreeCell extends TreeCell<Object> {

        @Override protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                String fqn;
                Node graphic = null;
                if (item instanceof Module) {
                    Module m = (Module) item;
                    fqn = m.getName();
                    if (m.isFile()) {
                        graphic = FXUIUtils.getIcon("file");
                    } else {
                        graphic = FXUIUtils.getIcon("folder");
                    }
                } else if (item instanceof Function) {
                    fqn = ((Function) item).getName();
                    graphic = FXUIUtils.getIcon("function");
                } else {
                    fqn = item.toString();
                }
                setText(fqn);
                setGraphic(graphic);
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }

    public class TreeViewSelectionChangeListener implements ChangeListener<TreeItem<Object>> {

        @Override public void changed(ObservableValue<? extends TreeItem<Object>> observable, TreeItem<Object> oldValue,
                TreeItem<Object> newValue) {
            if (newValue == null) {
                okButton.setDisable(true);
                documentArea.setText("");
                argumentPane.getChildren().clear();
                return;
            }
            TreeItem<Object> item = tree.getSelectionModel().getSelectedItem();
            String doc = "";
            boolean disable = true;
            functionItem = item;
            Object value = item.getValue();
            if (value instanceof Module) {
                doc = ((Module) value).getDocumentation();
            } else {
                doc = ((Function) value).getDocumentation();
                disable = false;
            }
            okButton.setDisable(disable);
            documentArea.setText(doc);
            argumentPane.getChildren().clear();
            if (item.isLeaf()) {
                addArguments(item);
            }
        }
    }

    private void addArguments(TreeItem<Object> item) {
        Function f = (Function) item.getValue();
        GridPane gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(10);
        VBox.setMargin(gridPane, new Insets(50, 0, 0, 120));
        List<Argument> arguments = f.getArguments();
        int rowIndex = 0;
        for (Argument argument : arguments) {
            Label label = new Label(argument.getName() + ": ");
            gridPane.add(label, 0, rowIndex);
            if (argument.getDefault() == null && argument.getDefaultList() == null) {
                TextField value = new TextField();
                gridPane.add(value, 1, rowIndex);
                VBox.setMargin(value, new Insets(0, 0, 3, 0));
            } else if (argument.getDefault() != null) {
                TextField value = new TextField();
                value.setText(argument.getDefault());
                gridPane.add(value, 1, rowIndex);
                VBox.setMargin(value, new Insets(0, 0, 3, 0));
            } else {
                ComboBox<String> value = new ComboBox<>(FXCollections.observableArrayList(argument.getDefaultList()));
                value.setEditable(true);
                value.getSelectionModel().select(0);
                gridPane.add(value, 1, rowIndex);
                VBox.setMargin(value, new Insets(0, 0, 3, 0));
            }
            rowIndex++;
        }
        argumentPane.getChildren().add(gridPane);
    }

    public class OkHandler implements EventHandler<ActionEvent> {

        @Override public void handle(ActionEvent event) {
            if (FunctionStage.this.functionItem == null) {
                return;
            }
            ObservableList<Node> children = argumentPane.getChildren();
            GridPane sp = (GridPane) children.get(0);
            children = sp.getChildren();
            String[] arguments = getArguments(children);
            functionArgumentHandler.handle(arguments, (Function) functionItem.getValue());
        }

        private String[] getArguments(ObservableList<Node> children) {
            String[] args = new String[children.size() / 2];
            for (int i = 1; i < children.size(); i += 2) {
                if (children.get(i) instanceof TextField) {
                    args[i / 2] = ((TextField) children.get(i)).getText();
                } else {
                    args[i / 2] = ((ComboBox<?>) children.get(i)).getSelectionModel().getSelectedItem().toString();
                }
            }
            return args;
        }
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }
}
