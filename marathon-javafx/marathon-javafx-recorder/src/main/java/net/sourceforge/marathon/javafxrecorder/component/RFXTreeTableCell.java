package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeTableCell extends RFXComponent {

    public RFXTreeTableCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public String _getValue() {
        TreeTableCell<?, ?> cell = (TreeTableCell<?, ?>) node;
        Node graphic = cell.getGraphic();
        RFXComponent component = getFinder().findRawRComponent(graphic, null, recorder);
        if (component != null)
            return component._getValue();
        if (graphic == null && !cell.isEditing())
            return cell.getTableColumn().getCellObservableValue(cell.getIndex()).getValue().toString();
        return null;
    }

}
