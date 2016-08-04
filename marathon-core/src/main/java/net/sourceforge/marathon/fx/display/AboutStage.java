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

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

public class AboutStage extends ModalDialog<VersionInfo> {
    private VBox infoBox = new VBox();
    private ButtonBar buttonBar = new ButtonBar();
    private Button creditsButton = FXUIUtils.createButton("credits", "Credits", true, "Credits");
    private Button okButton = FXUIUtils.createButton("ok", "OK", true, "OK");

    private ICreditsStageHandler creditsStageHandler;
    private VersionInfo versionInfo;

    public AboutStage(VersionInfo versionInfo) {
        super("About");
        this.versionInfo = versionInfo;
        creditsStageHandler = new ICreditsStageHandler() {
            @Override public void createCreditsStage() {
                CreditsStage creditsStage = new CreditsStage();
                creditsStage.getStage().showAndWait();
            }
        };
        initComponents();
    }

    public void initComponents() {
        HBox bulrbTitleBox = createBlurbTitle();
        Label companyLabel = new Label(versionInfo.getBlurbCompany());
        companyLabel.setId("companyName");
        Label websiteLabel = new Label(versionInfo.getBlurbWebsite());
        websiteLabel.setId("websiteAddress");
        Label creditsLabel = new Label(versionInfo.getBlurbCredits());
        creditsLabel.setId("creditsLabel");
        infoBox.setAlignment(Pos.TOP_CENTER);
        infoBox.getChildren().addAll(bulrbTitleBox, companyLabel, websiteLabel, creditsLabel);

        creditsButton.setOnAction((e) -> onCredits());
        okButton.setOnAction((e) -> onOK());

        buttonBar.setId("buttonBar");
        buttonBar.getButtons().addAll(creditsButton, okButton);

    }

    @Override protected Parent getContentPane() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color:black");
        root.getStyleClass().add("about-stage");
        root.setId("aboutStage");
        root.getChildren().addAll(FXUIUtils.createImage("marathon-about", versionInfo.getVersion()), infoBox, buttonBar);
        return root;
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
    }

    private HBox createBlurbTitle() {
        Label bulrbTitleLabel = new Label(versionInfo.getBlurbTitle());
        bulrbTitleLabel.setId("blurbTitle");
        bulrbTitleLabel.setAlignment(Pos.TOP_CENTER);

        HBox titleBox = new HBox();
        titleBox.setId("titleBar");
        titleBox.setAlignment(Pos.TOP_CENTER);
        titleBox.getChildren().addAll(bulrbTitleLabel);
        return titleBox;
    }

    protected void onOK() {
        dispose();
    }

    private void onCredits() {
        creditsStageHandler.createCreditsStage();
    }

    public void setCreditsStageHandler(ICreditsStageHandler creditsStageHandler) {
        this.creditsStageHandler = creditsStageHandler;
    }

    @Override protected void setDefaultButton() {
        okButton.setDefaultButton(true);
    }
}
