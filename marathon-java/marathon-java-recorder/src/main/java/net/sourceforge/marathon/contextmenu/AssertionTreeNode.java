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
package net.sourceforge.marathon.contextmenu;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor;

public class AssertionTreeNode extends DefaultMutableTreeNode {

    public static final Logger LOGGER = Logger.getLogger(AssertionTreeNode.class.getName());

    private static final long serialVersionUID = 1L;
    private final Object object;

    private final String property;

    public AssertionTreeNode(Object object) {
        this(object, null);
    }

    private AssertionTreeNode(Object object, String property) {
        super(new Object[] { object, property });
        this.object = object;
        this.property = property;
    }

    @Override
    public boolean isLeaf() {
        return isPrimitive(object);
    }

    private boolean isPrimitive(Object object) {
        return object == null || object.getClass() == Boolean.class || object.getClass() == Character.class
                || object.getClass() == Byte.class || object.getClass() == Short.class || object.getClass() == Integer.class
                || object.getClass() == Long.class || object.getClass() == Float.class || object.getClass() == Double.class
                || object.getClass() == Void.class || object.getClass() == String.class;
    }

    @Override
    public int getChildCount() {
        if (isLeaf()) {
            return 0;
        }
        if (object instanceof List) {
            return ((List<?>) object).size() + 1;
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).size() + 1;
        }
        if (object instanceof RComponent) {
            return ((RComponent) object).getMethods().size();
        }
        return getMethods(object).size();
    }

    @Override
    public TreeNode getChildAt(int index) {
        if (object instanceof List) {
            return getNodeForList((List<?>) object, index);
        }
        if (object instanceof Map) {
            return getNodeForMap((Map<?, ?>) object, index);
        }
        Method method;
        if (object instanceof RComponent) {
            Method o = ((RComponent) object).getMethods().get(index);
            method = o;
        } else {
            method = getMethods(object).get(index);
        }
        return getNodeForMethod(method);
    }

    private TreeNode getNodeForMap(Map<?, ?> map, int index) {
        if (index == 0) {
            return getNewNode(Integer.valueOf(map.size()), "size");
        }
        Entry<?, ?> entry = (Entry<?, ?>) map.entrySet().toArray()[index - 1];
        return getNewNode(entry.getValue(), "[" + entry.getKey().toString() + "]");
    }

    private TreeNode getNodeForList(List<?> l, int index) {
        if (index == 0) {
            return getNewNode(Integer.valueOf(l.size()), "size");
        }
        return getNewNode(l.get(index - 1), "[" + (index - 1) + "]");
    }

    private TreeNode getNodeForMethod(Method method) {
        Object r = null;
        try {
            method.setAccessible(true);
            r = method.invoke(object, new Object[] {});
        } catch (Throwable t) {
        }
        String p = getPropertyName(method.getName());
        return getNewNode(r, p);
    }

    private TreeNode getNewNode(Object r, String p) {
        if (r != null && r.getClass().isArray()) {
            r = RComponent.unboxPremitiveArray(r);
        } else if (r instanceof Collection) {
            r = RComponent.unboxPremitiveArray(((Collection<?>) r).toArray());
        }
        AssertionTreeNode node = new AssertionTreeNode(r, p);
        if (isPrimitive(r)) {
            node.setAllowsChildren(false);
        }
        node.setParent(this);
        return node;
    }

    private String getPropertyName(String name) {
        if (name.startsWith("is")) {
            return name.substring(2);
        }
        return name.substring(3);
    }

    private boolean isValidMethod(Method method) {
        String name = method.getName();
        if ((name.startsWith("get") || name.startsWith("is")) && method.getParameterTypes().length == 0
                && !method.getName().equals("getClass")) {
            return true;
        }
        return false;
    }

    private ArrayList<Method> getMethods(Object object) {
        ArrayList<Method> list = new ArrayList<Method>();
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            if (isValidMethod(method)) {
                list.add(method);
            }
        }
        sort(list);
        return list;
    }

    private void sort(List<Method> list) {
        Collections.sort(list, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                String name1 = o1.getName();
                String name2 = o2.getName();
                if (name1.startsWith("is")) {
                    name1 = name1.substring(2);
                } else {
                    name1 = name1.substring(3);
                }
                if (name2.startsWith("is")) {
                    name2 = name2.substring(2);
                } else {
                    name2 = name2.substring(3);
                }
                return name1.compareTo(name2);
            }
        });
    }

    public String getDisplayNode() {
        String d = object == null ? "null" : getObjectRepr();
        if (d.length() > 60) {
            d = d.substring(0, 56) + "...";
        }
        return d;
    }

    public String getDisplayValue() {
        return object == null ? "null" : getObjectRepr();
    }

    public String getValue() {
        return object == null ? null : getObjectRepr();
    }

    public String getProperty() {
        return property;
    }

    private String getObjectRepr() {
        return JavaElementPropertyAccessor.removeClassName(object);
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return property;
    }
}
