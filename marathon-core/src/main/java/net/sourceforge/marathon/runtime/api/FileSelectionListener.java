package net.sourceforge.marathon.runtime.api;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileSelectionListener implements ActionListener {
    private File previousDir;
    private IFileSelectedAction fsl;
    private FileFilter fileFilter;
    /*
     * Theoritically we need to get the parent so that we can popup the
     * JFileChooser relative to the parent . But there are some focus problems
     * when we pass the parent to the file chooser. Let us leave it here and
     * work on it when we get more time. FIXME: Find why this does not work
     * private Component parent;
     */
    private int mode = JFileChooser.FILES_ONLY;
    private boolean multipleSelection = false;
    private Object cookie;
    private Component parent;
    private final String title;

    public FileSelectionListener(IFileSelectedAction fsl, FileFilter filter, Component parent, Object cookie, String title) {
        this.parent = parent;
        this.fsl = fsl;
        fileFilter = filter;
        this.title = title;
        previousDir = new File(System.getProperty("user.home"));
        this.cookie = cookie;
    }

    public void setPreviousDir(File previousDir) {
        this.previousDir = previousDir;
    }

    public void setFileSelectionMode(int mode) {
        this.mode = mode;
    }

    public void setMultipleSelection(boolean selection) {
        multipleSelection = selection;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(previousDir);
        if (fileFilter != null)
            chooser.setFileFilter(fileFilter);
        chooser.setFileSelectionMode(mode);
        chooser.setMultiSelectionEnabled(multipleSelection);
        chooser.setDialogTitle(title);
        int selectedOption = chooser.showDialog(parent, "Select");
        previousDir = chooser.getCurrentDirectory();
        if (selectedOption == JFileChooser.APPROVE_OPTION) {
            if (multipleSelection)
                fsl.filesSelected(chooser.getSelectedFiles(), cookie);
            else
                fsl.filesSelected(new File[] { chooser.getSelectedFile() }, cookie);
        }
    }
}
