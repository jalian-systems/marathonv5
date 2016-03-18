package net.sourceforge.marathon.javafxagent.components;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.SplitPaneSample;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXSplitPaneElementTest extends JavaFXElementTest {

    private JavaFXAgent driver;
    private IJavaFXElement splitPane;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        splitPane = driver.findElementByTagName("split-pane");
    }

    @Test public void select() {
        SplitPane splitPaneNode = (SplitPane) getPrimaryStage().getScene().getRoot().lookup(".split-pane");
        JSONArray initialValue = new JSONArray(splitPaneNode.getDividerPositions());
        Platform.runLater(() -> {
            splitPane.marathon_select("[0.6]");
        });
        new Wait("Waiting for split pane to set divider location") {
            @Override public boolean until() {
                return initialValue.getDouble(0) != new JSONArray(splitPaneNode.getDividerPositions()).getDouble(0);
            }
        };
        JSONArray pa = new JSONArray(splitPaneNode.getDividerPositions());
        AssertJUnit.assertEquals(0.6, pa.getDouble(0), 0.2);
    }

    @Test public void select2() {
        SplitPane splitPaneNode = (SplitPane) getPrimaryStage().getScene().getRoot().lookup(".split-pane");
        JSONArray initialValue = new JSONArray(splitPaneNode.getDividerPositions());
        Platform.runLater(() -> {
            splitPane.marathon_select("[0.30158730158730157,0.8]");
        });
        new Wait("Waiting for split pane to set divider location") {
            @Override public boolean until() {
                return initialValue.getDouble(1) != new JSONArray(splitPaneNode.getDividerPositions()).getDouble(1);
            }
        };
        JSONArray pa = new JSONArray(splitPaneNode.getDividerPositions());
        AssertJUnit.assertEquals(0.8, pa.getDouble(1), 0.1);
    }

    @Override protected Pane getMainPane() {
        return new SplitPaneSample();
    }

}
