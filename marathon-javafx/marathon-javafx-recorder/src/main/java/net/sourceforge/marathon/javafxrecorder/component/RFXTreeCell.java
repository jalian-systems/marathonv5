package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTreeCell extends RFXComponent {

    public RFXTreeCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public String _getValue() {
        TreeCell<?> cell = (TreeCell<?>) node;
        Node graphic = cell.getGraphic();
        RFXComponent component = getFinder().findRawRComponent(graphic, null, recorder);
        if (graphic != null && component != null)
            return component._getValue();
        return super._getValue();
    }
}
