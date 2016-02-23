package net.sourceforge.marathon.javafxagent;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import net.sourceforge.marathon.javafxagent.components.ContextManager;

public class JavaElementPropertyAccessor {

    protected Node node;

    public JavaElementPropertyAccessor(Node component) {
        this.node = component;
    }

    public final String getAttribute(final String name) {
        return getAttribute(name, false);
    }

    public final String getAttribute(final String name, final boolean skipSelf) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return _getAttribute(name, skipSelf);
            }
        });
    }

    public final String _getAttribute(String name, boolean skipSelf) {
        if (name.startsWith("keystrokeFor-"))
            return getKeyStrokeFor(name.substring("keystrokeFor-".length()));
        String[] split = name.split("\\.");
        String first = split[0];
        Object attributeObject = null;
        try {
            if (!skipSelf)
                attributeObject = getAttributeObject(this, first);
        } catch (UnsupportedCommandException e) {
        }
        if (attributeObject == null) {
            Node c = node;
            attributeObject = getAttributeObject(c, first);
            if (attributeObject == null)
                return null;
        }
        for (int i = 1; i < split.length; i++) {
            attributeObject = getAttributeObject(attributeObject, split[i]);
            if (attributeObject == null)
                return null;
        }
        return toString(attributeObject);
    }

    private String getKeyStrokeFor(String action) {
        JSONArray r = new JSONArray();
        if (r.length() > 0)
            return r.toString();
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
    public final Object getAttributeObject(Object component, String name) {
        String isMethod = getIsMethod(name);
        Object o = null;
        try {
            o = EventQueueWait.call(component, isMethod);
        } catch (Throwable e) {
            if (!(e instanceof NoSuchMethodException))
                return null;
        }
        try {
            if (o == null) {
                String getMethod = getGetMethod(name);
                o = EventQueueWait.call(component, getMethod);
            }
        } catch (Throwable e) {
        }
        try {
            if (o == null) {
                o = EventQueueWait.call(component, name);
            }
        } catch (Throwable e) {
        }
        try {
            if (o == null) {
                o = getFieldValue(component, name);
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
        if (o == null && component instanceof Collection<?>) {
            ArrayList<Object> lo = new ArrayList<Object>();
            Collection<?> c = (Collection<?>) component;
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
        if (f == null)
            return null;
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
    public final boolean hasAttribue(String name) {
        return hasMethod(getIsMethod(name)) || hasMethod(getGetMethod(name));
    }

    private boolean hasMethod(String name) {
        try {
            node.getClass().getMethod(name);
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
    public final String getText() {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return _getText();
            }
        });
    }

    public String _getText() {
        Node c = node;
        Object attributeObject = getAttributeObject(c, "text");
        if (attributeObject == null)
            return null;
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
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#isSelected()
     */
    public final boolean isSelected() {
        return EventQueueWait.<Boolean> call_noexc(this, "_isSelected");
    }

    public boolean _isSelected() {
        String selected = _getAttribute("selected", true);
        if (selected != null)
            return Boolean.parseBoolean(selected);
        throw new UnsupportedCommandException("isSelected is not supported by " + node.getClass().getName(), null);
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
        return !node.isDisabled();
    }

    public final Node getComponent() {
        return node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getTagName()
     */
    public final String getTagName() {
        Class<?> javaClass = findJavaClass();
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
        return r.substring(0, 1).toLowerCase() + r.substring(1).replaceAll("[A-Z][A-Z]*", "-$0").toLowerCase();
    }

    private Class<?> findJavaClass() {
        Class<?> c = node.getClass();
        while (c.getPackage() == null
                || (!c.getPackage().getName().startsWith("javafx.scene")))
            c = c.getSuperclass();
        return c;
    }

    public final String getType() {
        String name = node.getClass().getName();
        if (name.startsWith("javafx.scene.control")) {
            return name.substring("javafx.scene.control.".length());
        }
        return name;
    }

    public final String getInstanceOf() {
        Class<?> klass = node.getClass();
        while (klass != null && klass.getPackage() != null && !klass.getPackage().getName().startsWith("javafx.scene.control")) {
            klass = klass.getSuperclass();
        }
        return klass == null ? null : klass.getName();
    }

    private List<Node> findAllComponents() {
        Node top = getTopWindow(node);
        List<Node> allComponents = new ArrayList<Node>();
        if (top != null)
            fillUp(allComponents, top);
        return allComponents;
    }

    private void fillUp(List<Node> allComponents, Node c) {
        if (!c.isVisible())
            return;
        allComponents.add(c);
        if (c instanceof Parent) {
            ObservableList<Node> components = ((Parent) c).getChildrenUnmodifiable();
            for (Node component : components) {
                fillUp(allComponents, component);
            }
        }
    }

    private Node getTopWindow(Node c) {
        while (c != null) {
            if (ContextManager.isContext(c))
                return c;
            c = c.getParent();
        }
        return null;
    }

    public final String getOMapClassName() {
        return null;
    }

    final public String getOMapClassSimpleName() {
        return null;
    }

    final public int getIndexOfType() {
        List<Node> allComponents = findAllComponents();
        int index = 0;
        Class<? extends Node> klass = node.getClass();
        for (Node c : allComponents) {
            if (c == node)
                return index;
            if (c.getClass().equals(klass))
                index++;
        }
        return -1;
    }

    final public String getFieldName() {
        List<String> fieldNames = getFieldNames();
        if (fieldNames.size() == 0)
            return null;
        return fieldNames.get(0);
    }

    final public List<String> getFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        Parent container = node.getParent();
        while (container != null) {
            findFields(node, container, fieldNames);
            container = container.getParent();
        }
        return fieldNames;
    }

    private void findFields(Node current, Node container, List<String> fieldNames) {
        Field[] declaredFields = container.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            boolean accessible = field.isAccessible();
            try {
                field.setAccessible(true);
                Object o = field.get(container);
                if (o == current)
                    fieldNames.add(field.getName());
            } catch (Throwable t) {
            } finally {
                field.setAccessible(accessible);
            }
        }
    }

    final public String getCText() {
        Object o = getAttributeObject(getComponent(), "text");
        if (o == null || !(o instanceof String) || o.equals(""))
            return null;
        return (String) o;
    }

    final public String getClassName() {
        return node.getClass().getName();
    }

    final public boolean getEnabled() {
        return !node.isDisabled();
    }

    final public String getToolTipText() {
        return node.getAccessibleHelp();
    }

    final public String getName() {
        return getComponent().getId();
    }

    final public String getAccessibleText() {
        return node.getAccessibleText();
    }

    final public Point2D getMidpoint() {
        EventQueueWait.call_noexc(this, "_makeVisible");
        return EventQueueWait.call_noexc(this, "_getMidpoint");
    }

    public Object _makeVisible() {
        return null;
    }

    public Point2D _getMidpoint() {
        Bounds d = node.getBoundsInParent();
        return new Point2D(d.getWidth() / 2, d.getHeight() / 2);
    }

    public static final List<String> LAST_RESORT_RECOGNITION_PROPERTIES = new ArrayList<String>();

    static {
        LAST_RESORT_RECOGNITION_PROPERTIES.add("type");
        LAST_RESORT_RECOGNITION_PROPERTIES.add("indexOfType");
    }

    public final Map<String, String> findURP(List<List<String>> rp) {
        List<Node> allComponents = findAllComponents();
        allComponents.remove(this.node);
        for (List<String> list : rp) {
            Map<String, String> rpValues = findValues(list);
            if (rpValues == null)
                continue;
            if (!hasAComponentsByRP(allComponents, rpValues))
                return rpValues;
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

    private boolean hasAComponentsByRP(List<Node> allComponents, Map<String, String> rpValues) {
        for (Node component : allComponents) {
            if (matchesRP(component, rpValues))
                return true;
        }
        return false;
    }

    private boolean matchesRP(Node component, Map<String, String> rpValues) {
        JavaElementPropertyAccessor pa = new JavaElementPropertyAccessor(component);
        Set<Entry<String, String>> entrySet = rpValues.entrySet();
        for (Entry<String, String> entry : entrySet) {
            if (!entry.getValue().equals(pa.getAttribute(entry.getKey())))
                return false;
        }
        return true;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JavaElementPropertyAccessor other = (JavaElementPropertyAccessor) obj;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        return true;
    }

    public final Map<String, String> findAttributes(Collection<String> props) {
        Map<String, String> r = new HashMap<String, String>();
        for (String prop : props) {
            String value = getAttribute(prop);
            if (value != null)
                r.put(prop, value);
        }
        return r;
    }

    public final String callMethod(JSONObject callDetails) {
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

    public static String removeClassName(Object object) {
        if (object == null)
            return "null";
        if (object.getClass().isArray()) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("[");
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                buffer.append(removeClassName(Array.get(object, i)));
                if (i != length - 1)
                    buffer.append(", ");
            }
            buffer.append("]");
            return buffer.toString();
        }
        if (object.getClass().isPrimitive() || object instanceof String)
            return object.toString();
        try {
            return object.toString().replaceFirst(object.getClass().getName(), "");
        } catch (Throwable t) {
            return object.toString();
        }
    }

}
