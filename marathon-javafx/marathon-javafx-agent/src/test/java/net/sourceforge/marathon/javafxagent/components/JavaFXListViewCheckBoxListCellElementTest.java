package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample;
import net.sourceforge.marathon.javafx.tests.CheckBoxListViewSample.Item;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXListViewCheckBoxListCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement listView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        listView = driver.findElementByTagName("list-view");
    }

    @Test public void selectListItemCheckBoxNotSelectedSelected() {
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("checked");
        new Wait("Wait for list item check box to be selected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("true");
            }
        };
    }

    @Test public void selectListItemCheckBoxSelectedSelected() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Item x = (Item) listViewNode.getItems().get(2);
        x.setOn(true);
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("checked");
        new Wait("Wait for list item check box to be selected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("true");
            }
        };
    }

    @Test public void selectListItemCheckBoxSelectedNotSelected() {
        ListView<?> listViewNode = (ListView<?>) getPrimaryStage().getScene().getRoot().lookup(".list-view");
        Item x = (Item) listViewNode.getItems().get(2);
        x.setOn(true);
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("unchecked");
        new Wait("Wait for list item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
    }

    @Test public void selectListItemCheckBoxNotSelectedNotSelected() {
        IJavaFXElement item = listView.findElementByCssSelector(".::select-by-properties('{\"select\":\"Item 3\"}')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        new Wait("Wait for list item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
        cb.marathon_select("unchecked");
        new Wait("Wait for list item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
    }

    @Override protected Pane getMainPane() {
        return new CheckBoxListViewSample();
    }
}
