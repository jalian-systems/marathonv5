package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXListView extends RFXComponent {

    private int index = -1;
    private Point2D point;
    private String cellValue;
    private String listText;
    private String cellText;

    public RFXListView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        this.point = point;
    }

    @Override public void focusGained(RFXComponent prev) {
        ListView<?> listView = ((ListView<?>) node);
        index = getIndexAt(listView, point);
        cellValue = getListCellValue(listView, index);
        cellText = getListSelectionText(listView, index);
        listText = getListSelectionText(listView);
    }

    private String getListCellValue(ListView<?> listView, int index) {
        if (index == -1)
            return null;
        ListCell<?> listCell = getCellAt(listView, index);
        RFXComponent cellComponent = getFinder().findRawRComponent(listCell, null, recorder);
        String ctext = cellComponent.getValue();
        return ctext;
    }

    @Override public void focusLost(RFXComponent next) {
        ListView<?> listView = ((ListView<?>) node);
        String currCellText = getListCellValue(listView, index);
        if (currCellText != null && !currCellText.equals(cellValue)) {
            recorder.recordSelect2(this, currCellText, true);
        }
        if (next == null || getComponent() != next.getComponent()) {
            String currListText = getListSelectionText(listView);
            if (!currListText.equals(listText)) {
                recorder.recordSelect(this, currListText);
            }
        }
    }

    @Override public String getCellInfo() {
        return cellText;
    }

    @Override public String[][] getContent() {
        return getContent((ListView<?>) node);
    }

    @Override public String _getText() {
        return getListSelectionText((ListView<?>) node);
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + index;
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
        if (index != other.index)
            return false;
        return true;
    }

    @Override public String toString() {
        return "RFXListView [index=" + index + ", cellText=" + cellText + ", listText=" + listText + "]";
    }

}
