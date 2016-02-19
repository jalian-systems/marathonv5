package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;

import net.sourceforge.marathon.javaagent.components.JTableHeaderItemJavaElement;
import net.sourceforge.marathon.javaagent.components.JTableHeaderJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RTableHeader extends RComponent {

    private int index;

    public RTableHeader(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mouseButton1Pressed(MouseEvent me) {
        index = ((JTableHeader) component).columnAtPoint(me.getPoint());
        recorder.recordClick2(this, me, true);
    }

    @Override public String getCellInfo() {
        return JTableHeaderItemJavaElement.getText((JTableHeader) component, index);
    }

    @Override public String[][] getContent() {
        return JTableHeaderJavaElement.getContent((JTableHeader) component);
    }
}
