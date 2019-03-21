package net.sourceforge.marathon.component.jide;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.json.JSONArray;

import com.jidesoft.swing.JideSplitPane;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RJideSplitPaneElement extends RComponent {

    private String prevLocations;

    public RJideSplitPaneElement(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override
    protected void mouseButton1Pressed(MouseEvent me) {
        JideSplitPane sp = (JideSplitPane) getComponent();
        prevLocations = new JSONArray(sp.getDividerLocations()).toString();
    }

    @Override
    protected void mouseReleased(MouseEvent me) {
        JideSplitPane sp = (JideSplitPane) getComponent();
        String currentDividerLoctions = new JSONArray(sp.getDividerLocations()).toString();
        if (!currentDividerLoctions.equals(prevLocations)) {
            recorder.recordSelect(this, currentDividerLoctions);
        }
    }

    @Override
    public String _getText() {
        JideSplitPane sp = (JideSplitPane) getComponent();
        return new JSONArray(sp.getDividerLocations()).toString();
    }

    @Override
    public String getTagName() {
        return "jide-split-pane";
    }
}
