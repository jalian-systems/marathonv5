package net.sourceforge.marathon.javafxagent;

public class StaleElementReferenceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StaleElementReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
