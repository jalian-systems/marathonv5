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

import ensemble.samples.controls.ColorPickerSample;
import javafx.application.Platform;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXColorPickerTest extends RFXComponentTest {

    @Test public void selectColor() {
        ColorPicker colorPicker = (ColorPicker) getPrimaryStage().getScene().getRoot().lookup(".color-picker");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXColorPicker rfxColorPicker = new RFXColorPicker(colorPicker, null, null, lr);
            colorPicker.setValue(Color.rgb(234, 156, 44));
            rfxColorPicker.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("#ea9c2c", recording.getParameters()[0]);
    }

    @Test public void getText() {
        ColorPicker colorPicker = (ColorPicker) getPrimaryStage().getScene().getRoot().lookup(".color-picker");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXColorPicker rfxColorPicker = new RFXColorPicker(colorPicker, null, null, lr);
            colorPicker.setValue(Color.rgb(234, 156, 44));
            rfxColorPicker.focusLost(null);
            text.add(rfxColorPicker._getText());
        });
        new Wait("Waiting for color picker text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("#ea9c2c", text.get(0));
    }

    @Test public void colorChooserWithColorName() {
        ColorPicker colorPicker = (ColorPicker) getPrimaryStage().getScene().getRoot().lookup(".color-picker");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXColorPicker rfxColorPicker = new RFXColorPicker(colorPicker, null, null, lr);
            colorPicker.setValue(Color.RED);
            rfxColorPicker.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("#ff0000", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new ColorPickerSample();
    }
}
