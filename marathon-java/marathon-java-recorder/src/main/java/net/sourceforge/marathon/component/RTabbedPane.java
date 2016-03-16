package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;

import net.sourceforge.marathon.javaagent.components.JTabbedPaneJavaElement;
import net.sourceforge.marathon.javaagent.components.JTabbedPaneTabJavaElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RTabbedPane extends RComponent {

    public RTabbedPane(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override public void stateChanged(ChangeEvent e) {
        JTabbedPane tp = (JTabbedPane) component;
        int selectedIndex = tp.getSelectedIndex();
        if (selectedIndex != -1) {
            recorder.recordSelect(this, JTabbedPaneTabJavaElement.getText(tp, selectedIndex));
        }
    }

    @Override public String[][] getContent() {
        return JTabbedPaneJavaElement.getContent((JTabbedPane) component);
    }

    @Override public String getText() {
        return JTabbedPaneJavaElement.getSelectedItemText((JTabbedPane) component);
    }

}
