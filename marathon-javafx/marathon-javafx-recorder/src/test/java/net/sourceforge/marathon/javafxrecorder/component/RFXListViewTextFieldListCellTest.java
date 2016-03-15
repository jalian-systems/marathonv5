package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;
import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TextFieldListViewSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXListViewTextFieldListCellTest extends RFXComponentTest {

    @Test public void select() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            TextFieldListCell<String> cell = (TextFieldListCell<String>) getCellAt(listView, 3);
            Point2D point = getPoint(listView, 3);
            RFXListView rfxListView = new RFXListView(listView, null, point, lr);
            rfxListView.focusGained(rfxListView);
            cell.startEdit();
            cell.updateItem("Item 4 Modified", false);
            cell.commitEdit("Item 4 Modified");
            rfxListView.focusLost(rfxListView);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Item 4 Modified", recording.getParameters()[0]);
    }

    public ListCell<?> getCellAt(ListView<?> listView, Integer index) {
        Set<Node> lookupAll = listView.lookupAll(".list-cell");
        for (Node node : lookupAll) {
            ListCell<?> cell = (ListCell<?>) node;
            if (cell.getIndex() == index) {
                return cell;
            }
        }
        return null;
    }

    private Point2D getPoint(ListView<?> listView, int index) {
        Set<Node> cells = listView.lookupAll(".list-cell");
        for (Node node : cells) {
            ListCell<?> cell = (ListCell<?>) node;
            if (cell.getIndex() == index) {
                Bounds bounds = cell.getBoundsInParent();
                return cell.localToParent(bounds.getWidth() / 2, bounds.getHeight() / 2);
            }
        }
        return null;
    }

    @Override protected Pane getMainPane() {
        return new TextFieldListViewSample();
    }
}
