package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JSpinner;

import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RDefaultEditor extends RComponent {

    public RDefaultEditor(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public String getText() {
        if (component instanceof JSpinner.DefaultEditor) {
            String text = ((JSpinner.DefaultEditor) component).getTextField().getText();
            return text;
        }
        return null;
    }

}
