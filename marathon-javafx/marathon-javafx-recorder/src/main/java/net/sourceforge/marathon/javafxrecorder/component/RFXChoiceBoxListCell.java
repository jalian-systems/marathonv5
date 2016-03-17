package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXChoiceBoxListCell extends RFXComponent {

    public RFXChoiceBoxListCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        ChoiceBoxListCell cell = (ChoiceBoxListCell) node;
        @SuppressWarnings("rawtypes")
        StringConverter converter = cell.getConverter();
        if (converter != null)
            return converter.toString(cell.getItem());
        return cell.getItem().toString();
    }
}
