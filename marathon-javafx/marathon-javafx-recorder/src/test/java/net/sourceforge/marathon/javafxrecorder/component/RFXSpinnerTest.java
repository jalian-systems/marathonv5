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

import javafx.application.Platform;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SpinnerSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXSpinnerTest extends RFXComponentTest {

    @Test
    public void selectListSpinner() {
        Spinner<?> spinner = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup(".spinner");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXSpinner rfxSpinner = new RFXSpinner(spinner, null, null, lr);
            spinner.getEditor().setText("March");
            rfxSpinner.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("March", recording.getParameters()[0]);
    }

    @Test
    public void getText() {
        Spinner<?> spinner = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup(".spinner");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXSpinner rfxSpinner = new RFXSpinner(spinner, null, null, lr);
            spinner.getEditor().setText("March");
            rfxSpinner.focusLost(null);
            text.add(rfxSpinner.getAttribute("text"));
        });
        new Wait("Waiting for spinner text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("March", text.get(0));
    }

    @Test
    public void selectListEditableSpinner() {
        Spinner<?> spinner = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup(".spinner");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            spinner.setEditable(true);
            RFXSpinner rfxSpinner = new RFXSpinner(spinner, null, null, lr);
            spinner.getEditor().setText("April");
            rfxSpinner.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("April", recording.getParameters()[0]);
    }

    @Test
    public void selectIntegerSpinner() {
        Spinner<?> spinner = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#integer-spinner");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXSpinner rfxSpinner = new RFXSpinner(spinner, null, null, lr);
            spinner.getEditor().setText("25");
            rfxSpinner.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("25", recording.getParameters()[0]);
    }

    @Test
    public void selectDoubleSpinner() {
        Spinner<?> spinner = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#double-spinner");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXSpinner rfxSpinner = new RFXSpinner(spinner, null, null, lr);
            spinner.getEditor().setText("35.5");
            rfxSpinner.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("35.5", recording.getParameters()[0]);
    }

    @Override
    protected Pane getMainPane() {
        return new SpinnerSample();
    }
}
