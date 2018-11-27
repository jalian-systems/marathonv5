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

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.marathon.checklist.CheckList;
import net.sourceforge.marathon.checklist.CheckList.CheckListItem;
import net.sourceforge.marathon.checklist.CheckList.CommentBox;
import net.sourceforge.marathon.checklist.CheckList.FailureNote;
import net.sourceforge.marathon.checklist.CheckList.Header;

public class CheckListForm extends JPanel {
    private static final long serialVersionUID = 1L;

    private transient DefaultFormBuilder builder;
    private CheckList checkList;

    private final Mode mode;

    private boolean dirty = false;

    public static enum Mode {
        DISPLAY(false, false), EDIT(true, false), ENTER(false, true);
        private boolean selectable;
        private boolean editable;

        Mode(boolean selectable, boolean editable) {
            this.setSelectable(selectable);
            this.setEditable(editable);
        }

        public void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }

        public boolean isSelectable() {
            return selectable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public boolean isEditable() {
            return editable;
        }

    }

    public CheckListForm(CheckList checklist, Mode mode) {
        this.mode = mode;
        this.checkList = checklist;
        buildPanel();
    }

    private void buildPanel() {
        FormLayout layout = new FormLayout("pref:grow");
        builder = new DefaultFormBuilder(layout, this);
        builder.border(Borders.DIALOG);
        if (mode.isSelectable()) {
            builder.append("Name");
            builder.append(getNameField());
            builder.append("Description");
            JScrollPane pane = new JScrollPane(getDescriptionField());
            pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            builder.append(pane);
        } else {
            if (checkList.getName().equals(""))
                builder.appendSeparator("<No Name>");
            else
                builder.appendSeparator(checkList.getName());
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(0, 1));
            panel.setBackground(Color.LIGHT_GRAY);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            String text = checkList.getDescription();
            if (text.equals(""))
                text = "<No Description>";
            StringTokenizer tok = new StringTokenizer(text, "\n");
            while (tok.hasMoreTokens()) {
                JLabel label = new JLabel(tok.nextToken());
                panel.add(label);
            }
            builder.append(panel);
            builder.appendRow("3dlu");
            builder.nextRow();
        }
        Iterator<CheckList.CheckListItem> items = checkList.getItems();
        while (items.hasNext())
            builder.append(getPanel(items.next()).getPanel(mode.isSelectable(), mode.isEditable()));
    }

    private CheckListItemPanel getPanel(CheckListItem item) {
        if (item instanceof CommentBox)
            return new CommentBoxPanel((CommentBox) item);
        else if (item instanceof FailureNote)
            return new FailureNotePanel((FailureNote) item);
        else if (item instanceof Header)
            return new HeaderPanel((Header) item);
        throw new RuntimeException("Unknown CheckListItem type: " + item.getClass().getName());
    }

    private JTextArea getDescriptionField() {
        JTextArea textArea = new JTextArea(checkList.getDescription(), 5, 0);
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateDescription(e);
            }

            public void insertUpdate(DocumentEvent e) {
                updateDescription(e);
            }

            public void removeUpdate(DocumentEvent e) {
                updateDescription(e);
            }

            private void updateDescription(DocumentEvent e) {
                dirty = true;
                Document document = e.getDocument();
                try {
                    checkList.setDescription(document.getText(0, document.getLength()));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

        });
        textArea.setEditable(mode.isSelectable());
        return textArea;
    }

    private JTextField getNameField() {
        JTextField textField = new JTextField(checkList.getName(), 30);
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateName(e);
            }

            public void insertUpdate(DocumentEvent e) {
                updateName(e);
            }

            public void removeUpdate(DocumentEvent e) {
                updateName(e);
            }

            private void updateName(DocumentEvent e) {
                dirty = true;
                Document document = e.getDocument();
                try {
                    checkList.setName(document.getText(0, document.getLength()));
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }

        });
        textField.setEditable(mode.isSelectable());
        return textField;
    }

    public void addTextArea(String label) {
        CommentBox commentBox = checkList.createCommentBox(label);
        builder.append((new CommentBoxPanel(commentBox)).getPanel(mode.isSelectable(), mode.isEditable()));
    }

    public void addChecklistItem(String label) {
        FailureNote failureNote = checkList.createFailureNote(label);
        builder.append((new FailureNotePanel(failureNote)).getPanel(mode.isSelectable(), mode.isEditable()));
    }

    public void addHeader(String label) {
        Header header = checkList.createHeader(label);
        builder.append((new HeaderPanel(header)).getPanel(mode.isSelectable(), mode.isEditable()));
    }

    public CheckList getCheckList() {
        return checkList;
    }

    public void deleteSelected() {
        if (CheckListItemPanel.selectedItem != null) {
            checkList.delete(CheckListItemPanel.selectedItem.getItem());
            CheckListItemPanel.selectedItem.deselect();
            CheckListItemPanel.selectedItem = null;
        }
        rebuildPanel();
    }

    private void rebuildPanel() {
        removeAll();
        buildPanel();
        repaint();
    }

    public void moveUpSelected() {
        if (CheckListItemPanel.selectedItem != null) {
            checkList.moveUp(CheckListItemPanel.selectedItem.getItem());
        }
        rebuildPanel();
    }

    public void moveDownSelected() {
        if (CheckListItemPanel.selectedItem != null) {
            checkList.moveDown(CheckListItemPanel.selectedItem.getItem());
        }
        rebuildPanel();
    }

    public boolean isSelectable() {
        return mode.isSelectable();
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isDirty() {
        return dirty;

    }
}