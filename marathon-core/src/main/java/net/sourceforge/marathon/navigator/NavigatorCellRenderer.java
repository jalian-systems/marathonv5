package net.sourceforge.marathon.navigator;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class NavigatorCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        File file = ((NavigatorTreeNode) value).getFile();
        if (file == null)
            return c;
        if (file instanceof Navigator.RootFile)
            c.setText(file.toString());
        else
            c.setText(file.getName());
        if (file instanceof Navigator.RootFile)
            c.setIcon(Icons.PROJECT);
        else if (file.isDirectory()) {
            if (expanded)
                c.setIcon(Icons.FOLDER);
            else
                c.setIcon(Icons.FOLDER_CLOSED);
        } else
            c.setIcon(Icons.FILE);
        return c;
    }
}
