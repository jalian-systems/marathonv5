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

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.ButtonBarX;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class FixtureSelection extends ModalDialog<String> {

    public static final Logger LOGGER = Logger.getLogger(FixtureSelection.class.getName());

    private ObservableList<String> fixtuers;
    private ListView<String> fixtureList = new ListView<>();
    private ButtonBarX buttonBar = new ButtonBarX();
    private Button selectButton = FXUIUtils.createButton("ok", "Select fixture", true, "Select");
    private Button cancelButton = FXUIUtils.createButton("cancel", "Cancel", true, "Cancel");
    private IFixtureSelectionHandler fixtureSelectionHandler;
    private String selectedFixture;

    public FixtureSelection(ObservableList<String> fixtuers, String selectedFixture) {
        super("Marathon - Select Fixture", "Set the fixture to be used for new test scripts", FXUIUtils.getIcon("selectFixture"));
        this.fixtuers = fixtuers;
        this.selectedFixture = selectedFixture;
        initComponents();
    }

    @Override
    public Parent getContentPane() {
        VBox root = new VBox();
        root.getStyleClass().add("fixture-selection");
        root.setId("fixtureSelection");
        root.getChildren().addAll(fixtureList, buttonBar);
        return root;
    }

    @Override
    protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    private void initComponents() {
        VBox.setVgrow(fixtureList, Priority.ALWAYS);
        fixtureList.setId("fixture-list-view");
        fixtureList.setItems(fixtuers);
        fixtureList.setOnMouseClicked((e) -> {
            if (e.getClickCount() == 2)
                onSelect(fixtureList.getSelectionModel().getSelectedItem());
        });
        fixtureList.getSelectionModel().select(selectedFixture);
        fixtureList.getSelectionModel().selectedIndexProperty().addListener((listener) -> {
            updateButtonState();
        });

        selectButton.setOnAction((e) -> onSelect(fixtureList.getSelectionModel().getSelectedItem()));
        cancelButton.setOnAction((e) -> onCancel());
        buttonBar.getButtons().addAll(selectButton, cancelButton);
        buttonBar.setButtonMinWidth(Region.USE_PREF_SIZE);
        updateButtonState();
    }

    private void updateButtonState() {
        selectButton.setDisable(fixtureList.getSelectionModel().getSelectedIndex() == -1);
    }

    private void onSelect(String selectedItem) {
        fixtureSelectionHandler.handleFixture(selectedItem);
        dispose();
    }

    protected void onCancel() {
        dispose();
    }

    @Override
    protected void setDefaultButton() {
        selectButton.setDefaultButton(true);
    }

    public void setFixtureSelectionHandler(IFixtureSelectionHandler fixtureSelectionHandler) {
        this.fixtureSelectionHandler = fixtureSelectionHandler;
    }
}
