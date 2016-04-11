package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXSlider extends RFXComponent {

    private double prevValue;

    public RFXSlider(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        prevValue = ((Slider) node).getValue();
    }

    @Override public void focusLost(RFXComponent next) {
        double current = ((Slider) node).getValue();
        if (current != prevValue)
            recorder.recordSelect(this, "" + current);
    }

    @Override public String _getText() {
        return getSliderValue((Slider) node);
    }
}
