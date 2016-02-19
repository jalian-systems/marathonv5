package net.sourceforge.marathon.objectmap;

import java.util.List;

public class ObjectMapException extends Exception {
    private static final long serialVersionUID = 1L;
    private final List<OMapComponent> matched;

    public ObjectMapException(String message) {
        this(message, null);
    }

    public ObjectMapException(String message, List<OMapComponent> matched) {
        super(message);
        this.matched = matched;
    }

    public List<OMapComponent> getMatched() {
        return matched;
    }
}
