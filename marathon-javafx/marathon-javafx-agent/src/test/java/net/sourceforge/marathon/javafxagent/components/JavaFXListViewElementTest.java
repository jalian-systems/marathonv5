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

import ensemble.samples.controls.list.SimpleListViewSample;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXListViewElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement listView;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        listView = driver.findElementByTagName("list-view");
    }

    @Test
    public void selectForNoCells() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        listView.marathon_select("[]");
        new Wait("Waiting for no selection") {
            @Override
            public boolean until() {
                return listViewNode.getSelectionModel().getSelectedIndices().size() == 0;
            }
        };
    }

    @Test
    public void selectForSingleItem() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Platform.runLater(() -> listView.marathon_select("[\"Row 2\"]"));
        new Wait("Waiting for list item to be select") {
            @Override
            public boolean until() {
                return listViewNode.getSelectionModel().getSelectedIndex() == 1;
            }
        };
    }

    @Test
    public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            listView.marathon_select("[\"Row 2\"]");
            text.add(listView.getAttribute("text"));
        });
        new Wait("Waiting for list item text") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[\"Row 2\"]", text.get(0));
    }

    @Test
    public void getTextForMultipleSelection() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            listView.marathon_select("[\"Row 2\",\"Row 20\"]");
            text.add(listView.getAttribute("text"));
        });
        new Wait("Waiting for list item text") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[\"Row 2\",\"Row 20\"]", text.get(0));
    }

    @Test
    public void selectForMultipleItems() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Platform.runLater(() -> listView.marathon_select("[\"Row 2\",\"Row 20\"]"));
        new Wait("Waiting for list item to be select") {
            @Override
            public boolean until() {
                ObservableList<Integer> selectedIndices = listViewNode.getSelectionModel().getSelectedIndices();
                return selectedIndices.size() == 2;
            }
        };
    }

    @Test
    public void selectForDuplicateItems() {
        @SuppressWarnings("unchecked")
        ListView<String> listViewNode = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                listViewNode.getItems().add(2, "Row 2");
            }
        });
        Platform.runLater(() -> listView.marathon_select("[\"Row 2(1)\"]"));
        new Wait("Waiting for list item to be select") {
            @Override
            public boolean until() {
                return listViewNode.getSelectionModel().getSelectedIndex() == 2;
            }
        };
    }

    @Test
    public void selectForMultipleDuplicates() {
        @SuppressWarnings("unchecked")
        ListView<String> listViewNode = (ListView<String>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                listViewNode.getItems().add(2, "Row 2");
                listViewNode.getItems().add(9, "Row 2");
                listViewNode.getItems().add(10, "Row 2");
            }
        });
        Platform.runLater(() -> listView.marathon_select("[\"Row 2(3)\"]"));
        new Wait("Waiting for list item to be select") {
            @Override
            public boolean until() {
                return listViewNode.getSelectionModel().getSelectedIndex() == 10;
            }
        };
    }

    @Test
    public void clickNthelement() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        IJavaFXElement item = listView.findElementByCssSelector(".::nth-item(3)");
        item.click();
        new Wait("Waiting for list item to be select") {
            @Override
            public boolean until() {
                return listViewNode.getSelectionModel().getSelectedIndex() == 2;
            }
        };
    }

    @Test
    public void getTextNthelement() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            IJavaFXElement item = listView.findElementByCssSelector(".::nth-item(3)");
            item.click();
            text.add(item.getAttribute("text"));

        });
        new Wait("Waiting for list item text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Long Row 3", text.get(0));
    }

    @Test
    public void assertContent() {
        String expected = "[[\"Row 1\",\"Row 2\",\"Long Row 3\",\"Row 4\",\"Row 5\",\"Row 6\",\"Row 7\",\"Row 8\",\"Row 9\",\"Row 10\",\"Row 11\",\"Row 12\",\"Row 13\",\"Row 14\",\"Row 15\",\"Row 16\",\"Row 17\",\"Row 18\",\"Row 19\",\"Row 20\"]]";
        AssertJUnit.assertEquals(expected, listView.getAttribute("content"));
    }

    @Override
    protected Pane getMainPane() {
        return new SimpleListViewSample();
    }
}
