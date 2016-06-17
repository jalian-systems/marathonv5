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

import ensemble.samples.controls.ColorPickerSample;
import javafx.application.Platform;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXColorPickerElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement colorpicker;

    @BeforeMethod public void initializeDirver() {
        driver = new JavaFXAgent();
        colorpicker = driver.findElementByTagName(".color-picker");
    }

    @Test public void selectColor() {
        ColorPicker colorPickerNode = (ColorPicker) getPrimaryStage().getScene().getRoot().lookup(".color-picker");
        Platform.runLater(() -> colorpicker.marathon_select("#ff0000"));
        new Wait("Waiting for color to be set.") {
            @Override public boolean until() {
                return colorPickerNode.getValue().toString().equals("0xff0000ff");
            }
        };
    }

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            colorpicker.marathon_select("#ff0000");
            text.add(colorpicker.getAttribute("text"));
        });
        new Wait("Waiting for color picker text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("#ff0000", text.get(0));
    }

    @Test(expectedExceptions = IllegalArgumentException.class) public void colorPickerWithInvalidColorCode() {
        colorpicker.marathon_select("#67899");
    }

    @Override protected Pane getMainPane() {
        return new ColorPickerSample();
    }
}
