package net.sourceforge.marathon.javaagent;

public class InvalidElementStateException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidElementStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
