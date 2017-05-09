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
package net.sourceforge.marathon.fx.api;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class ModalDialog<T> {

    public static final Logger LOGGER = Logger.getLogger(ModalDialog.class.getName());

	private static final Image LOGO16 = FXUIUtils.getImageURL("logo16");
	private static final Image LOGO32 = FXUIUtils.getImageURL("logo32");
	private static final Image LOGO64 = FXUIUtils.getImageURL("logo64");
	private static final Image LOGO128 = FXUIUtils.getImageURL("logo128");
	private static final Image LOGO256 = FXUIUtils.getImageURL("logo256");

	private Stage stage;
    private T returnValue;
    private String title;
    private double sceneWidth;
    private double sceneHeight;
    private ICancelHandler cancelHandler;
    private String subTitle;
    private Node icon;

    public ModalDialog(String title, String subTitle, Node icon) {
        this.title = title;
        this.subTitle = subTitle;
        this.icon = icon;
    }

    public T show(Window parent) {
        Stage stage = getStage();
        stage.initModality(Modality.APPLICATION_MODAL);
        focusOnFirstControl(stage.getScene().getRoot());
        stage.showAndWait();
        return getReturnValue();
    }

    private boolean focusOnFirstControl(Node node) {
        if (node.isFocusTraversable()) {
            if (node instanceof TableView<?>)
                return false;
            Platform.runLater(() -> node.requestFocus());
            return true;
        } else if (node instanceof Parent) {
            ObservableList<Node> cs = ((Parent) node).getChildrenUnmodifiable();
            for (Node c : cs) {
                if (focusOnFirstControl(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Stage getStage() {
        if (stage != null) {
            return stage;
        }
        Parent contentPane = getContentPane();
        BorderPane sceneContent = new BorderPane();
        if (title != null && !"".equals(title)) {
            VBox titleBox = new VBox();
            Label titleLabel = new Label(title, icon);
            titleLabel.getStyleClass().add("modaldialog-title");
            titleBox.getChildren().add(titleLabel);
            if (subTitle != null) {
                Label subTitleLabel = new Label(subTitle);
                subTitleLabel.getStyleClass().add("modaldialog-subtitle");
                if (icon != null)
                    subTitleLabel.setPadding(new Insets(0, 0, 0, 20));
                titleBox.getChildren().add(subTitleLabel);
            }
            titleBox.getChildren().add(new Separator());
            sceneContent.setTop(titleBox);
        }
        sceneContent.setCenter(contentPane);
        Scene scene;
        if (sceneWidth > 0 && sceneHeight > 0) {
            scene = new Scene(sceneContent, sceneWidth, sceneHeight);
        } else {
            scene = new Scene(sceneContent);
        }
        scene.getStylesheets().add(ModalDialog.class.getClassLoader()
                .getResource("net/sourceforge/marathon/fx/api/css/marathon.css").toExternalForm());
        initialize(scene);
        stage = new Stage();
        stage.setScene(scene);
        initialize(stage);
        stage.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                if (cancelHandler != null) {
                    cancelHandler.handleCancel();
                } else {
                    dispose();
                }
            }
        });
        if (title != null && stage.getTitle() == null) {
            stage.setTitle(title);
        }
        if (Boolean.getBoolean("marathon.show.id")) {
            addToolTips(contentPane);
        }
        setDefaultButton();
        focusOnFirstControl(stage.getScene().getRoot());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.sizeToScene();
        addIcons();
        return stage;
    }

	private void addIcons() {
		stage.getIcons().add(LOGO16);
		stage.getIcons().add(LOGO32);
		stage.getIcons().add(LOGO64);
		stage.getIcons().add(LOGO128);
		stage.getIcons().add(LOGO256);
	}

    public void addToolTips(Node node) {
        ObservableList<Node> children = getChildren(node);
        for (Node n : children) {
            addToolTips(n);
        }
        Tooltip.install(node, new Tooltip(createTooltipText(node)));
    }

    private String createTooltipText(Node node) {
        StringBuilder tooltipText = new StringBuilder();
        String id = node.getId();
        if (id != null && !"".equals(id)) {
            tooltipText.append("#" + id);
        }
        ObservableList<String> styleClass = node.getStyleClass();
        if (styleClass.size() > 0) {
            tooltipText.append("(");
            for (String string : styleClass) {
                tooltipText.append(string + ",");
            }
            tooltipText.setLength(tooltipText.length() - 1);
            tooltipText.append(")");
        }
        return tooltipText.toString();
    }

    private ObservableList<Node> getChildren(Node node) {
        if (node instanceof ButtonBar) {
            return ((ButtonBar) node).getButtons();
        }
        if (node instanceof ToolBar) {
            return ((ToolBar) node).getItems();
        }
        if (node instanceof Pane) {
            return ((Pane) node).getChildren();
        }
        if (node instanceof TabPane) {
            ObservableList<Node> contents = FXCollections.observableArrayList();
            ObservableList<Tab> tabs = ((TabPane) node).getTabs();
            for (Tab tab : tabs) {
                contents.add(tab.getContent());
            }
            return contents;
        }
        return FXCollections.observableArrayList();
    }

    protected void initialize(Stage stage) {
        stage.sizeToScene();
    }

    protected void initialize(Scene scene) {
    }

    protected abstract Parent getContentPane();

    protected abstract void setDefaultButton();

    public T getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(T value) {
        returnValue = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        stage.setTitle(title);
    }

    public void dispose() {
        if (stage != null) {
            stage.close();
        }
    }

    public void setCancelHandler(ICancelHandler cancelHandler) {
        this.cancelHandler = cancelHandler;
    }
}
