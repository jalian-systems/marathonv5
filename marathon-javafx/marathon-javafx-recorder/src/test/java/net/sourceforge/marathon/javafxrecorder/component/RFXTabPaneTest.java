package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.tabs.TabSample;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxagent.components.JavaFXTabPaneElementTest;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTabPaneTest extends RFXComponentTest {

    @Override protected Pane getMainPane() {
        return new TabSample();
    }

    @Test public void selectNormalTab() throws Throwable {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                rfxTabPane.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Tab 1", select.getParameters()[0]);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                tabPane.getSelectionModel().select(1);
                rfxTabPane.mouseClicked(null);
            }
        });
        recordings = lr.waitAndGetRecordings(2);
        select = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Tab 2", select.getParameters()[0]);
    }

    @Test public void selectNoTextTab() {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Tab tab = new Tab();
                tab.setGraphic(new ImageView(JavaFXTabPaneElementTest.imgURL.toString()));
                tabPane.getTabs().add(tab);
                tabPane.getSelectionModel().select(4);
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                rfxTabPane.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("middle", select.getParameters()[0]);
    }

    @Test public void selectNoIconAndTextTab() {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                tabPane.getTabs().add(new Tab());
                tabPane.getSelectionModel().select(4);
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                rfxTabPane.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("tabIndex-4", select.getParameters()[0]);
    }

    @Test public void tabDuplicates() throws Throwable {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                tabPane.getSelectionModel().select(1);
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                rfxTabPane.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Tab 2", select.getParameters()[0]);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                tabPane.getTabs().add(2, new Tab("Tab 2"));
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                tabPane.getSelectionModel().select(2);
                rfxTabPane.mouseClicked(null);
            }
        });
        recordings = lr.waitAndGetRecordings(2);
        select = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Tab 2(1)", select.getParameters()[0]);
    }

    @Test public void assertContent() throws Throwable {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, new LoggingRecorder());
        final Object[] content = new Object[] { null };
        Platform.runLater(new Runnable() {
            @Override public void run() {
                content[0] = rfxTabPane.getContent();
            }
        });
        new Wait("Waiting for contens.") {

            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        Assert.assertEquals("[[\"Tab 1\",\"Tab 2\",\"Tab 3\",\"Tab 4\"]]", a.toString());
    }
}
