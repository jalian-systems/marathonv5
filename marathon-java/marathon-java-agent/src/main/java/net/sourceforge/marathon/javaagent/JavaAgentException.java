package net.sourceforge.marathon.javaagent;

public final class JavaAgentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JavaAgentException(String message, Throwable cause) {
        super(message, cause);
    }
}
