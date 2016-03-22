package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ChoiceBoxTableViewSample;
import net.sourceforge.marathon.javafx.tests.ChoiceBoxTableViewSample.Person;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTableViewChoiceBoxTableCell extends RFXComponentTest {

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Test public void select() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            Point2D point = getPoint(tableView, 2, 1);
            ChoiceBoxTableCell cell = (ChoiceBoxTableCell) getCellAt(tableView, 1, 2);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            cell.startEdit();
            tableView.edit(1, (TableColumn) tableView.getColumns().get(2));
            Person person = (Person) tableView.getItems().get(1);
            person.setLastName("Jones");
            cell.commitEdit("Jones");
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Jones", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxTableViewSample();
    }
}
