package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ChoiceBoxListViewSample;
import net.sourceforge.marathon.javafxagent.Wait;
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
        Assert.assertEquals("[[\"Option 1\",\"Option 2\",\"Option 3\",\"Option 4\",\"Option 5\",\"Option 6\"]]", a.toString());
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxListViewSample();
    }
}
