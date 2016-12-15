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

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TreeTableSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTreeTableViewTest extends RFXComponentTest {

    @Test public void selectNoRows() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, null, lr);
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("", recording.getParameters()[0]);
    }

    @Test public void selectARow() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, null, lr);
            treeTableView.getSelectionModel().select(2);
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"rows\":[\"/Sales Department/Emma Jones\"]}", recording.getParameters()[0]);
    }

    @Test public void selectMulptipleRows() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, null, lr);
            treeTableView.getSelectionModel().selectIndices(2, 4);
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"rows\":[\"/Sales Department/Emma Jones\",\"/Sales Department/Anna Black\"]}",
                recording.getParameters()[0]);
    }

    @Test public void selectAllRows() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, null, lr);
            int count = treeTableView.getExpandedItemCount();
            for (int i = 0; i < count; i++) {
                treeTableView.getSelectionModel().select(i);
            }
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("all", recording.getParameters()[0]);
    }

    @Test public void selectNoCell() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            treeTableView.getSelectionModel().setCellSelectionEnabled(true);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, null, lr);
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("", recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void selectMultipleCells() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            TreeTableViewSelectionModel<?> selectionModel = treeTableView.getSelectionModel();
            selectionModel.setCellSelectionEnabled(true);
            selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
            Point2D point = getPoint(treeTableView, 1, 0);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, point, lr);
            rfxTreeTableView.focusGained(null);
            @SuppressWarnings("rawtypes")
            TreeTableColumn column = getTreeTableColumnAt(treeTableView, 0);
            selectionModel.select(1, column);
            selectionModel.select(3, getTreeTableColumnAt(treeTableView, 1));
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals(
                "{\"cells\":[[\"/Sales Department/Ethan Williams\",\"Employee\"],[\"/Sales Department/Michael Brown\",\"Email\"]]}",
                recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void selectACell() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            TreeTableViewSelectionModel<?> selectionModel = treeTableView.getSelectionModel();
            selectionModel.setCellSelectionEnabled(true);
            Point2D point = getPoint(treeTableView, 1, 0);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, point, lr);
            rfxTreeTableView.focusGained(null);
            @SuppressWarnings("rawtypes")
            TreeTableColumn column = getTreeTableColumnAt(treeTableView, 0);
            selectionModel.select(1, column);
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("{\"cells\":[[\"/Sales Department/Ethan Williams\",\"Employee\"]]}", recording.getParameters()[0]);
    }

    @SuppressWarnings("unchecked") @Test public void selectAllCells() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, null, lr);
            int count = treeTableView.getExpandedItemCount();
            treeTableView.getSelectionModel().selectRange(0, getTreeTableColumnAt(treeTableView, 0), count - 1,
                    getTreeTableColumnAt(treeTableView, 1));
            rfxTreeTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("all", recording.getParameters()[0]);
    }

    @Test public void getText() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, null, lr);
            treeTableView.getSelectionModel().select(2);
            rfxTreeTableView.focusLost(null);
            text.add(rfxTreeTableView.getAttribute("text"));
        });
        new Wait("Waiting for tree table view text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("{\"rows\":[\"/Sales Department/Emma Jones\"]}", text.get(0));
    }

    @Test public void getContent() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        final Object[] content = new Object[] { null };
        Platform.runLater(() -> {
            Point2D point = getPoint(treeTableView, 1, 1);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, point, null);
            content[0] = rfxTreeTableView.getContent();
        });
        new Wait("Wating for contents") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Sales Department\",\"\"],[\"Ethan Williams\",\"ethan.williams@example.com\"],[\"Emma Jones\",\"emma.jones@example.com\"],[\"Michael Brown\",\"michael.brown@example.com\"],[\"Anna Black\",\"anna.black@example.com\"],[\"Rodger York\",\"roger.york@example.com\"],[\"Susan Collins\",\"susan.collins@example.com\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }

    @SuppressWarnings("rawtypes") private TreeTableColumn getTreeTableColumnAt(TreeTableView<?> treeTableView, int index) {
        return treeTableView.getColumns().get(index);
    }

    @Override protected Pane getMainPane() {
        return new TreeTableSample();
    }
}
