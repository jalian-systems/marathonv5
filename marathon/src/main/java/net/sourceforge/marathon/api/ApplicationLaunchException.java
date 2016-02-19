package net.sourceforge.marathon.api;

public class ApplicationLaunchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ApplicationLaunchException(String message, Throwable cause) {
        super(message, cause);
    }
}
