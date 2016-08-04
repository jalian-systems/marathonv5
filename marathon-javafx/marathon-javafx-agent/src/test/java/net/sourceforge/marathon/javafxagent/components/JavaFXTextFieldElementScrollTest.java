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

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.javafx.tests.TextFiledScrollSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTextFieldElementScrollTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement textField;
    private IJavaFXElement button;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        List<IJavaFXElement> textFields = driver.findElementsByTagName("text-field");
        textField = textFields.get(18);
        List<IJavaFXElement> buttons = driver.findElementsByTagName("button");
        button = buttons.get(18);
    }

    @Test public void marathon_select() {
        getPrimaryStage().setWidth(250);
        getPrimaryStage().setHeight(150);
        new Wait("Setting width failed") {
            @Override public boolean until() {
                return getPrimaryStage().getHeight() == 150;
            }
        };
        Platform.runLater(() -> textField.marathon_select("Hello World"));
    }

    @Test public void click() {
        getPrimaryStage().setWidth(250);
        getPrimaryStage().setHeight(150);
        new Wait("Setting width failed") {
            @Override public boolean until() {
                return getPrimaryStage().getHeight() == 150;
            }
        };
        button.click();
    }

    @Override protected Pane getMainPane() {
        VBox vBox = new VBox();
        vBox.getChildren().add(new ScrollPane(new TextFiledScrollSample()));
        return vBox;
    }
}
