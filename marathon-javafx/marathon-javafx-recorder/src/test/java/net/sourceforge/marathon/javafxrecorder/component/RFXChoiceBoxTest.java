package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.json.JSONArray;
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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    @Test public void assertContent() {
        ChoiceBox<?> choiceBox = (ChoiceBox<?>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        final Object[] content = new Object[] { null };
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            content[0] = rfxChoiceBox.getContent();
        });
        new Wait("Waiting for content") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Dog\",\"Cat\",\"Horse\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }

    @Test public void assertContentDuplicate() {
        @SuppressWarnings("unchecked")
        ChoiceBox<String> choiceBox = (ChoiceBox<String>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        LoggingRecorder lr = new LoggingRecorder();
        final Object[] content = new Object[] { null };
        Platform.runLater(() -> {
            RFXChoiceBox rfxChoiceBox = new RFXChoiceBox(choiceBox, null, null, lr);
            choiceBox.getItems().add("Cat");
            content[0] = rfxChoiceBox.getContent();
        });
        new Wait("Waiting for content") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Dog\",\"Cat\",\"Horse\",\"Cat(1)\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxSample();
    }
}
