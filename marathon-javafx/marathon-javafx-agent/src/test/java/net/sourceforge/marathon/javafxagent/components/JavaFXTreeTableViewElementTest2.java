package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.CheckBoxTreeTableSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTreeTableViewElementTest2 extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement treeTable;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        treeTable = driver.findElementByTagName("tree-table-view");
    }

    @Test public void getText() {
        TreeTableView<?> treeTableNode = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            treeTableNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            treeTable.marathon_select("{\"rows\":[\"/Sales Department/Emma Jones\",\"/Sales Department/Anna Black\"]}");
            text.add(treeTable.getAttribute("text"));
        });
        new Wait("Waiting for tree table text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("{\"rows\":[\"/Sales Department/Emma Jones\",\"/Sales Department/Anna Black\"]}", text.get(0));
    }

    @Test public void assertContent() {
        String expected = "[[\"Sales Department\",\"\",\"unchecked\"],[\"Ethan Williams\",\"ethan.williams@example.com\",\"unchecked\"],[\"Emma Jones\",\"emma.jones@example.com\",\"unchecked\"],[\"Michael Brown\",\"michael.brown@example.com\",\"unchecked\"],[\"Anna Black\",\"anna.black@example.com\",\"unchecked\"],[\"Rodger York\",\"roger.york@example.com\",\"unchecked\"],[\"Susan Collins\",\"susan.collins@example.com\",\"unchecked\"]]";
        AssertJUnit.assertEquals(expected, treeTable.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new CheckBoxTreeTableSample();
    }

}
