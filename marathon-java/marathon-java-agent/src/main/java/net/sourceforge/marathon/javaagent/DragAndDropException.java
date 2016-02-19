package net.sourceforge.marathon.javaagent;

public class DragAndDropException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DragAndDropException(String msg, Exception cause) {
        super(msg, cause);
    }
}
