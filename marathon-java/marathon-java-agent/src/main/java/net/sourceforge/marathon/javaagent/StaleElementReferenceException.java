package net.sourceforge.marathon.javaagent;

public class StaleElementReferenceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StaleElementReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
