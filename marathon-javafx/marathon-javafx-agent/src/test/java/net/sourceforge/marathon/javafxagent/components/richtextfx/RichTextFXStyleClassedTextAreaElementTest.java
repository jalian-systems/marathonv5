package net.sourceforge.marathon.javafxagent.components.richtextfx;

import org.fxmisc.richtext.StyleClassedTextArea;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxagent.components.JavaFXElementTest;

public class RichTextFXStyleClassedTextAreaElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement styleClassedTextArea;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        styleClassedTextArea = driver.findElementByTagName("style-classed-text-area");
    }

    @Test
    public void marathon_select() {
        StyleClassedTextArea styleClassedTextAreaNode = (StyleClassedTextArea) getPrimaryStage().getScene().getRoot()
                .lookup(".styled-text-area");
        Platform.runLater(() -> {
            styleClassedTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(styleClassedTextAreaNode.getText());
            }
        };
    }

    @Test
    public void clear() {
        StyleClassedTextArea styleClassedTextAreaNode = (StyleClassedTextArea) getPrimaryStage().getScene().getRoot()
                .lookup(".styled-text-area");
        Platform.runLater(() -> {
            styleClassedTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(styleClassedTextAreaNode.getText());
            }
        };
        styleClassedTextArea.clear();
        new Wait("Waiting for the text area value to be cleared") {
            @Override
            public boolean until() {
                return "".equals(styleClassedTextAreaNode.getText());
            }
        };
    }

    @Test
    public void getText() {
        StyleClassedTextArea styleClassedTextAreaNode = (StyleClassedTextArea) getPrimaryStage().getScene().getRoot()
                .lookup(".styled-text-area");
        AssertJUnit.assertEquals("", styleClassedTextArea.getText());
        Platform.runLater(() -> {
            styleClassedTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(styleClassedTextAreaNode.getText());
            }
        };
        AssertJUnit.assertEquals("Hello World", styleClassedTextArea.getText());
    }

    @Test
    public void getAttributeText() {
        Platform.runLater(() -> {
            styleClassedTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area text") {
            @Override
            public boolean until() {
                return styleClassedTextArea.getAttribute("text").equals("Hello World");
            }
        };
        AssertJUnit.assertEquals("Hello World", styleClassedTextArea.getAttribute("text"));
    }

    @Override
    protected Pane getMainPane() {
        return new StyleClassedTextAreaSample();
    }

}