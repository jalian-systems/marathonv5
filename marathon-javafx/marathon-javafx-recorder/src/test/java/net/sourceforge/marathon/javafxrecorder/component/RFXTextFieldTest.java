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

import ensemble.samples.controls.text.TextFieldSample;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTextFieldTest extends RFXComponentTest {

    @Override
    protected Pane getMainPane() {
        return new TextFieldSample();
    }

    @Test
    public void select() throws InterruptedException {
        final TextField textField = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textField.setText("Hello World");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(textField, null, null, lr);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                rTextField.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Hello World", select.getParameters()[0]);
    }

    @Test
    public void getText() {
        final TextField textField = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
        LoggingRecorder lr = new LoggingRecorder();
        List<Object> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXComponent rTextField = new RFXTextInputControl(textField, null, null, lr);
            textField.setText("Hello World");
            rTextField.focusLost(null);
            text.add(rTextField.getAttribute("text"));
        });
        new Wait("Waiting for text field text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Hello World", text.get(0));
    }

    @Test
    public void selectWithSpecialChars() throws InterruptedException {
        final TextField textField = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textField.setText("Hello World'\"");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(textField, null, null, lr);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                rTextField.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Hello World'\"", select.getParameters()[0]);
    }

    @Test
    public void selectWithUtf8Chars() throws InterruptedException {
        final TextField textField = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textField.setText("å∫ç∂´ƒ©˙ˆ∆");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(textField, null, null, lr);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                rTextField.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("å∫ç∂´ƒ©˙ˆ∆", select.getParameters()[0]);
    }

}
