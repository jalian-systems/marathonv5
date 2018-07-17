/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javafxrecorder.component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import ensemble.samples.controls.tabs.TabSample;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTabPaneTest extends RFXComponentTest {

    public static URL imgURL = RFXTabPaneTest.class.getResource("/net/sourceforge/marathon/javafxrecorder/component/middle.png");

    @Override
    protected Pane getMainPane() {
        return new TabSample();
    }

    @Test
    public void selectNormalTab() throws Throwable {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                rfxTabPane.mouseClicked(null);
            }
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording select = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Tab 1", select.getParameters()[0]);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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

    @Test
    public void getText() throws Throwable {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        List<String> text = new ArrayList<>();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                tabPane.getSelectionModel().select(1);
                rfxTabPane.mouseClicked(null);
                text.add(rfxTabPane.getAttribute("text"));
            }
        });
        new Wait("Waiting for tab pane text.") {
            @Override
            public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Tab 2", text.get(0));
    }

    @Test
    public void selectNoTextTab() {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Tab tab = new Tab();
                tab.setGraphic(new ImageView(RFXTabPaneTest.imgURL.toString()));
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

    @Test
    public void selectNoIconAndTextTab() {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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

    @Test
    public void tabDuplicates() throws Throwable {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            @Override
            public void run() {
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

    @Test
    public void tabMultipleDuplicates() throws Throwable {
        TabPane tabPane = (TabPane) getPrimaryStage().getScene().getRoot().lookup(".tab-pane");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
            @Override
            public void run() {
                tabPane.getTabs().add(2, new Tab("Tab 2"));
                tabPane.getTabs().add(3, new Tab("Tab 2"));
                RFXTabPane rfxTabPane = new RFXTabPane(tabPane, null, null, lr);
                tabPane.getSelectionModel().select(3);
                rfxTabPane.mouseClicked(null);
            }
        });
        recordings = lr.waitAndGetRecordings(2);
        select = recordings.get(1);
        AssertJUnit.assertEquals("recordSelect", select.getCall());
        AssertJUnit.assertEquals("Tab 2(2)", select.getParameters()[0]);
    }

}
