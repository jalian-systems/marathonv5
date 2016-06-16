package net.sourceforge.marathon.runtime.api;

public class ScriptException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}
