package net.sourceforge.marathon.checklist;

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
import javax.swing.JToolBar;

import net.sourceforge.marathon.runtime.api.ButtonBarFactory;
import net.sourceforge.marathon.runtime.api.EscapeDialog;
import net.sourceforge.marathon.runtime.api.UIUtils;

import com.jgoodies.forms.builder.ButtonStackBuilder;

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

    @Override public void setVisible(boolean b) {
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
        if (checkListForm.isSelectable()) {
            contentPane.add(createToolBar(), BorderLayout.NORTH);
            contentPane.add(createVerticalButtonPanel(), BorderLayout.EAST);
        }
        scrollPane = new JScrollPane(this.checkListForm);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        return ButtonBarFactory.buildRightAlignedBar(getActionButtons());
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton headerButton = UIUtils.createHeaderButton();
        headerButton.addActionListener(this);
        JButton radioButton = UIUtils.createChecklistButton();
        radioButton.addActionListener(this);
        JButton textAreaButton = UIUtils.createTextboxButton();
        textAreaButton.addActionListener(this);
        toolBar.add(headerButton);
        toolBar.add(radioButton);
        toolBar.add(textAreaButton);
        toolBar.setFloatable(false);
        return toolBar;
    }

    private JPanel createVerticalButtonPanel() {
        ButtonStackBuilder builder = new ButtonStackBuilder();
        builder.addGlue();
        JButton upButton = UIUtils.createUpButton();
        upButton.addActionListener(this);
        builder.addButton(upButton);
        JButton removeButton = UIUtils.createRemoveButton();
        removeButton.addActionListener(this);
        builder.addButton(removeButton);
        JButton downButton = UIUtils.createDownButton();
        downButton.addActionListener(this);
        builder.addButton(downButton);
        builder.addGlue();
        return builder.getPanel();
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

    @Override public JButton getOKButton() {
        return actionButtons[0];
    }

    @Override public JButton getCloseButton() {
        return actionButtons[actionButtons.length - 1];
    }
}
