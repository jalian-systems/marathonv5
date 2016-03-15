package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TextFieldListViewSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXListViewTextFieldListCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement listView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        listView = driver.findElementByTagName("list-view");
    }

    @Test public void select() {
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Row 5\"}')");
        IJavaFXElement tf = item.findElementByCssSelector(".::editor");
        tf.marathon_select("Row 5 Modified");
        new Wait("Wait for list item text field to set text.") {
            @Override public boolean until() {
                String selected = tf.getAttribute("text");
                return selected.equals("Row 5 Modified");
            }
        };
    }

    @Override protected Pane getMainPane() {
        return new TextFieldListViewSample();
    }
}
