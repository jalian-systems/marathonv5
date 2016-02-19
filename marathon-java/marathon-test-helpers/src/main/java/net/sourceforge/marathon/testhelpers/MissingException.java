package net.sourceforge.marathon.testhelpers;

public class MissingException extends Exception {
    private static final long serialVersionUID = 1L;

    public MissingException(Class<? extends Exception> klass) {
        super("Expected exception " + klass.getName() + " is not thrown");
    }
}
