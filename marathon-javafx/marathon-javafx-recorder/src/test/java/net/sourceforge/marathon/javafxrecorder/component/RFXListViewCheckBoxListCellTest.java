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
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample.Item;
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
        AssertJUnit.assertEquals("checked", recording.getParameters()[0]);
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
        AssertJUnit.assertEquals("unchecked", recording.getParameters()[0]);
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
        return new CheckBoxListViewSample();
    }
}
