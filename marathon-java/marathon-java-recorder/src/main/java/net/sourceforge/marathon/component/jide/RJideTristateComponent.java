package net.sourceforge.marathon.component.jide;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import com.jidesoft.swing.TristateCheckBox;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javaagent.components.jide.JideTristateCheckBoxElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RJideTristateComponent extends RComponent {

    private int prevSelection;

    public static final int STATE_UNSELECTED = 0;
    public static final int STATE_SELECTED = 1;
    public static final int STATE_MIXED = 2;

    public RJideTristateComponent(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        int state = ((TristateCheckBox) component).getState();
        prevSelection = state;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int selected = ((TristateCheckBox) component).getState();
        if (selected != prevSelection) {
            recorder.recordSelect(this, JideTristateCheckBoxElement.states[selected]);
        }
        prevSelection = selected;
    }
}
