package net.sourceforge.marathon.javafxrecorder.component;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXListView extends RFXComponent {

    private String cellInfo;
    private ObservableList<?> prevSelectedItems;
    private String prevSelectedItemTexts;
    private Point2D point;
    private RFXComponent prevComponent;

    public RFXListView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        this.point = point;
    }

    @Override public void focusGained(RFXComponent prev) {
        ListView<?> comp = (ListView<?>) node;
        if (comp != null)
            prevSelectedItems = comp.getSelectionModel().getSelectedItems();
        prevSelectedItemTexts = getListSelectionText((ListView<?>) node);
        this.prevComponent = prev;
    }

    @Override public void focusLost(RFXComponent next) {
        ListView<?> listView = ((ListView<?>) node);
        String currentText = getCurrentText();
        if (currentText != null && !currentText.equals(prevSelectedItemTexts)) {
            recorder.recordSelect2(this, currentText, true);
        }
        if (prevSelectedItems != listView.getSelectionModel().getSelectedItems() && prevComponent != next) {
            ObservableList<?> selectedItems = listView.getSelectionModel().getSelectedItems();
            if (selectedItems == null || selectedItems.size() == 0)
                recorder.recordSelect(this, "[]");
            String text = getListSelectionText(listView);
            recorder.recordSelect(this, text);
        }
    }

    private String getCurrentText() {
        ListCell<?> listCell = (ListCell<?>) getTarget(node, point.getX(), point.getY());
        if (listCell == null)
            return null;
        return getListSelectionText((ListView<?>) node, getListItemIndex((ListView<?>) node, listCell.getItem().toString()));
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((prevSelectedItemTexts == null) ? 0 : prevSelectedItemTexts.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RFXListView other = (RFXListView) obj;
        if (prevSelectedItemTexts == null) {
            if (other.prevSelectedItemTexts != null)
                return false;
        } else if (!prevSelectedItemTexts.equals(other.prevSelectedItemTexts))
            return false;
        return true;
    }

    @Override public String[][] getContent() {
        return getContent((ListView<?>) node);
    }

    @Override public String getCellInfo() {
        return cellInfo;
    }

    private Node getTarget_source, getTarget_target;
    private double getTarget_x, getTarget_y;

    private Node getTarget(Node source, double x, double y) {
        if (getTarget_source == source && getTarget_x == x && getTarget_y == y) {
            return getTarget_target;
        }
        List<Node> hits = new ArrayList<>();
        if (!(source instanceof Parent))
            return source;
        ObservableList<Node> children = ((Parent) source).getChildrenUnmodifiable();
        for (Node child : children) {
            checkHit(child, x, y, hits, "");
        }
        Node target = hits.size() > 0 ? hits.get(hits.size() - 1) : source;
        getTarget_source = source;
        getTarget_target = target;
        getTarget_x = x;
        getTarget_y = y;
        if (target instanceof CheckBoxListCell<?>) {
            return target;
        }
        return null;
    }

    private void checkHit(Node child, double x, double y, List<Node> hits, String indent) {
        Bounds boundsInParent = child.getBoundsInParent();
        if (boundsInParent.contains(x, y)) {
            hits.add(child);
            if (!(child instanceof Parent))
                return;
            ObservableList<Node> childrenUnmodifiable = ((Parent) child).getChildrenUnmodifiable();
            for (Node node : childrenUnmodifiable) {
                checkHit(node, x, y, hits, "    " + indent);
            }
        }
    }

}
