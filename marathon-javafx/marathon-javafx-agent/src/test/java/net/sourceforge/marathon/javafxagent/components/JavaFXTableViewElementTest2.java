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

import ensemble.samples.controls.table.TableCellFactorySample;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTableViewElementTest2 extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement tableView;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        tableView = driver.findElementByTagName("table-view");
    }

    @Test
    public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            tableView.marathon_select("{\"rows\":[1]}");
            text.add(tableView.getAttribute("text"));
        });
        new Wait("Wating for table text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("{\"rows\":[1]}", text.get(0));
    }

    @Test
    public void assertContent() {
        String expected = "[:checked, Jacob, Smith, jacob.smith@example.com, :unchecked, Isabella, Johnson, isabella.johnson@example.com, :checked, Ethan, Williams, ethan.williams@example.com, :checked, Emma, Jones, emma.jones@example.com, :unchecked, Michael, Brown, michael.brown@example.com]";
        List<IJavaFXElement> elements = tableView.findElementsByCssSelector(".::all-cells");
        ArrayList<String> actual = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            actual.add(elements.get(i).getAttribute("text"));
        }
        AssertJUnit.assertEquals(expected, actual.toString());
    }

    @Override
    protected Pane getMainPane() {
        return new TableCellFactorySample();
    }
}
