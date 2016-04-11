package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ChoiceBoxListViewSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXListViewChoiceBoxListCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement listView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        listView = driver.findElementByTagName("list-view");
    }

    @Test public void select() {
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Option 5\"}')");
        IJavaFXElement choiceBox = item.findElementByCssSelector(".::editor");
        Platform.runLater(() -> {
            choiceBox.marathon_select("Option 3");
        });
        new Wait("Wait for list item combo box to set option.") {
            @Override public boolean until() {
                String selected = choiceBox.getAttribute("selectionModel.getSelectedIndex");
                return selected.equals("2");
            }
        };
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxListViewSample();
    }
}
