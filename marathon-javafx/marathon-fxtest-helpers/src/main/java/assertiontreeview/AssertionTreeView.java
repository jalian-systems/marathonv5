package assertiontreeview;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import assertiontreeview.AssertionTreeView.PropertyWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

final public class AssertionTreeView extends TreeView<AssertionTreeView.PropertyWrapper> {

	public static class PropertyWrapper {
		public PropertyWrapper(Object value, String property) {
			this.value = value;
			this.property = property;
		}

		String property;
		Object value;

		@Override
		public String toString() {
			if (property == null)
				return "root" + " (" + value + ")";
			return property + " (" + value + ")";
		}
	}

	public static class AssertionTreeItem extends TreeItem<AssertionTreeView.PropertyWrapper> {
		boolean done = false;

		public AssertionTreeItem(AssertionTreeView.PropertyWrapper object) {
			super(object);
		}

		@Override
		public String toString() {
			String property = getValue().property;
			if (property == null)
				return "root";
			return property;
		}

		public AssertionTreeItem(Object object, String name) {
			super(new AssertionTreeView.PropertyWrapper(object, name));
		}

		@Override
		public boolean isLeaf() {
			return isPrimitive(getValue());
		}

		private boolean isPrimitive(AssertionTreeView.PropertyWrapper wrapper) {
			Object object = wrapper.value;
			return object == null || object.getClass() == Boolean.class || object.getClass() == Character.class
					|| object.getClass() == Byte.class || object.getClass() == Short.class
					|| object.getClass() == Integer.class || object.getClass() == Long.class
					|| object.getClass() == Float.class || object.getClass() == Double.class
					|| object.getClass() == Void.class || object.getClass() == String.class;
		}

		@Override
		public ObservableList<TreeItem<AssertionTreeView.PropertyWrapper>> getChildren() {
			if (done)
				return super.getChildren();
			done = true;
			ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r = new ArrayList<>();
			Object object = getValue().value;
			if (object instanceof List)
				fillListValues((List) object, r);
			if (object instanceof Map)
				fillMapValues((Map) object, r);
			else if (object.getClass().isArray())
				fillArrayValues(object, r);
			else
				fillObjectValues(object, r);
			super.getChildren().setAll(r);
			return super.getChildren();
		}

		private void fillArrayValues(Object object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
			int length = Array.getLength(object);
			r.add(new AssertionTreeItem(new AssertionTreeView.PropertyWrapper(length, "size")));
			for (int i = 0; i < length; i++) {
				r.add(new AssertionTreeItem(
						new AssertionTreeView.PropertyWrapper(Array.get(object, i), "[" + i + "]")));
			}
		}

		private void fillObjectValues(Object object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
			ArrayList<Method> methods = getMethods(object);
			for (Method method : methods) {
				AssertionTreeItem item = getTreeItemForMethod(object, method);
				if (item != null)
					r.add(item);
			}
		}

		private AssertionTreeItem getTreeItemForMethod(Object object, Method method) {
			boolean accessible = method.isAccessible();
			try {
				method.setAccessible(true);
				return new AssertionTreeItem(method.invoke(object, new Object[] {}), getPropertyName(method.getName()));
			} catch (Throwable t) {
			} finally {
				method.setAccessible(accessible);
			}
			return null;
		}

		private String getPropertyName(String name) {
			if (name.startsWith("is"))
				name = name.substring(2);
			else
				name = name.substring(3);
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}

		private void fillMapValues(Map object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
			r.add(new AssertionTreeItem(object.size(), "size"));
			Set<Entry> entrySet = object.entrySet();
			for (Entry entry : entrySet) {
				r.add(new AssertionTreeItem(entry.getValue(), entry.getKey().toString()));
			}
		}

		private void fillListValues(List object, ArrayList<TreeItem<AssertionTreeView.PropertyWrapper>> r) {
			r.add(new AssertionTreeItem(object.size(), "size"));
			int index = 0;
			for (Object item : object) {
				r.add(new AssertionTreeItem(item, "[" + index++ + "]"));
			}
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
			for (int i = 0; i < methods.length; i++) {
				if (isValidMethod(methods[i]))
					list.add(methods[i]);
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
					if (name1.startsWith("is"))
						name1 = name1.substring(2);
					else
						name1 = name1.substring(3);
					if (name2.startsWith("is"))
						name2 = name2.substring(2);
					else
						name2 = name2.substring(3);
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