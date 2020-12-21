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
package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TreeViewSample1;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;

public class JavaFXTreeViewElementTest2 extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeView;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        treeView = driver.findElementByTagName("tree-view");
    }

    @Test
    public void assertContent() {
        TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        Platform.runLater(() -> {
            TreeItem<?> treeItem = treeViewNode.getTreeItem(treeViewNode.getExpandedItemCount() - 1);
            treeItem.setExpanded(true);
        });
        String expected = "[/Root node, /Root node/Child Node 1, /Root node/Child Node 2, /Root node/Child Node 3, /Root node/Child Node 3/Child Node 4, /Root node/Child Node 3/Child Node 5, /Root node/Child Node 3/Child Node 6, /Root node/Child Node 3/Child Node 7, /Root node/Child Node 3/Child Node 8, /Root node/Child Node 3/Child Node 9, /Root node/Child Node 3/Child Node 10, /Root node/Child Node 3/Child Node 11, /Root node/Child Node 3/Child Node 12, /Root node/Child Node 3/Child Node 13, /Root node/Child Node 3/Child Node 14, /Root node/Child Node 3/Child Node 15, /Root node/Child Node 3/Child Node 16, /Root node/Child Node 3/Child Node 17, /Root node/Child Node 3/Child Node 18, /Root node/Child Node 3/Child Node 19, /Root node/Child Node 3/Child Node 20, /Root node/Child Node 3/Child Node 21, /Root node/Child Node 3/Child Node 22, /Root node/Child Node 3/Child Node 23, /Root node/Child Node 3/Child Node 24]";

        List<IJavaFXElement> e = treeView.findElementsByCssSelector(".::all-nodes");
        ArrayList<String> actualValue = new ArrayList<String>();
        for (int i = 0; i < e.size(); i++) {
            actualValue.add(e.get(i).getAttribute("text"));
        }
        AssertJUnit.assertEquals(expected, actualValue.toString());
    }

    @Override
    protected Pane getMainPane() {
        return new TreeViewSample1();
    }
}
