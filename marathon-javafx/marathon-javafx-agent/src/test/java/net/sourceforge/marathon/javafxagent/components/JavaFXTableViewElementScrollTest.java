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
import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.sourceforge.marathon.javafx.tests.TableScrollSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTableViewElementScrollTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement tableView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        tableView = driver.findElementByTagName("table-view");
    }

    @Test public void scrollToRow() throws Throwable {
        Stage primaryStage = getPrimaryStage();
        primaryStage.setWidth(250);
        primaryStage.setHeight(250);
        TableView<?> tableViewNode = (TableView<?>) primaryStage.getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableView.marathon_select("{\"rows\":[10]}");
        });
        new Wait("Wating for rows to be select.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedIndex() == 10;
            }
        };
        new Wait("Waiting for the point to be in viewport") {
            @Override public boolean until() {
                Point2D point = getPoint(tableViewNode, 1, 10);
                return tableViewNode.getBoundsInLocal().contains(point);
            }
        };
    }

    @Test public void scrollMultipleRows() {
        Stage primaryStage = getPrimaryStage();
        primaryStage.setWidth(250);
        primaryStage.setHeight(250);
        TableView<?> tableViewNode = (TableView<?>) primaryStage.getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableView.marathon_select("{\"rows\":[2,9]}");
        });
        new Wait("Wating for rows to be select.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedIndices().size() > 1;
            }
        };
        new Wait("Waiting for the point to be in viewport") {
            @Override public boolean until() {
                Point2D point = getPoint(tableViewNode, 2, 9);
                return tableViewNode.getBoundsInLocal().contains(point);
            }
        };
    }

    @Test public void scrollToCell() throws Throwable {
        Stage primaryStage = getPrimaryStage();
        primaryStage.setWidth(250);
        primaryStage.setHeight(250);
        TableView<?> tableViewNode = (TableView<?>) primaryStage.getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setCellSelectionEnabled(true);
            tableView.marathon_select("{\"cells\":[[\"10\",\"Email\"]]}");
        });
        new Wait("Waiting for the point to be in viewport") {
            @Override public boolean until() {
                return getPoint(tableViewNode, 2, 10) != null;
            }
        };
        Point2D point = getPoint(tableViewNode, 2, 10);
        AssertJUnit.assertTrue(tableViewNode.getBoundsInLocal().contains(point));
    }

    @Test public void scrollTomnthCell() {
        Stage primaryStage = getPrimaryStage();
        primaryStage.setWidth(250);
        primaryStage.setHeight(250);
        TableView<?> tableViewNode = (TableView<?>) primaryStage.getScene().getRoot().lookup(".table-view");
        List<String> columnName = new ArrayList<>();
        Platform.runLater(() -> {
            JavaFXTableCellElement cell = (JavaFXTableCellElement) tableView.findElementByCssSelector(".::mnth-cell(7,3)");
            cell.getPseudoComponent();
            columnName.add(cell.getAttribute("viewColumnName"));
        });
        new Wait("Wating cells column name.") {
            @Override public boolean until() {
                return columnName.size() > 0;
            }
        };
        new Wait("Waiting for the point to be in viewport") {
            @Override public boolean until() {
                return getPoint(tableViewNode, 2, 7) != null;
            }
        };
        Point2D point = getPoint(tableViewNode, 2, 7);
        AssertJUnit.assertTrue(tableViewNode.getBoundsInLocal().contains(point));
        AssertJUnit.assertEquals("Email", columnName.get(0));
    }

    public Point2D getPoint(TableView<?> tableView, int columnIndex, int rowIndex) {
        Set<Node> tableRowCell = tableView.lookupAll(".table-row-cell");
        TableRow<?> row = null;
        for (Node tableRow : tableRowCell) {
            TableRow<?> r = (TableRow<?>) tableRow;
            if (r.getIndex() == rowIndex) {
                row = r;
                break;
            }
        }
        Set<Node> cells = row.lookupAll(".table-cell");
        for (Node node : cells) {
            TableCell<?, ?> cell = (TableCell<?, ?>) node;
            if (tableView.getColumns().indexOf(cell.getTableColumn()) == columnIndex) {
                Bounds bounds = cell.getBoundsInParent();
                Point2D localToParent = cell.localToParent(bounds.getWidth() / 2, bounds.getHeight() / 2);
                Point2D rowLocal = row.localToScene(localToParent, true);
                return rowLocal;
            }
        }
        return null;
    }

    @Override protected Pane getMainPane() {
        return new TableScrollSample();
    }
}
