package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.table.TableCellFactorySample;
import ensemble.samples.controls.table.TableCellFactorySample.EditingCell;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTableViewTextFieldTableCellTest extends RFXComponentTest {

    @Test public void select() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            Point2D point = getPoint(tableView, 1, 1);
            TableCellFactorySample.EditingCell tf = (EditingCell) getCellAt(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            tf.startEdit();
            tf.updateItem("Cell Modified", false);
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Cell Modified", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new TableCellFactorySample();
    }
}
