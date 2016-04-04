package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.AccordionSample;
import javafx.application.Platform;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
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

    @Test public void getText() {
        TitledPane titledPane = (TitledPane) getPrimaryStage().getScene().getRoot().lookup(".titled-pane");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXTitledPane rfxTitledPane = new RFXTitledPane(titledPane, null, null, lr);
            titledPane.setExpanded(true);
            rfxTitledPane.mouseButton1Pressed(null);
            text.add(rfxTitledPane.getAttribute("text"));
        });
        new Wait("Waiting for titled pane text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Node 1", text.get(0));
    }

    @Override protected Pane getMainPane() {
        return new AccordionSample();
    }
}
