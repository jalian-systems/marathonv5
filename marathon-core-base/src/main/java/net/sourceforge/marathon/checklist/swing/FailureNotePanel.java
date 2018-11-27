package net.sourceforge.marathon.checklist.swing;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.marathon.checklist.CheckList.CheckListItem;
import net.sourceforge.marathon.checklist.CheckList.FailureNote;

public class FailureNotePanel extends CheckListItemPanel {
    private FailureNote item;
    private JTextArea textArea;
    private JRadioButton success;
    private JRadioButton fail;
    private JRadioButton notes;
    private String text = "";

    public FailureNotePanel(FailureNote item) {
        this.item = item;
    }

    @Override
    protected JPanel createPanel(boolean selectable, boolean editable) {
        FormLayout layout = new FormLayout("pref,3dlu,pref:grow,pref,pref,pref,pref,pref,pref,pref", "pref,pref");
        final JPanel panel = new JPanel();
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, panel);

        JLabel jlabel = new JLabel(item.getLabel());
        builder.append(jlabel);
        builder.nextColumn(2);
        ButtonGroup group = new ButtonGroup();
        success = new JRadioButton("Success");
        fail = new JRadioButton("Fail");
        notes = new JRadioButton("Notes");
        group.add(success);
        group.add(fail);
        group.add(notes);

        builder.append(success);
        builder.append(fail);
        builder.append(notes);

        textArea = new JTextArea();
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                try {
                    text = e.getDocument().getText(0, e.getDocument().getLength());
                    item.setText(text);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            public void insertUpdate(DocumentEvent e) {
                try {
                    text = e.getDocument().getText(0, e.getDocument().getLength());
                    item.setText(text);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

            public void removeUpdate(DocumentEvent e) {
                try {
                    text = e.getDocument().getText(0, e.getDocument().getLength());
                    item.setText(text);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

        });
        textArea.setRows(4);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        builder.append(scroll, 10);

        success.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (success.isSelected())
                    item.setSelected(1);
                textArea.setEnabled(!success.isSelected());
                if (panel != null)
                    panel.repaint();
            }
        });
        fail.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (fail.isSelected())
                    item.setSelected(3);
                textArea.setEnabled(!success.isSelected());
                if (panel != null)
                    panel.repaint();
            }
        });
        notes.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (notes.isSelected())
                    item.setSelected(2);
                textArea.setEnabled(!success.isSelected());
                if (panel != null)
                    panel.repaint();
            }
        });
        success.setEnabled(editable);
        fail.setEnabled(editable);
        notes.setEnabled(editable);
        textArea.setEditable(editable);
        textArea.setText(text);
        if (item.getSelected() == 1)
            success.setSelected(true);
        else if (item.getSelected() == 3)
            fail.setSelected(true);
        else if (item.getSelected() == 2)
            notes.setSelected(true);
        else
            success.setSelected(editable);

        if (selectable) {
            setMouseListener(success);
            setMouseListener(fail);
            setMouseListener(notes);
            setMouseListener(textArea);
            setMouseListener(jlabel);
        }
        return builder.getPanel();
    }

    @Override
    public CheckListItem getItem() {
        return item;
    }

}
