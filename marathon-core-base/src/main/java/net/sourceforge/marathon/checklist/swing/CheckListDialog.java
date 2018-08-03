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
package net.sourceforge.marathon.checklist.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jgoodies.forms.builder.ButtonBarBuilder;

import net.sourceforge.marathon.runtime.api.swing.EscapeDialog;

public class CheckListDialog extends EscapeDialog implements ActionListener {
    private static final long serialVersionUID = 1L;

    private CheckListForm checkListForm;
    private JScrollPane scrollPane;
    private JButton[] actionButtons = new JButton[0];

    private boolean initialized = false;

    private boolean dirty = false;

    public CheckListDialog(JFrame parent, CheckListForm form) {
        super(parent, "", true);
        this.checkListForm = form;
        setLocationRelativeTo(parent);
    }

    public CheckListDialog(JDialog parent, CheckListForm form) {
        super(parent, "", true);
        setLocationRelativeTo(parent);
        this.checkListForm = form;
    }

    @Override
    public void setVisible(boolean b) {
        if (!initialized) {
            initializeDialog();
            initialized = true;
        }
        super.setVisible(b);
    }

    private void initializeDialog() {
        initialized = true;
        init();
        pack();
    }

    private void init() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        Container contentPane = getContentPane();
        scrollPane = new JScrollPane(this.checkListForm);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        return ButtonBarBuilder.create().addGlue().addButton(getActionButtons()).getPanel();
    }

    public void actionPerformed(ActionEvent e) {
        dirty = true;
        String source = e.getActionCommand();
        boolean added = false;
        if (source.equals("Header")) {
            String label = JOptionPane.showInputDialog(this, "Label", "New Header", JOptionPane.INFORMATION_MESSAGE);
            if (label == null)
                return;
            checkListForm.addHeader(label);
            added = true;
        } else if (source.equals("Checklist")) {
            String label = JOptionPane.showInputDialog(this, "Label", "New Checklist Item", JOptionPane.INFORMATION_MESSAGE);
            if (label == null)
                return;
            checkListForm.addChecklistItem(label);
            added = true;
        } else if (source.equals("Textbox")) {
            String label = JOptionPane.showInputDialog(this, "Label", "New Textbox", JOptionPane.INFORMATION_MESSAGE);
            if (label == null)
                return;
            checkListForm.addTextArea(label);
            added = true;
        } else if (source.equals("Remove")) {
            checkListForm.deleteSelected();
        } else if (source.equals("Up")) {
            checkListForm.moveUpSelected();
        } else if (source.equals("Down")) {
            checkListForm.moveDownSelected();
        }
        scrollPane.validate();
        if (added)
            checkListForm.scrollRectToVisible(checkListForm.getComponent(checkListForm.getComponentCount() - 1).getBounds());
    }

    public void setActionButtons(JButton[] actionButtons) {
        this.actionButtons = actionButtons;
    }

    public JButton[] getActionButtons() {
        return actionButtons;
    }

    public boolean isDirty() {
        return dirty || checkListForm.isDirty();
    }

    @Override
    public JButton getOKButton() {
        return actionButtons[0];
    }

    @Override
    public JButton getCloseButton() {
        return actionButtons[actionButtons.length - 1];
    }
    
}