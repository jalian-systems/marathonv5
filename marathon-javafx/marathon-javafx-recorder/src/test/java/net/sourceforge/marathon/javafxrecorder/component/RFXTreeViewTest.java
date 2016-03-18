package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.tree.TreeViewSample;
import javafx.application.Platform;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTreeViewTest extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new TreeViewSample();
    }

    @Test public void select() throws InterruptedException {
        @SuppressWarnings("rawtypes")
        final TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXComponent rTreeView = new RFXTreeView(treeView, null, null, lr);
            treeView.getSelectionModel().select(2);
            rTreeView.focusLost(new RFXTreeView(null, null, null, null));
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("[\"/Root node/Child Node 2\"]", select.getParameters()[0]);
    }

    @Test public void select_multiple() throws InterruptedException {
        @SuppressWarnings("rawtypes")
        final TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXComponent rTreeView = new RFXTreeView(treeView, null, null, lr);
            MultipleSelectionModel<?> selectionModel = treeView.getSelectionModel();
            selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
            treeView.getSelectionModel().select(1);
            treeView.getSelectionModel().select(2);
            rTreeView.focusLost(new RFXTreeView(null, null, null, null));
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("[\"/Root node/Child Node 1\",\"/Root node/Child Node 2\"]", select.getParameters()[0]);
    }

    @Test public void select_none() throws InterruptedException {
        @SuppressWarnings("rawtypes")
        final TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXComponent rTreeView = new RFXTreeView(treeView, null, null, lr);
            treeView.getSelectionModel().clearSelection();
            rTreeView.focusLost(new RFXTreeView(null, null, null, null));
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("[]", select.getParameters()[0]);
    }

}
