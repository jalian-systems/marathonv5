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
package net.sourceforge.marathon.mpf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.FileSelectionListener;
import net.sourceforge.marathon.runtime.api.IFileSelectedAction;
import net.sourceforge.marathon.runtime.api.UIUtils;

/**
 * MPFSelection allows the user to select a MPF file if not given on the command
 * line.
 */
public class MPFSelection extends JFrame implements IFileSelectedAction {
    private static final int MAX_SAVED_FILES = 10;
    private static final long serialVersionUID = 1L;
    public static final ImageIcon BANNER = new ImageIcon(
            MPFConfigurationUI.class.getClassLoader().getResource("net/sourceforge/marathon/mpf/images/banner.png"));;
    private JComboBox<String> dirName = new JComboBox<String>();
    private JButton browseButton = UIUtils.createBrowseButton();
    protected boolean isOKSelected = false;
    private JButton modifyButton = UIUtils.createEditButton();
    private JButton okButton = UIUtils.createSelectButton();
    private JButton newButton = UIUtils.createNewButton();
    private JButton cancelButton = UIUtils.createCancelButton();

    /**
     * Get the selection panel populated with the controls.
     * 
     * @return panel, the selection panel.
     */
    private JPanel getSelectionPanel() {
        PanelBuilder builder = new PanelBuilder(new FormLayout("left:p:none, 3dlu, fill:p:grow, 3dlu, d", "pref"));
        builder.border(Borders.DIALOG);
        CellConstraints cc1 = new CellConstraints();
        CellConstraints cc2 = new CellConstraints();
        loadFileNames();
        if (dirName.getItemCount() == 0)
            dirName.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        builder.addLabel("&Project directory:", cc1.xy(1, 1), dirName, cc2.xy(3, 1));
        builder.add(browseButton, cc1.xy(5, 1));
        browseButton.setMnemonic(KeyEvent.VK_R);
        FileSelectionListener fsl = new FileSelectionListener(this, new ProjectDirectoryFilter("Marathon Project Directories"),
                this, null, "Select Marathon Project Directory");
        if (dirName.getSelectedItem() != null) {
            String selectedItem = (String) dirName.getSelectedItem();
            File dir = new File(selectedItem).getParentFile();
            fsl.setPreviousDir(dir);
        }
        fsl.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        browseButton.addActionListener(fsl);
        return builder.getPanel();
    }

    /**
     * Load the recently used Marathon project directories from the user
     * preferences.
     */
    private void loadFileNames() {
        Preferences p = Preferences.userNodeForPackage(this.getClass());
        try {
            String[] keys = p.keys();
            for (int i = 0; i < keys.length; i++) {
                String fName = p.get("dirName" + i, null);
                if (fName == null)
                    continue;
                File file = new File(fName);
                if (isValidProjectDirectory(file))
                    dirName.addItem(fName);
            }
            if (dirName.getItemCount() > 0)
                dirName.setSelectedIndex(0);
        } catch (BackingStoreException e) {
            return;
        }
    }

    /**
     * Check whether given directory is a valid Marathon project directory
     * 
     * @param file
     * @return
     */
    private boolean isValidProjectDirectory(File file) {
        return file.exists() && file.isDirectory() && (new File(file, Constants.PROJECT_FILE)).exists();
    }

    /**
     * Store the current set of Marathon project directories into the user
     * preferences.
     */
    private void storeFileNames() {
        Preferences p = Preferences.userNodeForPackage(this.getClass());
        try {
            p.clear();
            p.flush();
            p = Preferences.userNodeForPackage(this.getClass());
            int itemCount = dirName.getItemCount();
            int selected = dirName.getSelectedIndex();
            p.put("dirName0", (String) dirName.getItemAt(selected));
            for (int i = 0, j = 1; i < itemCount && i < MAX_SAVED_FILES; i++) {
                if (i != selected)
                    p.put("dirName" + j++, (String) dirName.getItemAt(i));
            }
        } catch (BackingStoreException e) {
            return;
        }
    }

