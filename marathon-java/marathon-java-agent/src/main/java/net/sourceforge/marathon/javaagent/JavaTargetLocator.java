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
package net.sourceforge.marathon.javaagent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
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
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.components.FileDialogElement;
import sun.awt.AppContext;

public class JavaTargetLocator {

    public static final Logger LOGGER = Logger.getLogger(JavaTargetLocator.class.getName());

    public static JSONArray allProperties = new JSONArray();

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
        private List<List<String>> containerNP;

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
                @Override
                public String call() throws Exception {
                    WindowTitle windowTitle = new WindowTitle(currentWindow);
                    windowTitle.setContainerNamingProperties(containerNP);
                    return windowTitle.getTitle();
                }
            });
        }

        public void setContainerNamingProperties(List<List<String>> containerNP) {
            this.containerNP = containerNP;
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
                EventQueueWait.call_noexc(currentWindow, "setExtendedState", Frame.MAXIMIZED_BOTH);
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
            JavaElementPropertyAccessor pa = new JavaElementPropertyAccessor(currentWindow);
            for (int i = 0; i < allProperties.length(); i++) {
                String property = allProperties.getString(i);
                String attribute = pa.getAttribute(property);
                if (attribute != null)
                    object.put(property, attribute);
            }
            return object;
        }

        public String getOMapClassName() {
            if (currentWindow instanceof Frame || currentWindow instanceof Window || currentWindow instanceof Dialog) {
                String className = currentWindow.getClass().getName();
                Package pkg = currentWindow.getClass().getPackage();
                if (pkg == null) {
                    return className;
                }
                String pkgName = pkg.getName();
                if (!pkgName.startsWith("javax.swing") && !pkgName.startsWith("java.awt")) {
                    return className;
                }
                if (className.equals("javax.swing.ColorChooserDialog")) {
                    return className;
                }
                if (currentWindow instanceof JDialog) {
                    Component[] components = ((JDialog) currentWindow).getContentPane().getComponents();
                    if (components.length == 1 && components[0] instanceof JFileChooser) {
                        return JFileChooser.class.getName() + "#Dialog";
                    }
                    if (components.length == 1 && components[0] instanceof JOptionPane) {
                        return JOptionPane.class.getName() + "#Dialog_" + ((JOptionPane) components[0]).getMessageType() + "_"
                                + ((JOptionPane) components[0]).getOptionType();
                    }
                }
                return null;
            }
            return null;
        }

        public IJavaElement findFileDialogElement(JWindow dialog) {
            IJavaElement e = new FileDialogElement(dialog, driver, this);
            elements.put(e.createId(), e);
            return e;
        }

    }

    private IJavaAgent driver;
    private JWindow currentWindow;
    private Map<Window, JWindow> windows = new HashMap<Window, JavaTargetLocator.JWindow>();

    public JavaTargetLocator(IJavaAgent driver) {
        this.driver = driver;
    }

    public IJavaAgent window(final String windowDetails) {
        if (driver.getImplicitWait() != 0) {
            new EventQueueWait() {
                @Override
                public boolean till() {
                    try {
                        return window_internal(windowDetails) != null;
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
                @Override
                public IJavaAgent call() {
                    return window_internal(windowDetails);
                }
            });
        } catch (Exception e) {
            throw new NoSuchWindowException(e.getMessage(), e);
        }
    }

    private IJavaAgent window_internal(String windowDetails) {
        String nameOrHandleOrTitle = null;
        JSONObject winDetailsJsonObject = null;
        try {
            winDetailsJsonObject = new JSONObject(windowDetails);
            nameOrHandleOrTitle = winDetailsJsonObject.getString("title");
        } catch (Exception e) {
            nameOrHandleOrTitle = windowDetails;
        }
        Window[] windows = getValidWindows();
        for (Window window : windows) {
            JWindow jw = new JWindow(window);
            try {
                if (winDetailsJsonObject != null) {
                    jw.setContainerNamingProperties(getContainerNP(window, winDetailsJsonObject.getJSONObject("containerNP")));
                    allProperties = winDetailsJsonObject.getJSONArray("allProperties");
                }
            } catch (RuntimeException e) {
                LOGGER.warning(e.getMessage());
                throw e;
            }
            String title = jw.getTitle();
            if (nameOrHandleOrTitle.startsWith("/") && !nameOrHandleOrTitle.startsWith("//")) {
                if (title != null && title.matches(nameOrHandleOrTitle.substring(1))) {
                    setCurrentWindow(window);
                    return driver;
                }
            } else {
                if (nameOrHandleOrTitle.startsWith("//")) {
                    if (title != null && title.equals(nameOrHandleOrTitle.substring(1))) {
                        setCurrentWindow(window);
                        return driver;
                    }
                } else {
                    if (title != null && title.equals(nameOrHandleOrTitle)) {
                        setCurrentWindow(window);
                        return driver;
                    }
                }
            }
        }
        for (Window window : windows) {
            String handle = getWindowHandle(window);
            if (nameOrHandleOrTitle.equals(handle)) {
                setCurrentWindow(window);
                return driver;
            }
        }
        throw new NoSuchWindowException("Cannot find window: " + windowDetails, null);
    }

    private List<List<String>> getContainerNP(Window window, JSONObject map) {
        String wClassName = getWindowClassName(map, window);
        if (wClassName != null) {
            JSONArray npArray = map.getJSONArray(wClassName);
            List<List<String>> containerNP = new ArrayList<List<String>>();
            for (int i = 0; i < npArray.length(); i++) {
                List<String> pList = new ArrayList<String>();
                JSONArray pArray = (JSONArray) npArray.get(i);
                for (int j = 0; j < pArray.length(); j++) {
                    pList.add(pArray.getString(j));
                }
                containerNP.add(pList);
            }
            return containerNP;
        }
        return null;
    }

    private String getWindowClassName(JSONObject containerNP, Window window) {
        Class<?> klass = window.getClass();
        while (klass.getName() != null && !klass.getName().equals("java.lang.Object")) {
            if (containerNP.has(klass.getName()))
                return klass.getName();
            klass = klass.getSuperclass();
        }
        return null;
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
                if (window.getClass().getName().equals("javax.swing.SwingUtilities$SharedOwnerFrame")) {
                    continue;
                }
                if (window.getClass().getName().equals("javax.swing.Popup$HeavyWeightWindow")) {
                    continue;
                }
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
        return getFocusedWindowTitle();
    }

    private String getFocusedWindowTitle() {
        new Wait() {
            @Override
            public boolean until() {
                return findFocusWindow() != null;
            }

        }.wait("No focused window available", 60000, 500);
        Window focusWindow = findFocusWindow();
        if (focusWindow != null) {
            return new JWindow(focusWindow).getTitle();
        }
        return null;
    }

    private Window findFocusWindow() {
        Window w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
        if (w != null)
            return w;
        w = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        if (w != null)
            return w;

        Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (focusOwner != null)
            w = SwingUtilities.getWindowAncestor(focusOwner);
        if (w != null)
            return w;
        Window[] validWindows = getValidWindows();
        if (validWindows.length > 0)
            return validWindows[validWindows.length - 1];
        return null;
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

    private JWindow _getTopContainer() {
        if (currentWindow == null) {
            Window[] windows = getValidWindows();
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

    public JWindow getWindowForHandle(String windowHandle) {
        Window[] windows = getValidWindows();
        for (Window window : windows) {
            if (windowHandle.equals(getWindowHandle(window))) {
                return new JWindow(window);
            }
        }
        throw new NoSuchWindowException("No window found corresponding to the given window Handle", null);
    }

    public IJavaElement findElement(String id) {
        return getTopContainer().findElement(id);
    }

    public IJavaElement getActiveElement() {
        JWindow top = getTopContainer();
        Component active = top.getWindow().getFocusOwner();
        if (active == null) {
            throw new NoSuchElementException("Could not find focus owner for the topmost window", null);
        }
        return top.findElement(active);
    }

    public JWindow getCurrentWindow() {
        getTopContainer();
        return currentWindow;
    }

    public JSONObject getWindowProperties() {
        return getTopContainer().getWindowProperties();
    }

    public JWindow getFileDialogContainer() {
        Window[] pwindows = getValidWindows();
        for (Window window : pwindows) {
            if (window.getClass().getName().equals("java.awt.FileDialog")) {
                return new JWindow(window);
            }
        }
        throw new NoSuchElementException("Couldn't find file dialog window", null);
    }

}
