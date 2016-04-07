package net.sourceforge.marathon.javafxagent;

import java.lang.ref.WeakReference;
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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Window;
import javafx.util.Callback;
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

    public final String getId() {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return node.getId();
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
    public String getTagName() {
        return getTagName(node);
    }

    private String getTagName(Node n) {
        Class<?> c = findJavaClass(n);
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

    private Class<?> findJavaClass(Node n) {
        Class<?> c = n.getClass();
        if (this instanceof IPseudoElement)
            c = ((IPseudoElement) this).getPseudoComponent().getClass();
        while (c.getPackage() == null || (!c.getPackage().getName().startsWith("javafx.scene")))
            c = c.getSuperclass();
        return c;
    }

    public final String getType() {
        return getTagName();
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
        if (top != null) {
            fillUp(allComponents, top);
        }
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

    private Node getTopNode(Node n) {
        Node parent = null;
        while (parent == null) {
            if (ContextManager.isContext(n))
                parent = n;
            else if (n.getScene().getRoot() == n)
                parent = n;
            else
                n = n.getParent();
        }
        return parent;
    }

    public int getIndexOfType() {
        Object prop = node.getProperties().get("marathon.indexOfType");
        if (prop != null) {
            return (int) prop;
        }
        List<Node> allComponents = findAllComponents();
        int index = 0;
        String type = getType();
        for (Node c : allComponents) {
            if (c == node) {
                node.getProperties().put("marathon.indexOfType", index);
                return index;
            }
            if (type.equals(getTagName(c)))
                index++;
        }
        Logger.getLogger(JavaFXElementPropertyAccessor.class.getName())
                .warning("Could not find the component in allComponents: " + node.toString());
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
        if (node instanceof TextInputControl)
            return null;
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

    final public String getAccessibleName() {
        return node.getAccessibleRole().name();
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
        LAST_RESORT_RECOGNITION_PROPERTIES.add("tagName");
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

    protected String escapeSpecialCharacters(String name) {
        return name.replaceAll("/", "\\\\/");
    }

    protected Object[] buildTreePath(TreeItem<?> selectedItem) {
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

    protected String getPathText(List<TreeItem<?>> treePath) {
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

    protected String unescapeSpecialCharacters(String name) {
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

    @SuppressWarnings({ "rawtypes", "unchecked" }) public ListCell getCellAt(ListView listView, Integer index) {
        ListCell<?> cell = getVisibleCellAt(listView, index);
        if (cell != null)
            return cell;
        try {
            Callback<ListView, ListCell> cellFactory = listView.getCellFactory();
            ListCell listCell = null;
            if (cellFactory == null) {
                listCell = new ListCell() {
                    @Override public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else if (item instanceof Node) {
                            setText(null);
                            Node currentNode = getGraphic();
                            Node newNode = (Node) item;
                            if (currentNode == null || !currentNode.equals(newNode)) {
                                setGraphic(newNode);
                            }
                        } else {
                            /**
                             * This label is used if the item associated with
                             * this cell is to be represented as a String. While
                             * we will lazily instantiate it we never clear it,
                             * being more afraid of object churn than a minor
                             * "leak" (which will not become a "major" leak).
                             */
                            setText(item == null ? "null" : item.toString());
                            setGraphic(null);
                        }
                    }
                };
            } else {
                listCell = cellFactory.call(listView);
            }
            Object value = listView.getItems().get(index);
            Method updateItem = listCell.getClass().getDeclaredMethod("updateItem", new Class[] { Object.class, Boolean.TYPE });
            updateItem.invoke(listCell, value, false);
            return listCell;
        } catch (Throwable t) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes") public ListCell<?> getVisibleCellAt(ListView listView, Integer index) {
        Set<Node> lookupAll = listView.lookupAll(".list-cell");
        ListCell<?> cell = null;
        for (Node node : lookupAll) {
            if (((ListCell<?>) node).getIndex() == index) {
                cell = (ListCell<?>) node;
                break;
            }
        }
        if (cell != null && isShowing(cell))
            return cell;
        return null;
    }

    public TreeCell<?> getCellAt(TreeView<?> treeView, int index) {
        return (TreeCell<?>) getCellAt(treeView, getPath(treeView, rowToPath(index)));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) public Node getCellAt(TreeView treeView, TreeItem<?> treeItem1) {
        TreeCell visibleCell = getVisibleCellAt(treeView, treeItem1);
        if (visibleCell != null)
            return visibleCell;
        try {
            Callback<TreeView, TreeCell> cellFactory = treeView.getCellFactory();
            TreeCell treeCell = null;
            if (cellFactory == null) {
                treeCell = new TreeCell<Object>() {
                    private HBox hbox;

                    private WeakReference<TreeItem<Object>> treeItemRef;

                    private InvalidationListener treeItemGraphicListener = observable -> {
                        updateDisplay(getItem(), isEmpty());
                    };

                    private InvalidationListener treeItemListener = new InvalidationListener() {
                        @Override public void invalidated(Observable observable) {
                            TreeItem<Object> oldTreeItem = treeItemRef == null ? null : treeItemRef.get();
                            if (oldTreeItem != null) {
                                oldTreeItem.graphicProperty().removeListener(weakTreeItemGraphicListener);
                            }

                            TreeItem<Object> newTreeItem = getTreeItem();
                            if (newTreeItem != null) {
                                newTreeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
                                treeItemRef = new WeakReference<TreeItem<Object>>(newTreeItem);
                            }
                        }
                    };

                    private WeakInvalidationListener weakTreeItemGraphicListener = new WeakInvalidationListener(
                            treeItemGraphicListener);

                    private WeakInvalidationListener weakTreeItemListener = new WeakInvalidationListener(treeItemListener);

                    {
                        treeItemProperty().addListener(weakTreeItemListener);
                        if (getTreeItem() != null) {
                            getTreeItem().graphicProperty().addListener(weakTreeItemGraphicListener);
                        }
                    }

                    private void updateDisplay(Object item, boolean empty) {
                        if (item == null || empty) {
                            hbox = null;
                            setText(null);
                            setGraphic(null);
                        } else {
                            // update the graphic if one is set in the TreeItem
                            TreeItem<Object> treeItem = getTreeItem();
                            Node graphic = treeItem == null ? null : treeItem.getGraphic();
                            if (graphic != null) {
                                if (item instanceof Node) {
                                    setText(null);

                                    // the item is a Node, and the graphic
                                    // exists, so
                                    // we must insert both into an HBox and
                                    // present that
                                    // to the user (see RT-15910)
                                    if (hbox == null) {
                                        hbox = new HBox(3);
                                    }
                                    hbox.getChildren().setAll(graphic, (Node) item);
                                    setGraphic(hbox);
                                } else {
                                    hbox = null;
                                    setText(item.toString());
                                    setGraphic(graphic);
                                }
                            } else {
                                hbox = null;
                                if (item instanceof Node) {
                                    setText(null);
                                    setGraphic((Node) item);
                                } else {
                                    setText(item.toString());
                                    setGraphic(null);
                                }
                            }
                        }
                    }

                    @Override public void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        updateDisplay(item, empty);
                    }
                };
            } else {
                treeCell = cellFactory.call(treeView);
            }
            Method updateItem = treeCell.getClass().getDeclaredMethod("updateItem", new Class[] { Object.class, Boolean.TYPE });
            updateItem.invoke(treeCell, treeItem1.getValue(), false);
            return treeCell;
        } catch (Throwable t) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes") public TreeCell getVisibleCellAt(TreeView treeView, TreeItem<?> treeItem1) {
        Set<Node> lookupAll = treeView.lookupAll(".tree-cell");
        TreeCell cell = null;
        for (Node treeNode : lookupAll) {
            if (((TreeCell) treeNode).getTreeItem() == treeItem1) {
                cell = (TreeCell) treeNode;
                break;
            }
        }
        if (cell != null && isShowing(cell))
            return cell;
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
        if (point == null) {
            return listView.getSelectionModel().getSelectedIndex();
        }
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
        int itemCount = treeView.getExpandedItemCount();
        @SuppressWarnings("rawtypes")
        List<TreeCell> cells = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            cells.add(getCellAt(treeView, i));
        }
        TreeCell<?> selected = null;
        for (Node cellNode : cells) {
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

    public String getProgressText(ProgressBar progressBar) {
        return progressBar.getProgress() + "";
    }

    public int getColumnAt(TableView<?> tableView, Point2D point) {
        TableCell<?, ?> selected = getTableCellAt(tableView, point);
        if (selected == null)
            return -1;
        return tableView.getColumns().indexOf(selected.getTableColumn());
    }

    public int getRowAt(TableView<?> tableView, Point2D point) {
        TableCell<?, ?> selected = getTableCellAt(tableView, point);
        if (selected == null)
            return -1;
        return selected.getTableRow().getIndex();
    }

    private TableCell<?, ?> getTableCellAt(TableView<?> tableView, Point2D point) {
        point = tableView.localToScene(point);
        Set<Node> lookupAll = tableView.lookupAll(".table-cell");
        TableCell<?, ?> selected = null;
        for (Node cellNode : lookupAll) {
            Bounds boundsInScene = cellNode.localToScene(cellNode.getBoundsInLocal(), true);
            if (boundsInScene.contains(point)) {
                selected = (TableCell<?, ?>) cellNode;
                break;
            }
        }
        return selected;
    }

    public String getColumnName(TableView<?> tableView, int i) {
        ObservableList<?> columns = tableView.getColumns();
        TableColumn<?, ?> tableColumn = (TableColumn<?, ?>) columns.get(i);
        return tableColumn.getText();
    }

    public String getTableCellText(TableView<?> tableView, int row, int column) {
        if (column == -1 || row == -1)
            return null;
        String scolumn = getColumnName(tableView, column);
        if (scolumn == null || "".equals(scolumn))
            scolumn = "" + column;
        return new JSONObject().put("cell", new JSONArray().put(row).put(getColumnName(tableView, column))).toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) public TableCell<?, ?> getCellAt(TableView<?> tableView, int row, int column) {
        TableCell<?, ?> cell = getVisibleCellAt(tableView, row, column);
        if (cell != null)
            return cell;
        TableColumn tableColumn = (TableColumn) tableView.getColumns().get(column);
        cell = (TableCell) tableColumn.getCellFactory().call(tableColumn);
        Object value = tableColumn.getCellObservableValue(row).getValue();
        Method updateItem;
        try {
            updateItem = cell.getClass().getDeclaredMethod("updateItem", new Class[] { Object.class, Boolean.TYPE });
            updateItem.setAccessible(true);
            updateItem.invoke(cell, value, false);
            return cell;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public TableCell<?, ?> getVisibleCellAt(TableView<?> tableView, int row, int column) {
        Set<Node> lookupAll = tableView.lookupAll(".table-cell");
        TableCell<?, ?> cell = null;
        for (Node node : lookupAll) {
            TableCell<?, ?> cell1 = (TableCell<?, ?>) node;
            TableRow<?> tableRow = cell1.getTableRow();
            TableColumn<?, ?> tableColumn = cell1.getTableColumn();
            if (tableRow.getIndex() == row && tableColumn == tableView.getColumns().get(column)) {
                cell = cell1;
                break;
            }
        }
        if (cell != null && isShowing(cell))
            return cell;
        return null;
    }

    public boolean isShowing(Node cell) {
        boolean isShowing = false;
        Bounds boundsInLocal = cell.getBoundsInLocal();
        Bounds localToScreen = cell.localToScreen(boundsInLocal);
        Window w = cell.getScene().getWindow();
        BoundingBox screenBounds = new BoundingBox(w.getX(), w.getY(), w.getWidth(), w.getHeight());
        System.out.println("JavaFXElementPropertyAccessor.getVisibleCellAt(" + localToScreen + ")");
        System.out.println("JavaFXElementPropertyAccessor.getVisibleCellAt(" + screenBounds + ")");
        if (screenBounds.contains(localToScreen)) {
            System.out.println("JavaFXElementPropertyAccessor.getVisibleCellAt(" + cell + ")");
            isShowing = true;
        }
        return isShowing;
    }

    public Point2D getPoint(TableView<?> tableView, int columnIndex, int rowIndex) {
        Set<Node> tableRowCell = tableView.lookupAll(".table-row-cell");
        TableRow<?> row = null;
        for (Node tableRow : tableRowCell) {
            TableRow<?> r = (TableRow<?>) tableRow;
            if (r.getIndex() == rowIndex) {
                row = r;
                break;
            }
        }
        Set<Node> cells = row.lookupAll(".table-cell");
        for (Node node : cells) {
            TableCell<?, ?> cell = (TableCell<?, ?>) node;
            if ((tableView.getColumns().indexOf(cell.getTableColumn()) == columnIndex)) {
                Bounds bounds = cell.getBoundsInParent();
                Point2D localToParent = cell.localToParent(bounds.getWidth() / 2, bounds.getHeight() / 2);
                Point2D rowLocal = row.localToScene(localToParent, true);
                return rowLocal;
            }
        }
        return null;
    }

    public String getSelection(TableView<?> tableView) {
        TableViewSelectionModel<?> selectionModel = tableView.getSelectionModel();
        if (!selectionModel.isCellSelectionEnabled()) {
            ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
            if (tableView.getItems().size() == selectedIndices.size())
                return "all";
            if (selectedIndices.size() == 0)
                return "";
            return getRowSelectionText(selectedIndices);
        }

        @SuppressWarnings("rawtypes")
        ObservableList<TablePosition> selectedCells = selectionModel.getSelectedCells();
        int[] rows = new int[selectedCells.size()];
        int[] columns = new int[selectedCells.size()];
        int rowCount = tableView.getItems().size();
        int columnCount = tableView.getColumns().size();

        if (selectedCells.size() == (rowCount * columnCount))
            return "all";

        if (selectedCells.size() == 0)
            return "";
        JSONObject cells = new JSONObject();
        JSONArray value = new JSONArray();
        for (int i = 0; i < selectedCells.size(); i++) {
            TablePosition<?, ?> cell = selectedCells.get(i);
            rows[i] = cell.getRow();
            columns[i] = cell.getColumn();
            List<String> cellValue = new ArrayList<>();
            cellValue.add(cell.getRow() + "");
            cellValue.add(getColumnName(tableView, cell.getColumn()));
            value.put(cellValue);
        }
        cells.put("cells", value);
        return cells.toString();
    }

    public List<String> getSelectedColumnText(TableView<?> tableView, int[] columns) {
        List<String> text = new ArrayList<String>();
        for (int i = 0; i < columns.length; i++) {
            String columnName = getColumnName(tableView, columns[i]);
            text.add(escapeSpecialCharacters(columnName));
        }
        return text;
    }

    public String getRowSelectionText(ObservableList<Integer> selectedIndices) {
        JSONObject pa = new JSONObject();
        return pa.put("rows", selectedIndices).toString();
    }

    public int getColumnIndex(String columnName) {
        TableView<?> table = (TableView<?>) node;
        ObservableList<?> columns = table.getColumns();
        int ncolumns = columns.size();
        for (int i = 0; i < ncolumns; i++) {
            @SuppressWarnings("rawtypes")
            String column = ((TableColumn) columns.get(i)).getText();
            if (columnName.equals(escapeSpecialCharacters(column)))
                return i;
        }
        throw new RuntimeException("Could not find column " + columnName + " in table");
    }

    public int[] getCellRows(JSONObject rowObject) {
        JSONArray x = (JSONArray) rowObject.get("rows");
        int[] rows = new int[x.length()];
        for (int i = 0; i < x.length(); i++) {
            rows[i] = x.getInt(i);
        }
        return rows;
    }

    public int[] getCellColumns(JSONObject columnObject) {
        JSONArray x = (JSONArray) columnObject.get("columns");
        int[] columnIndex = new int[x.length()];
        for (int i = 0; i < x.length(); i++) {
            columnIndex[i] = getColumnIndex(x.getString(i));
        }
        return columnIndex;
    }

    public int[] getSelectedRows(String value) {
        JSONArray x = (JSONArray) new JSONObject(value).get("rows");
        int[] rows = new int[x.length()];
        for (int i = 0; i < x.length(); i++) {
            rows[i] = x.getInt(i);
        }
        return rows;
    }

    @SuppressWarnings("unchecked") public void selectCells(TableView<?> tableView, String value) {
        @SuppressWarnings("rawtypes")
        TableViewSelectionModel selectionModel = tableView.getSelectionModel();
        selectionModel.clearSelection();
        JSONObject cells = new JSONObject(value);
        JSONArray object = (JSONArray) cells.get("cells");
        for (int i = 0; i < object.length(); i++) {
            JSONArray jsonArray = object.getJSONArray(i);
            int rowIndex = Integer.parseInt(jsonArray.getString(0));
            int columnIndex = getColumnIndex(jsonArray.getString(1));
            @SuppressWarnings("rawtypes")
            TableColumn column = tableView.getColumns().get(columnIndex);
            if (getVisibleCellAt(tableView, rowIndex, columnIndex) == null) {
                tableView.scrollTo(rowIndex);
                tableView.scrollToColumn(column);
            }
            selectionModel.select(rowIndex, column);
        }
    }

    public String getTreeTableCellText(TreeTableView<?> treeTableView, int row, int column) {
        if (column == -1 || row == -1)
            return null;
        String scolumn = getTreeTableColumnName(treeTableView, column);
        if (scolumn == null || "".equals(scolumn))
            scolumn = "" + column;
        return new JSONObject()
                .put("cell", new JSONArray()
                        .put(getTreeTableNodePath(treeTableView, treeTableView.getSelectionModel().getModelItem(row))).put(scolumn))
                .toString();
    }

    public TreeTableCell<?, ?> getTreeTableCellAt(TreeTableView<?> treeTableView, Point2D point) {
        point = treeTableView.localToScene(point);
        Set<Node> lookupAll = treeTableView.lookupAll(".tree-table-cell");
        TreeTableCell<?, ?> selected = null;
        for (Node cellNode : lookupAll) {
            Bounds boundsInScene = cellNode.localToScene(cellNode.getBoundsInLocal(), true);
            if (boundsInScene.contains(point)) {
                selected = (TreeTableCell<?, ?>) cellNode;
                break;
            }
        }
        return selected;
    }

    protected int getTreeTableColumnAt(TreeTableView<?> treeTableView, Point2D point) {
        TreeTableCell<?, ?> selected = getTreeTableCellAt(treeTableView, point);
        if (selected == null)
            return -1;
        return treeTableView.getColumns().indexOf(selected.getTableColumn());
    }

    protected int getTreeTableRowAt(TreeTableView<?> treeTableView, Point2D point) {
        TreeTableCell<?, ?> selected = getTreeTableCellAt(treeTableView, point);
        if (selected == null)
            return -1;
        return selected.getTreeTableRow().getIndex();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) protected TreeTableCell<?, ?> getCellAt(TreeTableView<?> treeTableView, int row,
            int column) {
        TreeTableCell cell = getVisibleCellAt(treeTableView, row, column);
        if (cell != null)
            return cell;
        try {
            TreeTableColumn treeTableColumn = treeTableView.getColumns().get(column);
            Callback cellFactory = treeTableColumn.getCellFactory();
            cell = (TreeTableCell) cellFactory.call(treeTableColumn);
            Object value = treeTableColumn.getCellObservableValue(row).getValue();
            Method updateItem = cell.getClass().getDeclaredMethod("updateItem", new Class[] { Object.class, Boolean.TYPE });
            updateItem.setAccessible(true);
            updateItem.invoke(cell, value, false);
            return cell;
        } catch (Throwable t) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes") public TreeTableCell getVisibleCellAt(TreeTableView<?> treeTableView, int row, int column) {
        Set<Node> lookupAll = treeTableView.lookupAll(".tree-table-cell");
        TreeTableCell cell = null;
        for (Node node : lookupAll) {
            TreeTableCell<?, ?> cell1 = (TreeTableCell<?, ?>) node;
            TreeTableRow<?> tableRow = cell1.getTreeTableRow();
            TreeTableColumn<?, ?> tableColumn = cell1.getTableColumn();
            if (tableRow.getIndex() == row && tableColumn == treeTableView.getColumns().get(column)) {
                cell = cell1;
                break;
            }
        }
        return cell;
    }

    @SuppressWarnings("unchecked") public String getTextForTreeTableNodeObject(TreeTableView<?> treeTableView,
            TreeItem<?> lastPathComponent) {
        if (lastPathComponent == null || lastPathComponent.getValue() == null)
            return "";
        @SuppressWarnings("rawtypes")
        TreeTableColumn treeTableColumn = treeTableView.getTreeColumn();
        if (treeTableColumn == null)
            treeTableColumn = treeTableView.getColumns().get(0);
        ObservableValue<?> cellObservableValue = treeTableColumn.getCellObservableValue(lastPathComponent);
        String text = cellObservableValue.getValue().toString();
        if (text != null)
            return text;
        return lastPathComponent.getValue().toString();
    }

    public String getTextForTreeTableNode(TreeTableView<?> treeTableView, TreeItem<?> selectedItem) {
        StringBuilder sb = new StringBuilder();
        Object[] treePath = buildTreePath(selectedItem);
        int start = treeTableView.isShowRoot() ? 0 : 1;
        for (int i = start; i < treePath.length; i++) {
            String pathString = escapeSpecialCharacters(getTextForTreeTableNodeObject(treeTableView, (TreeItem<?>) treePath[i]));
            sb.append("/").append(pathString);
        }
        return sb.toString();
    }

    public JSONArray getTreeTableNodeText(TreeTableView<?> treeTableView, ObservableList<?> selectedItems) {
        JSONArray pa = new JSONArray();
        for (Object object : selectedItems) {
            pa.put(getTextForTreeTableNode(treeTableView, (TreeItem<?>) object));
        }
        return pa;
    }

    public String getTreeTableRowSelectionText(TreeTableView<?> treeTableView, ObservableList<?> selectedItems) {
        JSONObject pa = new JSONObject();
        return pa.put("rows", getTreeTableNodeText(treeTableView, selectedItems)).toString();
    }

    public String getTreeTableSelection(TreeTableView<?> treeTableView) {
        TreeTableViewSelectionModel<?> selectionModel = treeTableView.getSelectionModel();
        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
        ObservableList<?> selectedCells = selectionModel.getSelectedCells();
        int rowCount = treeTableView.getExpandedItemCount();
        int columnCount = treeTableView.getColumns().size();
        if (selectedIndices.size() == 0 || selectedCells.size() == 0)
            return "";
        else if ((!selectionModel.isCellSelectionEnabled() && selectedIndices.size() == treeTableView.getExpandedItemCount())
                || (selectionModel.isCellSelectionEnabled() && selectedCells.size() == (rowCount * columnCount)))
            return "all";
        else if (!selectionModel.isCellSelectionEnabled()) {
            return getTreeTableRowSelectionText(treeTableView, selectionModel.getSelectedItems());
        } else {
            int[] rows = new int[selectedCells.size()];
            int[] columns = new int[selectedCells.size()];
            JSONObject cells = new JSONObject();
            JSONArray value = new JSONArray();
            for (int i = 0; i < selectedCells.size(); i++) {
                TreeTablePosition<?, ?> cell = (TreeTablePosition<?, ?>) selectedCells.get(i);
                rows[i] = cell.getRow();
                columns[i] = cell.getColumn();
                List<String> cellValue = new ArrayList<>();
                cellValue.add(getTreeTableNodePath(treeTableView, selectionModel.getModelItem(cell.getRow())));
                cellValue.add(getTreeTableColumnName(treeTableView, cell.getColumn()));
                value.put(cellValue);
            }
            cells.put("cells", value);
            return cells.toString();
        }
    }

    private String getTreeTableNodePath(TreeTableView<?> treeTableView, TreeItem<?> treeItem) {
        return getTextForTreeTableNode(treeTableView, treeItem);
    }

    protected String getTreeTableColumnName(TreeTableView<?> treeTableView, int columnIndex) {
        ObservableList<?> columns = treeTableView.getColumns();
        TreeTableColumn<?, ?> tableColumn = (TreeTableColumn<?, ?>) columns.get(columnIndex);
        return tableColumn.getText();
    }

    protected int getTreeTableColumnIndex(TreeTableView<?> treeTableView, String columnName) {
        ObservableList<?> columns = treeTableView.getColumns();
        int ncolumns = columns.size();
        for (int i = 0; i < ncolumns; i++) {
            @SuppressWarnings("rawtypes")
            String column = ((TreeTableColumn) columns.get(i)).getText();
            if (columnName.equals(escapeSpecialCharacters(column)))
                return i;
        }
        throw new RuntimeException("Could not find column " + columnName + " in tree table");
    }

    @SuppressWarnings("unchecked") protected int getTreeTableNodeIndex(TreeTableView<?> treeTableView, String path) {
        String[] tokens = path.substring(1).split("(?<!\\\\)/");
        Object rootNode = treeTableView.getRoot();
        int start = treeTableView.isShowRoot() ? 1 : 0;
        List<TreeItem<?>> treePath = new ArrayList<TreeItem<?>>();
        treePath.add((TreeItem<?>) rootNode);
        StringBuilder searchedPath = new StringBuilder();
        if (treeTableView.isShowRoot()) {
            String rootNodeText = unescapeSpecialCharacters(tokens[0]);
            searchedPath.append("/" + rootNodeText);
            if (rootNode == null)
                throw new RuntimeException("TreeTableView does not have a root node!");
            if (!searchedPath.toString()
                    .equals("/" + getTextForTreeTableNodeObject(treeTableView, treePath.get(treePath.size() - 1))))
                throw new RuntimeException("TreeTableView root node does not match: Expected </"
                        + getTextForTreeTableNodeObject(treeTableView, treePath.get(treePath.size() - 1)) + "> Actual: <"
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
                if (childText.equals(getTextForTreeTableNodeObject(treeTableView, childPath.get(childPath.size() - 1)))) {
                    treePath = childPath;
                    matched = true;
                    break;
                }
            }
            if (!matched)
                return -1;
        }
        @SuppressWarnings("rawtypes")
        TreeItem treeItem = treePath.get(treePath.size() - 1);
        return treeTableView.getRow(treeItem);
    }

    protected int[] getTreeTableSelectedRows(TreeTableView<?> treeTableView, String value) {
        JSONArray o = new JSONObject(value).getJSONArray("rows");
        int[] rows = new int[o.length()];
        for (int i = 0; i < o.length(); i++)
            rows[i] = getTreeTableNodeIndex(treeTableView, o.getString(i));
        return rows;
    }

    @SuppressWarnings("unchecked") protected void selectTreeTableCells(TreeTableView<?> treeTableView, String value) {
        @SuppressWarnings("rawtypes")
        TreeTableViewSelectionModel selectionModel = treeTableView.getSelectionModel();
        JSONObject cells = new JSONObject(value);
        JSONArray object = (JSONArray) cells.get("cells");
        for (int i = 0; i < object.length(); i++) {
            JSONArray jsonArray = object.getJSONArray(i);
            int rowIndex = getTreeTableNodeIndex(treeTableView, jsonArray.getString(0));
            int columnIndex = getTreeTableColumnIndex(treeTableView, jsonArray.getString(1));
            @SuppressWarnings("rawtypes")
            TreeTableColumn column = treeTableView.getColumns().get(columnIndex);
            if (getVisibleCellAt(treeTableView, rowIndex, columnIndex) == null) {
                treeTableView.scrollToColumn(column);
                treeTableView.scrollTo(rowIndex);
            }
            selectionModel.select(rowIndex, column);
        }
    }

    @SuppressWarnings("rawtypes") protected TreeTableColumn getTreeTableColumnAt(TreeTableView<?> treeTableView, int index) {
        return treeTableView.getColumns().get(index);
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

    public String parentMenuText(ObservableList<Menu> menus, int index) {
        String original = getMenuText(menus, index);
        String itemText = original;
        int suffixIndex = 0;
        for (int i = 0; i < index; i++) {
            String current = getMenuText(menus, i);
            if (current.equals(original)) {
                itemText = String.format("%s(%d)", original, ++suffixIndex);
            }
        }
        return itemText;
    }

    private String getMenuText(ObservableList<Menu> menus, int index) {
        Menu menu = menus.get(index);
        String text = menu.getText();
        if (text == null || "".equals(text))
            return getMenuTextFromIcon(menu, index);
        return text;
    }

    @SuppressWarnings("deprecation") private String getMenuTextFromIcon(Menu menu, int index) {
        Node graphic = menu.getGraphic();
        if (graphic == null || !(graphic instanceof ImageView)) {
            return "menuindex-" + index;
        }
        return nameFromImage(((ImageView) graphic).getImage().impl_getUrl());
    }

    public String getTextForMenuItem(MenuItem menuItem, Menu parentMenu) {
        int index = parentMenu.getItems().indexOf(menuItem);
        String original = getMenuItemText(parentMenu, index);
        String itemText = original;
        int suffixIndex = 0;

        for (int i = 0; i < index; i++) {
            String current = getMenuItemText(parentMenu, i);

            if (current.equals(original)) {
                itemText = String.format("%s(%d)", original, ++suffixIndex);
            }
        }
        return itemText;
    }

    public String getMenuItemText(Menu parentMenu, int index) {
        MenuItem menuItem = parentMenu.getItems().get(index);
        String text = menuItem.getText();
        if (text == null || "".equals(text))
            return getTextFromIcon(menuItem, index);
        return text;
    }

    @SuppressWarnings("deprecation") protected String getTextFromIcon(MenuItem menuItem, int index) {
        Node graphic = menuItem.getGraphic();
        if (graphic == null || !(graphic instanceof ImageView)) {
            if (index == -1)
                return "EmptyTitleMenu";
            return "MenuItemIndex-" + index;
        }
        return nameFromImage(((ImageView) graphic).getImage().impl_getUrl());
    }

    protected String getButtonText(ButtonBase button) {
        return button.getText();
    }

    protected String getCheckBoxText(CheckBox checkBox) {
        return getButtonText(checkBox);
    }

    protected String getToggleText(ToggleButton toggleButton) {
        return getButtonText(toggleButton);
    }

    protected String getSliderValue(Slider slider) {
        return slider.getValue() + "";
    }

    protected String getHTMLEditorText(HTMLEditor htmlEditor) {
        return htmlEditor.getHtmlText();
    }

    protected String getTitledPaneText(TitledPane titledPane) {
        return titledPane.getText();
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

    public final String getLabeledBy() {
        Parent root = node.getScene().getRoot();
        Set<Node> allLabels = root.lookupAll(".label");
        for (Node node2 : allLabels) {
            Label label = (Label) node2;
            if (label.getLabelFor() == node) {
                return label.getText();
            }
        }
        return null;
    }
}
