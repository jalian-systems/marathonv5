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

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample.Item;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXListViewCheckBoxListCellTest extends RFXComponentTest {

    @Test public void selectListItemCheckBox() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Point2D point = getPoint(listView, 5);
                RFXListView rfxListView = new RFXListView(listView, null, point, lr);
                rfxListView.focusGained(rfxListView);
                Item x = (Item) listView.getItems().get(5);
                x.setOn(true);
                rfxListView.focusLost(rfxListView);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Item 6:checked", recording.getParameters()[0]);
    }

    @Test public void selectSelectedListItemCheckBox() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Point2D point = getPoint(listView, 6);
                RFXListView rfxListView = new RFXListView(listView, null, point, lr);
                Item x = (Item) listView.getItems().get(6);
                x.setOn(true);
                rfxListView.focusGained(rfxListView);
                x.setOn(false);
                rfxListView.focusLost(rfxListView);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Item 7:unchecked", recording.getParameters()[0]);
    }

    @Test public void assertContent() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        final Object[] content = new Object[] { null };
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXListView rfxListView = new RFXListView(listView, null, null, new LoggingRecorder());
                content[0] = rfxListView.getContent();
            }
        });
        new Wait("Waiting for contents.") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals(
                "[[\"Item 1:unchecked\",\"Item 2:unchecked\",\"Item 3:unchecked\",\"Item 4:unchecked\",\"Item 5:unchecked\",\"Item 6:unchecked\",\"Item 7:unchecked\",\"Item 8:unchecked\",\"Item 9:unchecked\",\"Item 10:unchecked\",\"Item 11:unchecked\",\"Item 12:unchecked\",\"Item 13:unchecked\",\"Item 14:unchecked\",\"Item 15:unchecked\",\"Item 16:unchecked\",\"Item 17:unchecked\",\"Item 18:unchecked\",\"Item 19:unchecked\",\"Item 20:unchecked\"]]",
                a.toString());
    }

    @Override protected Pane getMainPane() {
        return new CheckBoxListViewSample();
    }
}
