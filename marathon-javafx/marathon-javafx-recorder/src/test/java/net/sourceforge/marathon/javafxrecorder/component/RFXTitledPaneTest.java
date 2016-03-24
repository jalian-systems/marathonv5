package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.AccordionSample;
import javafx.application.Platform;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTitledPaneTest extends RFXComponentTest {

    @Test public void click() {
        TitledPane titledPane = (TitledPane) getPrimaryStage().getScene().getRoot().lookup(".titled-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXTitledPane rfxTitledPane = new RFXTitledPane(titledPane, null, null, lr);
            titledPane.setExpanded(true);
            rfxTitledPane.mouseButton1Pressed(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("click", recording.getCall());
    }

    @Override protected Pane getMainPane() {
        return new AccordionSample();
    }
}
