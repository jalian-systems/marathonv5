package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RUnknownComponent extends RComponent {

    public RUnknownComponent(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mousePressed(MouseEvent me) {
        handleRawRecording(recorder, me);
    }

    @Override protected void keyPressed(KeyEvent ke) {
        handleRawRecording(recorder, ke);
    }
}
