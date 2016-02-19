package net.sourceforge.marathon.javaagent;

public class SessionNotCreatedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public SessionNotCreatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
