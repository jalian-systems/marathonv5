package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RLabel extends RComponent {

    public RLabel(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseButton1Pressed(MouseEvent me) {
        recorder.recordClick(this, me);
    }
}
