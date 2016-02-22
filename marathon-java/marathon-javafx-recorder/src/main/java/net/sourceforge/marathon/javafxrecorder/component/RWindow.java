package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Window;

import net.sourceforge.marathon.javafxagent.WindowTitle;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

import org.json.JSONObject;

public class RWindow extends RComponent {

    public RWindow(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public JSONObject findContextHeirarchy() {
        return findContextHeirarchy((Container) component);
    }

    public String getTitle() {
        return new WindowTitle((Window) component).getTitle();
    }
}
