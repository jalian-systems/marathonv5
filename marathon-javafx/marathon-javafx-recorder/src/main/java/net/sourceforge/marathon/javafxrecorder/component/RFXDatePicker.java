package net.sourceforge.marathon.javafxrecorder.component;

import java.time.LocalDate;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXDatePicker extends RFXComponent {

    public RFXDatePicker(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusLost(RFXComponent next) {
        DatePicker datePicker = (DatePicker) node;
        LocalDate value = datePicker.getValue();
        if (value == null && datePicker.isEditable())
            recorder.recordSelect(this, datePicker.getEditor().getText());
        else
            recorder.recordSelect(this, getDatePickerText(datePicker, value));
    }

    @Override public String _getText() {
        return getDatePickerText((DatePicker) node, ((DatePicker) node).getValue());
    }
}
