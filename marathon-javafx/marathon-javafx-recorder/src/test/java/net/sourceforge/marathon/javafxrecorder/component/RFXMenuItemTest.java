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

import javafx.application.Platform;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import net.sourceforge.marathon.javafxagent.Wait;

public class RFXMenuItemTest extends RFXComponentTest {

    public static URL imgURL = RFXTabPaneTest.class.getResource("/net/sourceforge/marathon/javafxrecorder/component/middle.png");

    @Test public void menuPath() {
        List<String> path = new ArrayList<>();
        Platform.runLater(() -> {
            Menu menuFile = new Menu("File");
            MenuItem add = new MenuItem("Shuffle");
            MenuItem clear = new MenuItem("Clear");
            MenuItem exit = new MenuItem("Exit");
            menuFile.getItems().addAll(add, clear, new SeparatorMenuItem(), exit);
            RFXMenuItem rfxMenuItem = new RFXMenuItem(null, null);
            path.add(rfxMenuItem.getSelectedMenuPath(clear));
        });
        new Wait("Waiting for menu selection path") {
            @Override public boolean until() {
                return path.size() > 0;
            }
        };
        AssertJUnit.assertEquals("File>>Clear", path.get(0));
    }

    @Test public void menuItemIconNoText() {
        List<String> path = new ArrayList<>();
        Platform.runLater(() -> {
            Menu menuFile = new Menu("File");
            MenuItem add = new MenuItem("Shuffle");
            MenuItem clear = new MenuItem();
            clear.setGraphic(new ImageView(RFXTabPaneTest.imgURL.toString()));
            MenuItem exit = new MenuItem("Exit");
            menuFile.getItems().addAll(add, clear, new SeparatorMenuItem(), exit);
            RFXMenuItem rfxMenuItem = new RFXMenuItem(null, null);
            path.add(rfxMenuItem.getSelectedMenuPath(clear));
        });
        new Wait("Waiting for menu selection path") {
            @Override public boolean until() {
                return path.size() > 0;
            }
        };
        AssertJUnit.assertEquals("File>>middle", path.get(0));
    }

    @Test public void subMenuPath() {
        List<String> path = new ArrayList<>();
        Platform.runLater(() -> {
            Menu menuEdit = new Menu("Edit");
            Menu menuEffect = new Menu("Picture Effect");

            final MenuItem noEffects = new MenuItem("No Effects");

            menuEdit.getItems().addAll(menuEffect, noEffects);
            MenuItem add = new MenuItem("Shuffle");
            menuEffect.getItems().addAll(add);
            RFXMenuItem rfxMenuItem = new RFXMenuItem(null, null);
            path.add(rfxMenuItem.getSelectedMenuPath(add));
        });
        new Wait("Waiting for menu selection path") {
            @Override public boolean until() {
                return path.size() > 0;
            }
        };
        AssertJUnit.assertEquals("Edit>>Picture Effect>>Shuffle", path.get(0));
    }

    @Test public void specialChars() {
        List<String> path = new ArrayList<>();
        Platform.runLater(() -> {
            Menu menuView = new Menu("View");
            CheckMenuItem titleView = createMenuItem("Tit>le");
            CheckMenuItem binNameView = createMenuItem("Binomial name");
            CheckMenuItem picView = createMenuItem("Picture");
            CheckMenuItem descriptionView = createMenuItem("Decsription");

            menuView.getItems().addAll(titleView, binNameView, picView, descriptionView);
            RFXMenuItem rfxMenuItem = new RFXMenuItem(null, null);
            path.add(rfxMenuItem.getSelectedMenuPath(titleView));
        });
        new Wait("Waiting for menu selection path") {
            @Override public boolean until() {
                return path.size() > 0;
            }
        };
        AssertJUnit.assertEquals("View>>Tit\\>le", path.get(0));
    }

    @Test public void duplicateMenuPath() {
        List<String> path = new ArrayList<>();
        Platform.runLater(() -> {
            Menu menuFile = new Menu("File");
            MenuItem add = new MenuItem("Shuffle");

            MenuItem clear = new MenuItem("Clear");
            MenuItem clear1 = new MenuItem("Clear");
            MenuItem clear2 = new MenuItem("Clear");

            MenuItem exit = new MenuItem("Exit");

            menuFile.getItems().addAll(add, clear, clear1, clear2, new SeparatorMenuItem(), exit);
            RFXMenuItem rfxMenuItem = new RFXMenuItem(null, null);
            path.add(rfxMenuItem.getSelectedMenuPath(clear2));
        });
        new Wait("Waiting for menu selection path") {
            @Override public boolean until() {
                return path.size() > 0;
            }
        };
        AssertJUnit.assertEquals("File>>Clear(2)", path.get(0));
    }

    private static CheckMenuItem createMenuItem(String title) {
        CheckMenuItem cmi = new CheckMenuItem(title);
        cmi.setSelected(true);
        return cmi;
    }

    @Override protected Pane getMainPane() {
        return new StackPane(new MenuBar(new Menu("Menu test")));
    }
}
