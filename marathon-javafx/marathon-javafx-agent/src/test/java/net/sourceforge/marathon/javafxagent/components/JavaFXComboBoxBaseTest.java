package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.ComboBoxSample;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXComboBoxBaseTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private List<IJavaFXElement> combos;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        combos = driver.findElementsByTagName("combo-box");
    }

    @Test public void selectOption() {
        ComboBox<?> comboNode = (ComboBox<?>) getPrimaryStage().getScene().getRoot().lookup(".combo-box");
        IJavaFXElement comboBox = combos.get(0);
        Platform.runLater(() -> comboBox.marathon_select("Option 2"));
        new Wait("Waiting for combo box option to be set.") {
            @Override public boolean until() {
                return comboNode.getSelectionModel().getSelectedIndex() == 1;
            }
        };
    }

    @Test public void selectOption2() {
        ComboBox<?> comboNode = (ComboBox<?>) getPrimaryStage().getScene().getRoot().lookup(".combo-box");
        IJavaFXElement comboBox = combos.get(0);
        Platform.runLater(() -> comboBox.marathon_select("Option 5"));
        new Wait("Waiting for combo box option to be set.") {
            @Override public boolean until() {
                return comboNode.getSelectionModel().getSelectedIndex() == 4;
            }
        };
    }

    @Test public void selectnthItem() {
        IJavaFXElement comboBox = combos.get(0);
        IJavaFXElement option = comboBox.findElementByCssSelector(".::nth-option(3)");
        List<String> texts = new ArrayList<>();
        Platform.runLater(() -> {
            texts.add(option.getAttribute("text"));
        });
        new Wait("Waiting for combo box option to be set.") {
            @Override public boolean until() {
                return texts.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Option 3", texts.get(0));
    }

    @Test public void editorSelection() {
        IJavaFXElement comboBox = combos.get(1);
        Platform.runLater(() -> {
            comboBox.marathon_select("Option 3");
        });
        IJavaFXElement editor = comboBox.findElementByTagName("text-field");
        new Wait("Wating for editor text to set.") {

            @Override public boolean until() {
                return editor.getAttribute("text").equals("Option 3");
            }
        };
    }

    @Test public void editorDuplicateSelection() {
        Set<Node> comboBoxNodes = getPrimaryStage().getScene().getRoot().lookupAll(".combo-box");
        List<Node> boxes = new ArrayList<>(comboBoxNodes);
        @SuppressWarnings("unchecked")
        ComboBox<String> comboBoxNode = (ComboBox<String>) boxes.get(1);
        IJavaFXElement comboBox = combos.get(1);
        Platform.runLater(() -> {
            comboBoxNode.getItems().add(5, "Option 3");
            comboBox.marathon_select("Option 3(1)");
        });
        IJavaFXElement editor = comboBox.findElementByTagName("text-field");
        new Wait("Wating for editor text to set.") {

            @Override public boolean until() {
                return editor.getAttribute("text").equals("Option 3");
            }
        };
    }

    @Test public void editorOption() {
        IJavaFXElement comboBox = combos.get(1);
        Platform.runLater(() -> {
            comboBox.marathon_select("Option");
        });
        IJavaFXElement editor = comboBox.findElementByTagName("text-field");
        new Wait("Wating for editor text to set.") {

            @Override public boolean until() {
                return editor.getAttribute("text").equals("Option");
            }
        };
    }

    @Test public void selectDuplicateOption() {
        @SuppressWarnings("unchecked")
        ComboBox<String> comboNode = (ComboBox<String>) getPrimaryStage().getScene().getRoot().lookup(".combo-box");
        IJavaFXElement comboBox = combos.get(0);
        Platform.runLater(() -> {
            comboNode.getItems().add(3, "Option 2");
            comboBox.marathon_select("Option 2(1)");
        });
        new Wait("Waiting for combo box option to be set.") {
            @Override public boolean until() {
                return comboNode.getSelectionModel().getSelectedIndex() == 3;
            }
        };
    }

    @Test public void selectMultipleDuplicateOption() {
        @SuppressWarnings("unchecked")
        ComboBox<String> comboNode = (ComboBox<String>) getPrimaryStage().getScene().getRoot().lookup(".combo-box");
        IJavaFXElement comboBox = combos.get(0);
        Platform.runLater(() -> {
            comboNode.getItems().add(3, "Option 2");
            comboNode.getItems().add(5, "Option 2");
            comboBox.marathon_select("Option 2(2)");
        });
        new Wait("Waiting for combo box option to be set.") {
            @Override public boolean until() {
                return comboNode.getSelectionModel().getSelectedIndex() == 5;
            }
        };
    }

    @Test public void assertContent() {
        IJavaFXElement comboBox = combos.get(0);
        List<String> contents = new ArrayList<>();
        Platform.runLater(() -> {
            contents.add(comboBox.getAttribute("content"));
        });
        new Wait("Waiting for combo box option to be set.") {
            @Override public boolean until() {
                return contents.size() > 0;
            }
        };
        String expected = "[[\"Option 1\",\"Option 2\",\"Option 3\",\"Option 4\",\"Option 5\",\"Option 6\",\"Longer ComboBox item\",\"Option 7\"]]";
        AssertJUnit.assertEquals(expected, contents.get(0));
    }

    @Test public void assertContentDuplicate() {
        @SuppressWarnings("unchecked")
        ComboBox<String> comboNode = (ComboBox<String>) getPrimaryStage().getScene().getRoot().lookup(".combo-box");
        IJavaFXElement comboBox = combos.get(0);
        List<String> contents = new ArrayList<>();
        Platform.runLater(() -> {
            comboNode.getItems().add(3, "Option 2");
            contents.add(comboBox.getAttribute("content"));
        });
        new Wait("Waiting for combo box option to be set.") {
            @Override public boolean until() {
                return contents.size() > 0;
            }
        };
        String expected = "[[\"Option 1\",\"Option 2\",\"Option 3\",\"Option 2(1)\",\"Option 4\",\"Option 5\",\"Option 6\",\"Longer ComboBox item\",\"Option 7\"]]";
        AssertJUnit.assertEquals(expected, contents.get(0));
    }

    @Override protected Pane getMainPane() {
        return new ComboBoxSample();
    }
}
