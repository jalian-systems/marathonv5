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
package net.sourceforge.marathon;

import java.util.Properties;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.fx.display.VersionInfo;

public class MarathonSplashScreen extends ModalDialog<MarathonSplashScreenInfo> {

    private VersionInfo versionInfo;
    private static final Duration SPLASH_DISPLAY_TIME = new Duration(2000);

    public MarathonSplashScreen(VersionInfo versionInfo) {
        super("", null, null);
        this.versionInfo = versionInfo;
    }

    @Override protected Parent getContentPane() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color:black");
        root.setId("marathonITESplashScreen");
        root.getStyleClass().add("marathonite-splash-screen");
        root.getChildren().addAll(FXUIUtils.getImage("marathon-splash"), createInfo());
        Timeline timeline = new Timeline(new KeyFrame(SPLASH_DISPLAY_TIME, (e) -> {
            dispose();
        }));
        timeline.play();
        return root;
    }

    @Override protected void initialize(Stage stage) {
        super.initialize(stage);
        stage.initStyle(StageStyle.UNDECORATED);
    }

    private HBox createInfo() {
        Label blurbLabel = createLabel(versionInfo.getBlurbTitle());
        blurbLabel.setId("blurbLabel");

        HBox infoBox = new HBox();
        infoBox.getChildren().addAll(blurbLabel);
        infoBox.setAlignment(Pos.TOP_CENTER);
        return infoBox;
    }

    private Label createLabel(String labelText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill:white");
        return label;
    }

    @Override protected void setDefaultButton() {
    }

    public static class Main extends Application {
        @Override public void start(Stage primaryStage) throws Exception {
            Properties licensee = new Properties();
            licensee.put("Organization", "Jalian Systems Pvt. Ltd");
            licensee.put("License Type", "Trial");
            licensee.put("Expires", "18/10/2018");

            VersionInfo versionInfo = new VersionInfo("4.1.2.0", "Marathon GUI Testing Tool (Java/Swing) (Opensource version)",
                    "� Jalian Systems Private Ltd. and Other contributors",
                    "Visit our website: http://www.marathontesting.com for details",
                    "Marathon uses other OSS projects. See Credits for more information.");
            MarathonSplashScreen screen = new MarathonSplashScreen(versionInfo);
            screen.show(null);
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}
