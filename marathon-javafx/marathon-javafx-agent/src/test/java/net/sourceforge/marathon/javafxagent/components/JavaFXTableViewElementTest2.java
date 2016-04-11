package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.table.TableCellFactorySample;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTableViewElementTest2 extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement tableView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        tableView = driver.findElementByTagName("table-view");
    }

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            tableView.marathon_select("{\"rows\":[1]}");
            text.add(tableView.getAttribute("text"));
        });
        new Wait("Wating for table text.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("{\"rows\":[1]}", text.get(0));
    }

    @Test public void assertContent() {
        String expected = "[[\":checked\",\"Jacob\",\"Smith\",\"jacob.smith@example.com\"],[\":unchecked\",\"Isabella\",\"Johnson\",\"isabella.johnson@example.com\"],[\":checked\",\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\":checked\",\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\":unchecked\",\"Michael\",\"Brown\",\"michael.brown@example.com\"]]";
        AssertJUnit.assertEquals(expected, tableView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new TableCellFactorySample();
    }
}
