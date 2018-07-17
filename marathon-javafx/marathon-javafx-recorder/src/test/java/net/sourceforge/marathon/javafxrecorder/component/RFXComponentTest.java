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

import java.util.Set;

import org.testng.annotations.BeforeMethod;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.sourceforge.marathon.javafxagent.Wait;

public abstract class RFXComponentTest {

    public static class ApplicationHelper extends Application {

        public static void startApplication() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Application.launch(ApplicationHelper.class);
                }
            }).start();
        }

        private Stage primaryStage;

        @Override
        public void start(Stage primaryStage) throws Exception {
            this.primaryStage = primaryStage;
            RFXComponentTest.applicationHelper = this;
        }

        public void startGUI(Pane pane) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    primaryStage.hide();
                    primaryStage.setScene(new Scene(pane));
                    primaryStage.sizeToScene();
                    primaryStage.show();
                }
            });
            try {
                new Wait("Waiting for applicationHelper to be initialized") {
                    @Override
                    public boolean until() {
                        try {
                            return primaryStage.getScene().getRoot() == pane;
                        } catch (Throwable t) {
                            return false;
                        }
                    }
                };
            } catch (Throwable t) {
            }
        }

        public Stage getPrimaryStage() {
            return primaryStage;
        }
    }

    private static ApplicationHelper applicationHelper;

    public RFXComponentTest() {
    }

    @BeforeMethod
    public void startGUI() throws Throwable {
        if (applicationHelper == null) {
            ApplicationHelper.startApplication();
        }
        new Wait("Waiting for applicationHelper to be initialized") {
            @Override
            public boolean until() {
                return applicationHelper != null;
            }
        };
        if (applicationHelper == null) {
            throw new RuntimeException("Application Helper = null");
        }
        applicationHelper.startGUI(getMainPane());
        try {
            new Wait() {
                @Override
                public boolean until() {
                    return applicationHelper.getPrimaryStage().isShowing();
                }
            }.wait("Waiting for the primary stage to be displayed.", 10000);
        } catch (Throwable t) {
        }
    }

    protected abstract Pane getMainPane();

    public Stage getPrimaryStage() {
        return applicationHelper.getPrimaryStage();
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

    public ListCell<?> getCellAt(ListView<?> listView, Integer index) {
        Set<Node> lookupAll = listView.lookupAll(".list-cell");
        for (Node node : lookupAll) {
            ListCell<?> cell = (ListCell<?>) node;
            if (cell.getIndex() == index) {
                return cell;
            }
        }
        return null;
    }

    public Point2D getPoint(TreeView<?> treeView, int index) {
        Set<Node> cells = treeView.lookupAll(".tree-cell");
        for (Node node : cells) {
            TreeCell<?> cell = (TreeCell<?>) node;
            if (cell.getIndex() == index) {
                Bounds bounds = cell.getBoundsInParent();
                return cell.localToParent(bounds.getWidth() / 2, bounds.getHeight() / 2);
            }
        }
        return null;
    }

    public TreeCell<?> getCellAt(TreeView<?> treeView, int index) {
        Set<Node> lookupAll = treeView.lookupAll(".tree-cell");
        for (Node node : lookupAll) {
            TreeCell<?> cell = (TreeCell<?>) node;
            if (cell.getIndex() == index) {
                return cell;
            }
        }
        return null;
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

    public TableCell<?, ?> getCellAt(TableView<?> tableView, int rowIndex, int columnIndex) {
        Set<Node> tableRowCell = tableView.lookupAll(".table-row-cell");
        TableRow<?> row = null;
        for (Node tableRow : tableRowCell) {
            TableRow<?> r = (TableRow<?>) tableRow;
            if (r.getIndex() == rowIndex) {
                row = r;
                break;
            }
        }
        Set<Node> lookupAll = row.lookupAll(".table-cell");
        for (Node node : lookupAll) {
            TableCell<?, ?> cell = (TableCell<?, ?>) node;
            if (tableView.getColumns().indexOf(cell.getTableColumn()) == columnIndex) {
                return cell;
            }
        }
        return null;
    }

    protected Point2D getPoint(TreeTableView<?> treeTableView, int rowIndex, int columnIndex) {
        Set<Node> treeTableRowCell = treeTableView.lookupAll(".tree-table-row-cell");
        TreeTableRow<?> row = null;
        for (Node tableRow : treeTableRowCell) {
            TreeTableRow<?> r = (TreeTableRow<?>) tableRow;
            if (r.getIndex() == rowIndex) {
                row = r;
                break;
            }
        }
        Set<Node> cells = row.lookupAll(".tree-table-cell");
        for (Node node : cells) {
            TreeTableCell<?, ?> cell = (TreeTableCell<?, ?>) node;
            if (treeTableView.getColumns().indexOf(cell.getTableColumn()) == columnIndex) {
                Bounds bounds = cell.getBoundsInParent();
                Point2D localToParent = cell.localToParent(bounds.getWidth() / 2, bounds.getHeight() / 2);
                Point2D rowLocal = row.localToScene(localToParent, true);
                return rowLocal;
            }
        }
        return null;
    }
}
