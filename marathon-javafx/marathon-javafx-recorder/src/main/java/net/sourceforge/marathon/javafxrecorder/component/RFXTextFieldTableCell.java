package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTextFieldTableCell extends RFXComponent {

    public RFXTextFieldTableCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String _getValue() {
        TextFieldTableCell<?, ?> cell = (TextFieldTableCell<?, ?>) node;
        @SuppressWarnings("rawtypes")
        StringConverter converter = cell.getConverter();
        if (converter != null) {
            return converter.toString(cell.getItem());
        }
        return cell.getItem().toString();
    }

}
