package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.ChoiceBoxSample;
import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXChoiceBoxElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement choiceBox;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        choiceBox = driver.findElementByTagName("choice-box");
    }

    @Test public void select() {
        ChoiceBox<?> choiceBoxNode = (ChoiceBox<?>) getPrimaryStage().getScene().getRoot().lookup(".choice-box");
        Platform.runLater(() -> {
            choiceBox.marathon_select("Cat");
        });
        new Wait("Waiting for choice box option to be set.") {
            @Override public boolean until() {
                return choiceBoxNode.getSelectionModel().getSelectedIndex() == 1;
            }
        };
    }

    @Test public void assertContent() {
        List<String> contents = new ArrayList<>();
        Platform.runLater(() -> {
            contents.add(choiceBox.getAttribute("content"));
        });
        new Wait("Waiting for choice box content.") {
            @Override public boolean until() {
                return contents.size() > 0;
            }
        };
        String expected = "[[\"Dog\",\"Cat\",\"Horse\"]]";
        AssertJUnit.assertEquals(expected, contents.get(0));
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxSample();
    }

}
