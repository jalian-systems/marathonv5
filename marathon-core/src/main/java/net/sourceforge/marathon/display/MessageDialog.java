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
package net.sourceforge.marathon.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import net.sourceforge.marathon.editor.IEditor;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.editor.IEditorProvider.EditorType;
import net.sourceforge.marathon.runtime.api.EscapeDialog;
import net.sourceforge.marathon.runtime.api.UIUtils;

import com.jgoodies.forms.builder.ButtonBarBuilder;

public class MessageDialog extends EscapeDialog {
    private static final long serialVersionUID = 1L;
    private final String message;
    private JButton closeButton;
    private IEditorProvider editorProvider;

    public MessageDialog(String message, String title, IEditorProvider editorProvider) {
        super((JFrame) null, title, true);
        this.message = message;
        this.editorProvider = editorProvider;
        initialize();
    }

    private void initialize() {
        getContentPane().setLayout(new BorderLayout());
        closeButton = UIUtils.createCloseButton();
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        ButtonBarBuilder builder = new ButtonBarBuilder();
        builder.addButton(closeButton);
        getContentPane().add(builder.getPanel(), BorderLayout.SOUTH);
        IEditor editor = editorProvider.get(true, 1, EditorType.OTHER);
        editor.setText(message);
        editor.setEditable(false);
        getContentPane().add(new JScrollPane(editor.getComponent()));
        getContentPane().setPreferredSize(new Dimension(640, 480));
        pack();
        setLocationRelativeTo(null);
    }

    @Override public JButton getOKButton() {
        return closeButton;
    }

    @Override public JButton getCloseButton() {
        return closeButton;
    }
}
