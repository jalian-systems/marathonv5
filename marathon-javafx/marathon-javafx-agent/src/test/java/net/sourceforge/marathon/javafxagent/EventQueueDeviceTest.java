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
package net.sourceforge.marathon.javafxagent;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;

import ensemble.Sample;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.sourceforge.marathon.javafxagent.components.JavaFXElementTest;

public class EventQueueDeviceTest extends JavaFXElementTest {
    protected static TextField textField;
    protected IDevice driver;
    public static MenuBar menuBar;
    public static Menu menu;
    public static MenuItem exitItem;
    protected static boolean buttonClicked;
    protected static List<String> kss = new ArrayList<String>();
    protected static StringBuilder mouseText = new StringBuilder();
    protected static Button button;
    protected static CheckBox checkBox;
    protected static MouseButton clickedButton;
    protected static boolean exitItemCalled;

    public EventQueueDeviceTest() {
        super();
    }

    @BeforeMethod
    public void createDriver() {
        driver = new FXEventQueueDevice();
    }

    public static class TestApp extends Sample {

        private TestApp() {
            menuBar = new MenuBar();
            menu = new Menu("File");
            exitItem = new MenuItem("Exit");
            exitItem.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    exitItemCalled = true;
                }
            });
            menu.getItems().add(exitItem);
            menuBar.getMenus().add(menu);
            textField = new TextField();
            textField.setId("text");
            textField.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent e) {
                    addToList(e);
                }
            });
            button = new Button("Click me");
            button.setId("click-me");
            button.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                        buttonClicked = true;
                    }
                }
            });
            button.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent e) {
                    String s = "clicked";
                    if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
                        if (e.isPopupTrigger()) {
                            s = "contextClicked";
                        }
                        mouseText.append(getModifiersExText(e) + s + "(" + e.getClickCount() + ") ");
                        clickedButton = e.getButton();
                    }
                    if (e.getEventType() == MouseEvent.MOUSE_PRESSED) {
                        mouseText.append(getModifiersExText(e) + "pressed ");
                    }
                    if (e.getEventType() == MouseEvent.MOUSE_RELEASED) {
                        mouseText.append(getModifiersExText(e) + "released ");
                    }
                    if (e.getEventType() == MouseEvent.MOUSE_ENTERED) {
                        mouseText.append(getModifiersExText(e) + "entered ");
                    }
                    if (e.getEventType() == MouseEvent.MOUSE_EXITED) {
                        mouseText.append(getModifiersExText(e) + "exited ");
                    }
                }
            });
            checkBox = new CheckBox("Check Me!!");
            checkBox.setId("check-me");
            VBox root = new VBox();
            root.setId("vBox");
            root.getChildren().addAll(button, textField, menuBar, checkBox);
            getChildren().add(root);
        }
    }

    protected static String getModifiersExText(MouseEvent event) {
        StringBuffer buf = new StringBuffer();
        if (event.isMetaDown()) {
            buf.append("Meta");
            buf.append("+");
        }
        if (event.isControlDown()) {
            buf.append("Ctrl");
            buf.append("+");
        }
        if (event.isAltDown()) {
            buf.append("Alt");
            buf.append("+");
        }
        if (event.isShiftDown()) {
            buf.append("Shift");
            buf.append("+");
        }
        if (event.getButton() == MouseButton.PRIMARY) {
            buf.append("Button1");
            buf.append("+");
        }
        if (event.getButton() == MouseButton.MIDDLE) {
            buf.append("Button2");
            buf.append("+");
        }
        if (event.getButton() == MouseButton.SECONDARY) {
            buf.append("Button3");
            buf.append("+");
        }
        return buf.toString();
    }

    private static void addToList(KeyEvent e) {
        final StringBuilder sb = new StringBuilder();
        if (e.getEventType() == KeyEvent.KEY_PRESSED) {
            sb.append(checkModifier(e) + " pressed " + e.getCode());
        }
        if (e.getEventType() == KeyEvent.KEY_RELEASED) {
            sb.append(checkModifier(e) + " released " + e.getCode());
        }
        kss.add(sb.toString().trim());
    }

    private static String checkModifier(KeyEvent e) {
        String s = "";
        if (e.isShiftDown()) {
            s = "Shift";
        }
        if (e.isControlDown()) {
            s = "Ctrl";
        }
        if (e.isAltDown()) {
            s = "Alt";
        }
        if (e.isMetaDown()) {
            s = "Meta";
        }
        return s;
    }

    @Override
    protected Pane getMainPane() {
        return new TestApp();
    }
}
