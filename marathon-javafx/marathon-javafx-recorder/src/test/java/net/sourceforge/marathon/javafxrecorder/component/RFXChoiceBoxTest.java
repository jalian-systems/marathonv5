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

import ensemble.samples.controls.ChoiceBoxSample;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXChoiceBoxTest extends RFXComponentTest {

    @Test public void select() {
        ChoiceBox<?> choiceBox = (ChoiceBox<?>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getSelectionModel().select(1);
            rfxChoiceBox.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Cat", recording.getParameters()[0]);
    }

    @Test public void selectOptionWithQuotes() {
        @SuppressWarnings("unchecked")
        ChoiceBox<String> choiceBox = (ChoiceBox<String>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getItems().add(" \"Mouse \" ");
            choiceBox.getSelectionModel().select(" \"Mouse \" ");
            rfxChoiceBox.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals(" \"Mouse \" ", recording.getParameters()[0]);
    }

    @Test public void htmlOptionSelect() {
        @SuppressWarnings("unchecked")
        ChoiceBox<String> choiceBox = (ChoiceBox<String>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        String text = "This is a test text";
        final String htmlText = "<html><font color=\"RED\"><h1><This is also content>" + text + "</h1></html>";
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getItems().add(htmlText);
            choiceBox.getSelectionModel().select(htmlText);
            rfxChoiceBox.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals(text, recording.getParameters()[0]);
    }

    @Test public void selectDuplicateOption() {
        @SuppressWarnings("unchecked")
        ChoiceBox<String> choiceBox = (ChoiceBox<String>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getSelectionModel().select(1);
            rfxChoiceBox.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Cat", recording.getParameters()[0]);

        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getItems().add(2, "Cat");
            choiceBox.getSelectionModel().select(2);
            rfxChoiceBox.focusLost(null);
        });
        recordings = lr.waitAndGetRecordings(2);
        recording = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Cat(1)", recording.getParameters()[0]);
    }

    @Test public void selectMultipleDuplicateOption() {
        @SuppressWarnings("unchecked")
        ChoiceBox<String> choiceBox = (ChoiceBox<String>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getSelectionModel().select(1);
            rfxChoiceBox.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Cat", recording.getParameters()[0]);

        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getItems().add(2, "Cat");
            choiceBox.getItems().add("Cat");
            choiceBox.getSelectionModel().select(4);
            rfxChoiceBox.focusLost(null);
        });
        recordings = lr.waitAndGetRecordings(2);
        recording = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Cat(2)", recording.getParameters()[0]);
    }

    @Test public void getText() {
        ChoiceBox<?> choiceBox = (ChoiceBox<?>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getSelectionModel().select(1);
            rfxChoiceBox.focusLost(null);
            text.add(rfxChoiceBox._getText());
        });
        new Wait("Waiting for choice box text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Cat", text.get(0));
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxSample();
    }
}
