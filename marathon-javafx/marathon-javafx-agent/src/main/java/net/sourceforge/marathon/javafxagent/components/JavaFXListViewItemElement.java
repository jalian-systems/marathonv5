package net.sourceforge.marathon.javafxagent.components;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
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

    @Override public Node getPseudoComponent() {
        ListView<?> listView = (ListView<?>) getComponent();
        int index = getListItemIndex(listView, getListSelectionText(listView, itemIndex));
        if (index == -1)
            return null;
        listView.scrollTo(index);
        Set<Node> lookupAll = listView.lookupAll(".list-cell");
        for (Node node : lookupAll) {
            ListCell<?> cell = (ListCell<?>) node;
            if (cell.getItem() == listView.getItems().get(index))
                return cell;
        }
        return null;
    }

    @Override public void click() {
        Node listCell = getPseudoComponent();
        if (listCell != null && listCell instanceof CheckBoxListCell<?>) {
            clickListCell(((CheckBoxListCell<?>) listCell));
            return;
        }
        super.click();
    }

    @Override public void click(int button, int clickCount, double xoffset, double yoffset) {
        Node listCell = getPseudoComponent();
        if (listCell != null && listCell instanceof CheckBoxListCell<?>) {
            CheckBox cb = (CheckBox) ((CheckBoxListCell<?>) listCell).lookup(".check-box");
            IJavaFXElement cbElement = JavaFXElementFactory.createElement(cb, driver, window);
            cbElement.click(button, clickCount, xoffset, yoffset);
            return;
        }
        super.click(button, clickCount, xoffset, yoffset);
    }

    private void clickListCell(CheckBoxListCell<?> checkBoxListCell) {
        CheckBox cb = (CheckBox) checkBoxListCell.lookup(".check-box");
        IJavaFXElement cbElement = JavaFXElementFactory.createElement(cb, driver, window);
        cbElement.click();
    }

}
