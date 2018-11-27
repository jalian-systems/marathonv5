package net.sourceforge.marathon.checklist.swing;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.marathon.checklist.CheckList.CheckListItem;

public abstract class CheckListItemPanel {
    private transient JPanel panel;
    public static CheckListItemPanel selectedItem;

    protected abstract JPanel createPanel(boolean selectable, boolean editable);

    public JPanel getPanel(boolean selectable, boolean editable) {
        if (panel == null) {
            panel = createPanel(selectable, editable);
            if (selectable)
                setMouseListener(panel);
        }
        return panel;
    }

    public void deselect() {
        panel.setBorder(BorderFactory.createEmptyBorder());
    }

    public void select() {
        panel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
    }

    protected void setMouseListener(JComponent c) {
        c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedItem != null)
                    selectedItem.deselect();
                selectedItem = CheckListItemPanel.this;
                CheckListItemPanel.this.select();
            }
        });
    }

    abstract public CheckListItem getItem();
}
