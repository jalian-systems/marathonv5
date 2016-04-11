package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JSlider;

import net.sourceforge.marathon.javaagent.components.JSliderJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RSlider extends RComponent {

    private int value = -1;

    public RSlider(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusLost(RComponent next) {
        int current = ((JSlider) component).getValue();
        if (current != value)
            recorder.recordSelect(this, "" + current);
    }

    @Override public void focusGained(RComponent prev) {
        value = ((JSlider) component).getValue();
    }

    @Override public String getText() {
        return JSliderJavaElement.getCurrentValue((JSlider) component);
    }
}
