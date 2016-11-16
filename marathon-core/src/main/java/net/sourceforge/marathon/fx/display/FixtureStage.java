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

import java.util.List;
import java.util.Properties;

import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.api.ITestApplication;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.projectselection.ApplicationLayout;
import net.sourceforge.marathon.fx.projectselection.FormPane;
import net.sourceforge.marathon.runtime.TestApplication;

public class FixtureStage extends ModalDialog<FixtureStageInfo> {

    private TextField nameField = new TextField();
    private TextArea descriptionArea = new TextArea();
    private CheckBox reuseField = new CheckBox();
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private Button testButton = FXUIUtils.createButton("test", "Test Settings...", true, "Test");
    private ButtonBar buttonBar = new ButtonBar();
    private FixtureStageInfo fixtureStageInfo;
    private IFixtureStageInfoHandler fixtureStageInfoHandler;
    private ApplicationLayout applicationLayout;

    public FixtureStage(FixtureStageInfo fixtureStageInfo) {
        super("New Fixture", "Create a new fixture to use with tests", FXUIUtils.getIcon("selectFixture"));
        this.fixtureStageInfo = fixtureStageInfo;
        initComponents();
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    @Override protected Parent getContentPane() {
        VBox content = new VBox();
        content.setId("FixtureStage");
        content.getStyleClass().add("fixture");
        FormPane form = new FormPane("fixture-form", 2);
        //@formatter:off
        form.addFormField("Name: ", nameField)
            .addFormField("Description: ", descriptionArea)
            .addFormField("Resuse Fixture: ", reuseField);
        //@formatter:on
        content.getChildren().addAll(form, applicationLayout.getContent(), buttonBar);
        fixtureStageInfo.setProperties();
        return content;
    }

    private void initComponents() {
        descriptionArea.setPrefRowCount(2);
        applicationLayout = fixtureStageInfo.getApplicationLayout(this);

        buttonBar.setId("FixtureButtonBar");
        okButton.setOnAction((e) -> onOK());
        cancelButton.setOnAction((e) -> dispose());
        testButton.setOnAction((e) -> onTest());
        buttonBar.getButtons().addAll(okButton, cancelButton, testButton);
    }

    private void onTest() {
        if (!isValidInputs(true)) {
            return;
        }
        ITestApplication application = getApplicationTester();
        try {
            application.launch();
        } catch (Exception e1) {
            e1.printStackTrace();
            FXUIUtils.showMessageDialog(getStage(), "Unable to launch application " + e1, "", AlertType.INFORMATION);
        }
    }

    protected ITestApplication getApplicationTester() {
        fixtureStageInfo.setDescription(descriptionArea.getText());
        fixtureStageInfo.setReuseFixture(reuseField.isSelected());
        Properties props = fixtureStageInfo.getProperties();
        ITestApplication applciationTester = new TestApplication(null, props);
        return applciationTester;
    }

    private void onOK() {
        if (isValidInputs(true)) {
            fixtureStageInfo.setFixtureName(nameField.getText());
            fixtureStageInfo.setDescription(descriptionArea.getText());
            fixtureStageInfo.setReuseFixture(reuseField.isSelected());
            fixtureStageInfoHandler.handle(fixtureStageInfo);
            dispose();
        }
    }

    public void setFixtureStageInfoHandler(IFixtureStageInfoHandler fixtureStageInfoHandler) {
        this.fixtureStageInfoHandler = fixtureStageInfoHandler;
    }

    public boolean isValidInputs(boolean showAlert) {
        return isValidFixtureName() && fixtureStageInfo.isValidInput(showAlert);
    }

    public boolean isValidFixtureName() {
        String nameText = nameField.getText();
        if (nameText.length() <= 0) {
            FXUIUtils.showMessageDialog(getStage(), "Fixture name cannot be empty", "Fixture Name", AlertType.ERROR);
            return false;
        }
        if (nameText.contains(" ")) {
            FXUIUtils.showMessageDialog(getStage(), "Fixture name cannot have spaces", "Fixture Name", AlertType.ERROR);
            return false;
        }
        if (exists(nameText)) {
            FXUIUtils.showMessageDialog(getStage(), "Fixture with the given name already exists", "Fixture Name", AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean exists(String fixtureName) {
        List<String> fixtures = fixtureStageInfo.getFixtures();
        return fixtures.contains(fixtureName);
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }
}
