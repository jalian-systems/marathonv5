package net.sourceforge.marathon.runtime.api;

public class InterruptionError extends Error {
    private static final long serialVersionUID = 1L;

    public static void wait(Object monitor) {
        try {
            // FindBug Error: Ignore
            // Reason: The callers calls this method in a loop
            monitor.wait();
        } catch (InterruptedException e) {
            throw new InterruptionError();
        }
    }
}
