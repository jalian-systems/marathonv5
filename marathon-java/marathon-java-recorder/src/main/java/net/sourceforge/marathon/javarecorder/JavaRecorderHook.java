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
package net.sourceforge.marathon.javarecorder;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.JSONObject;

import net.sourceforge.marathon.component.FileDialogTransformer;
import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.component.RComponentFactory;
import net.sourceforge.marathon.component.RFileDialog;
import net.sourceforge.marathon.component.RUnknownComponent;
import net.sourceforge.marathon.contextmenu.ContextMenuHandler;
import net.sourceforge.marathon.javaagent.server.JavaServer;
import net.sourceforge.marathon.javarecorder.ws.WSRecorder;

public class JavaRecorderHook implements AWTEventListener, ChangeListener, ActionListener {

    public static final Logger LOGGER = Logger.getLogger(JavaRecorderHook.class.getName());

    public static String DRIVER = "Java";
    public static String DRIVER_VERSION = "1.0";
    public static String PLATFORM = System.getProperty("java.runtime.name");
    public static String PLATFORM_VERSION = System.getProperty("java.version");
    public static String OS = System.getProperty("os.name");
    public static String OS_ARCH = System.getProperty("os.arch");
    public static String OS_VERSION = System.getProperty("os.version");

    private static String windowTitle;

    private JSONOMapConfig objectMapConfiguration;
    private RComponentFactory finder;
    private IJSONRecorder recorder;
    private RComponent current;
    private String contextMenuKeyModifiers;
    private String contextMenuKey;
    private String menuModifiers;
    private ContextMenuHandler contextMenuHandler;

    public JavaRecorderHook(int port) {
        try {
            recorder = new WSRecorder(port);
            objectMapConfiguration = recorder.getObjectMapConfiguration();
            setContextMenuTriggers(recorder.getContextMenuTriggers());
            finder = new RComponentFactory(objectMapConfiguration);
            contextMenuHandler = new ContextMenuHandler(recorder, finder);
            Toolkit.getDefaultToolkit().addAWTEventListener(this,
                    AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                @Override public void eventDispatched(final AWTEvent event) {
                    if (event instanceof ComponentEvent && event.getSource() instanceof Component) {
                        AccessController.doPrivileged(new PrivilegedAction<Object>() {
                            @Override public Object run() {
                                handleListener((Component) event.getSource(), event, ChangeListener.class);
                                handleListener((Component) event.getSource(), event, ActionListener.class);
                                return null;
                            }
                        });
                    }
                }

                public void handleListener(Component c, AWTEvent event, Class<?> listener) {
                    if (event.getID() == ComponentEvent.COMPONENT_SHOWN) {
                        if (hasListener(c, listener)) {
                            removeListener(c, listener);
                            addListener(c, listener);
                        }
                    } else if (event.getID() == ComponentEvent.COMPONENT_HIDDEN) {
                        if (hasListener(c, listener)) {
                            removeListener(c, listener);
                        }
                    } else if (event.getID() == ComponentEvent.COMPONENT_MOVED) {
                        if (c instanceof Window) {
                            handleWindowStateEvent((Window) c);
                        }
                        if (hasListener(c, listener)) {
                            removeListener(c, listener);
                            addListener(c, listener);
                        }
                    } else if (event.getID() == ComponentEvent.COMPONENT_RESIZED) {
                        if (c instanceof Window) {
                            handleWindowStateEvent((Window) c);
                        }
                        if (hasListener(c, listener)) {
                            removeListener(c, listener);
                            addListener(c, listener);
                        }
                    }
                }

            }, AWTEvent.COMPONENT_EVENT_MASK);
            addListeners(ChangeListener.class);
            addListeners(ActionListener.class);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void handleWindowStateEvent(Window window) {
        if (!recorder.isRawRecording() || !window.isVisible()) {
            return;
        }
        Rectangle bounds = null;
        if (window instanceof Frame) {
            Frame w = (Frame) window;

            if (w.isUndecorated()) {
                // take this to mean: resizing and maximising isn't allowed
                // - this is to avoid a restore or resize operation on the
                // splash screen, for example
            } else {
                if (w.isResizable()) {
                    bounds = w.getBounds();
                }
            }
        } else if (window instanceof Dialog) {
            Dialog w = (Dialog) window;
            if (w.isUndecorated()) {
                // take this to mean: resizing and maximising isn't allowed
            } else {
                if (w.isResizable()) {
                    bounds = w.getBounds();
                }
                // No state to capture for dialogs - cannot maximise etc.
            }
        }
        if (bounds == null) {
            return;
        }
        RComponent r = finder.findRComponent(window, null, recorder);
        recorder.recordWindowState(r, bounds);
    }

    private void addListeners(Class<?> listener) {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            addListeners(window, listener);
        }
    }

