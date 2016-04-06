package net.sourceforge.marathon.javafxagent.components;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TreeViewSample1;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;

public class JavaFXTreeViewElementTest2 extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        treeView = driver.findElementByTagName("tree-view");
    }

    @Test public void assertContent() {
        TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
        Platform.runLater(() -> {
            TreeItem<?> treeItem = treeViewNode.getTreeItem(treeViewNode.getExpandedItemCount() - 1);
            treeItem.setExpanded(true);
        });
        String expected = "[[\"Root node\",\"Child Node 1\",\"Child Node 2\",\"Child Node 3\",\"Child Node 4\",\"Child Node 5\",\"Child Node 6\",\"Child Node 7\",\"Child Node 8\",\"Child Node 9\",\"Child Node 10\",\"Child Node 11\",\"Child Node 12\",\"Child Node 13\",\"Child Node 14\",\"Child Node 15\",\"Child Node 16\",\"Child Node 17\",\"Child Node 18\",\"Child Node 19\",\"Child Node 20\",\"Child Node 21\",\"Child Node 22\",\"Child Node 23\",\"Child Node 24\"]]";
        AssertJUnit.assertEquals(expected, treeView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new TreeViewSample1();
    }
}
