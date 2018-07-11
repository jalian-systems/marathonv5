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

import ensemble.samples.controls.buttons.RadioButtons;
import javafx.application.Platform;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXToggleButtonTest extends RFXComponentTest {

    @Override
    protected Pane getMainPane() {
        return new RadioButtons();
    }

    @Test
    public void selectRadioButtonNotSelected() {
        RadioButton radioButton = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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

    @Test
    public void selectRadioButtonSelected() {
        RadioButton radioButton = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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

    @Test
    public void getText() {
        RadioButton radioButton = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RFXToggleButton rfxToggleButton = new RFXToggleButton(radioButton, null, null, lr);
                radioButton.setSelected(false);
                rfxToggleButton.mouseEntered(null);
                radioButton.setSelected(true);
                rfxToggleButton.mouseClicked(null);
                text.add(rfxToggleButton._getText());
            }
        });
        new Wait("Waiting for toggle button text") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Hello", text.get(0));
    }

}
