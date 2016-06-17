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

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ComboBoxListViewSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXListViewComboBoxListCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement listView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        listView = driver.findElementByTagName("list-view");
    }

    @Test public void select() {
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Option 5\"}')");
        IJavaFXElement combo = item.findElementByCssSelector(".::editor");
        Platform.runLater(() -> {
            combo.marathon_select("Option 3");
        });
        new Wait("Wait for list item combo box to set option.") {
            @Override public boolean until() {
                String selected = combo.getAttribute("selectionModel.getSelectedIndex");
                return selected.equals("2");
            }
        };
    }

    @Test public void assertContent() {
        String expected = "[[\"Option 1\",\"Option 2\",\"Option 3\",\"Option 4\",\"Option 5\",\"Option 6\"]]";
        AssertJUnit.assertEquals(expected, listView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new ComboBoxListViewSample();
    }
}
