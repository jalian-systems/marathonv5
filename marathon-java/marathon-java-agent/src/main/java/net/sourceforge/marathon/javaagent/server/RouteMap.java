package net.sourceforge.marathon.javaagent.server;

import fi.iki.elonen.NanoHTTPD.Method;

public class RouteMap {
    private final Method method;
    private final String uri;
    private final java.lang.reflect.Method proc;

    public RouteMap(Method method, String uri) {
        this(method, uri, null);
    }

    public RouteMap(Method method, String uri, java.lang.reflect.Method proc) {
        this.method = method;
        this.uri = uri;
        this.proc = proc;
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public java.lang.reflect.Method getProc() {
        return proc;
    }

    @Override public String toString() {
        return " method = " + method + "\nuri = " + uri;
    }
}