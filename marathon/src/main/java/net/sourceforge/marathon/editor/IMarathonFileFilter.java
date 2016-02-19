package net.sourceforge.marathon.editor;

import javax.swing.filechooser.FileFilter;

public interface IMarathonFileFilter {

    FileFilter getChooserFilter();

    String getSuffix();

}
