package net.sourceforge.marathon.runtime.http;

import java.util.Date;
import java.util.logging.Level;

public class LogEntry {
    private final Level level;
    private final long timestamp;
    private final String message;

    public LogEntry(Level level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = new Date().getTime();
    }

    public String getLevel() {
        return level.toString();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
