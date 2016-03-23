package net.sourceforge.marathon.javafxrecorder.component;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXCheckBoxTreeTableCell extends RFXComponent {

    public RFXCheckBoxTreeTableCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        CheckBoxTreeTableCell cell = (CheckBoxTreeTableCell) node;
        @SuppressWarnings("unchecked")
        ObservableValue<Boolean> call = (ObservableValue<Boolean>) cell.getSelectedStateCallback().call(cell.getItem());
        int selection = call.getValue() ? 2 : 0;
        return JavaFXCheckBoxElement.states[selection];
    }

}
