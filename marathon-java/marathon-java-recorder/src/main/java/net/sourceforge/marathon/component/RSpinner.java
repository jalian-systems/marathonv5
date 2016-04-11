package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JSpinner;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RSpinner extends RComponent {

    private String oldValue;

    public RSpinner(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusGained(RComponent prev) {
        oldValue = getSpinnerText();
    }

    @Override public void focusLost(RComponent next) {
        String newValue = getSpinnerText();
        if (oldValue == null || !oldValue.equals(newValue)) {
            recorder.recordSelect(this, newValue);
        }
    }

    private String getSpinnerText() {
        JComponent editor = ((JSpinner) component).getEditor();

        if (editor == null) {
        } else {
            RComponentFactory finder = new RComponentFactory(omapConfig);
            if (editor instanceof JSpinner.DefaultEditor) {
                RComponent rComponent = finder.findRawRComponent(editor, null, recorder);
                return rComponent.getText();
            }
        }
        return null;
    }

    @Override protected void mousePressed(MouseEvent me) {
    }

    @Override protected void keyPressed(KeyEvent ke) {
    }
}
