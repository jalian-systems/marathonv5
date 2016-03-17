package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ChoiceBoxListViewSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXListViewChoiceBoxListCell extends RFXComponentTest {

    @Test public void select() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            ChoiceBoxListCell<String> cell = (ChoiceBoxListCell<String>) getCellAt(listView, 3);
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

    @Override protected Pane getMainPane() {
        return new ChoiceBoxListViewSample();
    }
}
