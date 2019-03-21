package net.sourceforge.marathon.javaagent.components.jide;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import com.jidesoft.swing.CheckBoxListCellRenderer;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public class JideCheckBoxListItemElement extends AbstractJavaElement {

    public JideCheckBoxListItemElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public boolean marathon_select(String value) {
        boolean selected = Boolean.parseBoolean(value);
        Component[] childern = ((CheckBoxListCellRenderer) component).getComponents();
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
                JList jlist = (JList) ((JComponent) component).getClientProperty("jlist");
                int item = (Integer) ((JComponent) component).getClientProperty("item");
                Point cellBound = jlist.indexToLocation(item);
                Point cBoxBound = checkBox.getLocation();
                if (jlist != null) {
                    IJavaElement jlistElement = JavaElementFactory.createElement((Component) jlist, driver, window);
                    jlistElement.click(0, 1, cellBound.x + cBoxBound.x, cellBound.y + cBoxBound.y);
                }
            }
        }
        return true;
    }

    @Override
    public String _getText() {
        CheckBoxListCellRenderer cboxListRenderer = (CheckBoxListCellRenderer) getComponent();
        Component[] childern = cboxListRenderer.getComponents();
        for (Component c : childern) {
            if (c instanceof JLabel) {
                return ((JLabel) c).getText();
            }
        }
        return super._getText();
    }

    public static boolean clicksInCheckBox(Point e, JList list) {
        int index = list.locationToIndex(e);
        int hotspot = new JCheckBox().getPreferredSize().width;
        Rectangle bounds = list.getCellBounds(index, index);
        if (bounds != null) {
            if (list.getComponentOrientation().isLeftToRight()) {
                return e.getX() < bounds.x + hotspot;
            } else {
                return e.getX() > bounds.x + bounds.width - hotspot;
            }
        } else {
            return false;
        }
    }
}
