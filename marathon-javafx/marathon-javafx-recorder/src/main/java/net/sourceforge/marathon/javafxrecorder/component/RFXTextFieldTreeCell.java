package net.sourceforge.marathon.javafxrecorder.component;

import java.util.logging.Logger;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTextFieldTreeCell extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXTextFieldTreeCell.class.getName());

    public RFXTextFieldTreeCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        TextFieldTreeCell<?> cell = (TextFieldTreeCell<?>) node;
        @SuppressWarnings("rawtypes")
        StringConverter converter = cell.getConverter();
        if (converter != null) {
            return converter.toString(cell.getItem());
        }
        return cell.getItem().toString();
    }

}
