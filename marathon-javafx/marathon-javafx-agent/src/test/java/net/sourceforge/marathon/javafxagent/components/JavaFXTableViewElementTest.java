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

import ensemble.samples.controls.table.TableSample;
import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTableViewElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement tableView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        tableView = driver.findElementByTagName("table-view");
    }

    @Test public void selectNoRow() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableView.marathon_select("");
        });
        new Wait("Wating for table deselect.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedIndex() == -1;
            }
        };
    }

    @Test public void selectARow() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableView.marathon_select("{\"rows\":[1]}");
        });
        new Wait("Wating for row to be select.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedIndex() == 1;
            }
        };
    }

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            tableView.marathon_select("{\"rows\":[1]}");
            text.add(tableView.getAttribute("text"));
        });
        new Wait("Wating for table text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("{\"rows\":[1]}", text.get(0));
    }

    @Test public void selectMultipleRows() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableView.marathon_select("{\"rows\":[1,3]}");
        });
        new Wait("Wating for rows to be select.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedIndices().size() > 1;
            }
        };
    }

    @Test public void selectNoCell() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setCellSelectionEnabled(true);
            tableView.marathon_select("");
        });
        new Wait("Wating for table cell deselect.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedCells().size() == 0;
            }
        };
    }

    @Test public void selectACell() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setCellSelectionEnabled(true);
            tableView.marathon_select("{\"cells\":[[\"1\",\"Last\"]]}");
        });
        new Wait("Wating for table cell deselect.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedCells().size() != 0;
            }
        };
    }

    @Test public void selectMultipleCells() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableViewNode.getSelectionModel().setCellSelectionEnabled(true);
            tableView.marathon_select("{\"cells\":[[\"1\",\"Last\"],[\"1\",\"First\"]]}");
        });
        new Wait("Wating for table cell deselect.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedCells().size() > 1;
            }
        };
    }

    @Test public void selectmnthCell() {
        List<String> columnName = new ArrayList<>();
        Platform.runLater(() -> {
            IJavaFXElement cell = tableView.findElementByCssSelector(".::mnth-cell(2,3)");
            columnName.add(cell.getAttribute("viewColumnName"));
        });
        new Wait("Wating cells column name.") {
            @Override public boolean until() {
                return columnName.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Email", columnName.get(0));
    }

    @Test public void allCells() {
        List<Integer> cells = new ArrayList<>();
        Platform.runLater(() -> {
            List<IJavaFXElement> cell = tableView.findElementsByCssSelector(".::all-cells");
            cells.add(cell.size());
        });
        new Wait("Wating for all cells.") {
            @Override public boolean until() {
                return cells.size() > 0;
            }
        };
        AssertJUnit.assertEquals(15, (int) cells.get(0));
    }

    @Test public void selectAllRows() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableView.marathon_select("all");
        });
        new Wait("Wating for rows to be select.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedIndices().size() > 1;
            }
        };
    }

    @Test public void selectAllCells() {
        TableView<?> tableViewNode = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        Platform.runLater(() -> {
            tableViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            tableViewNode.getSelectionModel().setCellSelectionEnabled(true);
            tableView.marathon_select("all");
        });
        new Wait("Wating for rows to be select.") {
            @Override public boolean until() {
                return tableViewNode.getSelectionModel().getSelectedIndices().size() > 1;
            }
        };
    }

    @Test public void assertContent() {
        String expected = "[[\"Jacob\",\"Smith\",\"jacob.smith@example.com\"],[\"Isabella\",\"Johnson\",\"isabella.johnson@example.com\"],[\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\"Michael\",\"Brown\",\"michael.brown@example.com\"]]";
        AssertJUnit.assertEquals(expected, tableView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new TableSample();
    }
}
