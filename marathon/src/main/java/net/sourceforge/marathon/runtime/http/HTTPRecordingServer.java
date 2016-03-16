package net.sourceforge.marathon.runtime.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import net.sourceforge.marathon.api.INamingStrategy;
import net.sourceforge.marathon.objectmap.ObjectMapConfiguration;
import net.sourceforge.marathon.objectmap.ObjectMapException;
import net.sourceforge.marathon.runtime.IRecordingServer;
import net.sourceforge.marathon.runtime.JSONScriptElement;
import net.sourceforge.marathon.runtime.NamingStrategyFactory;
import net.sourceforge.marathon.runtime.api.ContextMenuTriggers;
import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;
import net.sourceforge.marathon.runtime.api.WindowId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HTTPRecordingServer extends NanoHTTPD implements IRecordingServer {

    private final class JavaVersionScriptElement implements IScriptElement {
        private static final long serialVersionUID = 1L;

        @Override public String toScriptCode() {
            if (java_version != null)
                return Indent.getIndent() + "java_recorded_version = '" + java_version + "'\n";
            java_version = null;
            return "";
        }

        @Override public boolean isUndo() {
            return false;
        }

        @Override public WindowId getWindowId() {
            return null;
        }

        @Override public IScriptElement getUndoElement() {
            return null;
        }
    }

    private static final String NULL_OBJECT = new String();
    public static final String MIME_JSON = "application/json;charset=UTF-8";

    private final static ArrayList<net.sourceforge.marathon.runtime.http.RouteMap> routes;
    private IRecorder recorder;
    private boolean rawRecording;
    private Session latestSession;
    private Map<String, Session> liveSessions = new HashMap<String, Session>();
    private int port;
    INamingStrategy ns = NamingStrategyFactory.get();
    private String java_version;
    private boolean javaVersionRecorded = false;
    private boolean paused;
    private WindowId focusedWindowId;

    static {
        routes = new ArrayList<RouteMap>();
        // Custom
        routes.add(new RouteMap(Method.POST, "/session", getMethod("createSession")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId", getMethod("getSession")));
        routes.add(
                new RouteMap(Method.GET, "/session/:sessionId/object_map_configuration", getMethod("getObjectMapConfiguration")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/context_menu_triggers", getMethod("getContextMenuTriggers")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/raw_recording", getMethod("isRawRecording")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/record", getMethod("record")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/focused_window", getMethod("focusedWindow")));
    }

    public HTTPRecordingServer(int port) {
        super(port);
        this.port = port;
    }

    @Override public void start() {
        try {
            super.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to start httpd...", e);
        }
    }

    @Override public void startRecording(IRecorder recorder) {
        this.recorder = recorder;
        if (recorder != null && !javaVersionRecorded) {
            recorder.record(new JavaVersionScriptElement());
            javaVersionRecorded = true;
        }
    }

    @Override public void stopRecording() {
        this.recorder = null;
        ns.save();
    }

    @Override public void setRawRecording(boolean rawRecording) throws IOException {
        this.rawRecording = rawRecording;
    }

    @Override public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms,
            Map<String, String> files) {
        JSONObject jsonQuery = null;
        String query = parms.get("NanoHttpd.QUERY_STRING");
        if (query != null) {
            try {
                jsonQuery = new JSONObject(URLDecoder.decode(query, "utf-8"));
            } catch (JSONException | UnsupportedEncodingException e) {
                return NanoHTTPDnewFixedLengthResponse(Status.BAD_REQUEST, MIME_HTML, e.getMessage());
            }
        }
        StringBuilder logmsg = new StringBuilder();
        logmsg.append(method + "(" + uri);
        if (jsonQuery != null)
            logmsg.append(", " + jsonQuery);
        logmsg.append(") = ");
        Response response = serve_internal(uri, method, jsonQuery);
        logmsg.append(response);
        if (latestSession != null && !uri.contains("/log")) {
            latestSession.log(Level.INFO, logmsg.toString());
        }
        return response;
    }

    public Response serve_internal(String uri, Method method, JSONObject jsonQuery) {
        try {
            Route route = findRoute(method, uri);
            if (route != null && route.getProc() != null) {
                return handleRoute(route, jsonQuery);
            }
            if (route == null)
                return NanoHTTPDnewFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "Not Implemented: route = " + route);
            if (route.getProc() == null)
                return NanoHTTPDnewFixedLengthResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Not Implemented: route = " + route);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return NanoHTTPDnewFixedLengthResponse(Status.BAD_REQUEST, MIME_HTML, "");
    }

    public Route findRoute(Method method, String uri) {
        JSONObject params = new JSONObject();
        RouteMap map = findRouteMap(method, uri, params);
        if (map == null)
            return null;
        return new Route(map.getProc(), params, map);
    }

    private RouteMap findRouteMap(Method method, String uri, JSONObject params) {
        for (RouteMap route : HTTPRecordingServer.routes) {
            if (!route.getMethod().equals(method)) {
                continue;
            }
            String[] actualParts = uri.split("/");
            String[] expectedParts = route.getUri().split("/");
            if (actualParts.length != expectedParts.length)
                continue;
            Map<String, String> tParams = new HashMap<String, String>();
            boolean found = true;
            for (int i = 0; i < actualParts.length; i++) {
                if (expectedParts[i].startsWith(":")) {
                    tParams.put(expectedParts[i].substring(1), actualParts[i]);
                } else if (expectedParts[i].equals(actualParts[i]))
                    continue;
                else {
                    found = false;
                    break;
                }
            }
            if (found) {
                Set<Entry<String, String>> entrySet = tParams.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    params.put(entry.getKey(), entry.getValue());
                }
                return route;
            }
        }
        return null;
    }

    private Response handleRoute(Route route, JSONObject query) {
        JSONObject r = new JSONObject();
        r.put("name", route.getProc().getName());
        r.put("status", 0);
        try {
            JSONObject uriParams = route.getParams();
            Session session = null;
            if (uriParams.has("sessionId")) {
                session = liveSessions.get(uriParams.get("sessionId"));
                latestSession = session;
            }
            if (session != null)
                r.put("sessionId", session.getID());
            Object element = null;
            if (uriParams.has("id"))
                element = session.findElement(uriParams.getString("id"));
            Object window = null;
            if (uriParams.has("windowHandle"))
                window = session.getWindow(uriParams.getString("windowHandle"));
            Object result;
            result = invoke(route, query, uriParams, session, window, element);
            if (result == null)
                return NanoHTTPDnewFixedLengthResponse(Status.NO_CONTENT, MIME_HTML, null);
            if (result instanceof Response)
                return (Response) result;
            if (result == NULL_OBJECT)
                r.put("value", (Object) null);
            else
                r.put("value", result);
            return NanoHTTPDnewFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } catch (Exception e) {
            r.put("status", ErrorCodes.UNHANDLED_ERROR);
            r.put("value", new JSONObject().put("message", e.getClass().getName() + ":" + e.getMessage()).put("stackTrace",
                    getStackTrace(e)));
            return NanoHTTPDnewFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        }
    }

    private JSONArray getStackTrace(Throwable e) {
        JSONArray trace = new JSONArray();
        while (e != null) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement ste : stackTrace) {
                try {
                    trace.put(new JSONObject().put("fileName", ste.getFileName()).put("className", ste.getClassName())
                            .put("methodName", ste.getMethodName()).put("lineNumber", ste.getLineNumber()));
                } catch (JSONException e1) {
                }
            }
            e = e.getCause();
        }
        return trace;
    }

    public Object invoke(Route route, JSONObject query, JSONObject uriParams, Session session, Object window, Object element) {
        Object result;
        try {
            if (session == null)
                result = route.getProc().invoke(this, query, uriParams);
            else if (element != null)
                result = route.getProc().invoke(this, query, uriParams, session, element);
            else if (window != null)
                result = route.getProc().invoke(this, query, uriParams, session, window);
            else
                result = route.getProc().invoke(this, query, uriParams, session);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new RuntimeException(cause.getMessage(), cause);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    private static java.lang.reflect.Method getMethod(String name) {
        java.lang.reflect.Method[] methods = HTTPRecordingServer.class.getMethods();
        for (java.lang.reflect.Method method : methods) {
            if (method.getName().equals(name))
                return method;
        }
        return null;
    }

    public Response createSession(JSONObject query, JSONObject uriParams) {
        java_version = query.getString("platform.version");
        Session session = new Session();
        liveSessions.put(session.getID(), session);
        session.setLogLevel(getLogLevel(query));
        session.log(Level.INFO, "A new session created. sessionID = " + session.getID());
        try {
            RuntimeLogger.getRuntimeLogger().info("http-server", "Created a new session with ID " + session.getID(),
                    query.toString(2));
            Response r = NanoHTTPDnewFixedLengthResponse(Status.REDIRECT, MIME_HTML, null);
            r.addHeader("Location", new URL("http", "localhost", port, "/session/" + session.getID()).toString());
            return r;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public JSONObject getSession(JSONObject query, JSONObject uriParams, Session session) {
        return session.getDetails();
    }

    private Level getLogLevel(JSONObject query) {
        return Level.ALL;
    }

    public JSONObject getObjectMapConfiguration(JSONObject query, JSONObject uriParams, Session session) throws IOException {
        ObjectMapConfiguration omc = new ObjectMapConfiguration();
        omc.load();
        BeanToJsonConverter btjc = new BeanToJsonConverter();
        String s = btjc.convert(omc);
        return new JSONObject(s);
    }

    public JSONObject getContextMenuTriggers(JSONObject query, JSONObject uriParams, Session session) throws IOException {
        return new JSONObject().put("contextMenuKeyModifiers", ContextMenuTriggers.getContextMenuKeyModifiers())
                .put("contextMenuKey", ContextMenuTriggers.getContextMenuKeyCode())
                .put("menuModifiers", ContextMenuTriggers.getContextMenuModifiers());
    }

    public boolean isRawRecording(JSONObject query, JSONObject uriParams, Session session) {
        return rawRecording;
    }

    public JSONObject record(JSONObject query, JSONObject uriParams, Session session) throws IOException {
        if (paused || recorder == null)
            return new JSONObject();
        try {
            String name = null;
            try {
                name = query.getJSONObject("attributes").getString("suggestedName");
            } catch (JSONException e) {
            }
            recorder.record(new JSONScriptElement(createWindowId(query.getJSONObject("container")), ns.getName(query, name),
                    query.getJSONObject("event")));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new JSONObject();
    }

    public JSONObject focusedWindow(JSONObject query, JSONObject uriParams, Session session)
            throws IOException, JSONException, ObjectMapException {
        focusedWindowId = createWindowId(query.getJSONObject("container"));
        return new JSONObject();
    }

    private WindowId createWindowId(final JSONObject container) throws JSONException, ObjectMapException {
        WindowId parent = container.has("container") ? createWindowId(container.getJSONObject("container")) : null;
        return new WindowId(createTitle(container), parent, !container.getBoolean("is_window"));
    }

    private String createTitle(JSONObject container) throws JSONException, ObjectMapException {
        if (!container.getBoolean("is_window")) {
            return ns.getContainerName(container);
        }
        JSONObject urp = container.getJSONObject("urp");
        if (urp.has("title"))
            return urp.getString("title");
        urp = container.getJSONObject("containerURP");
        if (urp.has("title"))
            return urp.getString("title");
        urp = container.getJSONObject("attributes");
        return urp.getString("title");
    }

    private Response NanoHTTPDnewFixedLengthResponse(Status status, String mimeType, String data) {
        return new Response(status, mimeType, data);
    }

    @Override public void pauseRecording() {
        paused = true;
    }

    @Override public void resumeRecording() {
        paused = false;
    }

    public WindowId getFocusedWindowId() {
        return focusedWindowId;
    }
}
