package net.sourceforge.marathon.javafxrecorder.component;

import java.util.logging.Logger;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTextFieldListCell extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXChoiceBoxListCell.class.getName());

    public RFXTextFieldListCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        TextFieldListCell<?> cell = (TextFieldListCell<?>) node;
        @SuppressWarnings("rawtypes")
        StringConverter converter = cell.getConverter();
        if (converter != null) {
            return converter.toString(cell.getItem());
        }
        return cell.getItem().toString();
    }

}
