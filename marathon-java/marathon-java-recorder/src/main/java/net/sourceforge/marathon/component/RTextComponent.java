package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;

import javax.swing.text.JTextComponent;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RTextComponent extends RComponent {

    private String text = null;

    public RTextComponent(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusLost(RComponent next) {
        String t = ((JTextComponent) component).getText();
        if (!t.equals(text)) {
            recorder.recordSelect(this, t);
        }
    }

    @Override public void focusGained(RComponent prev) {
        text = ((JTextComponent) component).getText();
    }
}
