package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JProgressBar;

import net.sourceforge.marathon.javaagent.JProgressBarJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RProgressBar extends RComponent {

    public RProgressBar(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    public String getText() {
        return JProgressBarJavaElement.getText((JProgressBar) component);
    }

}