    private void addListeners(Container c, Class<?> listener) {
        if (hasListener(c, listener)) {
            addListener(c, listener);
        }
        Component[] components = c.getComponents();
        for (Component component : components) {
            if (component instanceof Container) {
                addListeners((Container) component, listener);
            } else if (hasListener(component, listener)) {
                addListener(component, listener);
            }
        }
    }

    private boolean hasListener(Component c, Class<?> listener) {
        try {
            c.getClass().getMethod("add" + listener.getSimpleName(), listener);
            return true;
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    protected void addListener(Component c, Class<?> listener) {
        try {
            Method method = c.getClass().getMethod("add" + listener.getSimpleName(), listener);
            method.invoke(c, this);
        } catch (Exception e) {
            LOGGER.warning("Unable to add a change listener to " + c.getClass());
        }
    }

    protected void removeListener(Component c, Class<?> listener) {
        try {
            Method method = c.getClass().getMethod("remove" + listener.getSimpleName(), listener);
            method.invoke(c, this);
        } catch (Exception e) {
        }
    }

    private void setContextMenuTriggers(JSONObject jsonObject) {
        contextMenuKeyModifiers = jsonObject.getString("contextMenuKeyModifiers");
        contextMenuKey = jsonObject.getString("contextMenuKey");
        menuModifiers = jsonObject.getString("menuModifiers");
    }

    public static void premain(final String args, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new FileDialogTransformer());
        final int port;
        if (args != null && args.trim().length() > 0) {
            port = Integer.parseInt(args.trim());
        } else {
            throw new Exception("Port number not specified");
        }
        windowTitle = System.getProperty("start.window.title", "");
        final AWTEventListener listener = new AWTEventListener() {
            boolean done = false;

            @Override public void eventDispatched(AWTEvent event) {
                if (done) {
                    return;
                }
                LOGGER.info("Checking for window: " + Thread.currentThread());
                if (!"".equals(windowTitle)) {
                    if (!isValidWindow()) {
                        LOGGER.info("Not a valid window");
                        return;
                    }
                }
                done = true;
                LOGGER.info("JavaVersion: " + System.getProperty("java.version"));
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override public Object run() {
                        return new JavaRecorderHook(port);
                    }
                });
            }

            private boolean isValidWindow() {
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (windowTitle.startsWith("/")) {
                        if (getTitle(window).matches(windowTitle.substring(1))) {
                            return true;
                        }
                    } else {
                        if (getTitle(window).equals(windowTitle)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private String getTitle(Window window) {
                if (window instanceof Dialog) {
                    return ((Dialog) window).getTitle();
                } else if (window instanceof Frame) {
                    return ((Frame) window).getTitle();
                }
                return window.getClass().getName();
            }

        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
    }

    @Override public void eventDispatched(final AWTEvent event) {
        if (recorder.isPaused() || recorder.isInsertingScript() || JavaServer.handlingRequest)
            return;
        try {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override public Object run() {
                    Object source = event.getSource();
                    if (event instanceof ActionEvent && source instanceof FileDialog) {
                        handleFileDialog((ActionEvent) event);
                        return null;
                    }
                    if (!(source instanceof Component)) {
                        return null;
                    }
                    if (event instanceof WindowEvent) {
                        handleWindowEvent((WindowEvent) event);
                        return null;
                    }
                    if (event instanceof KeyEvent && isContextMenuKeySequence((KeyEvent) event)) {
                        ((KeyEvent) event).consume();
                        contextMenuHandler.showPopup((KeyEvent) event);
                        return null;
                    }
                    if (event instanceof MouseEvent && isContextMenuSequence((MouseEvent) event)) {
                        ((MouseEvent) event).consume();
                        if (current != null && SwingUtilities.getWindowAncestor(current.getComponent()) != null) {
                            current.focusLost(null);
                        }
                        contextMenuHandler.showPopup((MouseEvent) event);
                        return null;
                    }
                    if (contextMenuHandler.isContextMenuOn()) {
                        return null;
                    }
                    Component component = (Component) source;
                    if (SwingUtilities.getWindowAncestor(component) == null) {
                        return null;
                    }
                    if (recorder.isRawRecording()) {
                        new RUnknownComponent(component, objectMapConfiguration, null, recorder).handleRawRecording(recorder,
                                event);
                        return null;
                    }
                    int id = event.getID();
                    AWTEvent eventx;
                    if (event instanceof MouseEvent) {
                        eventx = SwingUtilities.convertMouseEvent(((MouseEvent) event).getComponent(), (MouseEvent) event,
                                (Component) source);
                    } else {
                        eventx = event;
                    }
                    RComponent c = finder.findRComponent(component,
                            eventx instanceof MouseEvent ? ((MouseEvent) eventx).getPoint() : null, recorder);
                    if (isFocusChangeEvent(id) && !c.equals(current)) {
                        if (current != null && SwingUtilities.getWindowAncestor(current.getComponent()) != null) {
                            current.focusLost(c);
                        }
                        c.focusGained(current);
                        current = c;
                    }
                    // We need this. Note that c.equals(current) is not same as
                    // c == current
                    if (c.equals(current)) {
                        c = current;
                    }
                    c.processEvent(eventx);
                    return null;
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void handleFileDialog(ActionEvent event) {
        FileDialog fileDialog = (FileDialog) event.getSource();
        String file = event.getActionCommand();
        String filePath;
        if (file == null || "".equals(file)) {
            filePath = "";
        } else {
            filePath = fileDialog.getDirectory() + file;
        }
        new RFileDialog(recorder).record(filePath);
    }

    private void handleWindowEvent(WindowEvent event) {
        if (event.getID() == WindowEvent.WINDOW_CLOSING) {
            RComponent r = finder.findRComponent(event.getWindow(), null, recorder);
            recorder.recordWindowClosing(r);
        }
        if (event.getID() != WindowEvent.WINDOW_CLOSING) {
            RComponent r = finder.findRComponent(event.getWindow(), null, recorder);
            try {
                recorder.recordFocusedWindow(r);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isFocusChangeEvent(int id) {
        return id != MouseEvent.MOUSE_ENTERED && id != MouseEvent.MOUSE_EXITED && id != MouseEvent.MOUSE_MOVED;
    }

    public boolean isContextMenuSequence(MouseEvent e) {
        return e.getID() == MouseEvent.MOUSE_PRESSED && inputEventGetModifiersExText(e.getModifiersEx()).equals(menuModifiers);
    }

    public static String inputEventGetModifiersExText(int modifiers) {
        StringBuffer sb = new StringBuffer();

        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
            sb.append("Ctrl+");
        }
        if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
            sb.append("Meta+");
        }
        if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
            sb.append("Alt+");
        }
        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
            sb.append("Shift+");
        }
        if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0) {
            sb.append("Button1+");
        }
        if ((modifiers & InputEvent.BUTTON2_DOWN_MASK) != 0) {
            sb.append("Button2+");
        }
        if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0) {
            sb.append("Button3+");
        }
        String text = sb.toString();
        if (text.equals("")) {
            return text;
        }
        return text.substring(0, text.length() - 1);
    }

    public boolean isContextMenuKeySequence(KeyEvent event) {
        return event.getID() == KeyEvent.KEY_PRESSED && isContextMenuKey(event);
    }

    public boolean isContextMenuKey(KeyEvent event) {
        return keyEventGetKeyText(event.getKeyCode()).equals(contextMenuKey)
                && inputEventGetModifiersExText(event.getModifiersEx()).equals(contextMenuKeyModifiers);
    }

    public static String keyEventGetKeyText(int keycode) {
        if (keycode == KeyEvent.VK_TAB) {
            return "Tab";
        }
        if (keycode == KeyEvent.VK_CONTROL) {
            return "Ctrl";
        }
        if (keycode == KeyEvent.VK_ALT) {
            return "Alt";
        }
        if (keycode == KeyEvent.VK_SHIFT) {
            return "Shift";
        }
        if (keycode == KeyEvent.VK_META) {
            return "Meta";
        }
        if (keycode == KeyEvent.VK_SPACE) {
            return "Space";
        }
        if (keycode == KeyEvent.VK_BACK_SPACE) {
            return "Backspace";
        }
        if (keycode == KeyEvent.VK_HOME) {
            return "Home";
        }
        if (keycode == KeyEvent.VK_END) {
            return "End";
        }
        if (keycode == KeyEvent.VK_DELETE) {
            return "Delete";
        }
        if (keycode == KeyEvent.VK_PAGE_UP) {
            return "Pageup";
        }
        if (keycode == KeyEvent.VK_PAGE_DOWN) {
            return "Pagedown";
        }
        if (keycode == KeyEvent.VK_UP) {
            return "Up";
        }
        if (keycode == KeyEvent.VK_DOWN) {
            return "Down";
        }
        if (keycode == KeyEvent.VK_LEFT) {
            return "Left";
        }
        if (keycode == KeyEvent.VK_RIGHT) {
            return "Right";
        }
        if (keycode == KeyEvent.VK_ENTER) {
            return "Enter";
        }
        return KeyEvent.getKeyText(keycode);
    }

    @Override public void stateChanged(ChangeEvent e) {
        if (recorder.isRawRecording()) {
            return;
        }
        if (!(e.getSource() instanceof Component) || current == null || e.getSource() != current.getComponent()) {
            return;
        }
        current.stateChanged(e);
    }

    @Override public void actionPerformed(ActionEvent e) {
        if (recorder.isRawRecording()) {
            return;
        }
        if (!(e.getSource() instanceof Component)) {
            return;
        }
        Component component = (Component) e.getSource();
        RComponent c = finder.findRComponent(component, null, recorder);
        c.actionPerformed(e);
    }

}
