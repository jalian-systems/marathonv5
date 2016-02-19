package net.sourceforge.marathon.javaagent;

public class UnsupportedCommandException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnsupportedCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
