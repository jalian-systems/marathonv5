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

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ComboBoxListViewSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXListViewComboBoxListCell extends RFXComponentTest {

    @Test
    public void select() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            ComboBoxListCell<String> cell = (ComboBoxListCell<String>) getCellAt(listView, 3);
            Point2D point = getPoint(listView, 3);
            RFXListView rfxListView = new RFXListView(listView, null, point, lr);
            rfxListView.focusGained(rfxListView);
            cell.startEdit();
            cell.updateItem("Option 3", false);
            cell.commitEdit("Option 3");
            rfxListView.focusLost(rfxListView);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Option 3", recording.getParameters()[0]);
    }

    @Test
    public void selectEditable() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            ComboBoxListCell<String> cell = (ComboBoxListCell<String>) getCellAt(listView, 3);
            cell.setComboBoxEditable(true);
            Point2D point = getPoint(listView, 3);
            RFXListView rfxListView = new RFXListView(listView, null, point, lr);
            rfxListView.focusGained(rfxListView);
            cell.startEdit();
            cell.updateItem("Option 5", false);
            cell.commitEdit("Option 5");
            rfxListView.focusLost(rfxListView);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Option 5", recording.getParameters()[0]);
    }

    @Override
    protected Pane getMainPane() {
        return new ComboBoxListViewSample();
    }
}
