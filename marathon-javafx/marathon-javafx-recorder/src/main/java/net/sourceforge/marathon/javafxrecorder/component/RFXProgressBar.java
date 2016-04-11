package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXProgressBar extends RFXComponent {

    public RFXProgressBar(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseReleased(MouseEvent me) {
        ProgressBar progressBar = (ProgressBar) node;
        String currentValue = getProgressText(progressBar);
        if (currentValue != null && !currentValue.equals("-1"))
            recorder.recordSelect(this, currentValue);
    }

    @Override public String _getText() {
        return getProgressText((ProgressBar) node);
    }
}
