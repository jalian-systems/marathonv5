package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;

/**
 * Information of plug ins such as ScriptModel, Launcher.
 */
public class PlugInModelInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public String className;

    public PlugInModelInfo(String description, String className) {
        this.name = description;
        this.className = className;
    }

    @Override public String toString() {
        return name;
    }
}