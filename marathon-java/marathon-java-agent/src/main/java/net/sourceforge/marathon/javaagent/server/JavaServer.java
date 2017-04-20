/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javaagent.server;

import java.awt.AWTException;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import net.sourceforge.marathon.javaagent.Device;
import net.sourceforge.marathon.javaagent.Device.Type;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.InvalidElementStateException;
import net.sourceforge.marathon.javaagent.JavaAgentException;
import net.sourceforge.marathon.javaagent.JavaAgentKeys;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.MissingCommandParametersException;
import net.sourceforge.marathon.javaagent.NoSuchElementException;
import net.sourceforge.marathon.javaagent.NoSuchWindowException;
import net.sourceforge.marathon.javaagent.Platform;
import net.sourceforge.marathon.javaagent.SessionNotCreatedException;
import net.sourceforge.marathon.javaagent.StaleElementReferenceException;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;
import net.sourceforge.marathon.javaagent.script.JSONScriptRunner;

public class JavaServer extends NanoHTTPD {

    private static final Logger logger = Logger.getLogger(JavaServer.class.getName());

    public static final String MIME_JSON = "application/json;charset=UTF-8";

    private Map<String, Session> liveSessions = new HashMap<String, Session>();

    private static List<RouteMap> routes;
    private int port;

    private Session latestSession;
    private static final JSONObject hasCapabilities = new JSONObject();
    private JSONObject capabilities = new JSONObject();

    private static final String NULL_OBJECT = new String();

    private boolean exitOnQuit;

