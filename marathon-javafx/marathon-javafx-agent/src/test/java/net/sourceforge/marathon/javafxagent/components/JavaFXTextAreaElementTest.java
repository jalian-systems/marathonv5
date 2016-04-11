package net.sourceforge.marathon.javafxagent.components;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TextAreaSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTextAreaElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement textarea;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        textarea = driver.findElementByTagName("text-area");
    }

    @Test public void marathon_select() {
        TextArea textAreaNode = (TextArea) getPrimaryStage().getScene().getRoot().lookup(".text-area");
        textarea.marathon_select("Hello World");
        new Wait("Waiting for the text area value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(textAreaNode.getText());
            }
        };
    }

    @Test public void clear() {
        TextArea textAreaNode = (TextArea) getPrimaryStage().getScene().getRoot().lookup(".text-area");
        textarea.marathon_select("Hello World");
        new Wait("Waiting for the text area value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(textAreaNode.getText());
            }
        };
        textarea.clear();
        new Wait("Waiting for the text area value to be cleared") {
            @Override public boolean until() {
                return "".equals(textAreaNode.getText());
            }
        };
    }

    @Test public void getText() {
        TextArea textAreaNode = (TextArea) getPrimaryStage().getScene().getRoot().lookup(".text-area");
        AssertJUnit.assertEquals("", textarea.getText());
        textarea.marathon_select("Hello World");
        new Wait("Waiting for the text area value to be set") {
            @Override public boolean until() {
                return "Hello World".equals(textAreaNode.getText());
            }
        };
        AssertJUnit.assertEquals("Hello World", textarea.getText());
    }

    @Test public void getAttributeText() {
        Platform.runLater(() -> {
            textarea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area text") {
            @Override public boolean until() {
                return textarea.getAttribute("text").equals("Hello World");
            }
        };
        AssertJUnit.assertEquals("Hello World", textarea.getAttribute("text"));
    }

    @Override protected Pane getMainPane() {
        return new TextAreaSample();
    }
}
