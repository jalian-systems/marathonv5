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
package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TreeTableSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTreeTableViewElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeTable;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        treeTable = driver.findElementByTagName("tree-table-view");
    }

    @Test public void selectNoRow() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            treeTableNode.getSelectionModel().select(0);
            treeTable.marathon_select("");
        });
        new Wait("Waiting for no selection") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedIndices().size() == 0;
            }
        };
    }

    @Test public void selectAllRows() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            treeTableNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            treeTable.marathon_select("all");
        });
        new Wait("Waiting for all rows to be selected.") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedIndices().size() == treeTableNode.getExpandedItemCount();
            }
        };
    }

    @Test public void selectARow() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            treeTable.marathon_select("{\"rows\":[\"/Sales Department/Emma Jones\"]}");
        });
        new Wait("Waiting for row to be selected.") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedIndex() == 2;
            }
        };
    }

    @Test public void selectMultipleRows() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            treeTableNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            treeTable.marathon_select("{\"rows\":[\"/Sales Department/Emma Jones\",\"/Sales Department/Anna Black\"]}");
        });
        new Wait("Waiting for rows to be selected.") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedIndices().size() > 1;
            }
        };
    }

    @SuppressWarnings("unchecked") @Test public void selectNoCell() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            TreeTableViewSelectionModel<?> selectionModel = treeTableNode.getSelectionModel();
            selectionModel.setCellSelectionEnabled(true);
            selectionModel.select(0, getTreeTableColumnAt(treeTableNode, 0));
            treeTable.marathon_select("");
        });
        new Wait("Waiting for no cell selection") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedCells().size() == 0;
            }
        };
    }

    @SuppressWarnings("unchecked") @Test public void selectAllCells() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            TreeTableViewSelectionModel<?> selectionModel = treeTableNode.getSelectionModel();
            selectionModel.setCellSelectionEnabled(true);
            selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
            selectionModel.selectRange(0, getTreeTableColumnAt(treeTableNode, 0), treeTableNode.getExpandedItemCount() - 1,
                    getTreeTableColumnAt(treeTableNode, 1));
            treeTable.marathon_select("all");
        });
        new Wait("Waiting for all cells to be selected") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedCells()
                        .size() == (treeTableNode.getExpandedItemCount() * treeTableNode.getColumns().size());
            }
        };
    }

    @Test public void selectACell() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            TreeTableViewSelectionModel<?> selectionModel = treeTableNode.getSelectionModel();
            selectionModel.setCellSelectionEnabled(true);
            treeTable.marathon_select("{\"cells\":[[\"/Sales Department/Ethan Williams\",\"Employee\"]]}");
        });
        new Wait("Waiting for cell to be selected") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedCells().size() == 1;
            }
        };
    }

    @Test public void selectMultipleCells() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        Platform.runLater(() -> {
            TreeTableViewSelectionModel<?> selectionModel = treeTableNode.getSelectionModel();
            selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
            selectionModel.setCellSelectionEnabled(true);
            treeTable.marathon_select(
                    "{\"cells\":[[\"/Sales Department/Ethan Williams\",\"Employee\"],[\"/Sales Department/Michael Brown\",\"Email\"]]}");
        });
        new Wait("Waiting for cells to be selected") {
            @Override public boolean until() {
                return treeTableNode.getSelectionModel().getSelectedCells().size() == 2;
            }
        };
    }

    @Test public void selectPseudoElement() {
        List<Object> columnName = new ArrayList<>();
        Platform.runLater(() -> {
            IJavaFXElement e = treeTable.findElementByCssSelector(".::mnth-cell(3,2)");
            columnName.add(e.getAttribute("viewColumnName"));
        });
        new Wait("Waiting for column name") {
            @Override public boolean until() {
                return columnName.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Email", columnName.get(0));
    }

    @Test public void getText() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            treeTableNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            treeTable.marathon_select("{\"rows\":[\"/Sales Department/Emma Jones\",\"/Sales Department/Anna Black\"]}");
            text.add(treeTable.getAttribute("text"));
        });
        new Wait("Waiting for tree table text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("{\"rows\":[\"/Sales Department/Emma Jones\",\"/Sales Department/Anna Black\"]}", text.get(0));
    }

    @Test public void assertContent() {
        String expected = "[[\"Sales Department\",\"\"],[\"Ethan Williams\",\"ethan.williams@example.com\"],[\"Emma Jones\",\"emma.jones@example.com\"],[\"Michael Brown\",\"michael.brown@example.com\"],[\"Anna Black\",\"anna.black@example.com\"],[\"Rodger York\",\"roger.york@example.com\"],[\"Susan Collins\",\"susan.collins@example.com\"]]";
        AssertJUnit.assertEquals(expected, treeTable.getAttribute("content"));
    }

    @SuppressWarnings("rawtypes") private TreeTableColumn getTreeTableColumnAt(TreeTableView<?> treeTableView, int index) {
        return treeTableView.getColumns().get(index);
    }

    @Override protected Pane getMainPane() {
        return new TreeTableSample();
    }

}
