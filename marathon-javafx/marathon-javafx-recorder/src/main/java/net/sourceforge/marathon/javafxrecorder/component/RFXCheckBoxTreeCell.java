package net.sourceforge.marathon.javafxrecorder.component;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTreeCell;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXCheckBoxTreeCell extends RFXComponent {

    public RFXCheckBoxTreeCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public String _getValue() {
        @SuppressWarnings("rawtypes")
        CheckBoxTreeCell cell = (CheckBoxTreeCell) node;
        @SuppressWarnings("unchecked")
        ObservableValue<Boolean> call = (ObservableValue<Boolean>) cell.getSelectedStateCallback().call(cell.getTreeItem());
        int selection = call.getValue() ? 2 : 0;
        return JavaFXCheckBoxElement.states[selection];
    }
}
