package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;

public class SourceLine implements Serializable {
    private static final long serialVersionUID = 1L;
    public final String fileName;
    public final String functionName;
    public final int lineNumber;

    public SourceLine(String fileName, String functionName, int lineNumber) {
        this.fileName = fileName;
        this.functionName = functionName;
        this.lineNumber = lineNumber;
    }

    public String toString() {
        return fileName + ":" + functionName + ":" + lineNumber;
    }
}
