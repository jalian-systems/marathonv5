package net.sourceforge.marathon.javafxagent;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxagent.components.ContextManager;

public class JavaFXElementPropertyAccessor extends JavaPropertyAccessor {

    protected Node node;

    public JavaFXElementPropertyAccessor(Node component) {
        super(component);
        this.node = component;
    }

    public final String getText() {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return _getText();
            }
        });
    }

    public String _getText() {
        Node c = node;
        if (this instanceof IPseudoElement)
            c = ((IPseudoElement) this).getPseudoComponent();
        Object attributeObject = getAttributeObject(c, "text");
        if (attributeObject == null)
            return null;
        return attributeObject.toString();
    }

    public final String getValue() {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return _getValue();
            }
        });
    }

    public String _getValue() {
        Node c = node;
        if (this instanceof IPseudoElement)
            c = ((IPseudoElement) this).getPseudoComponent();
        Object attributeObject = getAttributeObject(c, "value");
        if (attributeObject == null)
            return _getText();
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
        if (this instanceof IPseudoElement)
            c = ((IPseudoElement) this).getPseudoComponent().getClass();
        while (c.getPackage() == null || (!c.getPackage().getName().startsWith("javafx.scene")))
            c = c.getSuperclass();
        return c;
    }

    public final String getType() {
        return getType(node.getClass());
    }

    private String getType(Class<? extends Node> klass) {
        String name = klass.getName();
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
        Node top = getTopNode(node);
        List<Node> allComponents = new ArrayList<Node>();
        if (top != null)
            fillUp(allComponents, top);
        return allComponents;
    }

    private void fillUp(List<Node> allComponents, Node c) {
        allComponents.add(c);
        if (c instanceof Parent) {
            ObservableList<Node> components = ((Parent) c).getChildrenUnmodifiable();
            for (Node component : components) {
                fillUp(allComponents, component);
            }
        }
    }

    private Node getTopNode(Node c) {
        while (c != null) {
            if (ContextManager.isContext(c))
                return c;
            if (c.getScene().getRoot() == c)
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

    public int getIndexOfType() {
        List<Node> allComponents = findAllComponents();
        int index = 0;
        String type = getType();
        for (Node c : allComponents) {
            if (c == node)
                return index;
            if (type.equals(getType(c.getClass())))
                index++;
        }
        Logger.getLogger(JavaFXElementPropertyAccessor.class.getName()).warning("Could not find the component in allComponents");
        Logger.getLogger(JavaFXElementPropertyAccessor.class.getName()).warning(node.toString());
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
        Bounds d = node.getBoundsInLocal();
        Point2D p = new Point2D(d.getWidth() / 2, d.getHeight() / 2);
        return p;
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
        JavaFXElementPropertyAccessor pa = new JavaFXElementPropertyAccessor(component);
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
        JavaFXElementPropertyAccessor other = (JavaFXElementPropertyAccessor) obj;
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

    public String getTextForNode(TreeView<?> treeView, TreeItem<?> selectedItem) {
        StringBuilder sb = new StringBuilder();
        Object[] treePath = buildTreePath(selectedItem);
        int start = treeView.isShowRoot() ? 0 : 1;
        for (int i = start; i < treePath.length; i++) {
            String pathString = escapeSpecialCharacters(getTextForNodeObject(treeView, (TreeItem<?>) treePath[i]));
            sb.append("/").append(pathString);
        }
        return sb.toString();
    }

    private String getTextForNodeObject(TreeView<?> treeView, TreeItem<?> lastPathComponent) {
        // We should be getting the *text* from the tree cell. We need to figure
        // out how to construct
        // a tree cell for a item that is not visible
        // Set<Node> nodes = treeView.lookupAll(".tree-cell");
        // for (Node node : nodes) {
        // TreeCell<?> cell = (TreeCell<?>) node;
        // if (lastPathComponent == cell.getTreeItem()) {
        // RFXComponent cellComponent = new
        // RFXComponentFactory(omapConfig).findRawRComponent(node, null,
        // recorder);
        // return cellComponent.getText();
        // }
        // }
        if (lastPathComponent == null || lastPathComponent.getValue() == null)
            return "";
        return lastPathComponent.getValue().toString();
    }

    private String escapeSpecialCharacters(String name) {
        return name.replaceAll("/", "\\\\/");
    }

    private Object[] buildTreePath(TreeItem<?> selectedItem) {
        List<Object> path = new ArrayList<>();
        path.add(selectedItem);
        while (selectedItem.getParent() != null) {
            path.add(selectedItem.getParent());
            selectedItem = selectedItem.getParent();
        }
        Collections.reverse(path);
        return path.toArray();
    }

    public TreeItem<?> getPath(TreeView<?> treeView, String path) {
        String[] tokens = path.substring(1).split("(?<!\\\\)/");
        Object rootNode = treeView.getRoot();
        int start = treeView.isShowRoot() ? 1 : 0;
        List<TreeItem<?>> treePath = new ArrayList<TreeItem<?>>();
        treePath.add((TreeItem<?>) rootNode);
        StringBuilder searchedPath = new StringBuilder();
        if (treeView.isShowRoot()) {
            String rootNodeText = unescapeSpecialCharacters(tokens[0]);
            searchedPath.append("/" + rootNodeText);
            if (rootNode == null)
                throw new RuntimeException("TreeView does not have a root node!");
            if (!searchedPath.toString().equals("/" + getPathText(treePath)))
                throw new RuntimeException("TreeView root node does not match: Expected </" + getPathText(treePath) + "> Actual: <"
                        + searchedPath.toString() + ">");
        }
        for (int i = start; i < tokens.length; i++) {
            String childText = unescapeSpecialCharacters(tokens[i]);
            searchedPath.append("/" + childText);
            boolean matched = false;
            TreeItem<?> item = (TreeItem<?>) treePath.get(treePath.size() - 1);
            item.setExpanded(true);
            for (int j = 0; j < item.getChildren().size(); j++) {
                Object child = item.getChildren().get(j);
                treePath.add((TreeItem<?>) child);
                List<TreeItem<?>> childPath = treePath;
                if (childText.equals(getPathText(childPath))) {
                    treePath = childPath;
                    matched = true;
                    break;
                }
            }
            if (!matched)
                return null;
        }
        return treePath.get(treePath.size() - 1);
    }

    private String getPathText(List<TreeItem<?>> treePath) {
        // We should be getting the *text* from the tree cell. We need to figure
        // out how to construct
        // a tree cell for a item that is not visible
        // Set<Node> nodes = treeView.lookupAll(".tree-cell");
        // for (Node node : nodes) {
        // TreeCell<?> cell = (TreeCell<?>) node;
        // if (lastPathComponent == cell.getTreeItem()) {
        // RFXComponent cellComponent = new
        // RFXComponentFactory(omapConfig).findRawRComponent(node, null,
        // recorder);
        // return cellComponent.getText();
        // }
        // }
        TreeItem<?> lastPathComponent = treePath.get(treePath.size() - 1);
        if (lastPathComponent == null || lastPathComponent.getValue() == null)
            return "";
        return lastPathComponent.getValue().toString();
    }

    private String unescapeSpecialCharacters(String name) {
        return name.replaceAll("\\\\/", "/");
    }

    public int getSelection(CheckBox cb) {
        int selection;
        if (cb.isAllowIndeterminate() && cb.isIndeterminate())
            selection = 1;
        else
            selection = cb.isSelected() ? 2 : 0;
        return selection;
    }

    public String getTextForTab(TabPane tabPane, Tab selectedTab) {
        int index = tabPane.getTabs().indexOf(selectedTab);
        String original = getItemText(tabPane, index);
        String itemText = original;
        int suffixIndex = 0;

        for (int i = 0; i < index; i++) {
            String current = getItemText(tabPane, i);

            if (current.equals(original)) {
                itemText = String.format("%s(%d)", original, ++suffixIndex);
            }
        }
        return itemText;
    }

    public String[][] getContent(TabPane node) {
        int nItems = node.getTabs().size();
        String[][] content = new String[1][nItems];
        for (int i = 0; i < nItems; i++) {
            content[0][i] = getTextForTab(node, node.getTabs().get(i));
        }
        return content;
    }

    public String getListSelectionText(ListView<?> listView) {
        ObservableList<Integer> selectedIndices = listView.getSelectionModel().getSelectedIndices();
        JSONArray pa = new JSONArray();
        for (int i = 0; i < selectedIndices.size(); i++) {
            pa.put(getListSelectionText(listView, selectedIndices.get(i)));
        }
        return pa.toString();
    }

    public String getListSelectionText(ListView<?> listView, Integer index) {
        String original = getListItemText(listView, index);
        String itemText = original;
        int suffixIndex = 0;
        for (int i = 0; i < index; i++) {
            String current = getListItemText(listView, i);
            if (current.equals(original))
                itemText = String.format("%s(%d)", original, ++suffixIndex);
        }
        return itemText;
    }

    public ListCell<?> getCellAt(ListView<?> listView, Integer index) {
        Set<Node> lookupAll = listView.lookupAll(".list-cell");
        for (Node node : lookupAll) {
            ListCell<?> cell = (ListCell<?>) node;
            if (cell.getIndex() == index) {
                return cell;
            }
        }
        return null;
    }

    public TreeCell<?> getCellAt(TreeView<?> treeView, int index) {
        return (TreeCell<?>) getCellAt(treeView, getPath(treeView, rowToPath(index)));
    }

    public Node getCellAt(TreeView<?> treeView, TreeItem<?> item) {
        Set<Node> lookupAll = treeView.lookupAll(".tree-cell");
        for (Node node : lookupAll) {
            TreeCell<?> cell = (TreeCell<?>) node;
            if (cell.getTreeItem() == item)
                return cell;
        }
        return null;
    }

    public String rowToPath(int row) {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        TreeItem<?> treeItem = treeView.getTreeItem(row);
        if (treeItem == null)
            throw new RuntimeException("Trying to create a tree item for row " + row + " which is invalid");
        return getTextForNode(treeView, treeItem);
    }

    private String getListItemText(ListView<?> listView, Integer index) {
        return listView.getItems().get(index).toString();
    }

    protected String[][] getContent(ListView<?> node) {
        int nItems = ((ListView<?>) node).getItems().size();
        String[][] content = new String[1][nItems];
        for (int i = 0; i < nItems; i++) {
            content[0][i] = getListSelectionText(node, i);
        }
        return content;
    }

    public int getListItemIndex(ListView<?> listView, String string) {
        ObservableList<?> items = listView.getItems();
        for (int i = 0; i < items.size(); i++) {
            String text = getListSelectionText(listView, i);
            if (text.equals(string))
                return i;
        }
        return -1;
    }

    protected int getIndexAt(ListView<?> listView, Point2D point) {
        point = listView.localToScene(point);
        Set<Node> lookupAll = listView.lookupAll(".list-cell");
        ListCell<?> selected = null;
        for (Node cellNode : lookupAll) {
            Bounds boundsInScene = cellNode.localToScene(cellNode.getBoundsInLocal(), true);
            if (boundsInScene.contains(point)) {
                selected = (ListCell<?>) cellNode;
                break;
            }
        }
        if (selected == null)
            return -1;
        return selected.getIndex();
    }

    public int getRowAt(TreeView<?> treeView, Point2D point) {
        point = treeView.localToScene(point);
        Set<Node> lookupAll = treeView.lookupAll(".tree-cell");
        TreeCell<?> selected = null;
        for (Node cellNode : lookupAll) {
            Bounds boundsInScene = cellNode.localToScene(cellNode.getBoundsInLocal(), true);
            if (boundsInScene.contains(point)) {
                selected = (TreeCell<?>) cellNode;
                break;
            }
        }
        if (selected == null)
            return -1;
        return selected.getIndex();
    }

    public String getComboBoxText(ComboBox<?> comboBox, int index, boolean appendText) {
        if (index == -1)
            return null;
        String original = getComboBoxItemText(comboBox, index);
        String itemText = original;
        int suffixIndex = 0;
        for (int i = 0; i < index; i++) {
            String current = getComboBoxItemText(comboBox, i);
            if (current.equals(original)) {
                if (appendText)
                    itemText = String.format("%s(%d)", original, ++suffixIndex);
                else
                    itemText = original;
            }
        }
        return itemText;
    }

    private String getComboBoxItemText(ComboBox<?> comboBox, int index) {
        return stripHTMLTags(comboBox.getItems().get(index).toString());
    }

    public String stripHTMLTags(String text) {
        Pattern p = Pattern.compile("(<\\s*html\\s*>)(.*)(<\\s*/html\\s*>)");
        Matcher m = p.matcher(text);
        if (m.matches())
            text = stripTags(m.group(2));
        return text;
    }

    private String stripTags(String text) {
        text = text.trim();
        int indexOfGT = text.indexOf("<");
        int indexOfLT = text.indexOf(">");
        if (indexOfGT != -1 && indexOfLT != -1 && indexOfLT > indexOfGT) {
            text = text.replace(text.substring(indexOfGT, indexOfLT + 1), "");
            text = stripTags(text);
        }
        return text;
    }

    public String[][] getContent(ComboBox<?> comboBox) {
        int nOptions = ((ComboBox<?>) comboBox).getItems().size();
        String[][] content = new String[1][nOptions];
        for (int i = 0; i < nOptions; i++) {
            content[0][i] = getComboBoxText(comboBox, i, true);
        }
        return content;
    }

    public int getComboBoxItemIndex(ComboBox<?> comboBox, String value) {
        ObservableList<?> items = comboBox.getItems();
        for (int i = 0; i < items.size(); i++) {
            String text = getComboBoxText(comboBox, i, true);
            if (text.equals(value))
                return i;
        }
        return -1;
    }

    public String getColorCode(Color c) {
        return String.format("#%02x%02x%02x", (int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255));
    }

    public String getDatePickerText(DatePicker datePicker, LocalDate value) {
        return datePicker.getConverter().toString(value);
    }

    public String getChoiceBoxText(ChoiceBox<?> choiceBox, int index) {
        if (index == -1)
            return null;
        String original = getChoiceBoxItemText(choiceBox, index);
        String itemText = original;
        int suffixIndex = 0;
        for (int i = 0; i < index; i++) {
            String current = getChoiceBoxItemText(choiceBox, i);
            if (current.equals(original))
                itemText = String.format("%s(%d)", original, ++suffixIndex);
        }
        return itemText;
    }

    @SuppressWarnings("unchecked") private String getChoiceBoxItemText(@SuppressWarnings("rawtypes") ChoiceBox choiceBox,
            int index) {
        @SuppressWarnings("rawtypes")
        StringConverter converter = choiceBox.getConverter();
        String text = null;
        if (converter == null)
            text = (choiceBox.getItems().get(index)).toString();
        else
            text = converter.toString(choiceBox.getItems().get(index));
        return stripHTMLTags(text);
    }

    public String[][] getContent(ChoiceBox<?> choiceBox) {
        int nOptions = choiceBox.getItems().size();
        String[][] content = new String[1][nOptions];
        for (int i = 0; i < nOptions; i++) {
            content[0][i] = getChoiceBoxText(choiceBox, i);
        }
        return content;
    }

    public int getChoiceBoxItemIndex(ChoiceBox<?> choiceBox, String value) {
        ObservableList<?> items = choiceBox.getItems();
        for (int i = 0; i < items.size(); i++) {
            String text = getChoiceBoxText(choiceBox, i);
            if (text.equals(value))
                return i;
        }
        return -1;
    }

    public String getSelectedTreeNodeText(TreeView<?> treeView, ObservableList<?> selectedItems) {
        JSONArray pa = new JSONArray();
        for (Object object : selectedItems) {
            pa.put(getTextForNode(treeView, (TreeItem<?>) object));
        }
        return pa.toString();
    }

    public String getSpinnerText(Spinner<?> spinner) {
        return spinner.getEditor().getText();
    }

    public String getDividerLocations(SplitPane splitPane) {
        return new JSONArray(splitPane.getDividerPositions()).toString();
    }

    private static String getItemText(TabPane tabPane, int index) {
        String titleAt = tabPane.getTabs().get(index).getText();
        if (titleAt == null || "".equals(titleAt)) {
            return getTabNameFromIcon(tabPane, index);
        }
        return titleAt;
    }

    @SuppressWarnings("deprecation") private static String getTabNameFromIcon(TabPane tabPane, int index) {
        Node graphic = tabPane.getTabs().get(index).getGraphic();
        if (graphic == null || !(graphic instanceof ImageView))
            return "tabIndex-" + index;
        return nameFromImage(((ImageView) graphic).getImage().impl_getUrl());
    }

    private static String nameFromImage(String description) {
        try {
            String name = new URL(description).getPath();
            if (name.lastIndexOf('/') != -1)
                name = name.substring(name.lastIndexOf('/') + 1);
            if (name.lastIndexOf('.') != -1)
                name = name.substring(0, name.lastIndexOf('.'));
            return name;
        } catch (MalformedURLException e) {
            return description;
        }
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
