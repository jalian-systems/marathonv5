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

import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SliderSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXSliderElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement slider;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        slider = driver.findElementByTagName("slider");
    }

    @Test public void setSliderValue() {
        Slider sliderNode = (Slider) getPrimaryStage().getScene().getRoot().lookup(".slider");
        slider.marathon_select("25.0");
        new Wait("Waiting for slider value to be set.") {
            @Override public boolean until() {
                return 25.0 == sliderNode.getValue();
            }
        };
    }

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            slider.marathon_select("25.0");
            text.add(slider.getAttribute("text"));
        });
        new Wait("Waiting for the slider text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("25.0", text.get(0));
    }

    @Test(expectedExceptions = NumberFormatException.class) public void illegalArgumentException() {
        Slider sliderNode = (Slider) getPrimaryStage().getScene().getRoot().lookup(".slider");
        slider.marathon_select("ten");
        new Wait("Waiting for slider value to be set.") {
            @Override public boolean until() {
                return 25.0 == sliderNode.getValue();
            }
        };
    }

    @Override protected Pane getMainPane() {
        return new SliderSample();
    }
}