    /**
     * Construct a MPFSelection frame.
     */
    public MPFSelection() {
        setTitle("Marathon - Select Directory");
        BannerPanel bannerPanel = new BannerPanel();
        String[] lines = { "Select a Marathon Project Directory" };
        BannerPanel.Sheet sheet = new BannerPanel.Sheet("Create and manage configuration", lines, BANNER);
        bannerPanel.addSheet(sheet, "main");
        getContentPane().add(bannerPanel, BorderLayout.NORTH);
        getContentPane().add(getSelectionPanel());
        dirName.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel comp = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (comp.getText().equals(""))
                    return comp;
                File file = new File(comp.getText());
                String fileName = file.getName() + " - " + (file.getParent() == null ? "." : file.getParent());
                comp.setText(fileName);
                comp.setToolTipText(file.toString());
                return comp;
            }
        });
        final JPopupMenu popup = new JPopupMenu();
        popup.add(getFrameworkMenuItem("Java/Swing Project", Constants.FRAMEWORK_SWING));
        popup.add(getFrameworkMenuItem("Java/FX Project", Constants.FRAMEWORK_FX));
        popup.add(getFrameworkMenuItem("Web Application Project", Constants.FRAMEWORK_WEB));
        newButton.setMnemonic(KeyEvent.VK_N);
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.show(newButton, 0, newButton.getHeight());
            }
        });
        modifyButton.setMnemonic(KeyEvent.VK_E);
        if (dirName.getSelectedIndex() == -1)
            modifyButton.setEnabled(false);
        modifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fname = (String) dirName.getSelectedItem();
                MPFConfigurationUI configurationUI = new MPFConfigurationUI(fname, MPFSelection.this);
                fname = configurationUI.getProjectDirectory();
                if (fname != null)
                    filesSelected(new File[] { new File(fname) }, null);
            }
        });
        if (dirName.getSelectedIndex() == -1)
            okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isOKSelected = true;
                synchronized(MPFSelection.this) {
                    MPFSelection.this.notifyAll();
                }
                dispose();
            }
        });
        dirName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyButton.setEnabled(true);
                okButton.setEnabled(true);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized(MPFSelection.this) {
                    MPFSelection.this.notifyAll();
                }
                dispose();
            }
        });
        ButtonBarBuilder bbb = new ButtonBarBuilder();
        bbb.addGlue();
        bbb.addButton(newButton);
        bbb.addButton(modifyButton);
        bbb.addUnrelatedGap();
        bbb.addButton(cancelButton);
        bbb.addButton(okButton);
        JPanel buttonPanel = bbb.getPanel();
        buttonPanel.setBorder(Borders.createEmptyBorder("0dlu, 0dlu, 3dlu, 7dlu"));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override public void actionPerformed(ActionEvent e) {
                cancelButton.doClick();
            }
        });
        getRootPane().setDefaultButton(okButton);
    }

    public JMenuItem getFrameworkMenuItem(String name, final String framework) {
        JMenuItem swingProject = new JMenuItem(name);
        swingProject.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                System.setProperty(Constants.PROP_PROJECT_FRAMEWORK, framework);
                MPFConfigurationUI configurationUI = new MPFConfigurationUI(MPFSelection.this);
                String fname = configurationUI.getProjectDirectory();
                if (fname != null)
                    filesSelected(new File[] { new File(fname) }, null);
            }
        });
        return swingProject;
    }

    /**
     * Return the selected MPF file.
     * 
     * @param arg
     *            , the filename given on command line
     * @return file, the selected file name
     */
    public String getProjectDirectory(String arg) {
        if (arg != null) {
            File file = new File(arg);
            if (!isValidProjectDirectory(file)) {
                JOptionPane.showMessageDialog(null, "Not a valid Marathon Project Directory");
            } else {
                if (findFile(file) == -1) {
                    dirName.addItem(file.toString());
                }
                dirName.setSelectedItem(arg);
                storeFileNames();
                return arg;
            }
        }
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocation(20, 20);
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                setVisible(true);
            }
        });
        waitForSelection();
        if (isOKSelected) {
            storeFileNames();
            return (String) dirName.getSelectedItem();
        }
        return null;
    }

    private void waitForSelection() {
        synchronized(this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * If the user selected a file using the 'browse' option, check whether the
     * file selected already exists in the fileName combo box.
     * 
     * @param file
     * @return index, the file name index into the combobox. -1 if a new file.
     */
    private int findFile(File file) {
        ComboBoxModel<String> model = dirName.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            String n = (String) dirName.getItemAt(i);
            try {
                if (new File(n).getCanonicalPath().equals(file.getCanonicalPath())) {
                    return i;
                }
            } catch (IOException e) {
                // Ignore
            }
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.mpf.FileSelectedAction#filesSelected(java.io
     * .File[], java.lang.Object)
     */
    public void filesSelected(File[] files, Object cookie) {
        File file = files[0];
        if (isValidProjectDirectory(file)) {
            if (findFile(file) == -1) {
                dirName.addItem(file.toString());
            }
            dirName.setSelectedItem(file.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Not a valid Marathon Project Directory");
        }
    }

}
