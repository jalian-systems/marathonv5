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
package net.sourceforge.marathon.fxcontextmenu;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;

final public class AssertionTreeView extends TreeView<AssertionTreeView.PropertyWrapper> {

    public static class PropertyWrapper {
        public PropertyWrapper(Object value, String property) {
            this.value = value;
            this.property = property;
            this.displayValue = this.value;
            if (this.value.getClass().isArray()) {
                this.displayValue = unboxPremitiveArray(this.value);
            }
        }

        String property;
        Object value;
        Object displayValue;

        @Override public String toString() {
            if (property == null) {
                return "root" + " (" + displayValue + ")";
            }
            return property + " (" + displayValue + ")";
        }

        private Object unboxPremitiveArray(Object r) {
            int length = Array.getLength(r);
            ArrayList<Object> list = new ArrayList<Object>();
            for (int i = 0; i < length; i++) {
                Object e = Array.get(r, i);
                if (e != null && e.getClass().isArray()) {
                    list.add(unboxPremitiveArray(e));
                } else {
                    list.add(e);
                }
            }
            return list;
        }

    }

    public static class AssertionTreeItem extends TreeItem<AssertionTreeView.PropertyWrapper> {
        boolean done = false;

        public AssertionTreeItem(AssertionTreeView.PropertyWrapper object) {
            super(object);
        }

        @Override public String toString() {
            String property = getValue().property;
            if (property == null) {
                return "root";
            }
            return property;
        }

        public AssertionTreeItem(Object object, String name) {
            super(new AssertionTreeView.PropertyWrapper(object, name));
        }

        @Override public boolean isLeaf() {
            return isPrimitive(getValue());
        }

        private boolean isPrimitive(AssertionTreeView.PropertyWrapper wrapper) {
            Object object = wrapper.value;
            return object == null || object.getClass() == Boolean.class || object.getClass() == Character.class
                    || object.getClass() == Byte.class || object.getClass() == Short.class || object.getClass() == Integer.class
                    || object.getClass() == Long.class || object.getClass() == Float.class || object.getClass() == Double.class
                    || object.getClass() == Void.class || object.getClass() == String.class;
        }

        @Override public ObservableList<TreeItem<AssertionTreeView.PropertyWrapper>> getChildren() {
            if (done) {
                return super.getChildren();
            }
            done = true;
            ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r = new ArrayList<>();
            Object object = getValue().value;
            if (object instanceof List) {
                fillListValues((List<?>) object, r);
            }
            if (object instanceof Map) {
                fillMapValues((Map<?, ?>) object, r);
            } else if (object.getClass().isArray()) {
                fillArrayValues(object, r);
            } else if (object instanceof Collection<?>) {
                fillCollectionValues((Collection<?>) object, r);
            } else {
                fillObjectValues(object, r);
            }
            super.getChildren().setAll(r);
            return super.getChildren();
        }

        private void fillCollectionValues(Collection<?> object, ArrayList<TreeItem<PropertyWrapper>> r) {
            int length = Array.getLength(object.toArray());
            r.add(new AssertionTreeItem(new AssertionTreeView.PropertyWrapper(length, "size")));
            for (int i = 0; i < length; i++) {
                r.add(new AssertionTreeItem(new AssertionTreeView.PropertyWrapper(Array.get(object.toArray(), i), "[" + i + "]")));
            }
        }

        private void fillArrayValues(Object object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
            int length = Array.getLength(object);
            r.add(new AssertionTreeItem(new AssertionTreeView.PropertyWrapper(length, "size")));
            for (int i = 0; i < length; i++) {
                r.add(new AssertionTreeItem(new AssertionTreeView.PropertyWrapper(Array.get(object, i), "[" + i + "]")));
            }
        }

        private void fillObjectValues(Object object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
            ArrayList<Method> methods;
            if (object instanceof RFXComponent) {
                methods = ((RFXComponent) object).getMethods();
            } else {
                methods = getMethods(object);
            }
            for (Method method : methods) {
                AssertionTreeItem item = getTreeItemForMethod(object, method);
                if (item != null) {
                    r.add(item);
                }
            }
        }

        private AssertionTreeItem getTreeItemForMethod(Object object, Method method) {
            boolean accessible = method.isAccessible();
            try {
                method.setAccessible(true);
                Object value = method.invoke(object, new Object[] {});
                if (value != null) {
                    return new AssertionTreeItem(value, getPropertyName(method.getName()));
                }
            } catch (Throwable t) {
            } finally {
                method.setAccessible(accessible);
            }
            return null;
        }

        private String getPropertyName(String name) {
            if (name.startsWith("is")) {
                name = name.substring(2);
            } else {
                name = name.substring(3);
            }
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }

        private void fillMapValues(Map<?, ?> object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
            r.add(new AssertionTreeItem(object.size(), "size"));
            Set<?> entrySet = object.entrySet();
            for (Object o : entrySet) {
                Entry<?, ?> entry = (Entry<?, ?>) o;
                r.add(new AssertionTreeItem(entry.getValue(), "[" + entry.getKey().toString() + "]"));
            }
        }

        private void fillListValues(List<?> object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
            r.add(new AssertionTreeItem(object.size(), "size"));
            int index = 0;
            for (Object item : object) {
                r.add(new AssertionTreeItem(item, "[" + index++ + "]"));
            }
        }

        private boolean isValidMethod(Method method) {
            String name = method.getName();
            if ((name.startsWith("get") || name.startsWith("is")) && method.getParameterTypes().length == 0
                    && !method.getName().equals("getClass") && !method.getName().startsWith("setOn")) {
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
                @Override public int compare(Method o1, Method o2) {
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

    }

    public void setRootObject(Object o) {
        setRoot(new AssertionTreeItem(new PropertyWrapper(o, "root")));
    }

    public AssertionTreeView() {
        super();
        setShowRoot(false);
    }
}
