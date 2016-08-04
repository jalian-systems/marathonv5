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

import java.util.Properties;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.api.ITestApplication;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.TestApplication;
import net.sourceforge.marathon.runtime.fx.api.IPropertiesLayout;

public class MPFConfigurationStage extends ModalDialog<MPFConfigurationInfo> {

    private TabPane tabPane;
    private Button saveButton;
    private Button testButton;
    private HBox banner = new HBox();
    private MPFConfigurationInfo mpfConfigurationInfo;
    protected IPropertiesLayout[] layouts;
    private Stage parent;

    public MPFConfigurationStage(Stage parent, MPFConfigurationInfo mpfConfigurationInfo) {
        super(mpfConfigurationInfo.getTitle());
        this.parent = parent;
        this.mpfConfigurationInfo = mpfConfigurationInfo;
        initComponents();
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
    }

    @Override protected Parent getContentPane() {
        VBox root = new VBox();
        root.setId("MPFConfigurationParent");
        root.getStyleClass().add("mpf-configuration");
        root.getChildren().addAll(banner, createTabPane(), createButtonBar());
        setProperties(mpfConfigurationInfo.getUserProperties());
        return root;
    }

    private void initComponents() {
        banner.setId("banner");
        Text titleText = new Text("Create and manage configuration");
        titleText.setId("MPFTitle");
        Text subTitleText = mpfConfigurationInfo.getDirName() == null ? new Text("Create a marathon project")
                : new Text("Update a Marathon Project");
        subTitleText.setId("MPFSubTitle");
        HBox subTitleBox = new HBox();
        subTitleBox.setId("subTitleBox");
        subTitleBox.getChildren().add(subTitleText);
        VBox titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.getChildren().addAll(titleText, subTitleBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox logoBox = new VBox();
        logoBox.getChildren().add(FXUIUtils.getImage("mpf-banner"));
        logoBox.setAlignment(Pos.CENTER_RIGHT);

        banner.getChildren().addAll(titleBox, spacer, logoBox);
        HBox.setHgrow(banner, Priority.ALWAYS);
    }

    private TabPane createTabPane() {
        tabPane = new TabPane();
        tabPane.setId("ConfigurationTabPane");
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        layouts = mpfConfigurationInfo.getProperties(this);
        for (IPropertiesLayout layout : layouts) {
            String name = layout.getName();
            Tab tab = new Tab(name, layout.getContent());
            tab.setId(name);
            tab.setGraphic(layout.getIcon());
            tabPane.getTabs().add(tab);
        }
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        return tabPane;
    }

    private ButtonBar createButtonBar() {
        ButtonBar buttonBar = new ButtonBar();
        buttonBar.setId("ConfigurationButtonBar");
        saveButton = FXUIUtils.createButton("save", "Save", true, "Save");
        saveButton.setOnAction((e) -> {
            onSave();
        });
        Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
        cancelButton.setOnAction((e) -> dispose());
        testButton = FXUIUtils.createButton("test", "Test Settings...", true, "Test");
        testButton.setOnAction((e) -> onTest());
        buttonBar.getButtons().addAll(saveButton, cancelButton, testButton);
        return buttonBar;
    }

    private void onTest() {
        if (!validInupt()) {
            return;
        }
        ITestApplication application = getApplicationTester();
        try {
            application.launch();
        } catch (Exception e1) {
            FXUIUtils.showMessageDialog(getStage(), "Unable to launch application " + e1, "", AlertType.INFORMATION);
        }
    }

    protected ITestApplication getApplicationTester() {
        Properties props = mpfConfigurationInfo.getProperties(layouts);
        ITestApplication applciationTester = new TestApplication(null, props);
        return applciationTester;
    }

    public void onSave() {
        if (validInupt()) {
            System.out.println("MPFConfigurationStage.onSave() valid input");
        } else {
            System.err.println("Not a valid input.");
        }
    }

    public boolean validInupt() {
        return validateLayoutInputs(true);
    }

    private boolean validateLayoutInputs(boolean showAlert) {
        if (layouts != null) {
            for (int i = 0; i < layouts.length; i++) {
                if (!layouts[i].isValidInput(showAlert)) {
                    tabPane.getSelectionModel().select(i);
                    return false;
                }
            }
        }
        return true;
    }

    public IPropertiesLayout[] getLayouts() {
        return layouts;
    }

    private void setProperties(Properties props) {
        setPropertiesToPanels(layouts, props);
    }

    private void setPropertiesToPanels(IPropertiesLayout[] layoutsArray, Properties props) {
        if (layoutsArray != null) {
            for (IPropertiesLayout element : layoutsArray) {
                element.setProperties(props);
            }
        }
    }

    @Override protected void setDefaultButton() {
        saveButton.setDefaultButton(true);
    }
}
