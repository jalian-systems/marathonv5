package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class PlaybackResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Collection<Failure> failures = new LinkedList<Failure>();

    public void addFailure(String message, SourceLine[] traceback, Throwable t) {
        failures.add(new Failure(message, traceback, t));
    }

    public Failure[] failures() {
        return (Failure[]) failures.toArray(new Failure[failures.size()]);
    }

    public int failureCount() {
        return failures.size();
    }

    public boolean hasFailure() {
        return failureCount() > 0;
    }

    public void addFailure(Failure[] f) {
        failures.addAll(Arrays.asList(f));
    }
}
