/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
