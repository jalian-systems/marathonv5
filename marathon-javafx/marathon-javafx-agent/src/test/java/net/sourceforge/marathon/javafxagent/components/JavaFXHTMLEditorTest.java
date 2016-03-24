package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.web.HTMLEditor;
import net.sourceforge.marathon.javafx.tests.HTMLEditorSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXHTMLEditorTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement htmlEditor;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        htmlEditor = driver.findElementByTagName("html-editor");
    }

    @Test public void select() {
        HTMLEditor htmlEditorNode = (HTMLEditor) getPrimaryStage().getScene().getRoot().lookup(".html-editor");
        Platform.runLater(() -> {
            htmlEditor.marathon_select("This html editor test");
        });
        new Wait("Waiting for html text to be set.") {
            @Override public boolean until() {
                String htmlText = htmlEditorNode.getHtmlText();
                return htmlText.equals("This html editor test");
            }
        };
    }

    @Override protected Pane getMainPane() {
        return new HTMLEditorSample();
    }

}
