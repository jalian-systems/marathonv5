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

import ensemble.samples.controls.buttons.HyperlinkSample;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXHyperlinkButtonTest extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new HyperlinkSample();
    }

    @Test public void click() {
        Hyperlink button = (Hyperlink) getPrimaryStage().getScene().getRoot().lookup(".hyperlink");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXButtonBase rfxButtonBase = new RFXButtonBase(button, null, null, lr);
                Point2D sceneXY = button.localToScene(new Point2D(3, 3));
                PickResult pickResult = new PickResult(button, sceneXY.getX(), sceneXY.getY());
                Point2D screenXY = button.localToScreen(new Point2D(3, 3));
                MouseEvent me = new MouseEvent(button, button, MouseEvent.MOUSE_PRESSED, 3, 3, sceneXY.getX(), screenXY.getY(),
                        MouseButton.PRIMARY, 1, false, false, false, false, true, false, false, false, false, false, pickResult);
                rfxButtonBase.mouseButton1Pressed(me);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("click", select.getCall());
        AssertJUnit.assertEquals("", select.getParameters()[0]);
    }
}
