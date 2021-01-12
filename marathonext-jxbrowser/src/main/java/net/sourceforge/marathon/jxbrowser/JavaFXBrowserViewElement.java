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
package net.sourceforge.marathon.jxbrowser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSObject;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.scene.Node;
import net.sourceforge.marathon.javafxagent.EventQueueWait;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.json.JSONObject;

public class JavaFXBrowserViewElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXBrowserViewElement.class.getName());

    private static final String script;

    static {
        InputStream r = JavaFXBrowserViewElement.class.getResourceAsStream("browserview_player.js");
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = r.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String script1;
        try {
            script1 = result.toString(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            script1 = "";
            e.printStackTrace();
        }
        script = script1;
    }

    public JavaFXBrowserViewElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
        init(component);
    }

    public static void init(Node source) {
        BrowserView webview = (BrowserView) source;
        if (webview.getProperties().get("marathon_player_installed") == null) {
            webview.getProperties().put("marathon_player_installed", Boolean.TRUE);
            Browser webEngine = webview.getBrowser();
            loadScript(webview, webEngine, -1);
            if (webEngine.getDocument() != null) {
                List<Long> framesIds = webEngine.getFramesIds();
                for (Long frameId : framesIds) {
                    loadScript(webview, webEngine, frameId);
                }
            }
            webEngine.addLoadListener(new LoadListener() {
                @Override
                public void onStartLoadingFrame(StartLoadingEvent arg0) {
                }

                @Override
                public void onProvisionalLoadingFrame(ProvisionalLoadingEvent arg0) {
                    webview.getProperties().remove("player" + arg0.getFrameId());
                    webview.getProperties().remove("document" + arg0.getFrameId());
                }

                @Override
                public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
                }

                @Override
                public void onFailLoadingFrame(FailLoadingEvent arg0) {
                }

                @Override
                public void onDocumentLoadedInMainFrame(LoadEvent arg0) {
                }

                @Override
                public void onDocumentLoadedInFrame(FrameLoadEvent arg0) {
                    if (arg0.isMainFrame())
                        loadScript(webview, webEngine, -1);
                    else
                        loadScript(webview, webEngine, arg0.getFrameId());
                }
            });
        }
    }

    private static void loadScript(BrowserView webview, Browser webEngine, long frameId) {
        try {
            webEngine.executeJavaScriptAndReturnValue(frameId, script);
            webview.getProperties().put("player" + frameId, webEngine.executeJavaScriptAndReturnValue(frameId, "$marathon_player"));
            webview.getProperties().put("document" + frameId, webEngine.executeJavaScriptAndReturnValue(frameId, "document"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("select-by-properties")) {
            try {
                JSONObject o = new JSONObject((String) params[0]);
                return selectByProperties(new ArrayList<IJavaFXElement>(), o);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        return super.getByPseudoElement(selector, params);
    }

    private static final Pattern SELECTOR_WITH_FRAME_ID = Pattern.compile("^(-?\\d+):(.*)$");

    private List<IJavaFXElement> selectByProperties(ArrayList<IJavaFXElement> arrayList, JSONObject o) {
        List<IJavaFXElement> r = new ArrayList<>();
        if (o.has("select")) {
            String selector = o.getString("select");
            Matcher matcher = SELECTOR_WITH_FRAME_ID.matcher(selector);
            long frameId = -1;
            if (matcher.matches()) {
                frameId = Long.parseLong(matcher.group(1));
                selector = matcher.group(2);
            }
            if (documentHasSelector(selector, frameId))
                r.add(new JavaFXBrowserViewItem(this, selector, frameId));
        }
        return r;
    }

    private boolean documentHasSelector(String selector, long frameId) {
        return EventQueueWait.exec(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    return ((BrowserView) getComponent()).getBrowser().getDocument() != null
                            && getComponent().getProperties().containsKey("player" + frameId) && hasSelector();
                } catch (Throwable t) {
                    return false;
                }
            }

            private boolean hasSelector() {
                JSObject player = ((JSValue) getComponent().getProperties().get("player" + frameId)).asObject();
                return player.getProperty("exists").asFunction().invoke(player, selector).getBooleanValue();
            }
        });
    }

    public void click(String selector, long frameId) {
        EventQueueWait.exec(new Runnable() {

            @Override
            public void run() {
                JSObject player = ((JSValue) getComponent().getProperties().get("player" + frameId)).asObject();
                player.getProperty("click").asFunction().invoke(player, selector);
            }
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public boolean select(String selector, String value, long frameId) {
        Boolean selected = EventQueueWait.exec(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                JSObject player = ((JSValue) getComponent().getProperties().get("player" + frameId)).asObject();
                return player.getProperty("select").asFunction().invoke(player, selector, value).getBooleanValue();
            }
        });
        try {
            if (selected)
                Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        return selected;
    }

    public static String getText(Node component, String selector, long frameId) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Object player = component.getProperties().get("player" + frameId);
                JSObject doc = ((JSValue) player).asObject();
                JSValue invoke = doc.getProperty("text").asFunction().invoke(doc, selector);
                return invoke.isNull() ? null : invoke.getStringValue();
            }
        });
    }

    public static String getLabeledBy(Node component, String selector, long frameId) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override
            public String call() throws Exception {
                JSObject doc = ((JSValue) component.getProperties().get("player" + frameId)).asObject();
                JSValue invoke = doc.getProperty("label").asFunction().invoke(doc, selector);
                return invoke.isNull() ? null : invoke.getStringValue();
            }
        });
    }

    public static String getValue(Node component, String selector, long frameId) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override
            public String call() throws Exception {
                JSObject doc = ((JSValue) component.getProperties().get("player" + frameId)).asObject();
                JSValue invoke = doc.getProperty("value").asFunction().invoke(doc, selector);
                return invoke.isNull() ? null : invoke.getStringValue();
            }
        });
    }

    public static Map<String, String> getAttributes(Node component, String selector, long frameId) {
        JSObject player = ((JSValue) component.getProperties().get("player" + frameId)).asObject();
        JSValue attributes = player.getProperty("attributes").asFunction().invoke(player, selector);
        String r = (String) attributes.getStringValue();
        JSONObject o = new JSONObject(r);
        String[] names = JSONObject.getNames(o);
        HashMap<String, String> rm = new HashMap<>();
        for (String name : names) {
            rm.put(name, o.getString(name));
        }
        return rm;
    }

    private static boolean initializedRemoteDebug = false;

    public static void initRemoteDebug() {
        LOGGER.warning("In initRemoteDebug. initializedRemoteDebug = " + initializedRemoteDebug);
        if (initializedRemoteDebug)
            return;
        initializedRemoteDebug = true;
        List<String> chromiumSwitches = BrowserPreferences.getChromiumSwitches();
        Optional<String> findFirst = chromiumSwitches.stream().filter(new Predicate<String>() {
            @Override
            public boolean test(String t) {
                return t.startsWith("--remote-debugging-port=");
            }
        }).findFirst();
        if (findFirst.isPresent()) {
            int port = Integer.parseInt(findFirst.get().substring("--remote-debugging-port=".length()).trim());
            System.setProperty("jxbrowser-remote-debugging-port", port + "");
            System.out.println("jxbrowser-remote-debugging-port(existing)=" + port);
        } else {
            int port = findFreePort();
            chromiumSwitches.add("--remote-debugging-port=" + port);
            BrowserPreferences.setChromiumSwitches(chromiumSwitches.toArray(new String[chromiumSwitches.size()]));
            System.setProperty("jxbrowser-remote-debugging-port", port + "");
            System.out.println("jxbrowser-remote-debugging-port(new)=" + port);
        }
    }

    private static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new RuntimeException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        initRemoteDebug();
    }

}
