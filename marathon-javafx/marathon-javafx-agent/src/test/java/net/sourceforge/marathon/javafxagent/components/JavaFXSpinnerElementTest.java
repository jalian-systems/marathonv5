package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SpinnerSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXSpinnerElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
    }

    @Test public void selectListSpinner() {
        IJavaFXElement spinner = driver.findElementByName("list-spinner");
        Spinner<?> spinnerNode = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#list-spinner");
        Platform.runLater(() -> {
            spinner.marathon_select("June");
        });
        new Wait("Waiting for spinner to set value") {
            @Override public boolean until() {
                Object value = spinnerNode.getValue();
                return value.toString().equals("June");
            }
        };
    }

    @Test public void selectEditableListSpinner() {
        IJavaFXElement spinner = driver.findElementByName("list-spinner");
        Spinner<?> spinnerNode = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#list-spinner");
        Platform.runLater(() -> {
            spinnerNode.setEditable(true);
            spinner.marathon_select("December");
        });
        new Wait("Waiting for spinner to set value") {
            @Override public boolean until() {
                Object value = spinnerNode.getValue();
                return value.toString().equals("December");
            }
        };
    }

    @Test public void listSpinnerWithInvalidValue() {
        IJavaFXElement spinner = driver.findElementByName("list-spinner");
        Spinner<?> spinnerNode = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#list-spinner");
        Platform.runLater(() -> {
            spinnerNode.setEditable(true);
            spinner.marathon_select("Decem");
        });
        new Wait("Spinner was not reset.") {
            @Override public boolean until() {
                Object value = spinnerNode.getValue();
                return value.toString().equals("January");
            }
        };
    }

    @Test public void selectIntegerSpinner() {
        IJavaFXElement spinner = driver.findElementByName("integer-spinner");
        Spinner<?> spinnerNode = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#integer-spinner");
        Platform.runLater(() -> {
            spinner.marathon_select("35");
        });
        new Wait("Waiting for spinner to set value") {
            @Override public boolean until() {
                Integer value = (Integer) spinnerNode.getValue();
                return value == 35;
            }
        };
    }

    @Test public void getText() {
        IJavaFXElement spinner = driver.findElementByName("integer-spinner");
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            spinner.marathon_select("35");
            text.add(spinner.getAttribute("text"));
        });
        new Wait("Waiting for the spinner text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("35", text.get(0));
    }

    @Test public void selectEditableIntegerSpinner() {
        IJavaFXElement spinner = driver.findElementByName("integer-spinner");
        Spinner<?> spinnerNode = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#integer-spinner");
        Platform.runLater(() -> {
            spinner.marathon_select("45");
        });
        new Wait("Waiting for spinner to set value") {
            @Override public boolean until() {
                Integer value = (Integer) spinnerNode.getValue();
                return value == 45;
            }
        };
    }

    @Test public void selectDoubleSpinner() {
        IJavaFXElement spinner = driver.findElementByName("double-spinner");
        Spinner<?> spinnerNode = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#double-spinner");
        Platform.runLater(() -> {
            spinnerNode.setEditable(false);
            spinner.marathon_select("38.9");
        });
        new Wait("Waiting for spinner to set value") {
            @Override public boolean until() {
                Double value = (Double) spinnerNode.getValue();
                return value == 38.9;
            }
        };
    }

    @Test public void selectEditableDoubleSpinner() {
        IJavaFXElement spinner = driver.findElementByName("double-spinner");
        Spinner<?> spinnerNode = (Spinner<?>) getPrimaryStage().getScene().getRoot().lookup("#double-spinner");
        Platform.runLater(() -> {
            spinner.marathon_select("49.0");
        });
        new Wait("Waiting for spinner to set value") {
            @Override public boolean until() {
                Double value = (Double) spinnerNode.getValue();
                return value == 49.0;
            }
        };
    }

    @Test public void cssSelectorEditor() {
        IJavaFXElement spinner = driver.findElementByName("list-spinner");
        IJavaFXElement listEditor = spinner.findElementByCssSelector(".::editor");
        AssertJUnit.assertEquals("January", listEditor.getText());
    }

    @Override protected Pane getMainPane() {
        return new SpinnerSample();
    }

}
