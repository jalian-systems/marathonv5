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

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TreeViewSample1;
import net.sourceforge.marathon.javafxagent.Wait;

public class RFXTreeViewTest2 extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new TreeViewSample1();
    }

    @SuppressWarnings("rawtypes") @Test public void assertContent() {
        TreeView treeView = (TreeView) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        int expandedItemCount = treeView.getExpandedItemCount();
        TreeItem treeItem = treeView.getTreeItem(expandedItemCount - 1);
        treeItem.setExpanded(true);
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
                "[[\"Root node\",\"Child Node 1\",\"Child Node 2\",\"Child Node 3\",\"Child Node 4\",\"Child Node 5\",\"Child Node 6\",\"Child Node 7\",\"Child Node 8\",\"Child Node 9\",\"Child Node 10\",\"Child Node 11\",\"Child Node 12\",\"Child Node 13\",\"Child Node 14\",\"Child Node 15\",\"Child Node 16\",\"Child Node 17\",\"Child Node 18\",\"Child Node 19\",\"Child Node 20\",\"Child Node 21\",\"Child Node 22\",\"Child Node 23\",\"Child Node 24\"]]",
                a.toString());
    }
}
