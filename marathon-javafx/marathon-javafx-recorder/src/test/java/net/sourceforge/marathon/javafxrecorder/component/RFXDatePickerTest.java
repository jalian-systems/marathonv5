package net.sourceforge.marathon.javafxrecorder.component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.DatePickerSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXDatePickerTest extends RFXComponentTest {

    @Test public void pickDate() {
        DatePicker datePicker = (DatePicker) getPrimaryStage().getScene().getRoot().lookup(".date-picker");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXDatePicker rfxDatePicker = new RFXDatePicker(datePicker, null, null, lr);
            datePicker.setValue(LocalDate.now());
            rfxDatePicker.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        LocalDate date = (LocalDate) datePicker.getChronology().date(LocalDate.now());
        AssertJUnit.assertEquals(datePicker.getConverter().toString(date), recording.getParameters()[0]);
    }

    @Test public void getText() {
        DatePicker datePicker = (DatePicker) getPrimaryStage().getScene().getRoot().lookup(".date-picker");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        RFXDatePicker rfxDatePicker = new RFXDatePicker(datePicker, null, null, lr);
        Platform.runLater(() -> {
            datePicker.setValue(LocalDate.now());
            text.add(rfxDatePicker._getText());
        });
        new Wait("Waiting for date picker text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals(datePicker.getConverter().toString(LocalDate.now()), text.get(0));
    }

    @Test public void pickEditorDate() {
        Set<Node> datePickerNodes = getPrimaryStage().getScene().getRoot().lookupAll(".date-picker");
        List<Node> pickers = new ArrayList<>(datePickerNodes);
        DatePicker datePicker = (DatePicker) pickers.get(1);
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXDatePicker rfxDatePicker = new RFXDatePicker(datePicker, null, null, lr);
            datePicker.getEditor().setText("8/8/2016");
            rfxDatePicker.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("8/8/2016", recording.getParameters()[0]);

    }

    @Override protected Pane getMainPane() {
        return new DatePickerSample();
    }
}
