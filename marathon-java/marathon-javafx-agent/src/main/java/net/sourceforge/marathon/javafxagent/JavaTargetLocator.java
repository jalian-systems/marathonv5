package net.sourceforge.marathon.javafxagent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import sun.awt.AppContext;

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
        private Window currentWindow;

        private ElementMap elements = new ElementMap();
        private Map<Component, IJavaElement> components = new HashMap<Component, IJavaElement>();

        private JWindow(Window window) {
            currentWindow = window;
            currentWindowHandle = JavaTargetLocator.getWindowHandle(window);
        }

        public Window getWindow() {
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

        public Dimension getSize() {
            return EventQueueWait.call_noexc(currentWindow, "getSize");
        }

        public Point getLocation() {
            return EventQueueWait.call_noexc(currentWindow, "getLocation");
        }

        public void setSize(int width, int height) {
            EventQueueWait.call_noexc(currentWindow, "setSize", width, height);
        }

        public void setLocation(int x, int y) {
            EventQueueWait.call_noexc(currentWindow, "setLocation", x, y);
        }

        public void maximize() {
            if (currentWindow instanceof JFrame) {
                EventQueueWait.call_noexc(currentWindow, "setExtendedState", JFrame.MAXIMIZED_BOTH);
            }
        }

        public IJavaElement addElement(IJavaElement je) {
            Component active = je instanceof IPseudoElement ? ((IPseudoElement) je).getParent().getComponent() : je.getComponent();
            IJavaElement found = components.get(active);
            if (found != null) {
                je.setId(found.getId());
                return je;
            }
            if (je instanceof IPseudoElement) {
                elements.put(((IPseudoElement) je).getParent().createId(), ((IPseudoElement) je).getParent());
                je.setId(((IPseudoElement) je).getParent().getId());
            } else {
                elements.put(je.createId(), je);
            }
            components.put(active, je instanceof IPseudoElement ? ((IPseudoElement) je).getParent() : je);
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

        public IJavaElement findElement(Component active) {
            IJavaElement found = components.get(active);
            if (found == null) {
                IJavaElement e = JavaElementFactory.createElement(active, driver, this);
                elements.put(e.createId(), e);
                components.put(active, e);
                found = e;
            }
            return found;
        }

        public IJavaElement findElementFromMap(Component active) {
            return components.get(active);
        }

        public JSONObject getWindowProperties() {
            String title = getTitle();
            String componentClassName = currentWindow.getClass().getName();
            String omapClassName = getOMapClassName();
            JSONObject object = new JSONObject();
            object.put("title", title).put("component.class.name", componentClassName).put("oMapClassName", omapClassName)
                    .put("tagName", "window");
            return object;
        }

        public String getOMapClassName() {
            if (currentWindow instanceof Frame || currentWindow instanceof Window || currentWindow instanceof Dialog) {
                String className = currentWindow.getClass().getName();
                Package pkg = currentWindow.getClass().getPackage();
                if (pkg == null)
                    return className;
                String pkgName = pkg.getName();
                if (!pkgName.startsWith("javax.swing") && !pkgName.startsWith("java.awt"))
                    return className;
                if (className.equals("javax.swing.ColorChooserDialog"))
                    return className;
                if (currentWindow instanceof JDialog) {
                    Component[] components = ((JDialog) currentWindow).getContentPane().getComponents();
                    if (components.length == 1 && components[0] instanceof JFileChooser)
                        return JFileChooser.class.getName() + "#Dialog";
                    if (components.length == 1 && components[0] instanceof JOptionPane)
                        return JOptionPane.class.getName() + "#Dialog_" + ((JOptionPane) components[0]).getMessageType() + "_"
                                + ((JOptionPane) components[0]).getOptionType();
                }
                return null;
            }
            return null;
        }

    }

    private IJavaAgent driver;
    private JWindow currentWindow;
    private Map<Window, JWindow> windows = new HashMap<Window, JavaTargetLocator.JWindow>();

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
        Window[] windows = getValidWindows();
        for (Window window : windows) {
            if (window.getName().equals(nameOrHandleOrTitle)) {
                setCurrentWindow(window);
                return driver;
            }
        }
        for (Window window : windows) {
            JWindow jw = new JWindow(window);
            String title = jw.getTitle();
            if (title != null && title.equals(nameOrHandleOrTitle)) {
                setCurrentWindow(window);
                return driver;
            }
        }
        for (Window window : windows) {
            String handle = getWindowHandle(window);
            if (nameOrHandleOrTitle.equals(handle)) {
                setCurrentWindow(window);
                return driver;
            }
        }
        throw new NoSuchWindowException("Cannot find window: " + nameOrHandleOrTitle, null);
    }

    public void deleteWindow() {
        Window w = getTopContainer().getWindow();
        windows.remove(w);
        getTopContainer().deleteWindow();
    }

    private Window[] getValidWindows() {
        List<Window> valid = new ArrayList<Window>();
        Set<AppContext> appContexts = AppContext.getAppContexts();
        for (AppContext appContext : appContexts) {
            Window[] windows = getWindows(appContext);
            for (Window window : windows) {
                if (window.getClass().getName().equals("javax.swing.SwingUtilities$SharedOwnerFrame"))
                    continue;
                if (window.getClass().getName().equals("javax.swing.Popup$HeavyWeightWindow"))
                    continue;
                if (window.isVisible()) {
                    valid.add(window);
                }
            }
        }
        return valid.toArray(new Window[valid.size()]);
    }

    private static Window[] getWindows(AppContext appContext) {
        synchronized (Window.class) {
            Window realCopy[];
            @SuppressWarnings("unchecked")
            Vector<WeakReference<Window>> windowList = (Vector<WeakReference<Window>>) appContext.get(Window.class);
            if (windowList != null) {
                int fullSize = windowList.size();
                int realSize = 0;
                Window fullCopy[] = new Window[fullSize];
                for (int i = 0; i < fullSize; i++) {
                    Window w = windowList.get(i).get();
                    if (w != null) {
                        fullCopy[realSize++] = w;
                    }
                }
                if (fullSize != realSize) {
                    realCopy = Arrays.copyOf(fullCopy, realSize);
                } else {
                    realCopy = fullCopy;
                }
            } else {
                realCopy = new Window[0];
            }
            return realCopy;
        }
    }

    private void setCurrentWindow(Window window) {
        JWindow jw = windows.get(window);
        if (jw == null) {
            jw = new JWindow(window);
            windows.put(window, jw);
        }
        currentWindow = jw;
        EventQueueWait.call_noexc(window, "toFront");
    }

    private static String getWindowHandle(Container topContainer) {
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
        Window[] windows = getValidWindows();
        for (Window window : windows) {
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
            Window[] windows = getValidWindows();
            if (windows.length == 1) {
                setCurrentWindow(windows[0]);
            } else if (windows.length > 1) {
                throw new NoSuchWindowException("No top level window is set. Java driver is unable to select from multiple windows", null);
            }
        }
        if (currentWindow == null)
            throw new NoSuchWindowException(
                    "No top level window is set. Java driver is unable to find a suitable implicit candidate", null);
        return currentWindow;
    }

    public JWindow getWindowForHandle(String windowHandle) {
        Window[] windows = getValidWindows();
        for (Window window : windows) {
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
        Component active = top.getWindow().getFocusOwner();
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
