package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.buttons.RadioButtons;
import javafx.application.Platform;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXToggleButtonTest extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new RadioButtons();
    }

    @Test public void selectRadioButtonNotSelected() {
        RadioButton radioButton = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXToggleButton rfxToggleButton = new RFXToggleButton(radioButton, null, null, lr);
                radioButton.setSelected(false);
                rfxToggleButton.mouseEntered(null);
                radioButton.setSelected(true);
                rfxToggleButton.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("true", select.getParameters()[0]);
    }

    @Test public void selectRadioButtonSelected() {
        RadioButton radioButton = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXToggleButton rfxToggleButton = new RFXToggleButton(radioButton, null, null, lr);
                radioButton.setSelected(true);
                rfxToggleButton.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("true", select.getParameters()[0]);
    }

}
