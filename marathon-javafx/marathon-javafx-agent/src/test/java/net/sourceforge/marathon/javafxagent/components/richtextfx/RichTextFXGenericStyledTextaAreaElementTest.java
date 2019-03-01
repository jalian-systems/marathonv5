package net.sourceforge.marathon.javafxagent.components.richtextfx;

import org.fxmisc.richtext.GenericStyledArea;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxagent.components.JavaFXElementTest;

@SuppressWarnings("rawtypes")
public class RichTextFXGenericStyledTextaAreaElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement codeArea;

    @BeforeMethod
    public void initializeDriver() {
        driver = new JavaFXAgent();
        codeArea = driver.findElementByTagName("code-area");
    }

    @Test
    public void marathon_select() {
        GenericStyledArea codeAreaNode = (GenericStyledArea) getPrimaryStage().getScene().getRoot().lookup(".code-area");
        Platform.runLater(() -> {
            codeArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(codeAreaNode.getText());
            }
        };
    }

    @Test
    public void clear() {
        GenericStyledArea codeAreaNode = (GenericStyledArea) getPrimaryStage().getScene().getRoot().lookup(".code-area");
        Platform.runLater(() -> {
            codeArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(codeAreaNode.getText());
            }
        };
        codeArea.clear();
        new Wait("Waiting for the text area value to be cleared") {
            @Override
            public boolean until() {
                return "".equals(codeAreaNode.getText());
            }
        };
    }

    @Test
    public void getText() {
        GenericStyledArea codeAreaNode = (GenericStyledArea) getPrimaryStage().getScene().getRoot().lookup(".code-area");
        AssertJUnit.assertEquals("", codeArea.getText());
        Platform.runLater(() -> {
            codeArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area value to be set") {
            @Override
            public boolean until() {
                return "Hello World".equals(codeAreaNode.getText());
            }
        };
        AssertJUnit.assertEquals("Hello World", codeArea.getText());
    }
    
    @Test
    public void getAttributeText() {
        Platform.runLater(() -> {
            codeArea.marathon_select("Hello World");
        });
        new Wait("Waiting for the text area text") {
            @Override
            public boolean until() {
                return codeArea.getAttribute("text").equals("Hello World");
            }
        };
        AssertJUnit.assertEquals("Hello World", codeArea.getAttribute("text"));
    }


    @Override
    protected Pane getMainPane() {
        return new CodeAreaSample();
    }

}