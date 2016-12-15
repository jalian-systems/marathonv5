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
package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.ProgressBarSample;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXProgressBarTest extends RFXComponentTest {

    @Test public void select() {
        ProgressBar progressBar = (ProgressBar) getPrimaryStage().getScene().getRoot().lookup(".progress-bar");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXProgressBar rfxProgressBar = new RFXProgressBar(progressBar, null, null, lr);
            progressBar.setProgress(0.56);
            rfxProgressBar.mouseReleased(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("0.56", recording.getParameters()[0]);
    }

    @Test public void getText() {
        ProgressBar progressBar = (ProgressBar) getPrimaryStage().getScene().getRoot().lookup(".progress-bar");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXProgressBar rfxProgressBar = new RFXProgressBar(progressBar, null, null, lr);
            progressBar.setProgress(0.56);
            rfxProgressBar.mouseReleased(null);
            text.add(rfxProgressBar.getAttribute("text"));
        });
        new Wait("Waiting for progress bar text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("0.56", text.get(0));
    }

    @Override protected Pane getMainPane() {
        return new ProgressBarSample();
    }
}
