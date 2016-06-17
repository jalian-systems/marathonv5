/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.tree.TreeViewSample;
import javafx.application.Platform;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
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
            RFXTreeView rTreeView = new RFXTreeView(treeView, null, null, lr);
            treeView.getSelectionModel().select(2);
            rTreeView.focusLost(new RFXTreeView(null, null, null, null));
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("[\"/Root node/Child Node 2\"]", select.getParameters()[0]);
    }

    @Test public void getText() {
        @SuppressWarnings("rawtypes")
        TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXTreeView rTreeView = new RFXTreeView(treeView, null, null, lr);
                treeView.getSelectionModel().select(2);
                rTreeView.focusLost(new RFXTreeView(null, null, null, null));
                text.add(rTreeView.getAttribute("text"));
            }
        });
        new Wait("Waiting for tree text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[\"/Root node/Child Node 2\"]", text.get(0));
    }

    @Test public void getTextForMultipleSelection() {
        @SuppressWarnings("rawtypes")
        TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXTreeView rTreeView = new RFXTreeView(treeView, null, null, lr);
                @SuppressWarnings("rawtypes")
                MultipleSelectionModel selectionModel = treeView.getSelectionModel();
                selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
                selectionModel.selectIndices(2, 3);
                rTreeView.focusLost(new RFXTreeView(null, null, null, null));
                text.add(rTreeView.getAttribute("text"));
            }
        });
        new Wait("Waiting for tree text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[\"/Root node/Child Node 2\",\"/Root node/Child Node 3\"]", text.get(0));
    }

    @Test public void select_multiple() throws InterruptedException {
        @SuppressWarnings("rawtypes")
        final TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            RFXTreeView rTreeView = new RFXTreeView(treeView, null, null, lr);
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
            RFXTreeView rTreeView = new RFXTreeView(treeView, null, null, lr);
            treeView.getSelectionModel().clearSelection();
            rTreeView.focusLost(new RFXTreeView(null, null, null, null));
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("[]", select.getParameters()[0]);
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
        Assert.assertEquals("[[\"Root node\",\"Child Node 1\",\"Child Node 2\",\"Child Node 3\"]]", a.toString());
    }
}
