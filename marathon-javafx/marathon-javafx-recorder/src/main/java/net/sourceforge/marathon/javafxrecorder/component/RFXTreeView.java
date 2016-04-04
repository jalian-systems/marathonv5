package net.sourceforge.marathon.javafxrecorder.component;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeView extends RFXComponent {

    private Point2D point;
    private int row = -1;
    private String cellValue;
    private String cellText;
    private String treeText;

    public RFXTreeView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        this.point = point;
    }

    @Override public void focusGained(RFXComponent prev) {
        TreeView<?> treeView = (TreeView<?>) node;
        row = getRowAt(treeView, point);
        cellValue = getTreeCellValue(treeView, row);
        cellText = getTextForNode(treeView, treeView.getTreeItem(row));
        treeText = getSelectedTreeNodeText(treeView, treeView.getSelectionModel().getSelectedItems());
    }

    private String getTreeCellValue(TreeView<?> treeView, int index2) {
        if (row == -1)
            return null;
        TreeCell<?> treeCell = getCellAt(treeView, row);
        RFXComponent cellComponent = getFinder().findRawRComponent(treeCell, null, recorder);
        String ctext = cellComponent.getValue();
        return ctext;
    }

    @Override public void focusLost(RFXComponent next) {
        TreeView<?> treeView = (TreeView<?>) getComponent();
        ObservableList<?> selectedItems = treeView.getSelectionModel().getSelectedItems();
        String currentCellText = getTreeCellValue(treeView, row);
        if (currentCellText != null && !currentCellText.equals(cellValue)) {
            recorder.recordSelect2(this, currentCellText, true);
        }
        if (next == null || getComponent() != next.getComponent()) {
            String currListText = getSelectedTreeNodeText(treeView, selectedItems);
            if (!currListText.equals(treeText)) {
                recorder.recordSelect(this, currListText);
            }
        }
    }

    @Override public String getCellInfo() {
        return cellText;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + row;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RFXTreeView other = (RFXTreeView) obj;
        if (row != other.row)
            return false;
        return true;
    }

    @Override public String _getText() {
        return getSelectedTreeNodeText((TreeView<?>) node, ((TreeView<?>) node).getSelectionModel().getSelectedItems());
    }

    @Override public String[][] getContent() {
        return getContent((TreeView<?>) node);
    }
}
