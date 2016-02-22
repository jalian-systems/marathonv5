package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RUnknownComponent extends RComponent {

    public RUnknownComponent(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mousePressed(MouseEvent me) {
        handleRawRecording(recorder, me);
    }

    @Override protected void keyPressed(KeyEvent ke) {
        handleRawRecording(recorder, ke);
    }
}
