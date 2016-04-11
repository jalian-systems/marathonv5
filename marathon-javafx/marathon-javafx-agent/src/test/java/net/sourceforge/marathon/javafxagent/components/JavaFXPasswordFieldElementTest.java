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

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        passwordField = driver.findElementByTagName("password-field");
    }

    @Test public void marathon_select() {
        PasswordField passwordFieldNode = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        passwordField.marathon_select("Hello World");
        new Wait("Waiting for the password field value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(passwordFieldNode.getText());
            }
        };
    }

    @Test public void clear() {
        PasswordField passwordFieldNode = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        passwordField.marathon_select("Hello World");
        new Wait("Waiting for the password field value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(passwordFieldNode.getText());
            }
        };
        passwordField.clear();
        new Wait("Waiting for the password field value to be cleared") {
            @Override public boolean until() {
                return "".equals(passwordFieldNode.getText());
            }
        };
    }

    @Test public void getText() {
        PasswordField passwordFieldNode = (PasswordField) getPrimaryStage().getScene().getRoot().lookup(".password-field");
        AssertJUnit.assertEquals("", passwordField.getText());
        passwordField.marathon_select("Hello World");
        new Wait("Waiting for the password field value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(passwordFieldNode.getText());
            }
        };
        AssertJUnit.assertEquals("Hello World", passwordField.getText());
    }

    @Override protected Pane getMainPane() {
        return new PasswordFieldSample();
    }
}
