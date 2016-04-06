package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTableCell extends RFXComponent {

    private Point2D point;

    public RFXTableCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        this.point = point;
    }

    @Override public String _getValue() {
        TableCell<?, ?> cell = (TableCell<?, ?>) node;
        Node graphic = cell.getGraphic();
        RFXComponent component = getFinder().findRawRComponent(graphic, point, recorder);
        if (graphic != null && component != null) {
            if (graphic instanceof CheckBox) {
                String cellText = cell.getText() == null ? "" : cell.getText();
                return cellText + ":" + component._getValue();
            }
            return component._getValue();
        }
        return super._getValue();
    }
}
