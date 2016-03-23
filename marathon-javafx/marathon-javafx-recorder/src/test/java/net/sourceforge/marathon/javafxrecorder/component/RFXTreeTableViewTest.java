package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

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
            for (int i = 0; i < count; i++)
                treeTableView.getSelectionModel().select(i);
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

    @SuppressWarnings("rawtypes") private TreeTableColumn getTreeTableColumnAt(TreeTableView<?> treeTableView, int index) {
        return treeTableView.getColumns().get(index);
    }

    @Override protected Pane getMainPane() {
        return new TreeTableSample();
    }
}
