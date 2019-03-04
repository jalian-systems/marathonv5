package net.sourceforge.marathon.javafxagent.components.richtextfx;

import org.fxmisc.richtext.InlineCssTextArea;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxagent.components.JavaFXElementTest;

public class RichTextFXInlineCssTextAreaElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement inlineCssTextArea;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        inlineCssTextArea = driver.findElementByTagName("inline-css-text-area");
    }

    @Test
    public void marathon_select() {
        InlineCssTextArea inlineCssTextAreaNode = (InlineCssTextArea) getPrimaryStage().getScene().getRoot()
                .lookup(".styled-text-area");
        Platform.runLater(() -> {
            inlineCssTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(inlineCssTextAreaNode.getText());
            }
        };
    }

    @Test
    public void clear() {
        InlineCssTextArea inlineCssTextAreaNode = (InlineCssTextArea) getPrimaryStage().getScene().getRoot()
                .lookup(".styled-text-area");
        Platform.runLater(() -> {
            inlineCssTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(inlineCssTextAreaNode.getText());
            }
        };
        inlineCssTextArea.clear();
        new Wait("Waiting for the text area value to be cleared") {
            @Override
            public boolean until() {
                return "".equals(inlineCssTextAreaNode.getText());
            }
        };
    }

    @Test
    public void getText() {
        InlineCssTextArea inlineCssTextAreaNode = (InlineCssTextArea) getPrimaryStage().getScene().getRoot()
                .lookup(".styled-text-area");
        AssertJUnit.assertEquals("", inlineCssTextArea.getText());
        Platform.runLater(() -> {
            inlineCssTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(inlineCssTextAreaNode.getText());
            }
        };
        AssertJUnit.assertEquals("Hello World", inlineCssTextArea.getText());
    }

    @Test
    public void getAttributeText() {
        Platform.runLater(() -> {
            inlineCssTextArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area text") {
            @Override
            public boolean until() {
                return inlineCssTextArea.getAttribute("text").equals("Hello World");
            }
        };
        AssertJUnit.assertEquals("Hello World", inlineCssTextArea.getAttribute("text"));
    }

    @Override
    protected Pane getMainPane() {
        return new InlineCSSTextAreaSample();
    }

}