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
package net.sourceforge.marathon.navigator;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.AbstractCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;

public class NavigatorCellEditor extends AbstractCellEditor implements TreeCellEditor {
    private static final long serialVersionUID = 1L;
    private JTextField textField = new JTextField(12);
    private File file;

    NavigatorCellEditor() {
        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    fireEditingStopped();
                }
            }
        });
    }

    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf,
            int row) {
        file = ((NavigatorTreeNode) value).getFile();
        textField.setText(file.getName());
        textField.setColumns(file.getName().length() < 12 ? 12 : file.getName().length() + 1);
        textField.selectAll();
        return textField;
    }

    public Object getCellEditorValue() {
        String name = textField.getText();
        return new File(file.getParentFile(), name);
    }
}
