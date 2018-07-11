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

import javafx.scene.control.PasswordField;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.PasswordFieldSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXPasswordFieldElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement passwordField;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        passwordField = driver.findElementByTagName("password-field");
    }

    @Test
    public void marathon_select() {
        PasswordField passwordFieldNode = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        passwordField.marathon_select("Hello World");
        new Wait("Waiting for the password field value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(passwordFieldNode.getText());
            }
        };
    }

    @Test
    public void clear() {
        PasswordField passwordFieldNode = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        passwordField.marathon_select("Hello World");
        new Wait("Waiting for the password field value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(passwordFieldNode.getText());
            }
        };
        passwordField.clear();
        new Wait("Waiting for the password field value to be cleared") {
            @Override
            public boolean until() {
                return "".equals(passwordFieldNode.getText());
            }
        };
    }

    @Test
    public void getText() {
        PasswordField passwordFieldNode = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        AssertJUnit.assertEquals("", passwordField.getText());
        passwordField.marathon_select("Hello World");
        new Wait("Waiting for the password field value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(passwordFieldNode.getText());
            }
        };
        AssertJUnit.assertEquals("Hello World", passwordField.getText());
    }

    @Override
    protected Pane getMainPane() {
        return new PasswordFieldSample();
    }
}
