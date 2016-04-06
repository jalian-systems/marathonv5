package net.sourceforge.marathon.javafxagent.components;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.table.TableCellFactorySample;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;

public class JavaFXTableViewCheckBoxElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement tableView;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        tableView = driver.findElementByTagName("table-view");
    }

    @Test public void assertContent() {
        String expected = "[[\":checked\",\"Jacob\",\"Smith\",\"jacob.smith@example.com\"],[\":unchecked\",\"Isabella\",\"Johnson\",\"isabella.johnson@example.com\"],[\":checked\",\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\":checked\",\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\":unchecked\",\"Michael\",\"Brown\",\"michael.brown@example.com\"]]";
        AssertJUnit.assertEquals(expected, tableView.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new TableCellFactorySample();
    }
}
