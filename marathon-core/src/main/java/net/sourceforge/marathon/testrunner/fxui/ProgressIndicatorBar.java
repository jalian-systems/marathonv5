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
package net.sourceforge.marathon.testrunner.fxui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.api.ModalDialog;

class ProgressIndicatorBar extends StackPane {

    public static final Logger LOGGER = Logger.getLogger(ProgressIndicatorBar.class.getName());

    final private ProgressBar bar = new ProgressBar();
    final private HBox progressBarString = new HBox();

    private int maxTestCount;
    private int errors;
    private int failures;
    private double progress;

    boolean error = false;

    private Text nRuns;
    private Text errorText;
    private Text failureText;

    final private static int DEFAULT_LABEL_PADDING = 12;

    ProgressIndicatorBar(int maxTestCount) {
        getStyleClass().add("test-runner");
        getStylesheets().add(ModalDialog.class.getClassLoader().getResource("net/sourceforge/marathon/fx/api/css/marathon.css")
                .toExternalForm());
        this.maxTestCount = maxTestCount;
        bar.setId("test-runner-bar");
        bar.setProgress(0);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setMinHeight(progressBarString.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 3);
        bar.setMinWidth(progressBarString.getBoundsInLocal().getWidth() + DEFAULT_LABEL_PADDING * 2);
        initProgressBarUI();
        getChildren().setAll(bar, progressBarString);
    }

    private void initProgressBarUI() {
        Label runLabel = new Label("Runs: ");
        runLabel.setMinWidth(Region.USE_PREF_SIZE);
        nRuns = new Text((int) progress + "/" + maxTestCount);

        Label errorLabel = new Label("Errors: ");
        errorLabel.setMinWidth(Region.USE_PREF_SIZE);
        errorLabel.setGraphic(FXUIUtils.getIcon("error"));
        errorLabel.setPadding(new Insets(0, 0, 0, 80));
        errorText = new Text(errors + "");

        Label failureLabel = new Label("Failures: ");
        failureLabel.setMinWidth(Region.USE_PREF_SIZE);
        failureLabel.setGraphic(FXUIUtils.getIcon("failure"));
        failureLabel.setPadding(new Insets(0, 0, 0, 80));
        failureText = new Text(failures + "");

        progressBarString.setAlignment(Pos.CENTER);
        progressBarString.setPadding(new Insets(5, 0, 5, 0));
        progressBarString.getChildren().addAll(runLabel, nRuns, errorLabel, errorText, failureLabel, failureText);
    }

    public void reset(int total) {
        error = false;
        errors = 0;
        failures = 0;
        setBarColor();
        this.maxTestCount = total;
        bar.setProgress(0);
        progress = 0;
        setString();
    }

    private void setString() {
        nRuns.setText((int) progress + "/" + maxTestCount);
        errorText.setText(errors + "");
        failureText.setText(failures + "");
    }

    public void increment() {
        progress++;
        bar.setProgress(progress / maxTestCount);
        setString();
    }

    private void setBarColor() {
        ObservableList<String> styleClass = bar.getStyleClass();
        String sucessColor = "green-bar";
        String failureColor = "red-bar";
        styleClass.removeAll(sucessColor);
        styleClass.removeAll(failureColor);
        styleClass.add(error ? failureColor : sucessColor);
    }

    public void incrementErrors() {
        errors++;
    }

    public void incrementFailures() {
        failures++;
    }

    public void setError(boolean b) {
        error = true;
        setBarColor();
    }
}
