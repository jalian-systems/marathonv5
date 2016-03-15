package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.json.JSONArray;
import org.testng.Assert;
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
                rfxListView.focusLost(null);
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
                rfxListView.focusLost(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Long Row 3\"]", recording.getParameters()[0]);
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
                rfxListView.focusLost(null);
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
                rfxListView.focusLost(null);
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
                rfxListView.focusLost(null);
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
                rfxListView.focusLost(null);
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
                rfxListView.focusLost(null);
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
                rfxListView.focusLost(null);
            }
        });
        recordings = lr.waitAndGetRecordings(2);
        recording = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("[\"Row 9(2)\"]", recording.getParameters()[0]);
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
        new Wait("Waiting for contens.") {

            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals(
                "[[\"Row 1\",\"Row 2\",\"Long Row 3\",\"Row 4\",\"Row 5\",\"Row 6\",\"Row 7\",\"Row 8\",\"Row 9\",\"Row 10\",\"Row 11\",\"Row 12\",\"Row 13\",\"Row 14\",\"Row 15\",\"Row 16\",\"Row 17\",\"Row 18\",\"Row 19\",\"Row 20\"]]",
                a.toString());
    }

    @Test public void assertContentDuplicates() {
        @SuppressWarnings("unchecked")
        ListView<String> listView = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        final Object[] content = new Object[] { null };
        Platform.runLater(new Runnable() {
            @Override public void run() {
                listView.getItems().add(9, "Row 9");
                RFXListView rfxListView = new RFXListView(listView, null, null, new LoggingRecorder());
                content[0] = rfxListView.getContent();
            }
        });
        new Wait("Waiting for contens.") {

            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals(
                "[[\"Row 1\",\"Row 2\",\"Long Row 3\",\"Row 4\",\"Row 5\",\"Row 6\",\"Row 7\",\"Row 8\",\"Row 9\",\"Row 9(1)\",\"Row 10\",\"Row 11\",\"Row 12\",\"Row 13\",\"Row 14\",\"Row 15\",\"Row 16\",\"Row 17\",\"Row 18\",\"Row 19\",\"Row 20\"]]",
                a.toString());
    }

    @Override protected Pane getMainPane() {
        return new SimpleListViewSample();
    }
}
