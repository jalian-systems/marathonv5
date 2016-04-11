package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

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

    @Override protected void keyTyped(KeyEvent ke) {
        handleRawRecording(recorder, ke);
    }
}
