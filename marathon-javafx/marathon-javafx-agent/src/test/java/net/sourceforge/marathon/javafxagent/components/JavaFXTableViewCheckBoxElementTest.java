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

import ensemble.samples.controls.table.TableCellFactorySample;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;

public class JavaFXTableViewCheckBoxElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement tableView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        tableView = driver.findElementByTagName("table-view");
    }

    @Test public void assertContent() {
        String expected = "[[\":checked\",\"Jacob\",\"Smith\",\"jacob.smith@example.com\"],[\":unchecked\",\"Isabella\",\"Johnson\",\"isabella.johnson@example.com\"],[\":checked\",\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\":checked\",\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\":unchecked\",\"Michael\",\"Brown\",\"michael.brown@example.com\"]]";
        AssertJUnit.assertEquals(expected, tableView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new TableCellFactorySample();
    }
}
