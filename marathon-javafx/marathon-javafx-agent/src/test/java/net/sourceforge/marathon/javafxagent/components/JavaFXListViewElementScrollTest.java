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

import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SimpleListViewScrollSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXListViewElementScrollTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement listView;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        listView = driver.findElementByTagName("list-view");
    }

    @Test
    public void scrollToItem() throws Throwable {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Platform.runLater(() -> listView.marathon_select("[\"Row 23\"]"));
        new Wait("Waiting for the point to be in viewport") {
            @Override
            public boolean until() {
                return getPoint(listViewNode, 22) != null;
            }
        };
        Point2D point = getPoint(listViewNode, 22);
        AssertJUnit.assertTrue(listViewNode.getBoundsInLocal().contains(point));
    }

    public Point2D getPoint(ListView<?> listView, int index) {
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

    @Test
    public void selectForMultipleItems() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Platform.runLater(() -> listView.marathon_select("[\"Row 2\",\"Row 25\"]"));
        new Wait("Waiting for the point to be in viewport") {
            @Override
            public boolean until() {
                return getPoint(listViewNode, 24) != null;
            }
        };
        Point2D point = getPoint(listViewNode, 24);
        AssertJUnit.assertTrue(listViewNode.getBoundsInLocal().contains(point));
    }

    @Test
    public void clickNthelement() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        JavaFXListViewItemElement item = (JavaFXListViewItemElement) listView.findElementByCssSelector(".::nth-item(23)");
        Platform.runLater(() -> {
            item.getPseudoComponent();
        });
        new Wait("Waiting for the point to be in viewport") {
            @Override
            public boolean until() {
                Point2D point = getPoint(listViewNode, 22);
                return listViewNode.getBoundsInLocal().contains(point);
            }
        };
    }

    @Override
    protected Pane getMainPane() {
        return new SimpleListViewScrollSample();
    }
}
