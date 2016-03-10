package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SliderSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXSliderTest extends RFXComponentTest {

    @Test public void sliderTest() throws Throwable {
        Slider slider = (Slider) getPrimaryStage().getScene().getRoot().lookup(".slider");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                slider.setValue(25.0);
                RFXSlider rfxSlider = new RFXSlider(slider, null, null, lr);
                rfxSlider.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("25.0", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new SliderSample();
    }
}
