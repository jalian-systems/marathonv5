/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.ProgressBarSample;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXProgressBarElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement progressBar;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        progressBar = driver.findElementByTagName("progress-bar");
    }

    @Test public void select() {
        ProgressBar progressBarNode = (ProgressBar) getPrimaryStage().getScene().getRoot().lookup(".progress-bar");
        Platform.runLater(() -> {
            progressBar.marathon_select("0.20");
        });
        new Wait("Wating for progress bar progress to be set.") {
            @Override public boolean until() {
                return progressBarNode.getProgress() == 0.2;
            }
        };
    }

    @Test public void getText() {
        List<String> value = new ArrayList<>();
        Platform.runLater(() -> {
            progressBar.marathon_select("0.20");
            value.add(progressBar.getAttribute("text"));
        });
        new Wait("Wating for progress bar value") {
            @Override public boolean until() {
                return value.size() > 0;
            }
        };
        AssertJUnit.assertEquals("0.2", value.get(0));
    }

    @Override protected Pane getMainPane() {
        return new ProgressBarSample();
    }

}
