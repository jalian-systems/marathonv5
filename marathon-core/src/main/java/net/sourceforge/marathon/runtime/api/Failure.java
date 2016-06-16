package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.Arrays;

public class Failure implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private SourceLine[] traceback;
    private Throwable throwable;

    public Failure(String message, SourceLine[] traceback, Throwable throwable) {
        this.message = message;
        this.traceback = traceback;
        this.throwable = throwable;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return message + "\n(" + Arrays.asList(traceback) + ")";
    }

    public SourceLine[] getTraceback() {
        return traceback;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
