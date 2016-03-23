package net.sourceforge.marathon.javafxagent.components;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;

public class JavaFXTreeTableViewCellElement extends JavaFXElement implements IPseudoElement {

    private String path;
    private int viewColumn;
    private JavaFXElement parent;

    public JavaFXTreeTableViewCellElement(JavaFXElement parent, int viewRow, int viewColumn) {
        super(parent);
        this.parent = parent;
        this.path = rowToTreeTablePath(viewRow);
        this.viewColumn = viewColumn;
    }

    public JavaFXTreeTableViewCellElement(JavaFXElement parent, String path, int viewColumn) {
        super(parent);
        this.parent = parent;
        this.path = path;
        this.viewColumn = viewColumn;
    }

    private String rowToTreeTablePath(int rowView) {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getComponent();
        TreeItem<?> treeItem = treeTableView.getTreeItem(rowView);
        if (treeItem == null)
            throw new RuntimeException("Trying to create a tree item for row " + rowView + " which is invalid");
        return getTextForTreeTableNode(treeTableView, treeItem);
    }

    @Override public IJavaFXElement getParent() {
        return parent;
    }

    @Override public String createHandle() {
        JSONObject pa = new JSONObject().put("cell", new JSONArray().put(path).put(getViewColumnName()));
        JSONObject o = new JSONObject().put("selector", "select-by-properties").put("parameters",
                new JSONArray().put(new JSONObject().put("select", pa.toString()).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public Node getPseudoComponent() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getComponent();
        int rowIndex = getTreeTableNodeIndex(treeTableView, path);
        treeTableView.scrollToColumnIndex(viewColumn);
        treeTableView.scrollTo(rowIndex);
        return getTreeTableCellAt(treeTableView, rowIndex, viewColumn);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor"))
            return Arrays.asList(JavaFXElementFactory.createElement(getEditor(), driver, window));
        return super.getByPseudoElement(selector, params);
    }

    private Node getEditor() {
        TreeTableCell<?, ?> cell = (TreeTableCell<?, ?>) getPseudoComponent();
        cell.startEdit();
        Node cellComponent = cell.getGraphic();
        cellComponent.getProperties().put("marathon.celleditor", true);
        return cellComponent;
    }

    public int getViewColumn() {
        return viewColumn + 1;
    }

    public String getViewColumnName() {
        String columnName = getTreeTableColumnName((TreeTableView<?>) parent.getComponent(), viewColumn);
        if (columnName == null)
            return "" + (viewColumn + 1);
        return columnName;
    }

    public String getColumn() {
        String columnName = getTreeTableColumnName((TreeTableView<?>) parent.getComponent(), viewColumn);
        if (columnName == null)
            return "" + viewColumn;
        return columnName;
    }

    public String getPath() {
        return path;
    }
}
