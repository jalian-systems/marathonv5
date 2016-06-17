/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.List;

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

    @Test public void getText() {
        List<String> text = new ArrayList<>();
        Platform.runLater(() -> {
            splitPane.marathon_select("[0.6]");
            text.add(splitPane.getAttribute("text"));
        });
        new Wait("Waiting for split pane text") {
            @Override public boolean until() {
                return text.size() > 0;
            }
        };
        AssertJUnit.assertEquals("[0.6,0.6008064516129032]", text.get(0));
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
