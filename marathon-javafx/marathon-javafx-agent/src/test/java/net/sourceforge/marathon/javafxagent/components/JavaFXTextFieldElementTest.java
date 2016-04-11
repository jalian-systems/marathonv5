package net.sourceforge.marathon.javafxagent.components;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.text.TextFieldSample;
import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTextFieldElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement textField;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        textField = driver.findElementByTagName("text-field");
    }

    @Test public void marathon_select() {
        TextField textFieldNode = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
        textField.marathon_select("Hello World");
        new Wait("Waiting for the text field value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(textFieldNode.getText());
            }
        };
    }

    @Test public void clear() {
        TextField textFieldNode = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
        textField.marathon_select("Hello World");
        new Wait("Waiting for the text field value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(textFieldNode.getText());
            }
        };
        textField.clear();
        new Wait("Waiting for the text field value to be cleared") {
            @Override public boolean until() {
                return "".equals(textFieldNode.getText());
            }
        };
    }

    @Test public void getText() {
        TextField textFieldNode = (TextField) getPrimaryStage().getScene().getRoot().lookup(".text-field");
        AssertJUnit.assertEquals("Text", textField.getText());
        textField.marathon_select("Hello World");
        new Wait("Waiting for the text field value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(textFieldNode.getText());
            }
        };
        AssertJUnit.assertEquals("Hello World", textField.getText());
    }

    @Test public void getAttributeText() {
        AssertJUnit.assertEquals("Text", textField.getText());
        Platform.runLater(() -> {
            textField.marathon_select("Hello World");
        });
        new Wait("Waiting for the text field text") {
            @Override public boolean until() {
                return textField.getAttribute("text").equals("Hello World");
            }
        };
        AssertJUnit.assertEquals("Hello World", textField.getAttribute("text"));
    }

    @Override protected Pane getMainPane() {
        return new TextFieldSample();
    }
}
