package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
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
        try {
            new Wait("Waiting for html text to be set.") {
                @Override public boolean until() {
                    String htmlText = htmlEditorNode.getHtmlText();
                    return htmlText.equals(
                            "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">This html editor test</body></html>");
                }
            };
        } catch (Throwable t) {
        }
        AssertJUnit.assertEquals(
                "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">This html editor test</body></html>",
                htmlEditor.getText());
        AssertJUnit.assertEquals(
                "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">This html editor test</body></html>",
                htmlEditorNode.getHtmlText());
    }

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            htmlEditor.marathon_select("This html editor test");
            text.add(htmlEditor.getAttribute("text"));
        });
        try {
            new Wait("Waiting for html text to be set.") {
                @Override public boolean until() {
                    return htmlEditor.getAttribute("text").equals(
                            "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">This html editor test</body></html>");
                }
            };
        } catch (Throwable t) {
        }
        AssertJUnit.assertEquals(
                "<html dir=\"ltr\"><head></head><body contenteditable=\"true\">This html editor test</body></html>",
                htmlEditor.getAttribute("text"));
    }

    @Override protected Pane getMainPane() {
        return new HTMLEditorSample();
    }

}
