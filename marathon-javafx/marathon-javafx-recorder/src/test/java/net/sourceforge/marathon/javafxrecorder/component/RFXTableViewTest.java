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

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.table.TableSample;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTableViewTest extends RFXComponentTest {

    @Test public void selectNoRows() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXTableView rfxTableView = new RFXTableView(tableView, null, null, lr);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("", recording.getParameters()[0]);
    }

    @Test public void selectNoCells() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setCellSelectionEnabled(true);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, null, lr);
            tableView.getSelectionModel().clearSelection();
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("", recording.getParameters()[0]);
    }

    @Test public void selectARow() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            tableView.getSelectionModel().select(1);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"rows\":[1]}", recording.getParameters()[0]);
    }

    @Test public void selectMulpitleRows() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            tableView.getSelectionModel().selectIndices(1, 3);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"rows\":[1,3]}", recording.getParameters()[0]);
    }

    @Test public void selectAllRows() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            tableView.getSelectionModel().selectRange(0, 5);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("all", recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void selectACell() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setCellSelectionEnabled(true);
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            @SuppressWarnings("rawtypes")
            TableColumn column = getTableColumnAt(tableView, 1);
            tableView.getSelectionModel().select(1, column);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"cells\":[[\"1\",\"Last\"]]}", recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void selectCell() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setCellSelectionEnabled(true);
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            @SuppressWarnings("rawtypes")
            TableColumn column = getTableColumnAt(tableView, 1);
            tableView.getSelectionModel().select(1, column);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"cells\":[[\"1\",\"Last\"]]}", recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void selectMultipleCells() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setCellSelectionEnabled(true);
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            @SuppressWarnings("rawtypes")
            TableColumn column = getTableColumnAt(tableView, 1);
            tableView.getSelectionModel().select(1, column);
            tableView.getSelectionModel().select(2, column);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"cells\":[[\"1\",\"Last\"],[\"2\",\"Last\"]]}", recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void selectAllCells() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setCellSelectionEnabled(true);
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            tableView.getSelectionModel().selectRange(0, getTableColumnAt(tableView, 0), 5, getTableColumnAt(tableView, 2));
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("all", recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void getText() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        List<Object> text = new ArrayList<>();
        Platform.runLater(() -> {
            tableView.getSelectionModel().setCellSelectionEnabled(true);
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            @SuppressWarnings("rawtypes")
            TableColumn column = getTableColumnAt(tableView, 1);
            tableView.getSelectionModel().select(1, column);
            rfxTableView.focusLost(null);
            text.add(rfxTableView.getAttribute("text"));
        });
        new Wait("Waiting for table text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("{\"cells\":[[\"1\",\"Last\"]]}", text.get(0));
    }

    @Test public void getContent() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        final Object[] content = new Object[] { null };
        Platform.runLater(() -> {
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, null);
            content[0] = rfxTableView.getContent();
        });
        new Wait("Wating for contents") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Jacob\",\"Smith\",\"jacob.smith@example.com\"],[\"Isabella\",\"Johnson\",\"isabella.johnson@example.com\"],[\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\"Michael\",\"Brown\",\"michael.brown@example.com\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }

    @SuppressWarnings("rawtypes") private TableColumn getTableColumnAt(TableView<?> tableView, int i) {
        return tableView.getColumns().get(i);
    }

    @Override protected Pane getMainPane() {
        return new TableSample();
    }
}
