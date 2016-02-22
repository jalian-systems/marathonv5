package net.sourceforge.marathon.javafxagent;

public class MissingCommandParametersException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MissingCommandParametersException(String message, Throwable cause) {
        super(message, cause);
    }
}
