package net.sourceforge.marathon.javafxagent.components;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.tabs.TabSample;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTabPaneElementTest extends JavaFXElementTest {

    public static URL imgURL = JavaFXTabPaneElementTest.class
            .getResource("/net/sourceforge/marathon/javafxagent/components/middle.png");
    private JavaFXAgent driver;
    private IJavaFXElement tabPane;

    @BeforeMethod public void initializeDriver() {
        driver = new JavaFXAgent();
        tabPane = driver.findElementByTagName("tab-pane");
    }

    @Test public void selectTab() {
        TabPane tabPaneNode = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        tabPane.marathon_select("Tab 2");
        new Wait("Waiting for the tab selection.") {
            @Override public boolean until() {
                return 1 == tabPaneNode.getSelectionModel().getSelectedIndex();
            }
        };
    }

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            tabPane.marathon_select("Tab 2");
            text.add(tabPane.getAttribute("text"));
        });
        new Wait("Waiting for the tab selection.") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Tab 2", text.get(0));
    }

    @Test public void selectAnInvalidTab() {
        AssertJUnit.assertEquals("0", tabPane.getAttribute("selectionModel.getSelectedIndex"));
        tabPane.marathon_select("Tab 21");
        AssertJUnit.assertEquals(false, tabPane.marathon_select("Tab 21"));
    }

    @Test public void selectTabWithNoText() {
        TabPane tabPaneNode = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Tab tab = new Tab();
                tab.setGraphic(new ImageView(imgURL.toString()));
                tabPaneNode.getTabs().add(tab);
            }
        });
        IJavaFXElement tab = tabPane.findElementByCssSelector(".::nth-tab(5)");
        tab.click();
        List<String> texts = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                texts.add(tab.getText());
            }
        });
        new Wait("Waiting for the tab selection.") {
            @Override public boolean until() {
                return texts.size() > 0;
            }
        };
        AssertJUnit.assertEquals("middle", texts.get(0));
    }

    @Test public void selectTabWithNoIconAndText() {
        TabPane tabPaneNode = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Tab tab = new Tab();
                tabPaneNode.getTabs().add(tab);
            }
        });
        IJavaFXElement tab = tabPane.findElementByCssSelector(".::nth-tab(5)");
        tab.click();
        List<String> texts = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                texts.add(tab.getText());
            }
        });
        new Wait("Waiting for the tab selection.") {
            @Override public boolean until() {
                return texts.size() > 0;
            }
        };
        AssertJUnit.assertEquals("tabIndex-4", texts.get(0));
    }

    @Test public void selectDuplicateTab() {
        TabPane tabPaneNode = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        IJavaFXElement tab = tabPane.findElementByCssSelector(".::nth-tab(2)");
        tab.click();
        new Wait("Waiting for the tab selection.") {
            @Override public boolean until() {
                return "Tab 2".equals(tab.getText());
            }
        };
        Platform.runLater(new Runnable() {
            @Override public void run() {
                Tab tab = new Tab("Tab 2");
                tabPaneNode.getTabs().add(2, tab);
            }
        });
        IJavaFXElement tab1 = tabPane.findElementByCssSelector(".::nth-tab(3)");
        tab1.click();
        new Wait("Waiting for the tab selection.") {
            @Override public boolean until() {
                return "Tab 2(1)".equals(tab1.getText());
            }
        };
    }

    public void assertContent() {
        AssertJUnit.assertEquals("[[\"Tab 1\",\"Tab 2\",\"Tab 3\",\"Tab 4\"]]", tabPane.getAttribute("content"));
    }

    @Override protected Pane getMainPane() {
        return new TabSample();
    }

}
