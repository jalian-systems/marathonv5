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
package net.sourceforge.marathon.javafxrecorder;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.sun.glass.ui.CommonDialogs;
import com.sun.javafx.stage.StageHelper;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sourceforge.marathon.fxcontextmenu.ContextMenuHandler;
import net.sourceforge.marathon.javafxagent.WindowTitle;
import net.sourceforge.marathon.javafxrecorder.component.FileChooserTransformer;
import net.sourceforge.marathon.javafxrecorder.component.MenuItemTransformer;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponentFactory;
import net.sourceforge.marathon.javafxrecorder.component.RFXFileChooser;
import net.sourceforge.marathon.javafxrecorder.component.RFXFolderChooser;
import net.sourceforge.marathon.javafxrecorder.component.RFXMenuItem;
import net.sourceforge.marathon.javafxrecorder.component.RFXUnknownComponent;
import net.sourceforge.marathon.javafxrecorder.ws.WSRecorder;

public class JavaFxRecorderHook implements EventHandler<Event> {

    public static final Logger LOGGER = Logger.getLogger(JavaFxRecorderHook.class.getName());

    private static final Logger logger = Logger.getLogger(JavaFxRecorderHook.class.getName());

    public static String DRIVER = "Java";
    public static String DRIVER_VERSION = "1.0";
    public static String PLATFORM = System.getProperty("java.runtime.name");
    public static String PLATFORM_VERSION = System.getProperty("java.version");
    public static String OS = System.getProperty("os.name");
    public static String OS_ARCH = System.getProperty("os.arch");
    public static String OS_VERSION = System.getProperty("os.version");

    public static EventType<Event> fileChooserEventType = new EventType<Event>("filechooser");
    public static EventType<Event> folderChooserEventType = new EventType<Event>("folderchooser");
    public EventHandler<ActionEvent> menuEvent = new MenuEventHandler();

    private static String windowTitle;

    private JSONOMapConfig objectMapConfiguration;
    private RFXComponentFactory finder;
    private IJSONRecorder recorder;
    private RFXComponent current;

    ContextMenuHandler contextMenuHandler;

