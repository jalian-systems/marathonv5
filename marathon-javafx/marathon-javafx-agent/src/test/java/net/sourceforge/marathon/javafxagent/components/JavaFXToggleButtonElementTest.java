package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.buttons.RadioButtons;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXToggleButtonElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement radioButton;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        radioButton = driver.findElementByTagName("radio-button");
    }

    @Override protected Pane getMainPane() {
        return new RadioButtons();
    }

    @Test public void selectRadioBottonSelectedSelected() {
        RadioButton radioButtonNode = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        radioButtonNode.setSelected(true);
        AssertJUnit.assertEquals(true, radioButtonNode.isSelected());
        radioButton.marathon_select("true");
        new Wait("Waiting for the radio button selection.") {
            @Override public boolean until() {
                return radioButtonNode.isSelected();
            }
        };
    }

    @Test public void selectRadiobuttonSelectedNotSelected() {
        Set<Node> radioButtonNodes = getPrimaryStage().getScene().getRoot().lookupAll(".radio-button");
        List<Node> nodes = new ArrayList<>(radioButtonNodes);
        RadioButton radioButtonNode = (RadioButton) nodes.get(1);
        AssertJUnit.assertEquals(true, radioButtonNode.isSelected());
        radioButton.marathon_select("false");
        new Wait("Waiting for the radio button deselect.") {
            @Override public boolean until() {
                return radioButtonNode.isSelected();
            }
        };
    }

    @Test public void selectRadiobuttonNotSelectedSelected() {
        RadioButton radioButtonNode = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        AssertJUnit.assertEquals(false, radioButtonNode.isSelected());
        radioButton.marathon_select("true");
        new Wait("Waiting for the radio button selection.") {
            @Override public boolean until() {
                return radioButtonNode.isSelected();
            }
        };
    }

    @Test public void selectRadiobuttoNotSelectedNotSelected() {
        RadioButton radioButtonNode = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        AssertJUnit.assertEquals(false, radioButtonNode.isSelected());
        radioButton.marathon_select("false");
        new Wait("Waiting for the radio button state.") {
            @Override public boolean until() {
                return !radioButtonNode.isSelected();
            }
        };
    }

    @Test public void getText() {
        RadioButton radioButtonNode = (RadioButton) getPrimaryStage().getScene().getRoot().lookup(".radio-button");
        AssertJUnit.assertEquals(false, radioButtonNode.isSelected());
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            radioButton.marathon_select("true");
            text.add(radioButton.getAttribute("text"));
        });
        new Wait("Waiting for the toggle button text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Hello", text.get(0));
    }

}
