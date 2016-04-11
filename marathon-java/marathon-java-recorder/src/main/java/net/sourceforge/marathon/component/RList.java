package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JList;

import net.sourceforge.marathon.javaagent.components.JListJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RList extends RComponent {

    public RList(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void focusLost(RComponent next) {
        JList list = (JList) component;
        Object[] selectedValues = list.getSelectedValues();
        if (selectedValues == null || selectedValues.length == 0) {
            recorder.recordSelect(this, "[]");
            return;
        }
        String text = getText();
        recorder.recordSelect(this, text);
    }

    @Override public String getText() {
        return JListJavaElement.getSelectionText((JList) component);
    }

    @Override public String[][] getContent() {
        return JListJavaElement.getContent((JList) component);
    }

    @Override protected void mousePressed(MouseEvent me) {
        // Ignore Ctrl+Clicks used to select the nodes
        if (me.getButton() == MouseEvent.BUTTON1 && isMenuShortcutKeyDown(me))
            return;
        if (me.getButton() != MouseEvent.BUTTON1)
            focusLost(null);
        super.mousePressed(me);
    }
}
