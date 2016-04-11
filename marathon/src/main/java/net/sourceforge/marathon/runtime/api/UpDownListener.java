package net.sourceforge.marathon.runtime.api;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;

class UpDownListener implements ActionListener {
    private JList<Object> list;
    private boolean shouldMoveUp;

    public UpDownListener(JList<Object> list, boolean shouldMoveUp) {
        this.list = list;
        this.shouldMoveUp = shouldMoveUp;
    }

    public void actionPerformed(ActionEvent e) {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex == -1)
            return;
        MovableItemListModel model = (MovableItemListModel) list.getModel();
        if (shouldMoveUp) {
            model.moveUp(selectedIndex);
            list.setSelectedIndex(selectedIndex - 1);
        } else {
            model.moveDown(selectedIndex);
            if (selectedIndex == model.getSize() - 1)
                list.setSelectedIndex(selectedIndex);
            else
                list.setSelectedIndex(selectedIndex + 1);
        }
    }
}
