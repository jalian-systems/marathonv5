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

import ensemble.samples.controls.buttons.CheckBoxes;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXCheckBoxElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement checkBox;
    private IJavaFXElement triStateCheckBox;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        checkBox = driver.findElementByTagName("check-box");
        triStateCheckBox = driver.findElementByCssSelector("check-box[text='Three state checkbox']");
    }

    @Override
    protected Pane getMainPane() {
        return new CheckBoxes();
    }

    @Test
    public void selectCheckboxNotSelectedSelected() throws Throwable {
        CheckBox checkBoxNode = (CheckBox) getPrimaryStage().getScene().getRoot().lookup(".check-box");
        AssertJUnit.assertEquals(false, checkBoxNode.isSelected());
        checkBox.marathon_select("checked");
        new Wait("Waiting for the check box selection.") {
            @Override
            public boolean until() {
                return checkBoxNode.isSelected();
            }
        };
    }

    @Test
    public void selectCheckboxSelectedSelected() throws Throwable {
        CheckBox checkBoxNode = (CheckBox) getPrimaryStage().getScene().getRoot().lookup(".check-box");
        checkBoxNode.setSelected(true);
        AssertJUnit.assertEquals(true, checkBoxNode.isSelected());
        checkBox.marathon_select("checked");
        new Wait("Waiting for the check box selection.") {
            @Override
            public boolean until() {
                return checkBoxNode.isSelected();
            }
        };
    }

    @Test
    public void selectCheckboxSelectedNotSelected() throws Throwable {
        CheckBox checkBoxNode = (CheckBox) getPrimaryStage().getScene().getRoot().lookup(".check-box");
        checkBoxNode.setSelected(true);
        AssertJUnit.assertEquals(true, checkBoxNode.isSelected());
        checkBox.marathon_select("unchecked");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return !checkBoxNode.isSelected();
            }
        };
    }

    @Test
    public void selectCheckboxNotSelectedNotSelected() throws Throwable {
        CheckBox checkBoxNode = (CheckBox) getPrimaryStage().getScene().getRoot().lookup(".check-box");
        AssertJUnit.assertEquals(false, checkBoxNode.isSelected());
        checkBox.marathon_select("unchecked");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return !checkBoxNode.isSelected();
            }
        };
    }

    @Test
    public void undefinedCheckboxNotSelectedNotSelected() throws Throwable {
        CheckBox checkBoxNode = findCheckbox("Three state checkbox");
        AssertJUnit.assertEquals(false, checkBoxNode.isSelected());
        triStateCheckBox.marathon_select("indeterminate");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return checkBoxNode.isIndeterminate();
            }
        };
    }

    @Test
    public void checkedCheckboxNotSelectedNotSelected() throws Throwable {
        CheckBox checkBoxNode = findCheckbox("Three state checkbox");
        AssertJUnit.assertEquals(false, checkBoxNode.isSelected());
        triStateCheckBox.marathon_select("checked");
        new Wait("Waiting for the check box deselect.") {
            @Override
            public boolean until() {
                return !checkBoxNode.isIndeterminate() && checkBoxNode.isSelected();
            }
        };
    }

    @Test
    public void getText() throws Throwable {
        CheckBox checkBoxNode = (CheckBox) getPrimaryStage().getScene().getRoot().lookup(".check-box");
        AssertJUnit.assertEquals(false, checkBoxNode.isSelected());
        checkBox.marathon_select("checked");
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> text.add(checkBox.getAttribute("text")));
        new Wait("Waiting for the check box text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Simple checkbox", text.get(0));
    }

    private CheckBox findCheckbox(String text) {
        Set<Node> checkBox = getPrimaryStage().getScene().getRoot().lookupAll(".check-box");
        for (Node node : checkBox) {
            if (((CheckBox) node).getText().equals(text)) {
                return (CheckBox) node;
            }
        }
        return null;
    }

}
