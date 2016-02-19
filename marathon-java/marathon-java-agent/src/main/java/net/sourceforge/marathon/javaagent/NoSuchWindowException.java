package net.sourceforge.marathon.javaagent;

public class NoSuchWindowException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSuchWindowException(String message, Throwable cause) {
        super(message, cause);
    }
}
