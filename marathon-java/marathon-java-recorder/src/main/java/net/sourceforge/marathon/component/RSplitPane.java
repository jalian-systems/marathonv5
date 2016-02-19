package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RSplitPane extends RComponent {

    private int dividerLocation;

    public RSplitPane(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseButton1Pressed(MouseEvent me) {
        JSplitPane c = (JSplitPane) (component instanceof JSplitPane ? component : SwingUtilities.getAncestorOfClass(
                JSplitPane.class, component));
        if (c == null)
            return;
        dividerLocation = c.getDividerLocation();
    }

    @Override protected void mouseReleased(MouseEvent me) {
        JSplitPane c = (JSplitPane) (component instanceof JSplitPane ? component : SwingUtilities.getAncestorOfClass(
                JSplitPane.class, component));
        if (c == null || dividerLocation == c.getDividerLocation())
            return;
        RComponent rComponent = new RComponentFactory(omapConfig).findRComponent(c, null, recorder);
        recorder.recordSelect(rComponent, "" + c.getDividerLocation());
    }

}
