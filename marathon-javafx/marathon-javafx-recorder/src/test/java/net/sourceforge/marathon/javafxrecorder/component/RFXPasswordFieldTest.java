/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.PasswordFieldSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXPasswordFieldTest extends RFXComponentTest {

    @Test public void select() {
        final PasswordField passwordField = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                passwordField.setText("Hello World");
            }
        });
        LoggingRecorder lr = new LoggingRecorder();
        RFXComponent rTextField = new RFXTextInputControl(passwordField, null, null, lr);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                rTextField.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Hello World", select.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new PasswordFieldSample();
    }
}
