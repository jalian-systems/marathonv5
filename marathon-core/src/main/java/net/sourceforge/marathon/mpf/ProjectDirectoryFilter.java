package net.sourceforge.marathon.mpf;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ProjectDirectoryFilter extends FileFilter {
    String description;

    public ProjectDirectoryFilter(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean accept(File pathname) {
        if (pathname.isDirectory())
            return true;
        return false;
    }
}
