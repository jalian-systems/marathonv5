package net.sourceforge.marathon.javafxagent.components;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.IPseudoElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElementFactory;

public class JavaFXListViewItemElement extends JavaFXElement implements IPseudoElement {

    private JavaFXElement parent;
    private int itemIndex;

    public JavaFXListViewItemElement(JavaFXElement parent, int item) {
        super(parent);
        this.parent = parent;
        this.itemIndex = item;
    }

    public JavaFXListViewItemElement(JavaFXElement parent, String itemText) {
        super(parent);
        this.parent = parent;
        this.itemIndex = getListItemIndex((ListView<?>) getComponent(), itemText);
    }

    @Override public IJavaFXElement getParent() {
        return parent;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "select-by-properties").put("parameters", new JSONArray()
                .put(new JSONObject().put("select", getListSelectionText((ListView<?>) getComponent(), itemIndex)).toString()));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public void _moveto() {
        Point2D midpoint = _getMidpoint();
        parent._moveto(midpoint.getX(), midpoint.getY());
    }

    @Override public void _moveto(double xoffset, double yoffset) {
        Node cell = getCellAt((ListView<?>) getComponent(), itemIndex);
        Point2D pCoords = cell.localToParent(xoffset, yoffset);
        parent._moveto(pCoords.getX(), pCoords.getY());
    }

    @Override public Point2D _getMidpoint() {
        Node cell = getCellAt((ListView<?>) getComponent(), itemIndex);
        Bounds boundsInParent = cell.getBoundsInParent();
        double x = boundsInParent.getWidth() / 2;
        double y = boundsInParent.getHeight() / 2;
        return cell.localToParent(x, y);
    }

    @Override public Node getPseudoComponent() {
        ListView<?> listView = (ListView<?>) getComponent();
        listView.scrollTo(itemIndex);
        return getCellAt(listView, itemIndex);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor"))
            return Arrays.asList(JavaFXElementFactory.createElement(getEditor(), driver, window));
        return super.getByPseudoElement(selector, params);
    }

    private Node getEditor() {
        ListCell<?> cell = (ListCell<?>) getPseudoComponent();
        cell.getListView().edit(cell.getIndex());
        Node cellComponent = cell.getGraphic();
        cellComponent.getProperties().put("marathon.celleditor", true);
        cellComponent.getProperties().put("marathon.cell", cell);
        return cellComponent;
    }

    @Override public String _getText() {
        ListCell<?> cell = (ListCell<?>) getPseudoComponent();
        Node graphic = cell.getGraphic();
        JavaFXElement graphicElement = (JavaFXElement) JavaFXElementFactory.createElement(graphic, driver, window);
        if (graphic != null && graphicElement != null) {
            if (graphic instanceof CheckBox)
                return cell.getText() + ":" + graphicElement._getValue();
            else
                return graphicElement._getValue();
        }
        JavaFXElement cellElement = (JavaFXElement) JavaFXElementFactory.createElement(cell, driver, window);
        return cellElement._getValue();
    }
}
