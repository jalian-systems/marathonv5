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
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class FileSelectionDialog extends EscapeDialog implements IFileSelectedAction {
    private static final long serialVersionUID = 1L;
    private JTextField dirField = new JTextField(30);
    private String fileName = null;
    private JButton okButton;
    private JDialog parent;
    private JButton cancelButton;

    public FileSelectionDialog(String title, JDialog parent, String fileType, String[] extensions) {
        super(parent, title, true);
        this.parent = parent;
        FormLayout layout = new FormLayout("3dlu, left:pref:grow, 3dlu, pref:grow, 3dlu, fill:pref, 3dlu",
                "3dlu, pref, 3dlu, pref, 3dlu");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints constraints = new CellConstraints();
        JLabel label = new JLabel("Name: ");
        builder.add(label, constraints.xy(2, 2));
        builder.add(dirField, constraints.xy(4, 2));
        dirField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                updateOKState();
            }

            public void insertUpdate(DocumentEvent e) {
                updateOKState();
            }

            public void removeUpdate(DocumentEvent e) {
                updateOKState();
            }

            private void updateOKState() {
                if (dirField.getText().equals(""))
                    okButton.setEnabled(false);
                else
                    okButton.setEnabled(true);
            }
        });
        JButton browse = UIUtils.createBrowseButton();
        browse.setMnemonic(KeyEvent.VK_R);
        FileSelectionListener browseListener;
        if (fileType != null) {
            browseListener = new FileSelectionListener(this, new FileExtensionFilter(fileType, extensions), this, null, title);
            browseListener.setMultipleSelection(true);
        } else {
            browseListener = new FileSelectionListener(this, null, this, null, title);
            browseListener.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }
        browse.addActionListener(browseListener);
        okButton = UIUtils.createOKButton();
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileName = dirField.getText();
                dispose();
            }
        });
        cancelButton = UIUtils.createCancelButton();
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel buttonPanel = ButtonBarFactory.buildOKCancelBar(okButton, cancelButton);
        builder.add(browse, constraints.xy(6, 2));
        builder.add(buttonPanel, constraints.xyw(2, 4, 5));
        getContentPane().add(builder.getPanel());
        pack();
    }

    public String getSelectedFiles() {
        dirField.setText("");
        fileName = null;
        setLocationRelativeTo(parent);
        setVisible(true);
        return fileName;
    }

    public void filesSelected(File[] files, Object cookie) {
        StringBuffer fileList = new StringBuffer();
        for (int i = 0; i < files.length - 1; i++) {
            fileList.append(files[i]).append(File.pathSeparator);
        }
        fileList.append(files[files.length - 1]);
        dirField.setText(fileList.toString());
    }

    @Override public JButton getOKButton() {
        return okButton;
    }

    @Override public JButton getCloseButton() {
        return cancelButton;
    }
}
