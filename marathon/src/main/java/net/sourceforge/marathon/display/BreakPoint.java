package net.sourceforge.marathon.display;

import java.io.Serializable;

public class BreakPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int linenumber;
    private final String filePath;

    public BreakPoint(String filePath, int linenumber) {
        this.filePath = filePath;
        this.linenumber = linenumber;
    }

    @Override public int hashCode() {
        return (filePath + linenumber).hashCode();
    }

    @Override public boolean equals(Object obj) {
        return obj != null && (obj instanceof BreakPoint) && ((BreakPoint) obj).filePath.equals(filePath)
                && ((BreakPoint) obj).linenumber == linenumber;
    }

    public boolean shouldSave() {
        return !filePath.startsWith("Untitled");
    }
}
