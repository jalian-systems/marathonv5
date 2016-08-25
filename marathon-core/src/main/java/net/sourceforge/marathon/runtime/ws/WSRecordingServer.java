package net.sourceforge.marathon.runtime.ws;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;

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
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class WSRecordingServer extends WebSocketServer implements IRecordingServer {

    private static final Logger logger = Logger.getLogger(WSRecordingServer.class.getName());

    private static class MenuItemScriptElement implements IScriptElement {
        private static final long serialVersionUID = 1L;
        private String type;
        private String value;
        private WindowId windowId;

        public MenuItemScriptElement(JSONObject o, WindowId windowId) {
            this.windowId = windowId;
            type = o.getString("menu_type");
            value = o.getString("value");
        }

        @Override public String toScriptCode() {
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("select_fx_menu", type, value);
        }

        @Override public WindowId getWindowId() {
            return windowId;
        }

        @Override public IScriptElement getUndoElement() {
            return null;
        }

        @Override public boolean isUndo() {
            return false;
        }

    }

    private static final class CommentScriptElement implements IScriptElement {
        private static final long serialVersionUID = 1L;
        private String comment;

        public CommentScriptElement(String comment) {
            this.comment = comment;
        }

        @Override public String toScriptCode() {
            return Indent.getIndent() + "# " + comment + "\n";
        }

        @Override public WindowId getWindowId() {
            return null;
        }

        @Override public IScriptElement getUndoElement() {
            return null;
        }

        @Override public boolean isUndo() {
            return false;
        }

    }

    private static final class ChooserScriptElement implements IScriptElement {
        private static final long serialVersionUID = 1L;
        private String type;
        private String value;

        public ChooserScriptElement(JSONObject o) {
            type = o.getString("type").equals("select_file_chooser") ? "#filechooser" : "#folderchooser";
            value = o.getString("value");
        }

        @Override public String toScriptCode() {
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("select_file_chooser", type, value);
        }

        @Override public WindowId getWindowId() {
            return null;
        }

        @Override public IScriptElement getUndoElement() {
            return null;
        }

        @Override public boolean isUndo() {
            return false;
        }

    }

    private static final class WindowClosingScriptElement implements IScriptElement {
        private static final long serialVersionUID = 1L;
        private String title;

        public WindowClosingScriptElement(JSONObject o) {
            title = o.getString("value");
        }

        @Override public String toScriptCode() {
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("window_closed", title);
        }

        @Override public WindowId getWindowId() {
            return null;
        }

        @Override public IScriptElement getUndoElement() {
            return null;
        }

        @Override public boolean isUndo() {
            return false;
        }

    }

    private static final class WindowStateScriptElement implements IScriptElement {
        private static final long serialVersionUID = 1L;
        private String title;
        private int x;
        private int y;
        private int width;
        private int height;

        public WindowStateScriptElement(JSONObject o) {
            JSONObject value = o.getJSONObject("value");
            title = value.getString("title");
            x = value.getInt("x");
            y = value.getInt("y");
            width = value.getInt("width");
            height = value.getInt("height");
        }

        @Override public String toScriptCode() {
            String bounds = "" + x + ":" + y + ":" + width + ":" + height;
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("window_changed", bounds);
        }

        @Override public WindowId getWindowId() {
            return new WindowId(title, null, "");
        }

        @Override public IScriptElement getUndoElement() {
            return null;
        }

        @Override public boolean isUndo() {
            return false;
        }

    }

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

    private IRecorder recorder;
    INamingStrategy ns = NamingStrategyFactory.get();
    private String java_version;
    private boolean javaVersionRecorded = false;
    private boolean paused;
    private WindowId focusedWindowId;

    public WSRecordingServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override public void start() {
        super.start();
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
        JSONObject o = new JSONObject();
        o.put("value", Boolean.toString(rawRecording));
        Collection<WebSocket> cs = connections();
        for (WebSocket webSocket : cs) {
            post(webSocket, "setRawRecording", o.toString());
        }
    }

    public JSONObject getObjectMapConfiguration() {
        ObjectMapConfiguration omc = new ObjectMapConfiguration();
        try {
            omc.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject();
        }
        BeanToJsonConverter btjc = new BeanToJsonConverter();
        String s = btjc.convert(omc);
        return new JSONObject(s);
    }

    public JSONObject getContextMenuTriggers() {
        return new JSONObject().put("contextMenuKeyModifiers", ContextMenuTriggers.getContextMenuKeyModifiers())
                .put("contextMenuKey", ContextMenuTriggers.getContextMenuKeyCode())
                .put("menuModifiers", ContextMenuTriggers.getContextMenuModifiers());
    }

    public JSONObject record(WebSocket conn, JSONObject query) throws IOException {
        logger.info("WSRecordingServer.record(" + query.toString(2) + ")");
        if (paused || recorder == null)
            return new JSONObject();
        try {
            String name = null;
            try {
                name = query.getJSONObject("attributes").getString("suggestedName");
            } catch (JSONException e) {
            }
            JSONObject eventObject = query.getJSONObject("event");
            String type = eventObject.getString("type");
            if (type.equals("comment")) {
                recorder.record(new CommentScriptElement(eventObject.getString("comment")));
                return new JSONObject();
            }
            WindowId windowId = createWindowId(query.getJSONObject("container"));
            if (type.equals("select_file_chooser") || type.equals("select_folder_chooser")) {
                recorder.record(new ChooserScriptElement(query.getJSONObject("event")));
                return new JSONObject();
            } else if (type.equals("select_fx_menu")) {
                recorder.record(new MenuItemScriptElement(query.getJSONObject("event"), windowId));
                return new JSONObject();
            }
            if (type.equals("window_closing_with_title")) {
                recorder.record(new WindowClosingScriptElement(query.getJSONObject("event")));
                return new JSONObject();
            }
            if (type.equals("window_state_with_title")) {
                recorder.record(new WindowStateScriptElement(query.getJSONObject("event")));
                return new JSONObject();
            }
            String cName = ns.getName(query, name);
            recorder.record(new JSONScriptElement(windowId, cName, query.getJSONObject("event")));
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return new JSONObject();
    }

    public JSONObject focusedWindow(WebSocket conn, JSONObject query) throws IOException, JSONException, ObjectMapException {
        focusedWindowId = createWindowId(query.getJSONObject("container"));
        return new JSONObject();
    }

    private WindowId createWindowId(final JSONObject container) throws JSONException, ObjectMapException {
        WindowId parent = container.has("container") ? createWindowId(container.getJSONObject("container")) : null;
        return new WindowId(createTitle(container), parent, container.getString("container_type"));
    }

    private String createTitle(JSONObject container) throws JSONException, ObjectMapException {
        return createRawTitle(container);
    }

    private String createRawTitle(JSONObject container) throws JSONException, ObjectMapException {
        if (!"window".equals(container.getString("container_type"))) {
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

    @Override public void pauseRecording() {
        paused = true;
    }

    @Override public void resumeRecording() {
        paused = false;
    }

    public WindowId getFocusedWindowId() {
        return focusedWindowId;
    }

    @Override public void onOpen(WebSocket conn, ClientHandshake handshake) {
        post(conn, "setContextMenuTriggers", getContextMenuTriggers().toString());
        post(conn, "setObjectMapConfig", getObjectMapConfiguration().toString());
    }

    @Override public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override public void onMessage(WebSocket conn, String message) {
        JSONObject o = new JSONObject(message);
        String method = o.getString("method");
        String data = o.has("data") ? o.getString("data") : null;
        serve(method, conn, data);
    }

    @Override public void onError(WebSocket conn, Exception ex) {
    }

    public void serve(String method, WebSocket conn, String data) {
        JSONObject jsonQuery = null;
        if (data != null) {
            try {
                jsonQuery = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
                logger.warning("Could not convert `" + data + "` to JSON");
                return;
            }
        }
        serve_internal(method, conn, jsonQuery);
    }

    public void serve_internal(String methodName, WebSocket conn, JSONObject jsonQuery) {
        java.lang.reflect.Method method = getMethod(methodName);
        if (method != null) {
            handleRoute(method, conn, jsonQuery);
        } else if (method == null) {
            logger.warning("Unknown method `" + methodName + "` called with " + jsonQuery);
        }
    }

    private void handleRoute(java.lang.reflect.Method method, WebSocket conn, JSONObject query) {
        try {
            invoke(method, conn, query);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning("Unable to execute method `" + method.getName() + "` with data " + query);
        }
    }

    public void invoke(java.lang.reflect.Method method, WebSocket conn, JSONObject query) {
        try {
            method.invoke(this, conn, query);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw new RuntimeException(cause.getMessage(), cause);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static java.lang.reflect.Method getMethod(String name) {
        java.lang.reflect.Method[] methods = WSRecordingServer.class.getMethods();
        for (java.lang.reflect.Method method : methods) {
            if (method.getName().equals(name))
                return method;
        }
        return null;
    }

    public void post(WebSocket conn, String method, String data) {
        JSONObject o = new JSONObject();
        o.put("method", method);
        o.put("data", data);
        conn.send(o.toString());
    }

    @Override public boolean isRecording() {
        return recorder != null;
    }

    public void reloadScript(WebSocket conn, JSONObject query) {
    }
}
