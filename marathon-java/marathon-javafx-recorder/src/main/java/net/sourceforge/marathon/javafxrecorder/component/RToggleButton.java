package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JToggleButton;

import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RToggleButton extends RComponent {
    private Boolean prevSelection;

    public RToggleButton(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseEntered(MouseEvent me) {
        prevSelection = ((JToggleButton) component).isSelected();
    }

    @Override protected void mouseClicked(MouseEvent me) {
        boolean selected = ((JToggleButton) component).isSelected();
        if (prevSelection == null || selected != prevSelection.booleanValue())
            recorder.recordSelect(this, Boolean.toString(selected));
        prevSelection = selected;
    }
}
