package net.sourceforge.marathon.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import javax.swing.JColorChooser;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RColorChooser extends RComponent {
    private String color;

    public RColorChooser(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusLost(RComponent next) {
        String currentColor = getColorCode(((JColorChooser) component).getColor());
        if (!currentColor.equals(color)) {
            recorder.recordSelect(this, currentColor);
        }
    }

    @Override public void focusGained(RComponent prev) {
        JColorChooser colorChooser = (JColorChooser) component;
        color = getColorCode(colorChooser.getColor());
    }

    private String getColorCode(Color color) {
        return "#" + Integer.toHexString((color.getRGB() & 0x00FFFFFF) | 0x1000000).substring(1);
    }
}
