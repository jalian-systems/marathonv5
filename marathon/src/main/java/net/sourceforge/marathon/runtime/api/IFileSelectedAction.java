package net.sourceforge.marathon.runtime.api;

import java.io.File;

public interface IFileSelectedAction {
    abstract public void filesSelected(File[] files, Object cookie);
}
