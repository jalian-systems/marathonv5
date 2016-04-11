package net.sourceforge.marathon.javafxagent.components;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;

public class JavaFXTreeViewNodeElement extends JavaFXElement implements IPseudoElement {

    private JavaFXTreeViewElement parent;
    private String path;

    public JavaFXTreeViewNodeElement(JavaFXTreeViewElement parent, String path) {
        super(parent);
        this.parent = parent;
        this.path = path;
    }

    public JavaFXTreeViewNodeElement(JavaFXTreeViewElement parent, int row) {
        super(parent);
        this.parent = parent;
        this.path = rowToPath(row);
    }

    @Override public IJavaFXElement getParent() {
        return parent;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "select-by-properties").put("parameters",
                new JSONArray().put(new JSONObject().put("select", path).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @SuppressWarnings("unchecked") @Override public Node getPseudoComponent() {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        @SuppressWarnings("rawtypes")
        TreeItem item = getPath(treeView, path);
        if (item == null)
            return null;
        treeView.scrollTo(treeView.getRow(item));
        return getCellAt(treeView, item);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor"))
            return Arrays.asList(JavaFXElementFactory.createElement(getEditor(), driver, window));
        return super.getByPseudoElement(selector, params);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) private Node getEditor() {
        TreeCell cell = (TreeCell) getPseudoComponent();
        TreeView treeView = (TreeView) getComponent();
        treeView.edit(cell.getTreeItem());
        Node cellComponent = cell.getGraphic();
        cellComponent.getProperties().put("marathon.celleditor", true);
        cellComponent.getProperties().put("marathon.cell", cell);
        return cellComponent;
    }

    @Override public void _moveto() {
        Point2D midpoint = _getMidpoint();
        parent._moveto(midpoint.getX(), midpoint.getY());
    }

    @Override public void _moveto(double xoffset, double yoffset) {
        Node cell = getPseudoComponent();
        Point2D pCoords = cell.localToParent(xoffset, yoffset);
        parent._moveto(pCoords.getX(), pCoords.getY());
    }

    @Override public Point2D _getMidpoint() {
        Node cell = getPseudoComponent();
        Bounds boundsInParent = cell.getBoundsInParent();
        double x = boundsInParent.getWidth() / 2;
        double y = boundsInParent.getHeight() / 2;
        return cell.localToParent(x, y);
    }

    @Override public Object _makeVisible() {
        getPseudoComponent();
        return null;
    }

    public String getPath() {
        return path;
    }

    @Override public String _getText() {
        TreeCell<?> cell = (TreeCell<?>) getPseudoComponent();
        JavaFXElement cellElement = (JavaFXElement) JavaFXElementFactory.createElement(cell, driver, window);
        return cellElement._getValue();
    }
}
