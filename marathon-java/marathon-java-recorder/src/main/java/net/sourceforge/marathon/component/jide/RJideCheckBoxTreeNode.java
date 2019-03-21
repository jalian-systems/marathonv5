package net.sourceforge.marathon.component.jide;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;

import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.swing.CheckBoxTreeCellRenderer;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javaagent.components.jide.JideCheckBoxTreeNodeElement;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class RJideCheckBoxTreeNode extends RComponent {

    private Point point;

    public RJideCheckBoxTreeNode(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        this.point = point;
    }

    @Override
    public String getText() {
        CheckBoxTreeCellRenderer cbListRenderer = (CheckBoxTreeCellRenderer) getComponent();
        Component[] childern = cbListRenderer.getComponents();
        for (Component c : childern) {
            if (c instanceof JLabel) {
                return ((JLabel) c).getText();
            }
        }
        return null;
    }

    public String getValue() {
        CheckBoxTreeCellRenderer cbTreeRenderer = (CheckBoxTreeCellRenderer) getComponent();
        if (cbTreeRenderer instanceof JComponent) {
            JTree tree = (JTree) cbTreeRenderer.getClientProperty("jtree");
            if (tree.getSelectionCount() > 1) {
                return null;
            }
        }
        Component[] childern = cbTreeRenderer.getComponents();
        for (Component c : childern) {
            if (c instanceof JCheckBox) {
                return String.valueOf(((JCheckBox) c).isSelected());
            }
        }
        return null;
    }

    public boolean isSelectCall() {
        CheckBoxTreeCellRenderer cbListRenderer = (CheckBoxTreeCellRenderer) getComponent();
        if (cbListRenderer instanceof JComponent) {
            CheckBoxTree tree = (CheckBoxTree) cbListRenderer.getClientProperty("jtree");
            int row = (Integer) cbListRenderer.getClientProperty("row");
            return JideCheckBoxTreeNodeElement.clicksInCheckBox(tree, point, tree.getPathForRow(row));
        }
        return false;
    }
}
