package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxTreeViewSample;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTreeViewCheckBoxTreeCellTest extends RFXComponentTest {

    @Test public void select() {
        TreeView<?> treeView = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Point2D point = getPoint(treeView, 1);
                RFXTreeView rfxListView = new RFXTreeView(treeView, null, point, lr);
                rfxListView.focusGained(rfxListView);
                CheckBoxTreeItem<?> treeItem = (CheckBoxTreeItem<?>) treeView.getTreeItem(1);
                treeItem.setSelected(true);
                rfxListView.focusLost(rfxListView);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Child Node 1:checked", recording.getParameters()[0]);
    }

    @Test public void selectSelectedTreeItemCheckBox() {
        TreeView<?> treeView = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Point2D point = getPoint(treeView, 1);
                RFXTreeView rfxListView = new RFXTreeView(treeView, null, point, lr);
                CheckBoxTreeItem<?> treeItem = (CheckBoxTreeItem<?>) treeView.getTreeItem(1);
                treeItem.setSelected(true);
                rfxListView.focusGained(rfxListView);
                treeItem.setSelected(false);
                rfxListView.focusLost(rfxListView);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Child Node 1:unchecked", recording.getParameters()[0]);
    }

    @Test public void assertContent() {
        @SuppressWarnings("rawtypes")
        TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        final Object[] content = new Object[] { null };
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXTreeView rTreeView = new RFXTreeView(treeView, null, null, null);
                content[0] = rTreeView.getContent();
            }
        });
        new Wait("Waiting for contents.") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals(
                "[[\"Root node:unchecked\",\"Child Node 1:unchecked\",\"Child Node 2:unchecked\",\"Child Node 3:unchecked\"]]",
                a.toString());
    }

    @Override protected Pane getMainPane() {
        return new CheckBoxTreeViewSample();
    }
}
