package net.sourceforge.marathon.runtime.http;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

import org.json.JSONObject;

public class Session {

    @SuppressWarnings("unused") private Level logLevel;
    private String id;
    private String start;

    public Session() {
        this.id = UUID.randomUUID().toString();
        this.start = new Date().toString();
    }

    public void log(Level info, String string) {
    }

    public String getID() {
        return id;
    }

    public Object findElement(String id) {
        return null;
    }

    public Object getWindow(String string) {
        return null;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public JSONObject getDetails() {
        return new JSONObject().put("start", start);
    }

}
