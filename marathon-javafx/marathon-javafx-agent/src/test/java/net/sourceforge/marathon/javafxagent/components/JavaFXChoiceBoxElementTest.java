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

import ensemble.samples.controls.ChoiceBoxSample;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXChoiceBoxElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement choiceBox;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        choiceBox = driver.findElementByTagName("choice-box");
    }

    @Test public void select() {
        ChoiceBox<?> choiceBoxNode = (ChoiceBox<?>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        Platform.runLater(() -> {
            choiceBox.marathon_select("Cat");
        });
        new Wait("Waiting for choice box option to be set.") {
            @Override public boolean until() {
                return choiceBoxNode.getSelectionModel().getSelectedIndex() == 1;
            }
        };
    }

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            choiceBox.marathon_select("Horse");
            text.add(choiceBox.getAttribute("text"));
        });
        new Wait("Waiting for choice box text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Horse", text.get(0));
    }

    @Test public void assertContent() {
        List<String> contents = new ArrayList<>();
        Platform.runLater(() -> {
            contents.add(choiceBox.getAttribute("content"));
        });
        new Wait("Waiting for choice box content.") {
            @Override public boolean until() {
                return contents.size() > 0;
            }
        };
        String expected = "[[\"Dog\",\"Cat\",\"Horse\"]]";
        AssertJUnit.assertEquals(expected, contents.get(0));
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxSample();
    }

}
