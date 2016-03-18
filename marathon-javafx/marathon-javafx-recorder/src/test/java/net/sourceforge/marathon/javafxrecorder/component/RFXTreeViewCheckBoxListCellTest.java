package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxTreeViewSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTreeViewCheckBoxListCellTest extends RFXComponentTest {

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
        AssertJUnit.assertEquals("checked", recording.getParameters()[0]);
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
        AssertJUnit.assertEquals("unchecked", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new CheckBoxTreeViewSample();
    }
}
