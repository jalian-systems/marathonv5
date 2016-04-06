package net.sourceforge.marathon.javafxagent.components;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ChoiceBoxTreeViewSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;

public class JavaFXTreeViewChoiceBoxTreeCellElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        treeView = driver.findElementByTagName("tree-view");
    }

    @Test public void assertContent() {
        String expected = "[[\"Root node\",\"Option 1\",\"Option 2\",\"Option 3\"]]";
        AssertJUnit.assertEquals(expected, treeView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new ChoiceBoxTreeViewSample();
    }
}
