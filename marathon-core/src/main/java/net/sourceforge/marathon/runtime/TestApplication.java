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
package net.sourceforge.marathon.runtime;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.io.output.WriterOutputStream;

import net.sourceforge.marathon.api.ITestApplication;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.ITestLauncher;
import net.sourceforge.marathon.runtime.api.MPFUtils;
import net.sourceforge.marathon.runtime.api.UIUtils;
import net.sourceforge.marathon.util.LauncherModelHelper;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class TestApplication extends JDialog implements ITestApplication {
    private static final long serialVersionUID = 1L;

    private final static class TextAreaWriter extends Writer {
        private JTextArea textArea;

        public TextAreaWriter(JTextArea area) {
            textArea = area;
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            final String newText = new String(cbuf, off, len);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    textArea.setText(textArea.getText() + newText);
                }
            });
        }

        public void close() throws IOException {
        }

        public void flush() throws IOException {
        }
    }

    private ITestLauncher launchCommand;
    private JTextArea commandField = new JTextArea(3, 30);
    private JTextArea outputArea = new JTextArea(4, 50);
    private JTextArea errorArea = new JTextArea(4, 50);
    private JButton closeButton = UIUtils.createCloseButton();

    public TestApplication(JDialog parent, Properties props) {
        super(parent);
        setLocationRelativeTo(parent);
        MPFUtils.replaceEnviron(props);
        String model = props.getProperty(Constants.PROP_PROJECT_LAUNCHER_MODEL);
        if (model == null || model.equals("")) {
            commandField.setText("Select a launcher and set the parameters required.");
            setVisible(true);
        } else {
            IRuntimeLauncherModel launcherModel = LauncherModelHelper.getLauncherModel(model);
            launchCommand = launcherModel.createLauncher(props);
        }
        setModal(true);
        PanelBuilder builder = new PanelBuilder(new FormLayout("pref:grow, 3dlu, pref",
                "pref, 3dlu, fill:p:grow, 3dlu, pref, 3dlu, fill:p:grow, 3dlu, pref, 3dlu, fill:p:grow, 3dlu, pref"));
        CellConstraints cellconstraints = new CellConstraints();
        builder.addSeparator("Command", cellconstraints.xyw(1, 1, 3));
        commandField.setEditable(false);
        commandField.setLineWrap(true);
        commandField.setWrapStyleWord(true);
        builder.add(new JScrollPane(commandField), cellconstraints.xyw(1, 3, 3));
        builder.addSeparator("Standard Output & Error", cellconstraints.xyw(1, 5, 3));
        outputArea.setEditable(false);
        builder.add(new JScrollPane(outputArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), cellconstraints.xyw(1, 7, 3));
        builder.addSeparator("Message", cellconstraints.xyw(1, 9, 3));
        errorArea.setEditable(false);
        builder.add(new JScrollPane(errorArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), cellconstraints.xyw(1, 11, 3));
        errorArea.setForeground(new Color(0xFF0000));
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (launchCommand != null)
                    launchCommand.destroy();
                dispose();
            }
        });
        builder.add(closeButton, cellconstraints.xy(3, 13));
        builder.border(Borders.DIALOG);
        getContentPane().add(builder.getPanel());
    }

    public void launch() throws IOException, InterruptedException {
        pack();
        if (launchCommand == null) {
            commandField.setText("This launcher does not support launch in test mode.");
            setVisible(true);
            return;
        }
        commandField.setCaretPosition(0);
        launchCommand.copyOutputTo(new WriterOutputStream(new TextAreaWriter(outputArea), Charset.defaultCharset()));
        launchCommand.setMessageArea(new WriterOutputStream(new TextAreaWriter(errorArea), Charset.defaultCharset()));
        if (launchCommand.start() == JOptionPane.OK_OPTION) {
            commandField.setText(launchCommand.toString());
            setVisible(true);
        }
    }

}
