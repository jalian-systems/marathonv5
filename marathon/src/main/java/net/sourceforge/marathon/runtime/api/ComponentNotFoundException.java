package net.sourceforge.marathon.runtime.api;

public class ComponentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
