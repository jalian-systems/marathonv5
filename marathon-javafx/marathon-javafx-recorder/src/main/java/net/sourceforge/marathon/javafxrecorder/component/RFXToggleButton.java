package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXToggleButton extends RFXComponent {

    private Boolean prevSelection;

    public RFXToggleButton(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseEntered(MouseEvent me) {
        prevSelection = ((ToggleButton) node).isSelected();
    }

    @Override protected void mouseClicked(MouseEvent me) {
        boolean selected = ((ToggleButton) node).isSelected();
        if (prevSelection == null || selected != prevSelection.booleanValue())
            recorder.recordSelect(this, Boolean.toString(selected));
        prevSelection = selected;
    }
}
