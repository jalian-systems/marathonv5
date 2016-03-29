package net.sourceforge.marathon.javafxagent.components;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        textField.marathon_select("Hello World");
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
