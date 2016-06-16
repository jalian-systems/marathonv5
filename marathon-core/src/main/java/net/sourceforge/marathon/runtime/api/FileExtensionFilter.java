package net.sourceforge.marathon.runtime.api;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileExtensionFilter extends FileFilter {
    String description;
    String[] extensions;

    public FileExtensionFilter(String description, String[] extensions) {
        this.description = description;
        this.extensions = extensions;
    }

    public String getDescription() {
        return description;
    }

    public boolean accept(File pathname) {
        if (pathname.isDirectory())
            return true;
        String fileName = pathname.getName();
        for (int i = 0; i < extensions.length; i++) {
            if (fileName.endsWith(extensions[i]))
                return true;
        }
        return false;
    }
}
