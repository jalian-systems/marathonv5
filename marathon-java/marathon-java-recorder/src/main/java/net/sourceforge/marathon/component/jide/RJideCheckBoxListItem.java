package net.sourceforge.marathon.component.jide;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import com.jidesoft.swing.CheckBoxListCellRenderer;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javaagent.components.jide.JideCheckBoxListItemElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RJideCheckBoxListItem extends RComponent {

    private Point point;

    public RJideCheckBoxListItem(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        this.point = point;
    }

    @Override
    public String getText() {
        CheckBoxListCellRenderer cbListRenderer = (CheckBoxListCellRenderer) getComponent();
        Component[] childern = cbListRenderer.getComponents();
        for (Component c : childern) {
            if (c instanceof JLabel) {
                return ((JLabel) c).getText();
            }
        }
        return null;
    }

    public String getValue() {
        CheckBoxListCellRenderer cbListRenderer = (CheckBoxListCellRenderer) getComponent();
        if (cbListRenderer instanceof JComponent) {
            JList list = (JList) cbListRenderer.getClientProperty("jlist");
            if (list.getSelectedIndices().length > 1) {
                return null;
            }
        }
        Component[] childern = cbListRenderer.getComponents();
        for (Component c : childern) {
            if (c instanceof JCheckBox) {
                return String.valueOf(((JCheckBox) c).isSelected());
            }
        }
        return null;
    }

    public boolean isSelectCall() {
        CheckBoxListCellRenderer cbListRenderer = (CheckBoxListCellRenderer) getComponent();
        if (cbListRenderer instanceof JComponent) {
            JList list = (JList) cbListRenderer.getClientProperty("jlist");
            return JideCheckBoxListItemElement.clicksInCheckBox(point, list);
        }
        return false;
    }

}
