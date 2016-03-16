package net.sourceforge.marathon.javafxagent.components;

import org.json.JSONObject;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.tree.TreeViewSample;
import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTreeViewElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        treeView = driver.findElementByTagName("tree-view");
    }

    @Test public void marathon_select_none() throws Throwable {
        TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        Platform.runLater(() -> treeView.marathon_select("[\"/Root node/Child Node 2\"]"));
        new Wait("Waiting for the text item to be selected.") {
            @Override public boolean until() {
                return 2 == treeViewNode.getSelectionModel().getSelectedIndex();
            }
        };
        Platform.runLater(() -> treeView.marathon_select("[]"));
        new Wait("Waiting for no items selected.") {
            @Override public boolean until() {
                return 0 == treeViewNode.getSelectionModel().getSelectedIndices().size();
            }
        };
    }

    @Test public void marathon_select() throws Throwable {
        TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        Platform.runLater(() -> treeView.marathon_select("[\"/Root node/Child Node 2\"]"));
        new Wait("Waiting for the text item to be selected.") {
            @Override public boolean until() {
                return 2 == treeViewNode.getSelectionModel().getSelectedIndex();
            }
        };
    }

    @Test public void marathon_select_multiple() throws Throwable {
        TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        treeViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Platform.runLater(() -> treeView.marathon_select("[\"/Root node/Child Node 1\",\"/Root node/Child Node 3/Child Node 7\"]"));
        new Wait("Waiting for the text item to be selected.") {
            @Override public boolean until() {
                return 2 == treeViewNode.getSelectionModel().getSelectedIndices().size();
            }
        };
    }

    @Test public void marathon_get_a_node() throws Throwable {
        JSONObject o = new JSONObject();
        o.put("select", "/Root node/Child Node 1");
        IJavaFXElement e = treeView.findElementByCssSelector(".::select-by-properties('" + o.toString() + "')");
        AssertJUnit.assertEquals("Child Node 1", e.getText());
    }

    @Test public void marathon_click_test() throws Throwable {
        treeView.click(0, 1, 56, 10);
        Thread.sleep(3000);
    }

    @Override protected Pane getMainPane() {
        return new TreeViewSample();
    }
}
