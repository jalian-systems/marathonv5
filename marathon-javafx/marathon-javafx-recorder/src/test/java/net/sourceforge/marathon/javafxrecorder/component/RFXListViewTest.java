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

import ensemble.samples.controls.list.SimpleListViewSample;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXListViewTest extends RFXComponentTest {

    @Test public void selectNoSelection() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[]", recording.getParameters()[0]);
    }

    @Test public void selectSingleItemSelection() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getSelectionModel().select(2);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Long Row 3\"]", recording.getParameters()[0]);
    }

    @Test public void getText() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getSelectionModel().select(2);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
                text.add(rfxListView.getAttribute("text"));
            }
        });
        new Wait("Waiting for list text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[\"Long Row 3\"]", text.get(0));
    }

    @Test public void getTextForMultipleSelection() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                MultipleSelectionModel<?> selectionModel = listView.getSelectionModel();
                selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
                selectionModel.selectIndices(2, 8);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
                text.add(rfxListView.getAttribute("text"));
            }
        });
        new Wait("Waiting for list text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[\"Long Row 3\",\"Row 9\"]", text.get(0));
    }

    @Test public void selectMultipleItemSelection() {
        ListView<?> listView = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                MultipleSelectionModel<?> selectionModel = listView.getSelectionModel();
                selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
                selectionModel.selectIndices(2, 6);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Long Row 3\",\"Row 7\"]", recording.getParameters()[0]);
    }

    @Test public void selectSpecialItemSelection() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getItems().add(7, " Special Characters ([],)");
                listView.getSelectionModel().select(7);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\" Special Characters ([],)\"]", recording.getParameters()[0]);
    }

    @Test public void selectDuplicate() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getSelectionModel().select(8);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Row 9\"]", recording.getParameters()[0]);

        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getItems().add(9, "Row 9");
                listView.getSelectionModel().clearAndSelect(9);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        recordings = lr.waitAndGetRecordings(2);
        recording = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Row 9(1)\"]", recording.getParameters()[0]);
    }

    @Test public void listMultipleDuplicates() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getSelectionModel().select(8);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Row 9\"]", recording.getParameters()[0]);

        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getItems().add(9, "Row 9");
                listView.getItems().add(10, "Row 9");
                listView.getSelectionModel().clearAndSelect(10);
                RFXListView rfxListView = new RFXListView(listView, null, null, lr);
                rfxListView.focusLost(new RFXListView(null, null, null, lr));
            }
        });
        recordings = lr.waitAndGetRecordings(2);
        recording = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Row 9(2)\"]", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new SimpleListViewSample();
    }
}
