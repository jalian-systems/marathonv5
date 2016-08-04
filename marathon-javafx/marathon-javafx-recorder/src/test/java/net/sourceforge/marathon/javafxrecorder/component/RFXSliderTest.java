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
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SliderSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXSliderTest extends RFXComponentTest {

    @Test public void slider() throws Throwable {
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

    @Test public void getText() throws Throwable {
        Slider slider = (Slider) getPrimaryStage().getScene().getRoot().lookup(".slider");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                slider.setValue(25.0);
                RFXSlider rfxSlider = new RFXSlider(slider, null, null, lr);
                rfxSlider.focusLost(null);
                text.add(rfxSlider.getAttribute("text"));
            }
        });
        new Wait("Waiting for slider text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("25.0", text.get(0));
    }

    @Override protected Pane getMainPane() {
        return new SliderSample();
    }
}
