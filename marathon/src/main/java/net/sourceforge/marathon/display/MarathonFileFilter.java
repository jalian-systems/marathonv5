package net.sourceforge.marathon.display;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import net.sourceforge.marathon.editor.IMarathonFileFilter;
import net.sourceforge.marathon.runtime.api.IScriptModel;

public class MarathonFileFilter extends FileFilter implements IMarathonFileFilter {
    private String suffix;
    private final IScriptModel scriptModel;

    public MarathonFileFilter(String sourceFileSuffix, IScriptModel scriptModel) {
        suffix = sourceFileSuffix;
        this.scriptModel = scriptModel;
    }

    public boolean accept(File f) {
        if (f.isDirectory() && !f.getName().startsWith("."))
            return true;
        return !f.isDirectory() && scriptModel.isSourceFile(f);
    }

    public String getDescription() {
        return "Marathon Source Files";
    }

    public FileFilter getChooserFilter() {
        return this;
    }

    public String getSuffix() {
        return suffix;
    }
}
