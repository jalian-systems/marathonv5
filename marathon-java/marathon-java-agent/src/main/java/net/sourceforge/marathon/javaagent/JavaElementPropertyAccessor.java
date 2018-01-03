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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.Box.Filler;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.components.ContextManager;

public class JavaElementPropertyAccessor {

    public static Logger LOGGER = Logger.getLogger(JavaElementPropertyAccessor.class.getName());

    protected Component component;
    private static final Pattern arrayPattern = Pattern.compile("(.*)\\[([^\\]]*)\\]$");

    public JavaElementPropertyAccessor(Component component) {
        this.component = component;
    }

    public String getAttribute(final String name) {
        return getAttribute(name, false);
    }

    public String getAttribute(final String name, final boolean skipSelf) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return _getAttribute(name, skipSelf);
            }
        });
    }

    public String _getAttribute(String name, boolean skipSelf) {
        if (name.startsWith("keystrokeFor-")) {
            return getKeyStrokeFor(name.substring("keystrokeFor-".length()));
        }
        String[] split = name.split("\\.");
        String first = split[0];
        Object attributeObject = null;
        try {
            if (!skipSelf) {
                attributeObject = getAttributeObject(this, first);
            }
        } catch (UnsupportedCommandException e) {
        }
        if (attributeObject == null) {
            Component c = component;
            if (this instanceof IPseudoElement) {
                c = ((IPseudoElement) this).getPseudoComponent();
            }
            attributeObject = getAttributeObject(c, first);
            if (attributeObject == null) {
                return null;
            }
        }
        for (int i = 1; i < split.length; i++) {
            attributeObject = getAttributeObject(attributeObject, split[i]);
            if (attributeObject == null) {
                return null;
            }
        }
        return toString(attributeObject);
    }

    private String getKeyStrokeFor(String action) {
        JSONArray r = new JSONArray();
        if (component instanceof JComponent) {
            InputMap inputMap = ((JComponent) component).getInputMap();
            KeyStroke[] allKeys = inputMap.allKeys();
            for (KeyStroke ks : allKeys) {
                if (action.equals(inputMap.get(ks))) {
                    r.put(ks.toString());
                }
            }
        }
        if (r.length() > 0) {
            return r.toString();
        }
        return null;
    }

    private String toString(Object attributeObject) {
        return removeClassName(attributeObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#getAttributeObject(java
     * .lang.String)
     */
    public Object getAttributeObject(Object srcobj, String name) {
        Object o = null;
        Matcher matcher = arrayPattern.matcher(name);
        if (matcher.matches()) {
            try {
                o = getAttributeObject(srcobj, getGetMethod(matcher.group(1)));
                if (o != null) {
                    if (o instanceof Map<?, ?>) {
                        o = ((Map<?, ?>) o).get(matcher.group(2));
                        LOGGER.info("Accessing map with " + matcher.group(2) + " = " + o);
                    } else
                        o = EventQueueWait.call(o, "get", Integer.parseInt(matcher.group(2)));
                }
            } catch (NoSuchMethodException e) {
                LOGGER.info("Method get not found for " + o.getClass());
            }
        }
        try {
            if (o == null) {
                String isMethod = getIsMethod(name);
                o = EventQueueWait.call(srcobj, isMethod);
            }
        } catch (Throwable e) {
            if (!(e instanceof NoSuchMethodException)) {
                return null;
            }
        }
        try {
            if (o == null) {
                String getMethod = getGetMethod(name);
                o = EventQueueWait.call(srcobj, getMethod);
            }
        } catch (Throwable e) {
        }
        try {
            if (o == null) {
                o = EventQueueWait.call(srcobj, name);
            }
        } catch (Throwable e) {
        }
        try {
            if (o == null) {
                o = getFieldValue(srcobj, name);
            }
        } catch (Throwable e) {
        }
        if (o != null && o.getClass().isArray() && !o.getClass().getComponentType().isPrimitive()) {
            ArrayList<Object> lo = new ArrayList<Object>();
            Object[] oa = (Object[]) o;
            for (Object object : oa) {
                lo.add(object);
            }
            o = lo;
        }
        if (o != null && o instanceof Map<?, ?>) {
            Map<String, Object> lm = new HashMap<String, Object>();
            Map<?, ?> om = (Map<?, ?>) o;
            Set<?> keySet = om.keySet();
            for (Object object : keySet) {
                lm.put(object.toString(), om.get(object));
            }
            o = lm;
        }
        if (o != null && o instanceof Collection<?>) {
            ArrayList<Object> lo = new ArrayList<Object>();
            Collection<?> oa = (Collection<?>) o;
            for (Object object : oa) {
                lo.add(object);
            }
            o = lo;
        }
        if (o == null && srcobj instanceof Collection<?>) {
            ArrayList<Object> lo = new ArrayList<Object>();
            Collection<?> c = (Collection<?>) srcobj;
            for (Object object : c) {
                lo.add(getAttributeObject(object, name));
            }
            return lo;
        }
        return o;
    }

    private Object getFieldValue(Object component, String name) {
        Field f = null;
        Class<? extends Object> klass = component.getClass();
        while (f == null && klass != null) {
            try {
                f = klass.getDeclaredField(name);
            } catch (SecurityException e) {
                return null;
            } catch (NoSuchFieldException e) {
                klass = klass.getSuperclass();
            }
        }
        if (f == null) {
            return null;
        }
        boolean accessible = f.isAccessible();
        try {
            f.setAccessible(true);
            return f.get(component);
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } finally {
            f.setAccessible(accessible);
        }
        return null;
    }

    private String getIsMethod(String name) {
        return "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private String getGetMethod(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#hasAttribue(java.lang
     * .String)
     */
    public boolean hasAttribue(String name) {
        return hasMethod(getIsMethod(name)) || hasMethod(getGetMethod(name));
    }

    private boolean hasMethod(String name) {
        try {
            component.getClass().getMethod(name);
            return true;
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getText()
     */
    public String getText() {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return _getText();
            }
        });
    }

    public String _getText() {
        Component c = component;
        if (this instanceof IPseudoElement) {
            c = ((IPseudoElement) this).getPseudoComponent();
        }
        Object attributeObject = getAttributeObject(c, c instanceof JToggleButton ? "selected" : "text");
        if (attributeObject == null) {
            return null;
        }
        return attributeObject.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaElement#isDisplayed()
     */
    final public boolean isDisplayed() {
        return EventQueueWait.<Boolean> call_noexc(this, "_isDisplayed");
    }

    public boolean _isDisplayed() {
        try {
            component.getLocationOnScreen();
        } catch (IllegalComponentStateException e) {
            return false;
        } catch (Throwable e) {
            throw new UnsupportedCommandException("getLocationOnScreen is not supported by " + component.getClass().getName(), e);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getLocation()
     */
    public Point getLocation() {
        return EventQueueWait.call_noexc(this, "_getLocation");
    }

    public Point _getLocation() {
        java.awt.Point p = component.getLocation();
        return new Point(p.x, p.y);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getSize()
     */
    public Dimension getSize() {
        return EventQueueWait.call_noexc(this, "_getSize");
    }

    public Dimension _getSize() {
        java.awt.Dimension d = component.getSize();
        return new Dimension(d.width, d.height);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaElement#isSelected()
     */
    public boolean isSelected() {
        return EventQueueWait.<Boolean> call_noexc(this, "_isSelected");
    }

    public boolean _isSelected() {
        String selected = _getAttribute("selected", true);
        if (selected != null) {
            return Boolean.parseBoolean(selected);
        }
        throw new UnsupportedCommandException("isSelected is not supported by " + component.getClass().getName(), null);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaElement#isEnabled()
     */
    final public boolean isEnabled() {
        return EventQueueWait.<Boolean> call_noexc(this, "_isEnabled");
    }

    public boolean _isEnabled() {
        return component.isEnabled();
    }

    public Component getComponent() {
        return component;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getTagName()
     */
    public String getTagName() {
        Class<?> c = component.getClass();
        if (this instanceof IPseudoElement) {
            c = ((IPseudoElement) this).getPseudoComponent().getClass();
        }
        return getTagName(c);
    }

    private String getTagName(Class<?> klass) {
        Class<?> javaClass = findJavaClass(klass);
        Class<?> c = javaClass;
        String simpleName = c.getSimpleName();
        while ("".equals(simpleName)) {
            c = c.getSuperclass();
            simpleName = c.getSimpleName();
        }
        return hyphenated(c);
    }

    private String hyphenated(Class<?> klass) {
        String r = klass.getSimpleName();
        if ((klass.getPackage().getName().equals("javax.swing") || klass.getPackage().getName().startsWith("javax.swing."))
                && r.charAt(0) == 'J') {
            r = r.substring(1);
        }
        return r.substring(0, 1).toLowerCase() + r.substring(1).replaceAll("[A-Z][A-Z]*", "-$0").toLowerCase();
    }

    private Class<?> findJavaClass(Class<?> c) {
        while (c.getPackage() == null
                || !c.getPackage().getName().startsWith("java.awt") && !c.getPackage().getName().startsWith("javax.swing")) {
            c = c.getSuperclass();
        }
        return c;
    }

    public String getType() {
        String name = component.getClass().getName();
        if (name.startsWith("javax.swing")) {
            return name.substring("javax.swing.".length());
        }
        return name;
    }

    public String getLabelText() {
        if (component instanceof JLabel) {
            String text = ((JLabel) component).getText();
            if (text != null && !text.equals("")) {
                return "lbl:" + stripLastColon(text);
            }
        }
        return null;
    }

    public String getInstanceOf() {
        Class<?> klass = component.getClass();
        while (klass != null && klass.getPackage() != null && !klass.getPackage().getName().startsWith("javax.swing")
                && !klass.getPackage().getName().startsWith("java.awt")) {
            klass = klass.getSuperclass();
        }
        return klass == null ? null : klass.getName();
    }

    public Point getPosition() {
        return component.getLocationOnScreen();
    }

    public String getPrecedingLabel() {
        Container container = component.getParent();
        if (container == null) {
            return null;
        }
        List<Component> allComponents = findAllComponents();
        // Find labels in the same row (LTR)
        // In the same row: labelx < componentx, labely >= componenty
        Point locComponent = component.getLocationOnScreen();
        List<Component> rowLeft = new ArrayList<Component>();
        for (Component label : allComponents) {
            Point locLabel = label.getLocationOnScreen();
            if (!(label instanceof JPanel) && locLabel.getX() < locComponent.getX() && locLabel.getY() >= locComponent.getY()
                    && locLabel.getY() <= locComponent.getY() + component.getHeight() && !(label instanceof Filler)) {
                rowLeft.add(label);
            }
        }
        Collections.sort(rowLeft, new Comparator<Component>() {
            @Override public int compare(Component o1, Component o2) {
                Point locO1 = o1.getLocationOnScreen();
                Point locO2 = o2.getLocationOnScreen();
                return (int) (locO1.getX() - locO2.getX());
            }
        });
        if (rowLeft.size() > 0 && rowLeft.get(rowLeft.size() - 1) instanceof JLabel) {
            return stripLastColon(((JLabel) rowLeft.get(rowLeft.size() - 1)).getText().trim());
        }
        return null;
    }

    private List<Component> findAllComponents() {
        Component top = getTopWindow(component);
        List<Component> allComponents = new ArrayList<Component>();
        if (top != null) {
            fillUp(allComponents, top);
        }
        return allComponents;
    }

    private void fillUp(List<Component> allComponents, Component c) {
        if (!c.isVisible() || !c.isShowing()) {
            return;
        }
        allComponents.add(c);
        if (c instanceof Container) {
            Component[] components = ((Container) c).getComponents();
            for (Component component : components) {
                fillUp(allComponents, component);
            }
        }
        if (c instanceof Window) {
            Window[] ownedWindows = ((Window) c).getOwnedWindows();
            for (Window window : ownedWindows) {
                fillUp(allComponents, window);
            }
        }
    }

    private Component getTopWindow(Component c) {
        while (c != null) {
            if (c instanceof Window || ContextManager.isContext(c)) {
                return c;
            }
            c = c.getParent();
        }
        return null;
    }

    public String getOMapClassName() {
        if (component instanceof Frame || component instanceof Window || component instanceof Dialog
                || component instanceof JInternalFrame) {
            String className = component.getClass().getName();
            Package pkg = component.getClass().getPackage();
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
            if (component instanceof JDialog) {
                Component[] components = ((JDialog) component).getContentPane().getComponents();
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

    public String getOMapClassSimpleName() {
        if (component instanceof Frame || component instanceof Window || component instanceof Dialog
                || component instanceof JInternalFrame) {
            String className = component.getClass().getName();
            String simpleName = component.getClass().getSimpleName();
            Package pkg = component.getClass().getPackage();
            if (pkg == null) {
                return simpleName;
            }
            String pkgName = pkg.getName();
            if (!pkgName.startsWith("javax.swing") && !pkgName.startsWith("java.awt")) {
                return simpleName;
            }
            if (className.equals("javax.swing.ColorChooserDialog")) {
                return simpleName;
            }
            if (component instanceof JDialog) {
                Component[] components = ((JDialog) component).getContentPane().getComponents();
                if (components.length == 1 && components[0] instanceof JFileChooser) {
                    return JFileChooser.class.getSimpleName() + "#Dialog";
                }
                if (components.length == 1 && components[0] instanceof JOptionPane) {
                    return JOptionPane.class.getSimpleName() + "#Dialog";
                }
            }
            return null;
        }
        return null;
    }

    public int getIndexOfType() {
        List<Component> allComponents = findAllComponents();
        int index = 0;
        Class<? extends Component> klass = component.getClass();
        String tagName = getTagName(klass);
        for (Component c : allComponents) {
            if (c == component) {
                return index;
            }
            if (getTagName(c.getClass()).equals(tagName)) {
                index++;
            }
        }
        return -1;
    }

    public String getFieldName() {
        List<String> fieldNames = getFieldNames();
        if (fieldNames.size() == 0) {
            return null;
        }
        return fieldNames.get(0);
    }

    public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        Container container = component.getParent();
        while (container != null) {
            findFields(component, container, fieldNames);
            container = container.getParent();
        }
        return fieldNames;
    }

    private void findFields(Component current, Component container, List<String> fieldNames) {
        Field[] declaredFields = container.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            boolean accessible = field.isAccessible();
            try {
                field.setAccessible(true);
                Object o = field.get(container);
                if (o == current) {
                    fieldNames.add(field.getName());
                }
            } catch (Throwable t) {
            } finally {
                field.setAccessible(accessible);
            }
        }
    }

    public String getCText() {
        Object o = getAttributeObject(getComponent(), "text");
        if (o == null || !(o instanceof String) || o.equals("")) {
            return null;
        }
        return (String) o;
    }

    public String getButtonText() {
        if (component instanceof AbstractButton) {
            return getCText();
        }
        return null;
    }

    public String getButtonIconFile() {
        if (component instanceof AbstractButton) {
            return getIconFile();
        }
        return null;
    }

    public String getIconFile() {
        Object o = getAttributeObject(getComponent(), "icon");
        if (o == null || !(o instanceof Icon)) {
            return null;
        }
        Icon icon = (Icon) o;
        if (icon instanceof ImageIcon) {
            String description = ((ImageIcon) icon).getDescription();
            if (description != null && description.length() != 0) {
                return mapFromImageDescription(description);
            }
        }
        return null;
    }

    public static String mapFromImageDescription(String description) {
        try {
            String name = new URL(description).getPath();
            if (name.lastIndexOf('/') != -1) {
                name = name.substring(name.lastIndexOf('/') + 1);
            }
            if (name.lastIndexOf('.') != -1) {
                name = name.substring(0, name.lastIndexOf('.'));
            }
            return name;
        } catch (MalformedURLException e) {
            return description;
        }
    }

    public String getClassName() {
        return component.getClass().getName();
    }

    public boolean getEnabled() {
        return component.isEnabled();
    }

    public String getToolTipText() {
        if (component instanceof JComponent) {
            return ((JComponent) component).getToolTipText();
        }
        return null;
    }

    public String getName() {
        return getComponent().getName();
    }

    private String stripLastColon(String name) {
        if (name.endsWith(":")) {
            name = name.substring(0, name.length() - 1).trim();
        }
        if (name.length() == 0) {
            return null;
        }
        return name;
    }

    public String getMenuKey() {
        int menuShortcutKeyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if ((menuShortcutKeyMask & Event.CTRL_MASK) == Event.CTRL_MASK) {
            return "Control";
        }
        if ((menuShortcutKeyMask & Event.META_MASK) == Event.META_MASK) {
            return "Meta";
        }
        return "";
    }

    public String getAccessibleName() {
        if (component instanceof JTabbedPane) {
            return null;
        }
        return component.getAccessibleContext().getAccessibleName();
    }

    public Point getMidpoint() {
        EventQueueWait.call_noexc(this, "_makeVisible");
        return EventQueueWait.call_noexc(this, "_getMidpoint");
    }

    public Object _makeVisible() {
        return null;
    }

    public Point _getMidpoint() {
        java.awt.Dimension d = component.getSize();
        return new Point(d.width / 2, d.height / 2);
    }

    public String getLabeledBy() {
        if (getComponent() instanceof JComponent) {
            try {
                JLabel label = (JLabel) ((JComponent) getComponent()).getClientProperty("labeledBy");
                if (label != null && label.getText() != null && !label.getText().equals("")) {
                    return stripLastColon(label.getText().trim());
                }
            } catch (ClassCastException e) {
            }
        }
        return null;
    }

    public static final List<String> LAST_RESORT_RECOGNITION_PROPERTIES = new ArrayList<String>();

    static {
        LAST_RESORT_RECOGNITION_PROPERTIES.add("tagName");
        LAST_RESORT_RECOGNITION_PROPERTIES.add("indexOfType");
    }

    public Map<String, String> findURP(List<List<String>> rp) {
        List<Component> allComponents = findAllComponents();
        allComponents.remove(this.component);
        for (List<String> list : rp) {
            Map<String, String> rpValues = findValues(list);
            if (rpValues == null) {
                continue;
            }
            if (!hasAComponentsByRP(allComponents, rpValues)) {
                return rpValues;
            }
        }
        return findValues(LAST_RESORT_RECOGNITION_PROPERTIES);
    }

    private Map<String, String> findValues(List<String> list) {
        Map<String, String> rpValues = new HashMap<String, String>();
        for (String attribute : list) {
            String value = getAttribute(attribute);
            if (value == null || "".equals(value)) {
                rpValues = null;
                break;
            }
            rpValues.put(attribute, value);
        }
        return rpValues;
    }

    private boolean hasAComponentsByRP(List<Component> allComponents, Map<String, String> rpValues) {
        for (Component component : allComponents) {
            if (matchesRP(component, rpValues)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesRP(Component component, Map<String, String> rpValues) {
        JavaElementPropertyAccessor pa = new JavaElementPropertyAccessor(component);
        Set<Entry<String, String>> entrySet = rpValues.entrySet();
        for (Entry<String, String> entry : entrySet) {
            if (!entry.getValue().equals(pa.getAttribute(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (component == null ? 0 : component.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JavaElementPropertyAccessor other = (JavaElementPropertyAccessor) obj;
        if (component == null) {
            if (other.component != null) {
                return false;
            }
        } else if (!component.equals(other.component)) {
            return false;
        }
        return true;
    }

    public Map<String, String> findAttributes(Collection<String> props) {
        Map<String, String> r = new HashMap<String, String>();
        for (String prop : props) {
            String value = getAttribute(prop);
            if (value != null) {
                r.put(prop, value);
            }
        }
        return r;
    }

    public static class InternalFrameMonitor {

        static final List<JInternalFrame> frames = new ArrayList<JInternalFrame>();

        public static int getIndex(Component component) {
            return InternalFrameMonitor.frames.indexOf(component);
        }

        public static void init() {
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                @Override public void eventDispatched(AWTEvent event) {
                    if (event.getSource() instanceof JInternalFrame) {
                        if (event.getID() == ComponentEvent.COMPONENT_SHOWN) {
                            InternalFrameMonitor.frames.add((JInternalFrame) event.getSource());
                        }
                        if (event.getID() == ComponentEvent.COMPONENT_HIDDEN) {
                            InternalFrameMonitor.frames.remove(event.getSource());
                        }
                    }
                }
            }, AWTEvent.COMPONENT_EVENT_MASK);
        }
    }

    public int getInternalFrameIndex2() {
        return InternalFrameMonitor.getIndex(component);
    }

    public String callMethod(JSONObject callDetails) {
        String methodName = callDetails.getString("method");
        JSONObject parameters = callDetails.getJSONObject("parameters");
        try {
            Method method = this.getClass().getMethod(methodName, JSONObject.class);
            return (String) method.invoke(this, parameters);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return null;
    }

    public Color getBackground() {
        return component.getBackground();
    }

    public Color getForeground() {
        return component.getForeground();
    }

    public Font getFont() {
        return component.getFont();
    }

    public String getFontFamily() {
        return component.getFont().getFamily();
    }

    public Border getBorder() {
        if (component instanceof JComponent) {
            return ((JComponent) component).getBorder();
        }
        return null;
    }

    public Integer getColumnCount() {
        if (component instanceof JTable) {
            return ((JTable) component).getColumnCount();
        }
        if (component instanceof JTableHeader) {
            return ((JTableHeader) component).getColumnModel().getColumnCount();
        }
        return null;
    }

    public Integer getRowCount() {
        if (component instanceof JTable) {
            return ((JTable) component).getRowCount();
        }
        return null;
    }

    public Integer getItemCount() {
        if (component instanceof JComboBox) {
            return ((JComboBox) component).getItemCount();
        }
        return null;
    }

    public Integer getModelSize() {
        if (component instanceof JList) {
            return ((JList) component).getModel().getSize();
        }
        return null;
    }

    public String getColumnName(int c) {
        JTable table = (JTable) component;
        JTableHeader tableHeader = table.getTableHeader();
        String columnName;
        if (tableHeader != null) {
            columnName = tableHeader.getColumnModel().getColumn(c).getHeaderValue().toString();
        } else {
            columnName = table.getColumnName(c);
        }
        return columnName;
    }

    public static String removeClassName(Object object) {
        if (object == null) {
            return "null";
        }
        if (object.getClass().isArray()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("[");
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                buffer.append(removeClassName(Array.get(object, i)));
                if (i != length - 1) {
                    buffer.append(", ");
                }
            }
            buffer.append("]");
            return buffer.toString();
        }
        if (object.getClass().isPrimitive() || object instanceof String) {
            return object.toString();
        }
        try {
            return object.toString().replaceFirst(object.getClass().getName(), "");
        } catch (Throwable t) {
            return object.toString();
        }
    }

}
