package net.sourceforge.marathon.javafxagent;

public class UnsupportedCommandException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