    public JavaFxRecorderHook(int port) {
        try {
            logger.info("Starting HTTP Recorder on : " + port);
            recorder = new WSRecorder(port);
            objectMapConfiguration = recorder.getObjectMapConfiguration();
            setContextMenuTriggers(recorder.getContextMenuTriggers());
            finder = new RFXComponentFactory(objectMapConfiguration);
            contextMenuHandler = new ContextMenuHandler(recorder, finder);
            ObservableList<Stage> stages = StageHelper.getStages();
            for (Stage stage : stages) {
                addEventFilter(stage);
            }
            stages.addListener(new ListChangeListener<Stage>() {
                @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
                    c.next();
                    if (c.wasAdded()) {
                        List<? extends Stage> addedSubList = c.getAddedSubList();
                        for (Stage stage : addedSubList) {
                            addEventFilter(stage);
                        }
                    }
                    if (c.wasRemoved()) {
                        List<? extends Stage> removed = c.getRemoved();
                        for (Stage stage : removed) {
                            removeEventFilter(stage);
                        }
                    }
                }

            });
        } catch (UnknownHostException e) {
            logger.log(Level.WARNING, "Error in Recorder startup", e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error in Recorder startup", e);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static class ContextMenuTriggerCheck {
        private String keyModifiers;
        private String key;
        private String mouseModifiers;

        public ContextMenuTriggerCheck(String contextMenuKeyModifiers, String contextMenuKey, String menuModifiers) {
            this.keyModifiers = contextMenuKeyModifiers;
            this.key = contextMenuKey;
            this.mouseModifiers = menuModifiers;
        }

        public boolean isContextMenuEvent(Event event) {
            if (event instanceof MouseEvent) {
                return isContextMenuMouseEvent((MouseEvent) event);
            } else if (event instanceof KeyEvent) {
                return isContextMenuKeyEvent((KeyEvent) event);
            }
            return false;
        }

        private boolean isContextMenuKeyEvent(KeyEvent event) {
            if (!event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
                return false;
            }
            if (event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.SHIFT || event.getCode() == KeyCode.ALT
                    || event.getCode() == KeyCode.META) {
                return false;
            }
            String keyText = isModifiers(event) ? ketEventGetModifiersExText(event) + "+" : "";
            keyText += keyEventGetKeyText(event.getCode());
            StringBuffer contextMenuText = new StringBuffer();
            if (keyModifiers != null && !keyModifiers.equals("")) {
                contextMenuText.append(keyModifiers + "+");
            }
            contextMenuText.append(key);
            return contextMenuText.toString().equals(keyText);
        }

        public static boolean isModifiers(KeyEvent e) {
            if (e.isAltDown() || e.isControlDown() || e.isMetaDown() || e.isShiftDown()) {
                return true;
            }
            return false;
        }

        public static String ketEventGetModifiersExText(KeyEvent event) {
            StringBuffer sb = new StringBuffer();

            if (event.isControlDown()) {
                sb.append("Ctrl+");
            }
            if (event.isMetaDown()) {
                sb.append("Meta+");
            }
            if (event.isAltDown()) {
                sb.append("Alt+");
            }
            if (event.isShiftDown()) {
                sb.append("Shift+");
            }
            String text = sb.toString();
            if (text.equals("")) {
                return text;
            }
            return text.substring(0, text.length() - 1);
        }

        public static String keyEventGetKeyText(KeyCode keyCode) {
            if (keyCode == KeyCode.TAB) {
                return "Tab";
            }
            if (keyCode == KeyCode.CONTROL) {
                return "Ctrl";
            }
            if (keyCode == KeyCode.ALT) {
                return "Alt";
            }
            if (keyCode == KeyCode.SHIFT) {
                return "Shift";
            }
            if (keyCode == KeyCode.META) {
                return "Meta";
            }
            if (keyCode == KeyCode.SPACE) {
                return "Space";
            }
            if (keyCode == KeyCode.BACK_SPACE) {
                return "Backspace";
            }
            if (keyCode == KeyCode.HOME) {
                return "Home";
            }
            if (keyCode == KeyCode.END) {
                return "End";
            }
            if (keyCode == KeyCode.DELETE) {
                return "Delete";
            }
            if (keyCode == KeyCode.PAGE_UP) {
                return "Pageup";
            }
            if (keyCode == KeyCode.PAGE_DOWN) {
                return "Pagedown";
            }
            if (keyCode == KeyCode.UP) {
                return "Up";
            }
            if (keyCode == KeyCode.DOWN) {
                return "Down";
            }
            if (keyCode == KeyCode.LEFT) {
                return "Left";
            }
            if (keyCode == KeyCode.RIGHT) {
                return "Right";
            }
            if (keyCode == KeyCode.ENTER) {
                return "Enter";
            }
            return keyCode.getName();
        }

        private boolean isContextMenuMouseEvent(MouseEvent event) {
            return mouseModifiers.equals(mouseEventGetModifiersExText(event));
        }

        public static String mouseEventGetModifiersExText(MouseEvent event) {
            StringBuffer sb = new StringBuffer();
            if (event.isControlDown()) {
                sb.append("Ctrl+");
            }
            if (event.isMetaDown()) {
                sb.append("Meta+");
            }
            if (event.isAltDown()) {
                sb.append("Alt+");
            }
            if (event.isShiftDown()) {
                sb.append("Shift+");
            }
            if (event.isPrimaryButtonDown()) {
                sb.append("Button1+");
            }
            if (event.isMiddleButtonDown()) {
                sb.append("Button2+");
            }
            if (event.isSecondaryButtonDown()) {
                sb.append("Button3+");
            }
            String text = sb.toString();
            if (text.equals("")) {
                return text;
            }
            return text.substring(0, text.length() - 1);
        }
    }

    private ContextMenuTriggerCheck contextMenuTriggerCheck;

    protected Stage recordWindowState;

    private void setContextMenuTriggers(JSONObject jsonObject) {
        String contextMenuKeyModifiers = jsonObject.getString("contextMenuKeyModifiers");
        String contextMenuKey = jsonObject.getString("contextMenuKey");
        String menuModifiers = jsonObject.getString("menuModifiers");
        contextMenuTriggerCheck = new ContextMenuTriggerCheck(contextMenuKeyModifiers, contextMenuKey, menuModifiers);

    }

    private static final EventType<?> events[] = { MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED, MouseEvent.MOUSE_CLICKED,
            KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, KeyEvent.KEY_TYPED };

    private void removeEventFilter(Stage stage) {
        stage.getScene().getRoot().removeEventFilter(Event.ANY, JavaFxRecorderHook.this);
    }

    private void addEventFilter(Stage stage) {
        stage.getScene().getRoot().getProperties().put("marathon.fileChooser.eventType", fileChooserEventType);
        stage.getScene().getRoot().getProperties().put("marathon.folderChooser.eventType", folderChooserEventType);
        stage.getScene().getRoot().addEventFilter(Event.ANY, JavaFxRecorderHook.this);
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            @Override public void handle(WindowEvent event) {
                recorder.recordWindowClosing(new WindowTitle(stage).getTitle());
            }
        });
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                recordWindowState = stage;
            }
        });
        stage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                recordWindowState = stage;
            }
        });
        stage.xProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                recordWindowState = stage;
            }
        });
        stage.yProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                recordWindowState = stage;
            }
        });
        stage.getScene().getRoot().getProperties().put("marathon.menu.handler", menuEvent);
        stage.getScene().getRoot().addEventFilter(Event.ANY, JavaFxRecorderHook.this);
    }

    public static void premain(final String args, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new MenuItemTransformer());
        instrumentation.addTransformer(new FileChooserTransformer());
        logger.info("JavaVersion: " + System.getProperty("java.version"));
        final int port;
        if (args != null && args.trim().length() > 0) {
            port = Integer.parseInt(args.trim());
        } else {
            throw new Exception("Port number not specified");
        }
        windowTitle = System.getProperty("start.window.title", "");
        ObservableList<Stage> stages = StageHelper.getStages();
        stages.addListener(new ListChangeListener<Stage>() {
            boolean done = false;

            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
                if (done) {
                    return;
                }
                if (!"".equals(windowTitle)) {
                    logger.warning("WindowTitle is not supported yet... Ignoring it.");
                }
                c.next();
                if (c.wasAdded()) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        @Override public Object run() {
                            return new JavaFxRecorderHook(port);
                        }
                    });
                    done = true;
                }
            }
        });
    }

    @Override public void handle(Event event) {
        try {
            handle_internal(event);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void handle_internal(Event event) {
        if (contextMenuTriggerCheck.isContextMenuEvent(event) || contextMenuHandler.isShowing()) {
            event.consume();
            contextMenuHandler.showPopup(event);
            return;
        }
        if (event.getEventType().getName().equals("filechooser")) {
            handleFileChooser(event);
            return;
        }
        if (event.getEventType().getName().equals("folderchooser")) {
            handleFolderChooser(event);
            return;
        }
        if (!isVaildEvent(event.getEventType())) {
            return;
        }
        if (recordWindowState != null && recorder.isRawRecording()) {
            recorder.recordWindowState(new WindowTitle(recordWindowState).getTitle(), recordWindowState.xProperty().intValue(),
                    recordWindowState.yProperty().intValue(), recordWindowState.widthProperty().intValue(),
                    recordWindowState.heightProperty().intValue());
            recordWindowState = null;
        }
        if (!(event.getTarget() instanceof Node) || !(event.getSource() instanceof Node)) {
            return;
        }
        Point2D point = null;
        if (event instanceof MouseEvent) {
            point = new Point2D(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
        }
        if (recorder.isRawRecording()) {
            new RFXUnknownComponent((Node) event.getTarget(), objectMapConfiguration, null, recorder).handleRawRecording(recorder,
                    event);
            return;
        }
        RFXComponent c = finder.findRComponent((Node) event.getTarget(), point, recorder);
        if (!c.equals(current) && isFocusChangeEvent(event)) {
            if (current != null && isShowing(current)) {
                current.focusLost(c);
            }
            c.focusGained(current);
            current = c;
        }
        // We Need This.
        if (c.equals(current)) {
            c = current;
        }
        c.processEvent(event);
    }

    private void handleFolderChooser(Event event) {
        Node source = (Node) event.getSource();
        File folder = (File) source.getProperties().get("marathon.selectedFolder");
        new RFXFolderChooser(recorder).record(folder);
    }

    private void handleFileChooser(Event event) {
        Node source = (Node) event.getSource();
        CommonDialogs.FileChooserResult files = (CommonDialogs.FileChooserResult) source.getProperties()
                .get("marathon.selectedFiles");
        List<File> selectedFiles = null;
        if (files != null) {
            selectedFiles = files.getFiles();
        }
        new RFXFileChooser(recorder).record(selectedFiles);
    }

    private boolean isVaildEvent(EventType<? extends Event> eventType) {
        return Arrays.asList(events).contains(eventType);
    }

    private boolean isFocusChangeEvent(Event event) {
        return event.getEventType().equals(MouseEvent.MOUSE_PRESSED) || event.getEventType().equals(KeyEvent.KEY_PRESSED);
    }

    private boolean isShowing(RFXComponent component) {
        try {
            return component.getComponent().getScene().getWindow().isShowing();
        } catch (Throwable t) {
            return false;
        }
    }

    public class MenuEventHandler implements EventHandler<ActionEvent> {
        @Override public void handle(ActionEvent event) {
            if (event.getSource() instanceof Menu) {
                return;
            }
            new RFXMenuItem(recorder, objectMapConfiguration).record(event);
        }
    }
}
