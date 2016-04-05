package net.sourceforge.marathon.javafxrecorder.component;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxListCell;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXCheckBoxListCell extends RFXComponent {

    public RFXCheckBoxListCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @SuppressWarnings("unchecked") @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        CheckBoxListCell cell = (CheckBoxListCell) node;
        ObservableValue<Boolean> call = (ObservableValue<Boolean>) cell.getSelectedStateCallback().call(cell.getItem());
        int selection = call.getValue() ? 2 : 0;
        String text = cell.getText() + ":" + JavaFXCheckBoxElement.states[selection];
        return text;
    }
}
