package net.sourceforge.marathon.javafxagent.components;

import org.json.JSONObject;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxTreeViewSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTreeViewCheckBoxTreeCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        treeView = driver.findElementByTagName("tree-view");
    }

    @Test public void selectTreeItemCheckBoxNotSelectedSelected() {
        JSONObject o = new JSONObject();
        o.put("select", "/Root node/Child Node 1");
        IJavaFXElement item = treeView.findElementByCssSelector(".::select-by-properties('" + o.toString() + "')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("Child Node 1:checked");
        new Wait("Wait for tree item check box to be selected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("true");
            }
        };
    }

    @Test public void selectTreeItemCheckBoxSelectedSelected() {
        TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        CheckBoxTreeItem<?> treeItem = (CheckBoxTreeItem<?>) treeViewNode.getTreeItem(2);
        treeItem.setSelected(true);
        JSONObject o = new JSONObject();
        o.put("select", "/Root node/Child Node 2");
        IJavaFXElement item = treeView.findElementByCssSelector(".::select-by-properties('" + o.toString() + "')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("Child Node 2:checked");
        new Wait("Wait for tree item check box to be selected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("true");
            }
        };
    }

    @Test public void selectTreeItemCheckBoxSelectedNotSelected() {
        TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        CheckBoxTreeItem<?> treeItem = (CheckBoxTreeItem<?>) treeViewNode.getTreeItem(2);
        treeItem.setSelected(true);
        JSONObject o = new JSONObject();
        o.put("select", "/Root node/Child Node 2");
        IJavaFXElement item = treeView.findElementByCssSelector(".::select-by-properties('" + o.toString() + "')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        cb.marathon_select("Child Node 2:unchecked");
        new Wait("Wait for tree item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
    }

    @Test public void selectTreeItemCheckBoxNotSelectedNotSelected() {
        JSONObject o = new JSONObject();
        o.put("select", "/Root node/Child Node 2");
        IJavaFXElement item = treeView.findElementByCssSelector(".::select-by-properties('" + o.toString() + "')");
        IJavaFXElement cb = item.findElementByCssSelector(".::editor");
        new Wait("Wait for tree item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
        cb.marathon_select("Child Node 2:unchecked");
        new Wait("Wait for tree item check box to be deselected") {
            @Override public boolean until() {
                String selected = cb.getAttribute("selected");
                return selected.equals("false");
            }
        };
    }

    @Test public void assertContent() {
        String expected = "[[\"Root node:unchecked\",\"Child Node 1:unchecked\",\"Child Node 2:unchecked\",\"Child Node 3:unchecked\"]]";
        AssertJUnit.assertEquals(expected, treeView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new CheckBoxTreeViewSample();
    }
}
