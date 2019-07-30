package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.swing.CheckBoxTreeCellRenderer;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JideCheckBoxTreeNodeElement extends AbstractJavaElement {

    public JideCheckBoxTreeNodeElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public boolean marathon_select(String value) {
        boolean selected = Boolean.parseBoolean(value);
        Component[] childern = ((CheckBoxTreeCellRenderer) component).getComponents();
        JCheckBox checkBox = null;
        for (Component c : childern) {
            if (c instanceof JCheckBox) {
                checkBox = ((JCheckBox) c);
                break;
            }
        }
        if (checkBox != null) {
            boolean current = checkBox.isSelected();
            if (selected != current) {
                JTree jtree = (JTree) ((JComponent) component).getClientProperty("jtree");
                int item = (Integer) ((JComponent) component).getClientProperty("row");
                Point cellBound = jtree.getPathBounds(jtree.getPathForRow(item)).getLocation();
                Point cBoxBound = checkBox.getLocation();
                if (jtree != null) {
                    IJavaElement jtreeNode = JavaElementFactory.createElement((Component) jtree, driver, window);
                    jtreeNode.click(0, 1, cellBound.x + cBoxBound.x, cellBound.y + cBoxBound.y);
                }
            }
        }
        return true;
    }

    @Override
    public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor")) {
            return Arrays.asList((IJavaElement) this);
        }
        return super.getByPseudoElement(selector, params);
    }

    @Override
    public String _getText() {
        CheckBoxTreeCellRenderer cboxListRenderer = (CheckBoxTreeCellRenderer) getComponent();
        Component[] childern = cboxListRenderer.getComponents();
        for (Component c : childern) {
            if (c instanceof JLabel) {
                return ((JLabel) c).getText();
            }
        }
        return super._getText();
    }

    public static boolean clicksInCheckBox(CheckBoxTree tree, Point e, TreePath path) {
        int hotspot = new JCheckBox().getPreferredSize().width;
        if (!tree.isCheckBoxVisible(path)) {
            return false;
        } else {
            Rectangle bounds = tree.getPathBounds(path);
            if (tree.getComponentOrientation().isLeftToRight()) {
                return e.getX() < bounds.x + hotspot;
            } else {
                return e.getX() > bounds.x + bounds.width - hotspot;
            }
        }
    }

}
