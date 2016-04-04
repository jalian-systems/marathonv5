package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxagent.components.JavaFXCheckBoxElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXCheckBox extends RFXComponent {

    private Integer prevSelection; // 0 - unchecked, 1 - indeterminate, 2 -
                                   // checked

    public RFXCheckBox(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseEntered(MouseEvent me) {
        prevSelection = getSelection((CheckBox) node);
    }

    @Override protected void mouseClicked(MouseEvent me) {
        int selection = getSelection((CheckBox) node);
        if (prevSelection == null || selection != prevSelection)
            recorder.recordSelect(this, JavaFXCheckBoxElement.states[selection]);
        prevSelection = selection;
    }

    @Override public String _getValue() {
        int selection = getSelection((CheckBox) node);
        return JavaFXCheckBoxElement.states[selection];
    }

    @Override public String _getText() {
        return getCheckBoxText((CheckBox) node);
    }
}
