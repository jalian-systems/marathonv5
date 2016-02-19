package net.sourceforge.marathon.runtime.api;

import java.awt.Component;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class DirectoryFileRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;
    private ImageIcon dirIcon = new ImageIcon(getClass().getClassLoader().getResource(
            "net/sourceforge/marathon/mpf/images/dir_obj.gif"));;
    private ImageIcon jarIcon = new ImageIcon(getClass().getClassLoader().getResource(
            "net/sourceforge/marathon/mpf/images/jar_obj.gif"));;

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (!(value instanceof File))
            return comp;
        File file = (File) value;
        if (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"))
            comp.setIcon(jarIcon);
        else
            comp.setIcon(dirIcon);
        String fileName = file.getName();
        if (file.getParent() != null)
            fileName = fileName + " - " + file.getParent();
        comp.setText(fileName);
        return comp;
    }
}
