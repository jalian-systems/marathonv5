/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TextFieldTreeViewSample;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTreeViewTextFieldTreeCellTest extends RFXComponentTest {

    @Test public void select() {
        @SuppressWarnings("unchecked")
        TreeView<String> treeView = (TreeView<String>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            TextFieldTreeCell<String> cell = (TextFieldTreeCell<String>) getCellAt(treeView, 2);
            Point2D point = getPoint(treeView, 2);
            RFXTreeView rfxTreeView = new RFXTreeView(treeView, null, point, lr);
            rfxTreeView.focusGained(rfxTreeView);
            cell.startEdit();
            cell.updateItem("Child node 4 Modified", false);
            cell.commitEdit("Child node 4 Modified");
            rfxTreeView.focusLost(rfxTreeView);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Child node 4 Modified", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new TextFieldTreeViewSample();
    }
}
