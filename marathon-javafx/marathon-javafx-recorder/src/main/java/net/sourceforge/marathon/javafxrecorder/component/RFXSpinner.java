package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXSpinner extends RFXComponent {

    private String oldValue;

    public RFXSpinner(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RFXComponent prev) {
        oldValue = getSpinnerText((Spinner<?>) node);
    }

    @Override public void focusLost(RFXComponent next) {
        Spinner<?> spinner = (Spinner<?>) node;
        String currentValue = getSpinnerText(spinner);
        if (!currentValue.equals(oldValue))
            recorder.recordSelect(this, currentValue);
    }

    @Override public String _getText() {
        return getSpinnerText((Spinner<?>) node);
    }
}
