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
package net.sourceforge.marathon.javafxrecorder.ws;

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

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxagent.KeysMap;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;

public class WSRecorder implements IJSONRecorder {

    public static final Logger LOGGER = Logger.getLogger(WSRecorder.class.getName());

    private Timer clickTimer;
    private static Integer timerinterval;
    private Timer windowStateTimer;
    private WSClient wsClient;
    private JSONOMapConfig jsonOMapConfig;
    private boolean rawRecording;
    private JSONObject contextMenuTriggers;

    private boolean paused;

    static {
        timerinterval = Integer.valueOf(300);
    }

    public WSRecorder(int port) throws IOException, URISyntaxException {
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

    @Override public void recordSelect(RFXComponent r, String state) {
        recordSelect2(r, state, false);
    }

    @Override public void recordClick(RFXComponent r, MouseEvent e) {
        recordClick2(r, e, false);
    }

    @Override public void recordClick2(final RFXComponent r, MouseEvent e, boolean withCellInfo) {
        final JSONObject event = new JSONObject();
        event.put("type", "click");
        int button = e.getButton() == MouseButton.PRIMARY ? java.awt.event.MouseEvent.BUTTON1 : java.awt.event.MouseEvent.BUTTON3;
        event.put("button", button);
        event.put("clickCount", e.getClickCount());
        event.put("modifiersEx", buildModifiersText(e));
        double x = e.getX();
        double y = e.getY();
        Node source = (Node) e.getSource();
        Node target = r.getComponent();
        Point2D sts = source.localToScreen(new Point2D(0, 0));
        Point2D tts = target.localToScreen(new Point2D(0, 0));
        x = e.getX() - tts.getX() + sts.getX();
        y = e.getY() - tts.getY() + sts.getY();
        event.put("x", x);
        event.put("y", y);
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

    @Override public void recordRawMouseEvent(final RFXComponent r, MouseEvent e) {
        final JSONObject event = new JSONObject();
        event.put("type", "click_raw");
        int button = e.getButton() == MouseButton.PRIMARY ? java.awt.event.MouseEvent.BUTTON1 : java.awt.event.MouseEvent.BUTTON3;
        event.put("button", button);
        event.put("clickCount", e.getClickCount());
        event.put("modifiersEx", buildModifiersText(e));
        Node source = (Node) e.getSource();
        Node target = r.getComponent();
        Point2D sts = source.localToScene(new Point2D(e.getX(), e.getY()));
        Point2D tts = target.sceneToLocal(sts);
        event.put("x", tts.getX());
        event.put("y", tts.getY());
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

    private String buildModifiersText(MouseEvent e) {
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

    private String buildModifiersText(KeyEvent e) {
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

    @Override public void recordRawKeyEvent(RFXComponent r, KeyEvent e) {
        JSONObject event = new JSONObject();
        event.put("type", "key_raw");
        KeyCode keyCode = e.getCode();
        if (keyCode.isModifierKey()) {
            return;
        }
        if ((keyCode.isFunctionKey() || keyCode.isArrowKey() || keyCode.isKeypadKey() || keyCode.isMediaKey()
                || keyCode.isNavigationKey() || e.isControlDown() || e.isMetaDown() || e.isAltDown()
                || needManualRecording(keyCode)) && e.getEventType() == KeyEvent.KEY_PRESSED) {
            String mtext = buildModifiersText(e);
            event.put("modifiersEx", mtext);
            KeysMap keysMap = KeysMap.findMap(e.getCode());
            if (keysMap == KeysMap.NULL) {
                return;
            }
            String keyText;
            if (keysMap == null) {
                keyText = e.getText();
            } else {
                keyText = keysMap.toString();
            }
            event.put("keyCode", keyText);
        } else if (e.getEventType() == KeyEvent.KEY_TYPED && !e.isControlDown() && !needManualRecording(keyCode)) {
            char[] cs = e.getCharacter().toCharArray();
            if (cs.length == 0) {
                return;
            }
            for (char c : cs) {
                if (Character.isISOControl(c) && hasMapping(c)) {
                    event.put("keyChar", getMapping(c));
                } else {
                    event.put("keyChar", "" + c);
                }
            }
        } else {
            return;
        }
        recordEvent(r, event);
    }

    private boolean needManualRecording(KeyCode keyCode) {
        return keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE || keyCode == KeyCode.ESCAPE || keyCode == KeyCode.SPACE;
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

    @Override public void recordSelect2(RFXComponent r, String state, boolean withCellInfo) {
        JSONObject event = new JSONObject();
        event.put("type", "select");
        event.put("value", state);
        if (withCellInfo) {
            event.put("cellinfo", r.getCellInfo());
        }
        recordEvent(r, event);
    }

    private void recordEvent(RFXComponent r, JSONObject event) {
        JSONObject o = new JSONObject();
        o.put("event", event);
        fill(r, o);
        sendRecordMessage(o);
    }

    @Override public void recordFileChooser(String state) {
        JSONObject event = new JSONObject();
        event.put("type", "select_file_chooser");
        event.put("value", state);
        JSONObject o = new JSONObject();
        o.put("event", event);
        sendRecordMessage(o);
    }

    @Override public void recordFolderChooser(String state) {
        JSONObject event = new JSONObject();
        event.put("type", "select_folder_chooser");
        event.put("value", state);
        JSONObject o = new JSONObject();
        o.put("event", event);
        sendRecordMessage(o);
    }

    @Override public void recordSelectMenu(RFXComponent r, String menuType, String selection) {
        JSONObject event = new JSONObject();
        event.put("type", "select_fx_menu");
        event.put("value", selection);
        event.put("menu_type", menuType);
        JSONObject o = new JSONObject();
        o.put("event", event);
        o.put("container", r.findContextHeirarchy());
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

    private void fill(RFXComponent r, JSONObject o) {
        o.put("request", "record-action");
        o.put("urp", r.findURP());
        o.put("attributes", r.findAttributes());
        o.put("container", r.findContextHeirarchy());
    }

    @Override public boolean isCreatingObjectMap() {
        return false;
    }

    @Override public void recordAction(RFXComponent r, String action, String property, Object value) {
        JSONObject event = new JSONObject();
        event.put("type", action);
        event.put("value", value);
        event.put("property", property);
        event.put("cellinfo", r.getCellInfo());
        recordEvent(r, event);
    }

    @Override public void recordWindowClosing(RFXComponent r) {
        JSONObject event = new JSONObject();
        event.put("type", "window_closed");
        recordEvent(r, event);
    }

    @Override public void recordWindowState(final RFXComponent r, Rectangle2D bounds) {
        final JSONObject event = new JSONObject();
        event.put("type", "window_state");
        event.put("bounds", bounds.getMinX() + ":" + bounds.getMinY() + ":" + bounds.getWidth() + ":" + bounds.getHeight());
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

    @Override public void recordMenuItem(RFXComponent r) {
        JSONObject event = new JSONObject();
        event.put("type", "menu_item");
        event.put("value", "");
        recordEvent(r, event);

    }

    @Override public void recordFocusedWindow(RFXComponent r) throws IOException {
        JSONObject o = new JSONObject();
        o.put("container", r.findContextHeirarchy((Parent) r.getComponent()));
        postJSON("focusedWindow", o);
    }

    @Override public void recordWindowClosing(String title) {
        JSONObject event = new JSONObject();
        event.put("type", "window_closing_with_title");
        event.put("value", title);
        JSONObject o = new JSONObject();
        o.put("event", event);
        sendRecordMessage(o);
    }

    @Override public void recordWindowState(String title, int x, int y, int width, int height) {
        JSONObject event = new JSONObject();
        event.put("type", "window_state_with_title");
        JSONObject value = new JSONObject();
        value.put("title", title);
        value.put("x", x);
        value.put("y", y);
        value.put("width", width);
        value.put("height", height);
        event.put("value", value);
        JSONObject o = new JSONObject();
        o.put("event", event);
        sendRecordMessage(o);
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

    private void postJSON(String method, JSONObject data) throws IOException {
        post(method, data.toString());
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

}
