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

import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ComboBoxTreeViewSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;

public class JavaFXTreeViewComboBoxTreeCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeView;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        treeView = driver.findElementByTagName("tree-view");
    }

    @Test
    public void assertContent() {
        String expected = "[[\"Root node\",\"Option 1\",\"Option 2\",\"Option 3\"]]";
        AssertJUnit.assertEquals(expected, treeView.getAttribute("content"));
    }

    @Override
    protected Pane getMainPane() {
        return new ComboBoxTreeViewSample();
    }
}
