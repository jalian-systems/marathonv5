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

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample.Item;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXListViewCheckBoxListCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement listView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        listView = driver.findElementByTagName("list-view");
    }

    @Test public void selectListItemCheckBoxNotSelectedSelected() {
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("Item 3:checked");
        new Wait("Wait for list item check box to be selected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("true");
            }
        };
    }

    @Test public void selectListItemCheckBoxSelectedSelected() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Item x = (Item) listViewNode.getItems().get(2);
        x.setOn(true);
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("Item 3:checked");
        new Wait("Wait for list item check box to be selected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("true");
            }
        };
    }

    @Test public void selectListItemCheckBoxSelectedNotSelected() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Item x = (Item) listViewNode.getItems().get(2);
        x.setOn(true);
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("Item 3:unchecked");
        new Wait("Wait for list item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
    }

    @Test public void selectListItemCheckBoxNotSelectedNotSelected() {
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        new Wait("Wait for list item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
        cb.marathon_select("Item 3:unchecked");
        new Wait("Wait for list item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
    }

    @Test public void assertContent() {
        String expected = "[[\"Item 1:unchecked\",\"Item 2:unchecked\",\"Item 3:unchecked\",\"Item 4:unchecked\",\"Item 5:unchecked\",\"Item 6:unchecked\",\"Item 7:unchecked\",\"Item 8:unchecked\",\"Item 9:unchecked\",\"Item 10:unchecked\",\"Item 11:unchecked\",\"Item 12:unchecked\",\"Item 13:unchecked\",\"Item 14:unchecked\",\"Item 15:unchecked\",\"Item 16:unchecked\",\"Item 17:unchecked\",\"Item 18:unchecked\",\"Item 19:unchecked\",\"Item 20:unchecked\"]]";
        AssertJUnit.assertEquals(expected, listView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new CheckBoxListViewSample();
    }
}
