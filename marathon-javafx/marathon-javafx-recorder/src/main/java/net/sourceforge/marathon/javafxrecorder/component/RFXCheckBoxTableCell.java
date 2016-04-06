package net.sourceforge.marathon.javafxrecorder.component;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXCheckBoxTableCell extends RFXComponent {

    public RFXCheckBoxTableCell(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Override public String _getValue() {
        CheckBoxTableCell cell = (CheckBoxTableCell) node;
        Callback selectedStateCallback = cell.getSelectedStateCallback();
        String cbText;
        if (selectedStateCallback != null) {
            ObservableValue<Boolean> call = (ObservableValue<Boolean>) selectedStateCallback.call(cell.getItem());
            int selection = call.getValue() ? 2 : 0;
            cbText = JavaFXCheckBoxElement.states[selection];
        } else {
            Node cb = cell.getGraphic();
            RFXComponent comp = getFinder().findRawRComponent(cb, null, null);
            cbText = comp._getValue();

        }
        String cellText = cell.getText();
        if (cellText == null)
            cellText = "";
        String text = cellText + ":" + cbText;
        return text;
    }
}
