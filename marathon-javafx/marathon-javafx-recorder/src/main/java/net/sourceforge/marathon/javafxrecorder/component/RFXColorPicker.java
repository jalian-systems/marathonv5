package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXColorPicker extends RFXComponent {

    private String prevColor;

    public RFXColorPicker(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        ColorPicker colorPicker = (ColorPicker) node;
        prevColor = getColorCode(colorPicker.getValue());
    }

    @Override public void focusLost(RFXComponent next) {
        String currentColor = getColorCode(((ColorPicker) node).getValue());
        if (!currentColor.equals(prevColor)) {
            recorder.recordSelect(this, currentColor);
        }
    }

    @Override public String _getText() {
        return getColorCode(((ColorPicker) node).getValue());
    }
}
