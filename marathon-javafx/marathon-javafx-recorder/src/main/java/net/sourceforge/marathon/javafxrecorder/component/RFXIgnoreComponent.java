package net.sourceforge.marathon.javafxrecorder.component;

import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXIgnoreComponent extends RFXComponent {

    public RFXIgnoreComponent(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void processEvent(Event event) {
    }
}