    static {
        try {
            // Supported Capabilities
            hasCapabilities.put("browserName", "java");
            hasCapabilities.put("version", "1.0");
            hasCapabilities.put("platform", Platform.getCurrent().toString());
            hasCapabilities.put("javascriptEnabled", true);
            hasCapabilities.put("takesScreenshot", true);
            hasCapabilities.put("handlesAlerts", false);
            hasCapabilities.put("databaseEnabled", false);
            hasCapabilities.put("locationContextEnabled", false);
            hasCapabilities.put("applicationCacheEnabled", false);
            hasCapabilities.put("browserConnectionEnabled", false);
            hasCapabilities.put("cssSelectorsEnabled", false);
            hasCapabilities.put("webStorageEnabled", false);
            hasCapabilities.put("rotatable", false);
            hasCapabilities.put("acceptSslCerts", false);
            hasCapabilities.put("nativeEvents", true);
            hasCapabilities.put("loggingPrefs", new JSONObject().put("driver", "ALL"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static {
        routes = new ArrayList<RouteMap>();
        routes.add(new RouteMap(Method.GET, "/status", getMethod("getStatus")));
        routes.add(new RouteMap(Method.POST, "/session", getMethod("createSession")));
        routes.add(new RouteMap(Method.GET, "/sessions", getMethod("getSessions")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId", getMethod("getCapabilities")));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId", getMethod("quitSession")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/timeouts", getMethod("setTimeouts")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/timeouts/async_script"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/timeouts/implicit_wait", getMethod("setImplicitTimeout")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/window_handle", getMethod("getWindowHandle")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/window_handles", getMethod("getWindowHandles")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/url", getMethod("getWindowProperties")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/url"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/forward"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/back"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/refresh"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/execute", getMethod("execute")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/execute_async", getMethod("executeAsync")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/screenshot", getMethod("getScreenShot")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/ime/available_engines"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/ime/active_engine"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/ime/activated"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/ime/deactivate"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/ime/activate"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/frame"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/window", getMethod("getWindow")));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId/window", getMethod("closeSession")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/window/:windowHandle/size", getMethod("setWindowSize", true)));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/window/:windowHandle/size", getMethod("getWindowSize")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/window/:windowHandle/position",
                getMethod("setWindowPosition", true)));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/window/:windowHandle/position", getMethod("getWindowPosition")));
        routes.add(
                new RouteMap(Method.POST, "/session/:sessionId/window/:windowHandle/maximize", getMethod("maximizeWindow", true)));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/cookie"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/cookie"));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId/cookie"));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId/cookie/:name"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/source"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/title", getMethod("getWindowTitle")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element", getMethod("findElement")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/elements", getMethod("findElements")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element/active", getMethod("findActiveElement")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element/:id/element", getMethod("findElementOfElement")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element/:id/elements", getMethod("findElementsOfElement")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element/:id/click", getMethod("clickElement", true)));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element/:id/submit", getMethod("submitElement", true)));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/text", getMethod("getElementText")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element/:id/value", getMethod("sendKeysElement", true)));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/keys", getMethod("sendKeys", true)));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/name", getMethod("getElementName")));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/element/:id/clear", getMethod("clearElement")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/selected", getMethod("isSelected")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/enabled", getMethod("isEnabled")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/attribute/:name", getMethod("getElementAttribute")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/equals/:other", getMethod("elementEquals")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/displayed", getMethod("isDisplayed")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/location", getMethod("getElementLocation")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/location_in_view"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/size", getMethod("getElementSize")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/element/:id/css/:propertyName"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/orientation"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/orientation"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/alert_text"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/alert_text"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/accept_alert"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/dismiss_alert"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/moveto", getMethod("moveto", true)));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/click", getMethod("click", true)));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/buttondown", getMethod("buttondown", true)));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/buttonup", getMethod("buttonup", true)));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/doubleclick", getMethod("doubleclick", true)));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/touch/click"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/touch/down"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/touch/up"));
        routes.add(new RouteMap(Method.POST, "session/:sessionId/touch/move"));
        routes.add(new RouteMap(Method.POST, "session/:sessionId/touch/scroll"));
        routes.add(new RouteMap(Method.POST, "session/:sessionId/touch/scroll"));
        routes.add(new RouteMap(Method.POST, "session/:sessionId/touch/doubleclick"));
        routes.add(new RouteMap(Method.POST, "session/:sessionId/touch/longclick"));
        routes.add(new RouteMap(Method.POST, "session/:sessionId/touch/flick"));
        routes.add(new RouteMap(Method.POST, "session/:sessionId/touch/flick"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/location"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/location"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/local_storage"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/local_storage"));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId/local_storage"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/local_storage/key/:key"));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId/local_storage/key/:key"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/local_storage/size"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/session_storage"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/session_storage"));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId/session_storage"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/session_storage/key/:key"));
        routes.add(new RouteMap(Method.DELETE, "/session/:sessionId/session_storage/key/:key"));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/session_storage/size"));
        routes.add(new RouteMap(Method.POST, "/session/:sessionId/log", getMethod("getLogs")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/log/types", getMethod("getLogTypes")));
        routes.add(new RouteMap(Method.GET, "/session/:sessionId/application_cache/status"));

        // Custom
        routes.add(new RouteMap(Method.GET, "/info", getMethod("getInfo")));
    }

    public JavaServer(int port) {
        this(port, false);
    }

    public JavaServer(int port, boolean exitOnQuit) {
        super(port);
        this.exitOnQuit = exitOnQuit;
        this.port = port;
        initCapabilities();
    }

    private void initCapabilities() {
        // Current Capabilities
        capabilities.put("browserName", "java");
        capabilities.put("version", "1.0");
        capabilities.put("platform", Platform.getCurrent().toString());
        capabilities.put("javascriptEnabled", true);
        capabilities.put("takesScreenshot", true);
        capabilities.put("handlesAlerts", false);
        capabilities.put("databaseEnabled", false);
        capabilities.put("locationContextEnabled", false);
        capabilities.put("applicationCacheEnabled", false);
        capabilities.put("browserConnectionEnabled", false);
        capabilities.put("cssSelectorsEnabled", false);
        capabilities.put("webStorageEnabled", false);
        capabilities.put("rotatable", false);
        capabilities.put("acceptSslCerts", false);
        capabilities.put("nativeEvents", false);
        capabilities.put("loggingPrefs", new JSONObject().put("driver", "ALL"));
    }

    public int getPort() {
        return port;
    }

    private static java.lang.reflect.Method getMethod(String name, boolean hasEvents) {
        java.lang.reflect.Method[] methods = JavaServer.class.getMethods();
        for (java.lang.reflect.Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    private static java.lang.reflect.Method getMethod(String name) {
        return getMethod(name, false);
    }

    public Route findRoute(Method method, String uri) {
        JSONObject params = new JSONObject();
        RouteMap map = findRouteMap(method, uri, params);
        if (map == null) {
            return null;
        }
        return new Route(map.getProc(), params, map);
    }

    private RouteMap findRouteMap(Method method, String uri, JSONObject params) {
        for (RouteMap route : JavaServer.routes) {
            if (!route.getMethod().equals(method)) {
                continue;
            }
            String[] actualParts = uri.split("/");
            String[] expectedParts = route.getUri().split("/");
            if (actualParts.length != expectedParts.length) {
                continue;
            }
            Map<String, String> tParams = new HashMap<String, String>();
            boolean found = true;
            for (int i = 0; i < actualParts.length; i++) {
                if (expectedParts[i].startsWith(":")) {
                    tParams.put(expectedParts[i].substring(1), actualParts[i]);
                } else if (expectedParts[i].equals(actualParts[i])) {
                    continue;
                } else {
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

    @Override public Response serve(String uri, Method method, Map<String, String> header, Map<String, String> parms,
            Map<String, String> files) {
        JSONObject jsonQuery = null;
        String query = files.get("postData");
        logger.info("JavaServer.serve(" + method + " " + uri + "): " + (query != null ? query : "{}"));
        if (query != null) {
            try {
                jsonQuery = new JSONObject(query);
            } catch (JSONException e) {
                logger.info("JavaServer.serve(): " + query);
                return newFixedLengthResponse(Status.BAD_REQUEST, MIME_HTML, e.getMessage());
            }
        }
        StringBuilder logmsg = new StringBuilder();
        logmsg.append(method + "(" + uri);
        if (jsonQuery != null) {
            logmsg.append(", " + jsonQuery);
        }
        logmsg.append(") = ");
        Response response = serve_internal(uri, method, jsonQuery == null ? new JSONObject() : jsonQuery);
        logmsg.append(toString(response));
        if (latestSession != null && !uri.contains("/log")) {
            if(Boolean.getBoolean("keepLog"))
                latestSession.log(Level.INFO, logmsg.toString());
        }
        logger.info(logmsg.toString());
        return response;
    }

    private String toString(Response response) {
        Map<String, String> r = new HashMap<String, String>();
        r.put("status", response.getStatus().toString());
        InputStream data = response.getData();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        int n = 1024;
        try {
            while ((b = data.read()) != -1 && n-- > 0)
                baos.write(b);
        } catch (IOException e) {
        } finally {
            try {
                data.reset();
            } catch (IOException e) {
            }
        }
        if(n <= 0)
            r.put("data", new String(baos.toByteArray()) + "...");
        else
            r.put("data", new String(baos.toByteArray()));
        try {
            baos.close();
        } catch (IOException e) {
        }
        return r.toString();
    }

    public Response serve_internal(String uri, Method method, JSONObject jsonQuery) {
        try {
            Route route = findRoute(method, uri);
            if (route != null && route.getProc() != null) {
                return handleRoute(route, jsonQuery);
            }
            if (route == null) {
                return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "Not Implemented: (route is null)");
            }
            if (route.getProc() == null) {
                return newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Not Implemented: route = " + route);
            }
        } catch (Throwable e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            e.printStackTrace();
        }
        return newFixedLengthResponse(Status.BAD_REQUEST, MIME_HTML, "");
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
            if (session != null) {
                r.put("sessionId", session.getID());
            }
            IJavaElement element = null;
            if (uriParams.has("id")) {
                element = session.findElement(uriParams.getString("id"));
            }
            JWindow window = null;
            if (uriParams.has("windowHandle")) {
                window = session.getWindow(uriParams.getString("windowHandle"));
            }
            Object result;
            result = invoke(route, query, uriParams, session, window, element);
            if (result == null) {
                return newFixedLengthResponse(Status.NO_CONTENT, MIME_HTML, null);
            }
            if (result instanceof Response) {
                return (Response) result;
            }
            if (result == NULL_OBJECT) {
                r.put("value", (Object) null);
            } else {
                r.put("value", result);
            }
            return newFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } catch (NoSuchWindowException e) {
            r.put("status", ErrorCodes.NO_SUCH_WINDOW);
            r.put("value", new JSONObject().put("message", e.getMessage()).put("stackTrace", getStackTrace(e)));
            return newFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } catch (NoSuchElementException e) {
            r.put("status", ErrorCodes.NO_SUCH_ELEMENT);
            r.put("value", new JSONObject().put("message", e.getMessage()).put("stackTrace", getStackTrace(e)));
            return newFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } catch (MissingCommandParametersException e) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, e.getMessage());
        } catch (UnsupportedCommandException e) {
            return newFixedLengthResponse(Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, e.getMessage());
        } catch (InvalidElementStateException e) {
            r.put("status", ErrorCodes.INVALID_ELEMENT_STATE);
            r.put("value", new JSONObject().put("message", e.getMessage()).put("stackTrace", getStackTrace(e)));
            return newFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } catch (StaleElementReferenceException e) {
            r.put("status", ErrorCodes.STALE_ELEMENT_REFERENCE);
            r.put("value", new JSONObject().put("message", e.getMessage()).put("stackTrace", getStackTrace(e)));
            return newFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } catch (SessionNotCreatedException e) {
            r.put("status", ErrorCodes.SESSION_NOT_CREATED);
            r.put("value", new JSONObject().put("message", e.getMessage()).put("stackTrace", getStackTrace(e)));
            return newFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            r.put("status", ErrorCodes.UNHANDLED_ERROR);
            r.put("value", new JSONObject().put("message", e.getClass().getName() + ":" + e.getMessage()).put("stackTrace",
                    getStackTrace(e)));
            return newFixedLengthResponse(Status.OK, MIME_JSON, r.toString());
        } finally {
            afterEvent();
        }
    }

    private void afterEvent() {
        EventQueueWait.empty();
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

    public Object invoke(Route route, JSONObject query, JSONObject uriParams, Session session, JWindow window,
            IJavaElement element) {
        Object result;
        try {
            if (session == null) {
                result = route.getProc().invoke(this, query, uriParams);
            } else if (element != null) {
                result = route.getProc().invoke(this, query, uriParams, session, element);
            } else if (window != null) {
                result = route.getProc().invoke(this, query, uriParams, session, window);
            } else {
                result = route.getProc().invoke(this, query, uriParams, session);
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof MissingCommandParametersException) {
                throw (MissingCommandParametersException) cause;
            }
            if (cause instanceof JSONException) {
                throw (JSONException) cause;
            }
            if (cause instanceof NoSuchElementException) {
                throw (NoSuchElementException) cause;
            }
            if (cause instanceof NoSuchWindowException) {
                throw (NoSuchWindowException) cause;
            }
            if (cause instanceof UnsupportedCommandException) {
                throw (UnsupportedCommandException) cause;
            }
            if (cause instanceof InvalidElementStateException) {
                throw (InvalidElementStateException) cause;
            }
            if (cause instanceof StaleElementReferenceException) {
                throw (StaleElementReferenceException) cause;
            }
            if (cause instanceof SessionNotCreatedException) {
                throw (SessionNotCreatedException) cause;
            }
            if (cause instanceof JavaAgentException) {
                throw (JavaAgentException) cause;
            }
            throw new JavaAgentException(cause.getMessage(), cause);
        } catch (IllegalArgumentException e) {
            throw new JavaAgentException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new JavaAgentException(e.getMessage(), e);
        }
        return result;
    }

    public Response createSession(JSONObject query, JSONObject uriParams) {
        if (query.has("requiredCapabilities")) {
            JSONObject required = (JSONObject) query.get("requiredCapabilities");
            String okCaps = hasCapabilities(required);
            if (okCaps != null) {
                throw new SessionNotCreatedException(okCaps, null);
            }
        }
        Type t = Device.Type.EVENT_QUEUE;
        if (capabilities.getBoolean("nativeEvents")) {
            t = Device.Type.ROBOT;
        }
        Logger.getLogger(JavaServer.class.getName()).info("Creating device with type: " + t);
        Session session = new Session(t);
        liveSessions.put(session.getID(), session);
        session.setLogLevel(getLogLevel(query));
        session.log(Level.INFO, "A new session created. sessionID = " + session.getID());
        try {
            Response r = newFixedLengthResponse(Status.REDIRECT, MIME_HTML, null);
            r.addHeader("Location", new URL("http", "localhost", port, "/session/" + session.getID()).toString());
            return r;
        } catch (MalformedURLException e) {
            throw new JavaAgentException(e.getMessage(), e);
        }
    }

    public JSONArray getSessions(JSONObject query, JSONObject uriParams) {
        JSONArray r = new JSONArray();
        Set<Entry<String, Session>> x = liveSessions.entrySet();
        for (Entry<String, Session> entry : x) {
            JSONObject o = new JSONObject();
            o.put("id", entry.getKey());
            o.put("capabilities", hasCapabilities);
            r.put(o);
        }
        return r;
    }

    private Level getLogLevel(JSONObject query) {
        JSONObject prefs;
        if (query.has("requiredCapabilities") && query.getJSONObject("requiredCapabilities").has("loggingPrefs")) {
            prefs = query.getJSONObject("requiredCapabilities").getJSONObject("loggingPrefs");
        } else if (query.has("desiredCapabilities") && query.getJSONObject("desiredCapabilities").has("loggingPrefs")) {
            prefs = query.getJSONObject("desiredCapabilities").getJSONObject("loggingPrefs");
        } else {
            return Level.ALL;
        }
        if (prefs.has("driver")) {
            return Level.parse(prefs.getString("driver"));
        }
        return Level.ALL;
    }

    private String hasCapabilities(JSONObject required) {
        @SuppressWarnings("rawtypes")
        Iterator keys = required.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (!hasCapabilities.has(key)) {
                return "Do not have the capability by name " + key;
            }
            Object rvalue = required.get(key);
            capabilities.put(key, rvalue);
            if (rvalue instanceof Boolean && !((Boolean) rvalue).booleanValue()) {
                continue;
            }
            Object lvalue = hasCapabilities.get(key);
            if (!lvalue.equals(rvalue)) {
                if (key.equals("loggingPrefs")) {
                    continue;
                }
                if (key.equals("platform")) {
                    Platform lPlatform = Platform.valueOf((String) lvalue);
                    Platform rPlatform = Platform.valueOf((String) rvalue);
                    if (rPlatform.is(lPlatform)) {
                        continue;
                    }
                }
                if (key.equals("version") && rvalue.equals("")) {
                    continue;
                }
                return "Java Driver does not support `" + key + "`" + (rvalue instanceof Boolean ? "" : " for value " + rvalue);
            }
        }
        return null;
    }

    public JSONObject getCapabilities(JSONObject query, JSONObject uriParams, Session session) {
        return capabilities;
    }

    public JSONObject findElement(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "using", "value");
        IJavaElement e = session.findElement(query.getString("using"), query.getString("value"));
        return new JSONObject().put("ELEMENT", e.getHandle());
    }

    public String getElementName(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        return element.getTagName();
    }

    public JSONObject getElementLocation(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        return new JSONObject(element.getLocation());
    }

    public String[] toStringArray(JSONArray value) {
        String[] s = new String[value.length()];
        for (int i = 0; i < value.length(); i++) {
            s[i] = value.getString(i);
        }
        return s;
    }

    public void closeSession(JSONObject query, JSONObject uriParams, Session session) {
        session.deleteWindow();
    }

    public JSONObject getElementSize(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        return new JSONObject(element.getSize());
    }

    public String getElementAttribute(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        String attribute = null;
        try {
            attribute = element.getAttribute(URLDecoder.decode(uriParams.getString("name"), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (attribute == null) {
            return NULL_OBJECT;
        }
        return attribute;
    }

    public JSONObject getStatus(JSONObject query, JSONObject uriParams) {
        JSONObject v = new JSONObject();
        JSONObject os = new JSONObject();
        os.put("arch", System.getProperty("os.arch", "unknown"));
        os.put("name", System.getProperty("os.name", "unknown"));
        os.put("version", System.getProperty("os.version", "unknown"));
        v.put("os", os);
        JSONObject build = new JSONObject();
        build.put("version", "1.0");
        return v.put("build", build);
    }

    public JSONObject getInfo(JSONObject query, JSONObject uriParams) {
        JSONObject v = new JSONObject();
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        v.put("boot-class-path", bean.getBootClassPath());
        v.put("start-time", bean.getStartTime());
        v.put("commandline", System.getProperty("sun.java.command"));
        return v;
    }

    public JSONArray getWindowHandles(JSONObject query, JSONObject uriParams, Session session) {
        return new JSONArray(session.getWindowHandles());
    }

    public void getWindow(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "name");
        session.window(query.getString("name"));
    }

    public void setImplicitTimeout(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "ms");
        session.setTimeout(query.getLong("ms"));
    }

    public void setTimeouts(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "ms");
        session.setTimeout(query.getLong("ms"));
    }

    public void quitSession(JSONObject query, JSONObject uriParams, Session session) {
        if (exitOnQuit) {
            session.quit();
        }
        liveSessions.remove(session.getID());
    }

    public String getWindowHandle(JSONObject query, JSONObject uriParams, Session session) {
        return session.getWindowHandle();
    }

    public JSONObject getWindowSize(JSONObject query, JSONObject uriParams, Session session, JWindow window) {
        return new JSONObject(window.getSize());
    }

    public JSONObject getWindowPosition(JSONObject query, JSONObject uriParams, Session session, JWindow window) {
        return new JSONObject(window.getLocation());
    }

    public void setWindowSize(JSONObject query, JSONObject uriParams, Session session, JWindow window) {
        checkRequiredArguments(query, "width", "height");
        window.setSize(query.getInt("width"), query.getInt("height"));
    }

    public void setWindowPosition(JSONObject query, JSONObject uriParams, Session session, JWindow window) {
        checkRequiredArguments(query, "x", "y");
        window.setLocation(query.getInt("x"), query.getInt("y"));
    }

    public void maximizeWindow(JSONObject query, JSONObject uriParams, Session session, JWindow window) {
        window.maximize();
    }

    public String getWindowTitle(JSONObject query, JSONObject uriParams, Session session) {
        String title = session.getTitle();
        if (title == null) {
            return NULL_OBJECT;
        }
        return title;
    }

    public String getElementText(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        String text = element.getAttribute("text");
        if (text == null) {
            return NULL_OBJECT;
        }
        return text;
    }

    public JSONArray findElements(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "using", "value");
        JSONArray r = new JSONArray();
        List<IJavaElement> es = session.findElements(query.getString("using"), query.getString("value"));
        for (IJavaElement e : es) {
            r.put(new JSONObject().put("ELEMENT", e.getHandle()));
        }
        return r;
    }

    public JSONObject findActiveElement(JSONObject query, JSONObject uriParams, Session session) {
        IJavaElement e = session.getActiveElement();
        return new JSONObject().put("ELEMENT", e.getHandle());
    }

    public JSONObject findElementOfElement(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        checkRequiredArguments(query, "using", "value");
        IJavaElement e = session.findElement(element, query.getString("using"), query.getString("value"));
        return new JSONObject().put("ELEMENT", e.getHandle());
    }

    public JSONArray findElementsOfElement(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        checkRequiredArguments(query, "using", "value");
        JSONArray r = new JSONArray();
        List<IJavaElement> es = session.findElements(element, query.getString("using"), query.getString("value"));
        for (IJavaElement e : es) {
            r.put(new JSONObject().put("ELEMENT", e.getHandle()));
        }
        return r;
    }

    public void clearElement(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        element.clear();
    }

    public boolean isSelected(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        return element.isSelected();
    }

    public boolean isEnabled(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        return element.isEnabled();
    }

    public boolean isDisplayed(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        return element.isDisplayed();
    }

    public boolean elementEquals(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        IJavaElement other = session.findElement(uriParams.getString("other"));
        return element.equals(other);
    }

    public JSONArray getLogTypes(JSONObject query, JSONObject uriParams, Session session) {
        return new JSONArray().put("driver");
    }

    public JSONArray getLogs(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "type");
        JSONArray logEntries = new JSONArray();
        if ("driver".equals(query.getString("type"))) {
            session.fillLog(logEntries);
        }
        return logEntries;
    }

    public String getWindowProperties(JSONObject query, JSONObject uriParams, Session session) {
        JSONObject props = session.getWindowProperties();
        return props.toString();
    }

    // User Actions
    private static class ComponentState {
        public IJavaElement element;
        public int x;
        public int y;
    }

    private static ComponentState lastComponenet = new ComponentState();

    public void moveto(JSONObject query, JSONObject uriParams, Session session) {
        IJavaElement element = null;
        if (query.has("element")) {
            element = session.findElement(query.getString("element"));
        }
        boolean hasOffset = query.has("xoffset");
        if (hasOffset != query.has("yoffset")) {
            throw new MissingCommandParametersException("Missing x-offset or y-offset. Provide both x and y offsets.", null);
        }
        if (element == null && !hasOffset) {
            throw new MissingCommandParametersException("One of the element or offset is expected.", null);
        }
        int xoffset, yoffset;
        if (hasOffset) {
            xoffset = query.getInt("xoffset");
            yoffset = query.getInt("yoffset");
        } else {
            Point p = element.getMidpoint();
            xoffset = p.x;
            yoffset = p.y;
        }
        if (element == null) {
            if (hasOffset && lastComponenet.element != null) {
                element = lastComponenet.element;
                xoffset += lastComponenet.x;
                yoffset += lastComponenet.y;
            } else {
                element = session.getActiveElement();
            }
        }
        element.moveto(xoffset, yoffset);
        lastComponenet.x = xoffset;
        lastComponenet.y = yoffset;
        lastComponenet.element = element;
    }

    public void clickElement(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        if (lastComponenet.element != null && lastComponenet.element.equals(element)) {
            element.click(0, 1, lastComponenet.x, lastComponenet.y);
        } else {
            Point p = element.getMidpoint();
            element.click(0, 1, p.x, p.y);
            lastComponenet.element = element;
            lastComponenet.x = p.x;
            lastComponenet.y = p.y;
        }
    }

    public void click(JSONObject query, JSONObject uriParams, Session session) {
        int button = 0;
        if (query.has("button")) {
            button = query.getInt("button");
        }
        click(session, button, 1);
    }

    public void submitElement(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        element.submit();
    }

    public void sendKeysElement(JSONObject query, JSONObject uriParams, Session session, IJavaElement element) {
        checkRequiredArguments(query, "value");
        JSONArray value = query.getJSONArray("value");
        value.put(JavaAgentKeys.NULL.subSequence(0, 1));
        element.sendKeys(toStringArray(value));
    }

    public void sendKeys(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "value");
        IJavaElement element = null;
        if (lastComponenet.element != null) {
            element = lastComponenet.element;
        } else {
            element = session.getActiveElement();
        }
        element.sendKeys(toStringArray(query.getJSONArray("value")));
    }

    public void buttondown(JSONObject query, JSONObject uriParams, Session session) {
        int button = 0;
        if (query != null && query.has("button")) {
            button = query.getInt("button");
        }
        IJavaElement element = null;
        int xoffset, yoffset;
        if (lastComponenet.element != null) {
            element = lastComponenet.element;
            xoffset = lastComponenet.x;
            yoffset = lastComponenet.y;
        } else {
            element = session.getActiveElement();
            Point p = element.getMidpoint();
            xoffset = p.x;
            yoffset = p.y;
        }
        element.buttonDown(button, xoffset, yoffset);
    }

    public void buttonup(JSONObject query, JSONObject uriParams, Session session) {
        int button = 0;
        if (query.has("button")) {
            button = query.getInt("button");
        }
        IJavaElement element = null;
        int xoffset, yoffset;
        if (lastComponenet.element != null) {
            element = lastComponenet.element;
            xoffset = lastComponenet.x;
            yoffset = lastComponenet.y;
        } else {
            element = session.getActiveElement();
            Point p = element.getMidpoint();
            xoffset = p.x;
            yoffset = p.y;
        }
        element.buttonUp(button, xoffset, yoffset);
    }

    public void doubleclick(JSONObject query, JSONObject uriParams, Session session) {
        click(session, 0, 2);
    }

    private void click(Session session, int button, int clickCount) {
        IJavaElement element = null;
        int xoffset, yoffset;
        if (lastComponenet.element != null) {
            element = lastComponenet.element;
            xoffset = lastComponenet.x;
            yoffset = lastComponenet.y;
        } else {
            element = session.getActiveElement();
            Point p = element.getMidpoint();
            xoffset = p.x;
            yoffset = p.y;
        }
        element.click(button, clickCount, xoffset, yoffset);
    }

    public Object execute(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "script", "args");
        JSONScriptRunner scriptRunner = new JSONScriptRunner(query.getString("script"), query.getJSONArray("args"), session,
                ExecuteMode.SYNC);
        return scriptRunner.execute();
    }

    public Object executeAsync(JSONObject query, JSONObject uriParams, Session session) {
        checkRequiredArguments(query, "script", "args");
        JSONScriptRunner scriptRunner = new JSONScriptRunner(query.getString("script"), query.getJSONArray("args"), session,
                ExecuteMode.ASYNC);
        return scriptRunner.execute();
    }

    private void checkRequiredArguments(JSONObject query, String... args) {
        for (String arg : args) {
            if (!query.has(arg)) {
                throw new MissingCommandParametersException("Required parameter `" + arg + "` is missing", null);
            }
        }
    }

    public String getScreenShot(JSONObject query, JSONObject uriParams, Session session) throws AWTException, IOException {
        return Base64.encodeToString(session.getScreenShot(), false);
    }

}
