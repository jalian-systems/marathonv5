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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.marathon.compat.JavaCompatibility;
import net.sourceforge.marathon.javafxagent.components.JavaFXContextMenuElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXDirectoryChooserElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXFileChooserElement;
import net.sourceforge.marathon.javafxagent.components.JavaFXMenuBarElement;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class JavaFXTargetLocator {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTargetLocator.class.getName());

    public static class ElementMap {
        private Map<String, IJavaFXElement> elements = new HashMap<String, IJavaFXElement>();

        public void put(String id, IJavaFXElement je) {
            elements.put(id, je);
        }

        public IJavaFXElement get(String id) {
            return elements.get(id);
        }
    }

    public class JFXWindow {

        private String currentWindowHandle;
        private Stage currentWindow;

        private ElementMap elements = new ElementMap();
        private Map<Node, IJavaFXElement> components = new HashMap<Node, IJavaFXElement>();

        private JFXWindow(Stage window) {
            currentWindow = window;
            currentWindowHandle = JavaFXTargetLocator.getWindowHandle(window);
        }

        public Stage getWindow() {
            return currentWindow;
        }

        public String getHandle() {
            return currentWindowHandle;
        }

        public String getTitle() {
            return EventQueueWait.exec(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return new WindowTitle(currentWindow).getTitle();
                }
            });
        }

        public void deleteWindow() {
            EventQueueWait.call_noexc(currentWindow, "close");
        }

        public Dimension2D getSize() {
            return EventQueueWait.exec(new Callable<Dimension2D>() {
                @Override
                public Dimension2D call() throws Exception {
                    return new Dimension2D(currentWindow.getWidth(), currentWindow.getHeight());
                }
            });
        }

        public Point2D getLocation() {
            return EventQueueWait.exec(new Callable<Point2D>() {
                @Override
                public Point2D call() throws Exception {
                    return new Point2D(currentWindow.getX(), currentWindow.getY());
                }
            });
        }

        public void setSize(int width, int height) {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    currentWindow.setWidth(width);
                    currentWindow.setHeight(height);
                }
            });
        }

        public void setLocation(int x, int y) {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    currentWindow.setX(x);
                    currentWindow.setY(y);
                }
            });
        }

        public void maximize() {
            if (currentWindow instanceof Stage) {
                javafx.application.Platform.runLater(() -> currentWindow.setMaximized(true));
            }
        }

        public IJavaFXElement addElement(IJavaFXElement je) {
            Node active = je instanceof IPseudoElement ? ((IPseudoElement) je).getParent().getComponent() : je.getComponent();
            IJavaFXElement found = components.get(active);
            if (found != null) {
                je.setId(found.getElementId());
                return je;
            }
            elements.put(je.createId(), je);
            components.put(active, je);
            return je;
        }

        public IJavaFXElement findElement(String id) {
            String info = null;
            try {
                id = URLDecoder.decode(id, "utf8");
            } catch (UnsupportedEncodingException e1) {
                // Can't happen
            }
            int indexOf = id.indexOf('#');
            if (indexOf > 0) {
                String idPart = id.substring(0, indexOf);
                info = id.substring(indexOf + 1);
                id = idPart;
            }
            IJavaFXElement e = elements.get(id);
            if (e == null) {
                throw new NoSuchElementException("Could not find element for the given id in the topmost window", null);
            }
            if (info == null) {
                return e;
            }
            JSONObject pobj = new JSONObject(info);
            String selector = pobj.getString("selector");
            JSONArray parray = pobj.getJSONArray("parameters");
            Object[] params = new Object[parray.length()];
            for (int i = 0; i < parray.length(); i++) {
                params[i] = parray.get(i);
            }
            LOGGER.info("getByPseudoElement(" + selector + ", " + Arrays.asList(params));
            e = e.getByPseudoElement(selector, params).get(0);
            return e;
        }

        public IJavaFXElement findElement(Node active) {
            IJavaFXElement found = components.get(active);
            if (found == null) {
                IJavaFXElement e = JavaFXElementFactory.createElement(active, driver, this);
                elements.put(e.createId(), e);
                components.put(active, e);
                found = e;
            }
            return found;
        }

        public IJavaFXElement findElementFromMap(Node component) {
            return components.get(component);
        }

        public JSONObject getWindowProperties() {
            String title = getTitle();
            String componentClassName = currentWindow.getClass().getName();
            String omapClassName = getOMapClassName();
            JSONObject object = new JSONObject();
            object.put("title", title).put("component.class.name", componentClassName).put("oMapClassName", omapClassName)
                    .put("tagName", "window").put("type", componentClassName);
            return object;
        }

        public String getOMapClassName() {
            if (currentWindow instanceof Stage) {
                String className = currentWindow.getClass().getName();
                Package pkg = currentWindow.getClass().getPackage();
                if (pkg == null) {
                    return className;
                }
                String pkgName = pkg.getName();
                if (!pkgName.startsWith(Stage.class.getPackage().getName())) {
                    return className;
                }
                return null;
            }
            return null;
        }

        public IJavaFXElement findFileChooserElement() {
            IJavaFXElement e = new JavaFXFileChooserElement(driver, this);
            elements.put(e.createId(), e);
            return e;
        }

        public IJavaFXElement findDirectoryChooserElement() {
            IJavaFXElement e = new JavaFXDirectoryChooserElement(driver, this);
            elements.put(e.createId(), e);
            return e;
        }

        public IJavaFXElement findMenuBarElement() {
            MenuBar menuBar = (MenuBar) getMenuBar().get(0);
            IJavaFXElement e = new JavaFXMenuBarElement(menuBar, driver, this);
            elements.put(e.createId(), e);
            return e;
        }

        private List<Node> getMenuBar() {
            List<Node> nodes = new ArrayList<>();
            new Wait("Unable to find menu bar component") {
                @Override
                public boolean until() {
                    Node menubar = currentWindow.getScene().getRoot().lookup(".menu-bar");
                    if (menubar != null) {
                        nodes.add(menubar);
                    }
                    return nodes.size() > 0;
                }
            };
            ;
            return nodes;
        }

        public IJavaFXElement findContextMenuElement() {
            IJavaFXElement e = new JavaFXContextMenuElement((ContextMenu) getContextMenu().get(0), driver, this);
            elements.put(e.createId(), e);
            return e;
        }

        private List<Window> getContextMenu() {
            List<Window> contextMenus = new ArrayList<>();
            new Wait("Unable to context menu") {
                @Override
                public boolean until() {
                    Iterator<Window> windows = JavaCompatibility.getWindows();
                    while (windows.hasNext()) {
                        Window window = windows.next();
                        if (window instanceof ContextMenu) {
                            contextMenus.add(window);
                        }
                    }
                    return contextMenus.size() > 0;
                }
            };
            ;
            return contextMenus;
        }

    }

    private IJavaFXAgent driver;
    private JFXWindow currentWindow;
    private Map<Stage, JFXWindow> windows = new HashMap<Stage, JavaFXTargetLocator.JFXWindow>();

    public JavaFXTargetLocator(IJavaFXAgent driver) {
        this.driver = driver;
    }

    public IJavaFXAgent window(final String nameOrHandleOrTitle) {
        if (driver.getImplicitWait() != 0) {
            new EventQueueWait() {
                @Override
                public boolean till() {
                    try {
                        return window_internal(nameOrHandleOrTitle) != null;
                    } catch (NoSuchWindowException e) {
                        return false;
                    }
                }
            }.wait_noexc("Timedout waiting for the window", driver.getImplicitWait(), 50);
        }
        // We need the following call (even with set implicitWait) for throwing
        // an exception on error
        try {
            return EventQueueWait.exec(new Callable<IJavaFXAgent>() {
                @Override
                public IJavaFXAgent call() {
                    return window_internal(nameOrHandleOrTitle);
                }
            });
        } catch (Exception e) {
            throw new NoSuchWindowException(e.getMessage(), e);
        }
    }

    private IJavaFXAgent window_internal(String nameOrHandleOrTitle) {
        Stage[] windows = getValidWindows();
        for (Stage window : windows) {
            if (nameOrHandleOrTitle.equals(window.getTitle())) {
                setCurrentWindow(window);
                return driver;
            }
        }
        for (Stage window : windows) {
            JFXWindow jw = new JFXWindow(window);
            String title = jw.getTitle();
            if (title != null && title.equals(nameOrHandleOrTitle)) {
                setCurrentWindow(window);
                return driver;
            }
        }
        for (Stage window : windows) {
            String handle = getWindowHandle(window);
            if (nameOrHandleOrTitle.equals(handle)) {
                setCurrentWindow(window);
                return driver;
            }
        }
        throw new NoSuchWindowException("Cannot find window: " + nameOrHandleOrTitle, null);
    }

    public void deleteWindow() {
        Stage w = getTopContainer().getWindow();
        windows.remove(w);
        getTopContainer().deleteWindow();
    }

    private Stage[] getValidWindows() {
        ObservableList<Stage> stages = JavaCompatibility.getStages();
        List<Stage> valid = new ArrayList<Stage>();
        for (Stage window : stages) {
            if (window.isShowing()) {
                valid.add(window);
            }
        }
        return valid.toArray(new Stage[valid.size()]);
    }

    private void setCurrentWindow(Stage window) {
        JFXWindow jw = windows.get(window);
        if (jw == null) {
            jw = new JFXWindow(window);
            windows.put(window, jw);
        }
        currentWindow = jw;
        EventQueueWait.call_noexc(window, "toFront");
    }

    private static String getWindowHandle(Stage topContainer) {
        return Integer.toHexString(System.identityHashCode(topContainer));
    }

    public String getTitle() {
        return getTopContainer().getTitle();
    }

    public String getWindowHandle() {
        return getTopContainer().getHandle();
    }

    public Collection<String> getWindowHandles() {
        Collection<String> windowHandles = new ArrayList<String>();
        Stage[] windows = getValidWindows();
        for (Stage window : windows) {
            windowHandles.add(getWindowHandle(window));
        }
        return windowHandles;
    }

    public JFXWindow getTopContainer() {
        new Wait() {
            @Override
            public boolean until() {
                try {
                    _getTopContainer();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }.wait("No top level window available", 60000, 500);
        return _getTopContainer();
    }

    public JFXWindow getFocusedWindow() {
        Stage[] windows = getValidWindows();
        for (Stage stage : windows) {
            if (stage.isFocused())
                return new JFXWindow(stage);
        }
        if (windows.length > 0)
            return new JFXWindow(windows[0]);
        return null;
    }

    private JFXWindow _getTopContainer() {
        if (currentWindow == null) {
            Stage[] windows = getValidWindows();
            if (windows.length == 1) {
                setCurrentWindow(windows[0]);
            } else if (windows.length > 1) {
                throw new NoSuchWindowException("No top level window is set. Java driver is unable to select from multiple windows",
                        null);
            }
        }
        if (currentWindow == null) {
            throw new NoSuchWindowException(
                    "No top level window is set. Java driver is unable to find a suitable implicit candidate", null);
        }
        return currentWindow;
    }

    public JFXWindow getWindowForHandle(String windowHandle) {
        Stage[] windows = getValidWindows();
        for (Stage window : windows) {
            if (windowHandle.equals(getWindowHandle(window))) {
                return new JFXWindow(window);
            }
        }
        throw new NoSuchWindowException("No window found corresponding to the given window Handle", null);
    }

    public IJavaFXElement findElement(String id) {
        return getTopContainer().findElement(id);
    }

    public IJavaFXElement getActiveElement() {
        JFXWindow top = getTopContainer();
        Node active = top.getWindow().getScene().getFocusOwner();
        if (active == null) {
            throw new NoSuchElementException("Could not find focus owner for the topmost window", null);
        }
        return top.findElement(active);
    }

    public JFXWindow getCurrentWindow() {
        getTopContainer();
        return currentWindow;
    }

    public JSONObject getWindowProperties() {
        return getTopContainer().getWindowProperties();
    }

}
