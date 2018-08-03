package net.sourceforge.marathon.checklist.swing;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.marathon.checklist.CheckList.CheckListItem;
import net.sourceforge.marathon.checklist.CheckList.CommentBox;

public class CommentBoxPanel extends CheckListItemPanel {
    private CommentBox item;
    private JTextArea textArea;
    protected String text;

    public CommentBoxPanel(CommentBox item) {
        this.item = item;
    }

    @Override
    protected JPanel createPanel(boolean selectable, boolean editable) {
        FormLayout layout = new FormLayout("pref:grow", "pref, pref");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        JLabel jlabel = new JLabel(item.getLabel());
        builder.append(jlabel);
        textArea = new JTextArea();
        textArea.setRows(4);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        builder.append(scroll);
        JPanel panel = builder.getPanel();
        textArea.setEditable(editable);
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
        textArea.setText(text);
        if (selectable) {
            setMouseListener(jlabel);
            setMouseListener(textArea);
        }
        return panel;
    }

    @Override
    public CheckListItem getItem() {
        return item;
    }

}
