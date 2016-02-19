package net.sourceforge.marathon.javaagent.server;

import org.json.JSONObject;

public class Route {
    private final java.lang.reflect.Method proc;
    private final JSONObject params;
    private final RouteMap map;

    public Route(java.lang.reflect.Method proc, JSONObject params, RouteMap map) {
        this.proc = proc;
        this.params = params;
        this.map = map;
    }

    public java.lang.reflect.Method getProc() {
        return proc;
    }

    public JSONObject getParams() {
        return params;
    }

    public RouteMap getMap() {
        return map;
    }

    @Override public String toString() {
        return "proc = " + (proc != null ? proc.getName() : "") + " params = " + params + " map = " + map;
    }
}