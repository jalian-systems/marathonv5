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
package net.sourceforge.marathon.javarecorder.ws;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.javaagent.KeysMap;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class WSRecorder implements IJSONRecorder {

    public static final Logger LOGGER = Logger.getLogger(WSRecorder.class.getName());

    private Timer clickTimer;
    private static Integer timerinterval;
    private Timer windowStateTimer;
    private WSClient wsClient;
    private JSONOMapConfig jsonOMapConfig;
    private JSONObject contextMenuTriggers;
    private boolean rawRecording;

    static {
        timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
        if (timerinterval == null) {
            timerinterval = Integer.valueOf(300);
        }
    }

    public WSRecorder(final int port) throws IOException, URISyntaxException {
        wsClient = AccessController.doPrivileged(new PrivilegedAction<WSClient>() {
            @Override public WSClient run() {
                try {
                    return new WSClient(port, WSRecorder.this);
                } catch (URISyntaxException e) {
                    return null;
                }
            }
        });
        if (wsClient == null) {
            throw new URISyntaxException("Invalid syntax!!!", "port = " + port);
        }
    }

    private void postJSON(String method, JSONObject data) throws IOException {
        post(method, data.toString());
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
        String mtext = buildModifiersText(e);
        event.put("modifiersEx", mtext);
        event.put("x", e.getX());
        event.put("y", e.getY());
        if (withCellInfo) {
            event.put("cellinfo", r.getCellInfo());
        }
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
        String mtext = buildModifiersText(e);
        event.put("modifiersEx", mtext);
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

    private String buildModifiersText(InputEvent e) {
        StringBuilder sb = new StringBuilder();
        if (e.isAltDown()) {
            sb.append("Alt+");
        }
        if (e.isControlDown()) {
            sb.append("Ctrl+");
        }
        if (e.isMetaDown()) {
            sb.append("Meta+");
        }
        if (e.isShiftDown()) {
            sb.append("Shift+");
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        String mtext = sb.toString();
        return mtext;
    }

    @Override public void recordRawKeyEvent(RComponent r, KeyEvent e) {
        JSONObject event = new JSONObject();
        event.put("type", "key_raw");
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_META || keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_ALT
                || keyCode == KeyEvent.VK_CONTROL) {
            return;
        }
        if ((e.isActionKey() || e.isControlDown() || e.isMetaDown() || e.isAltDown()) && e.getID() == KeyEvent.KEY_PRESSED) {
            String mtext = buildModifiersText(e);
            event.put("modifiersEx", mtext);
            KeysMap keysMap = KeysMap.findMap(e.getKeyCode());
            if (keysMap == KeysMap.NULL) {
                return;
            }
            String keyText;
            if (keysMap == null) {
                keyText = KeyEvent.getKeyText(e.getKeyCode());
            } else {
                keyText = keysMap.toString();
            }
            event.put("keyCode", keyText);
        } else if (e.getID() == KeyEvent.KEY_TYPED && !e.isControlDown()) {
            if (Character.isISOControl(e.getKeyChar()) && hasMapping(e.getKeyChar())) {
                event.put("keyChar", getMapping(e.getKeyChar()));
            } else {
                event.put("keyChar", "" + e.getKeyChar());
            }
        } else {
            return;
        }
        recordEvent(r, event);
    }

    private String getMapping(char keyChar) {
        return controlKeyMappings.get(keyChar);
    }

    private boolean hasMapping(char keyChar) {
        return controlKeyMappings.get(keyChar) != null;
    }

    private static final Map<Character, String> controlKeyMappings = new HashMap<Character, String>();
    private static final Logger logger = Logger.getLogger(WSRecorder.class.getName());

    static {
        controlKeyMappings.put('\n', "Enter");
        controlKeyMappings.put('\t', "Tab");
        controlKeyMappings.put('\b', "Backspace");
        controlKeyMappings.put('\033', "Escape");
        controlKeyMappings.put('\177', "Delete");
    }

    @Override public void recordSelect2(RComponent r, String state, boolean withCellInfo) {
        JSONObject event = new JSONObject();
        event.put("type", "select");
        event.put("value", state);
        if (withCellInfo) {
            event.put("cellinfo", r.getCellInfo());
        }
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
        if (windowStateTimer != null) {
            windowStateTimer.cancel();
        }
        windowStateTimer = new Timer();
        windowStateTimer.schedule(new TimerTask() {
            @Override public void run() {
                recordEvent(r, event);
                windowStateTimer = null;
            }
        }, 200);
    }

    private final Object jsonOMapConfigLock = new Object();

    @Override public JSONOMapConfig getObjectMapConfiguration() {
        if (jsonOMapConfig == null) {
            synchronized (jsonOMapConfigLock) {
                if (jsonOMapConfig != null) {
                    return jsonOMapConfig;
                }
                try {
                    jsonOMapConfigLock.wait(10000);
                } catch (InterruptedException e) {
                }
            }
        }
        return jsonOMapConfig;
    }

    public void setObjectMapConfig(JSONObject o) {
        jsonOMapConfig = new JSONOMapConfig(o);
        synchronized (jsonOMapConfigLock) {
            jsonOMapConfigLock.notifyAll();
        }
    }

    private final Object contextMenuTriggersLock = new Object();

    private boolean paused;

    @Override public JSONObject getContextMenuTriggers() {
        if (contextMenuTriggers == null) {
            synchronized (contextMenuTriggersLock) {
                if (contextMenuTriggers != null) {
                    return contextMenuTriggers;
                }
                try {
                    contextMenuTriggersLock.wait(10000);
                } catch (InterruptedException e) {
                }
            }
        }
        return contextMenuTriggers;
    }

    public void setContextMenuTriggers(JSONObject contextMenuTriggers) {
        this.contextMenuTriggers = contextMenuTriggers;
        synchronized (contextMenuTriggersLock) {
            contextMenuTriggersLock.notifyAll();
        }
    }

    @Override public boolean isRawRecording() {
        return rawRecording;
    }

    public void setRawRecording(JSONObject o) {
        rawRecording = o.getBoolean("value");
    }

    public void setRecordingPause(JSONObject o) {
        paused = o.getBoolean("value");
    }

    @Override public boolean isPaused() {
        return paused;
    }

    @Override public void recordMenuItem(RComponent r) {
        JSONObject event = new JSONObject();
        event.put("type", "menu_item");
        event.put("value", "");
        recordEvent(r, event);

    }

    @Override public void recordFocusedWindow(RComponent r) throws IOException {
        JSONObject o = new JSONObject();
        JSONObject container = r.findContextHeirarchy((Container) r.getComponent());
        if (container != null) {
            o.put("container", container);
            postJSON("focusedWindow", o);
        }
    }

    public void post(final String method) throws IOException {
        post(method, null);
    }

    public void post(final String method, final String postData) throws IOException {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override public Object run() {
                wsClient.post(method, postData);
                return null;
            }
        });
    }

    public void onOpen() {
        Logger.getLogger(WSClient.class.getName()).info("Connected to server");
    }

    public void onMessage(String message) {
        JSONObject o = new JSONObject(message);
        String method = o.getString("method");
        String data = o.has("data") ? o.getString("data") : null;
        serve(method, data);
    }

    public void serve(String method, String data) {
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
        serve_internal(method, jsonQuery);
    }

    public void serve_internal(String methodName, JSONObject jsonQuery) {
        java.lang.reflect.Method method = getMethod(methodName);
        if (method != null) {
            handleRoute(method, jsonQuery);
        } else if (method == null) {
            logger.warning("Unknown method `" + methodName + "` called with " + jsonQuery);
        }
    }

    private void handleRoute(java.lang.reflect.Method method, JSONObject query) {
        try {
            invoke(method, query);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning("Unable to execute method `" + method.getName() + "` with data " + query);
        }
    }

    public void invoke(java.lang.reflect.Method method, JSONObject query) {
        try {
            method.invoke(this, query);
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
        java.lang.reflect.Method[] methods = WSRecorder.class.getMethods();
        for (java.lang.reflect.Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        return null;
    }

    @Override public void recordFileDialog(String state) {
        JSONObject event = new JSONObject();
        event.put("type", "select_file_dialog");
        event.put("value", state);
        JSONObject o = new JSONObject();
        o.put("event", event);
        o.put("container", new JSONObject());
        sendRecordMessage(o);
    }

}
