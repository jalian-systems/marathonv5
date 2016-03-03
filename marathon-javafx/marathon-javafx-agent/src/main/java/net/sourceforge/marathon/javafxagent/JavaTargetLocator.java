package net.sourceforge.marathon.javafxagent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.stage.Stage;

public class JavaTargetLocator {

    public static class ElementMap {
        private Map<String, IJavaElement> elements = new HashMap<String, IJavaElement>();

        public void put(String id, IJavaElement je) {
            elements.put(id, je);
        }

        public IJavaElement get(String id) {
            return elements.get(id);
        }
    }

    public class JWindow {

        private String currentWindowHandle;
        private Stage currentWindow;

        private ElementMap elements = new ElementMap();
        private Map<Node, IJavaElement> components = new HashMap<Node, IJavaElement>();

        private JWindow(Stage window) {
            currentWindow = window;
            currentWindowHandle = JavaTargetLocator.getWindowHandle(window);
        }

        public Stage getWindow() {
            return currentWindow;
        }

        public String getHandle() {
            return currentWindowHandle;
        }

        public String getTitle() {
            return EventQueueWait.exec(new Callable<String>() {
                @Override public String call() throws Exception {
                    return new WindowTitle(currentWindow).getTitle();
                }
            });
        }

        public void deleteWindow() {
            EventQueueWait.call_noexc(currentWindow, "dispose");
        }

        public Dimension2D getSize() {
            return EventQueueWait.call_noexc(currentWindow, "getSize");
        }

        public Point2D getLocation() {
            return EventQueueWait.call_noexc(currentWindow, "getLocation");
        }

        public void setSize(int width, int height) {
            EventQueueWait.call_noexc(currentWindow, "setSize", width, height);
        }

        public void setLocation(int x, int y) {
            EventQueueWait.call_noexc(currentWindow, "setLocation", x, y);
        }

        public void maximize() {
            if (currentWindow instanceof Stage) {
                ((Stage) currentWindow).setMaximized(true);
            }
        }

        public IJavaElement addElement(IJavaElement je) {
            Node active = je instanceof IPseudoElement ? ((IPseudoElement) je).getParent().getComponent() : je.getComponent();
            IJavaElement found = components.get(active);
            if (found != null) {
                je.setId(found.getId());
                return je;
            }
            elements.put(je.createId(), je);
            components.put(active, je);
            return je;
        }

        public IJavaElement findElement(String id) {
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
            IJavaElement e = elements.get(id);
            if (e == null)
                throw new NoSuchElementException("Could not find element for the given id in the topmost window", null);
            if (info == null) {
                return e;
            }
            JSONObject pobj = new JSONObject(info);
            String selector = pobj.getString("selector");
            JSONArray parray = pobj.getJSONArray("parameters");
            Object[] params = new Object[parray.length()];
            for (int i = 0; i < parray.length(); i++)
                params[i] = parray.get(i);
            e = e.getByPseudoElement(selector, params).get(0);
            return e;
        }

        public IJavaElement findElement(Node active) {
            IJavaElement found = components.get(active);
            if (found == null) {
                IJavaElement e = JavaElementFactory.createElement(active, driver, this);
                elements.put(e.createId(), e);
                components.put(active, e);
                found = e;
            }
            return found;
        }

        public IJavaElement findElementFromMap(Node component) {
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
                if (pkg == null)
                    return className;
                String pkgName = pkg.getName();
                if (!pkgName.startsWith(Stage.class.getPackage().getName()))
                    return className;
                return null;
            }
            return null;
        }

    }

    private IJavaAgent driver;
    private JWindow currentWindow;
    private Map<Stage, JWindow> windows = new HashMap<Stage, JavaTargetLocator.JWindow>();

    public JavaTargetLocator(IJavaAgent driver) {
        this.driver = driver;
    }

    public IJavaAgent window(final String nameOrHandleOrTitle) {
        if (driver.getImplicitWait() != 0) {
            new EventQueueWait() {
                @Override public boolean till() {
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
            return EventQueueWait.exec(new Callable<IJavaAgent>() {
                @Override public IJavaAgent call() {
                    return window_internal(nameOrHandleOrTitle);
                }
            });
        } catch (Exception e) {
            throw new NoSuchWindowException(e.getMessage(), e);
        }
    }

    private IJavaAgent window_internal(String nameOrHandleOrTitle) {
        Stage[] windows = getValidWindows();
        for (Stage window : windows) {
            if (window.getTitle().equals(nameOrHandleOrTitle)) {
                setCurrentWindow(window);
                return driver;
            }
        }
        for (Stage window : windows) {
            JWindow jw = new JWindow(window);
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
        ObservableList<Stage> stages = StageHelper.getStages();
        List<Stage> valid = new ArrayList<Stage>();
        for (Stage window : stages) {
            if (window.isShowing()) {
                valid.add(window);
            }
        }
        return valid.toArray(new Stage[valid.size()]);
    }

    private void setCurrentWindow(Stage window) {
        JWindow jw = windows.get(window);
        if (jw == null) {
            jw = new JWindow(window);
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

    public JWindow getTopContainer() {
        new Wait() {
            @Override public boolean until() {
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

    private JWindow _getTopContainer() {
        if (currentWindow == null) {
            Stage[] windows = getValidWindows();
            if (windows.length == 1) {
                setCurrentWindow(windows[0]);
            } else if (windows.length > 1) {
                throw new NoSuchWindowException("No top level window is set. Java driver is unable to select from multiple windows",
                        null);
            }
        }
        if (currentWindow == null)
            throw new NoSuchWindowException(
                    "No top level window is set. Java driver is unable to find a suitable implicit candidate", null);
        return currentWindow;
    }

    public JWindow getWindowForHandle(String windowHandle) {
        Stage[] windows = getValidWindows();
        for (Stage window : windows) {
            if (windowHandle.equals(getWindowHandle(window)))
                return new JWindow(window);
        }
        throw new NoSuchWindowException("No window found corresponding to the given window Handle", null);
    }

    public IJavaElement findElement(String id) {
        return getTopContainer().findElement(id);
    }

    public IJavaElement getActiveElement() {
        JWindow top = getTopContainer();
        Node active = top.getWindow().getScene().getFocusOwner();
        if (active == null)
            throw new NoSuchElementException("Could not find focus owner for the topmost window", null);
        return top.findElement(active);
    }

    public JWindow getCurrentWindow() {
        getTopContainer();
        return currentWindow;
    }

    public JSONObject getWindowProperties() {
        return getTopContainer().getWindowProperties();
    }

}
