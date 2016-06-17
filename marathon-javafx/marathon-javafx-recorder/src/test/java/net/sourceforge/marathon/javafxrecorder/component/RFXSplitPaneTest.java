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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SplitPaneSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXSplitPaneTest extends RFXComponentTest {

    @Test public void selectSplitPane() {
        SplitPane splitPane = (SplitPane) getPrimaryStage().getScene().getRoot().lookup(".split-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXSplitPane rfxSplitPane = new RFXSplitPane(splitPane, null, null, lr);
            splitPane.setDividerPosition(0, 0.6);
            rfxSplitPane.mouseReleased(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals(0.6, new JSONArray((String) recording.getParameters()[0]).getDouble(0));
    }

    @Test public void getText() {
        SplitPane splitPane = (SplitPane) getPrimaryStage().getScene().getRoot().lookup(".split-pane");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXSplitPane rfxSplitPane = new RFXSplitPane(splitPane, null, null, lr);
            splitPane.setDividerPosition(0, 0.6);
            rfxSplitPane.mouseReleased(null);
            text.add(rfxSplitPane.getAttribute("text"));
        });
        new Wait("Waiting for split pane text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[0.6,0.6008064516129032]", text.get(0));
    }

    @Override protected Pane getMainPane() {
        return new SplitPaneSample();
    }
}
