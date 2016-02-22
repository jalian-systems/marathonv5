package net.sourceforge.marathon.javafxrecorder.http;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.KeyStroke;

import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;
import net.sourceforge.marathon.javafxrecorder.JavaHook;
import net.sourceforge.marathon.javafxrecorder.component.RComponent;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import sun.net.www.protocol.http.HttpURLConnection;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HTTPRecorder implements IJSONRecorder {

    private int port;
    private String base;
    private String sessionID;
    private Timer clickTimer;
    private static Integer timerinterval;
    private Timer windowStateTimer;

    static {
        timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        if (timerinterval == null)
            timerinterval = Integer.valueOf(300);
    }

    public HTTPRecorder(int port) throws IOException {
        base = "http://localhost:" + port;
        this.port = port;
        createSession();
    }

    private void createSession() throws IOException {
        sessionID = postJSON("session", createTargetDetailsObject()).getString("sessionId");
        base = "http://localhost:" + port + "/session/" + sessionID;
    }

    private JSONObject postJSON(String path, JSONObject data) throws IOException {
        Response response = post(path, data.toString());
        if (response.getStatus() != Status.OK)
            throw new RuntimeException("Internal Error: Unable to create a session.");
        return new JSONObject(new JSONTokener(response.getData()));
    }

    private JSONObject getJSON(String path) throws IOException {
        Response response = get(path);
        if (response.getStatus() != Status.OK)
            throw new RuntimeException("Internal Error: Unable to create a session.");
        return new JSONObject(new JSONTokener(response.getData()));
    }

    private JSONObject createTargetDetailsObject() {
        JSONObject o = new JSONObject();
        o.put("driver", JavaHook.DRIVER);
        o.put("driver.version", JavaHook.DRIVER_VERSION);
        o.put("platform", JavaHook.PLATFORM);
        o.put("platform.version", JavaHook.PLATFORM_VERSION);
        o.put("os", JavaHook.OS);
        o.put("os.version", JavaHook.OS_VERSION);
        o.put("os.arch", JavaHook.OS_ARCH);
        return o;
    }

    @Override public void recordSelect(RComponent r, String state) {
        recordSelect2(r, state, false);
    }

    @Override public void recordClick(RComponent r, MouseEvent e) {
        recordClick2(r, e, false);
    }

    @Override public void recordClick2(final RComponent r, MouseEvent e, boolean withCellInfo) {
        final JSONObject event = new JSONObject();
        event.put("type", "click");
        event.put("button", e.getButton());
        event.put("clickCount", e.getClickCount());
        event.put("modifiersEx", e.getModifiersEx());
        event.put("x", e.getX());
        event.put("y", e.getY());
        if (withCellInfo)
            event.put("cellinfo", r.getCellInfo());
        final JSONObject o = new JSONObject();
        o.put("event", event);
        fill(r, o);
        if (e.getClickCount() == 1) {
            clickTimer = new Timer();
            clickTimer.schedule(new TimerTask() {
                @Override public void run() {
                    sendRecordMessage(o);
                }
            }, timerinterval.intValue());
        } else if (e.getClickCount() == 2) {
            if (clickTimer != null) {
                clickTimer.cancel();
                clickTimer = null;
            }
            sendRecordMessage(o);
        }
    }

    @Override public void recordRawMouseEvent(final RComponent r, MouseEvent e) {
        final JSONObject event = new JSONObject();
        event.put("type", "click_raw");
        event.put("button", e.getButton());
        event.put("clickCount", e.getClickCount());
        event.put("modifiersEx", e.getModifiersEx());
        event.put("x", e.getX());
        event.put("y", e.getY());
        final JSONObject o = new JSONObject();
        o.put("event", event);
        fill(r, o);
        if (e.getClickCount() == 1) {
            clickTimer = new Timer();
            clickTimer.schedule(new TimerTask() {
                @Override public void run() {
                    sendRecordMessage(o);
                }
            }, timerinterval.intValue());
        } else if (e.getClickCount() == 2) {
            if (clickTimer != null) {
                clickTimer.cancel();
                clickTimer = null;
            }
            sendRecordMessage(o);
        }
    }

    @Override public void recordRawKeyEvent(RComponent r, KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_META || keyCode == KeyEvent.VK_ALT || keyCode == KeyEvent.VK_CONTROL
                || keyCode == KeyEvent.VK_SHIFT)
            return;
        JSONObject event = new JSONObject();
        event.put("type", "key_raw");
        KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
        String kss = ks.toString();
        event.put("ks", kss);
        event.put("keyChar", e.getKeyChar());
        recordEvent(r, event);
    }

    @Override public void recordSelect2(RComponent r, String state, boolean withCellInfo) {
        JSONObject event = new JSONObject();
        event.put("type", "select");
        event.put("value", state);
        if (withCellInfo)
            event.put("cellinfo", r.getCellInfo());
        recordEvent(r, event);
    }

    private void recordEvent(RComponent r, JSONObject event) {
        JSONObject o = new JSONObject();
        o.put("event", event);
        fill(r, o);
        sendRecordMessage(o);
    }

    private void sendRecordMessage(JSONObject o) {
        try {
            postJSON("record", o);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void fill(RComponent r, JSONObject o) {
        o.put("request", "record-action");
        o.put("urp", r.findURP());
        o.put("attributes", r.findAttributes());
        o.put("container", r.findContextHeirarchy());
    }

    @Override public boolean isCreatingObjectMap() {
        return false;
    }

    @Override public void recordAction(RComponent r, String action, String property, Object value) {
        JSONObject event = new JSONObject();
        event.put("type", action);
        event.put("value", value);
        event.put("property", property);
        event.put("cellinfo", r.getCellInfo());
        recordEvent(r, event);
    }

    @Override public void recordSelectMenu(RComponent r, String selection) {
        JSONObject event = new JSONObject();
        event.put("type", "select_menu");
        event.put("value", selection);
        recordEvent(r, event);
    }

    @Override public void recordWindowClosing(RComponent r) {
        JSONObject event = new JSONObject();
        event.put("type", "window_closed");
        recordEvent(r, event);
    }

    @Override public void recordWindowState(final RComponent r, Rectangle bounds) {
        final JSONObject event = new JSONObject();
        event.put("type", "window_state");
        event.put("bounds", bounds.x + ":" + bounds.y + ":" + bounds.width + ":" + bounds.height);
        final JSONObject o = new JSONObject();
        o.put("event", event);
        if (windowStateTimer != null)
            windowStateTimer.cancel();
        windowStateTimer = new Timer();
        windowStateTimer.schedule(new TimerTask() {
            @Override public void run() {
                recordEvent(r, event);
                windowStateTimer = null;
            }
        }, 200);
    }

    @Override public JSONOMapConfig getObjectMapConfiguration() throws IOException {
        return new JSONOMapConfig(getJSON("object_map_configuration").getJSONObject("value"));
    }

    @Override public JSONObject getContextMenuTriggers() throws JSONException, IOException {
        return getJSON("context_menu_triggers").getJSONObject("value");
    }

    @Override public boolean isRawRecording() throws IOException {
        boolean r = getJSON("raw_recording").getBoolean("value");
        return r;
    }

    public Response get(final String path) throws IOException {
        return AccessController.doPrivileged(new PrivilegedAction<Response>() {
            @Override public Response run() {
                try {
                    URL obj = new URL(base + "/" + path);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // optional default is GET
                    con.setRequestMethod("GET");
                    con.setInstanceFollowRedirects(true);

                    // add request header
                    int responseCode = con.getResponseCode();
                    String contentType = con.getContentType();
                    InputStream data = con.getInputStream();
                    return NanoHTTPDnewChunkedResponse(findStatus(responseCode), contentType, data);
                } catch (Throwable t) {
                    return null;
                }
            }
        });
    }

    public Response post(final String path, final String postData) throws IOException {
        return AccessController.doPrivileged(new PrivilegedAction<Response>() {
            @Override public Response run() {
                try {
                    URL obj = new URL(base + "/" + path);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                    // add reuqest header
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    con.setInstanceFollowRedirects(true);

                    // Send post request
                    con.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(postData);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    String contentType = con.getContentType();
                    InputStream data = con.getInputStream();
                    return NanoHTTPDnewChunkedResponse(findStatus(responseCode), contentType, data);
                } catch (Throwable t) {
                    return null;
                }
            }
        });
    }

    private Status findStatus(int responseCode) {
        Status[] values = Status.values();
        for (Status status : values) {
            if (status.getRequestStatus() == responseCode)
                return status;
        }
        return Status.INTERNAL_ERROR;
    }

    private Response NanoHTTPDnewChunkedResponse(Status status, String mimeType, InputStream data) {
        return new Response(status, mimeType, data);
    }

    @Override public void recordMenuItem(RComponent r) {
        JSONObject event = new JSONObject();
        event.put("type", "menu_item");
        event.put("value", "");
        recordEvent(r, event);

    }

    @Override public void recordFocusedWindow(RComponent r) throws IOException {
        JSONObject o = new JSONObject();
        o.put("container", r.findContextHeirarchy((Container) r.getComponent()));
        postJSON("focused_window", o);
    }

}
