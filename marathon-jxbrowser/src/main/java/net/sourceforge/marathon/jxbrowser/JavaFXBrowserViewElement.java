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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.teamdev.jxbrowser.chromium.Browser;
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
            if (webEngine.getDocument() != null) {
                List<Long> framesIds = webEngine.getFramesIds();
                boolean mainFrame = true;
                for (Long frameId : framesIds) {
                    loadScript(webview, webEngine, frameId, mainFrame);
                    mainFrame = false;
                }
            }
            webEngine.addLoadListener(new LoadListener() {
                @Override public void onStartLoadingFrame(StartLoadingEvent arg0) {
                }

                @Override public void onProvisionalLoadingFrame(ProvisionalLoadingEvent arg0) {
                }

                @Override public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
                }

                @Override public void onFailLoadingFrame(FailLoadingEvent arg0) {
                }

                @Override public void onDocumentLoadedInMainFrame(LoadEvent arg0) {
                }

                @Override public void onDocumentLoadedInFrame(FrameLoadEvent arg0) {
                    loadScript(webview, webEngine, arg0.getFrameId(), arg0.isMainFrame());
                }
            });
        }
    }

    private static void loadScript(BrowserView webview, Browser webEngine, long frameId, boolean mainFrame) {
        webEngine.executeJavaScript(frameId, script);
        webview.getProperties().put("player" + frameId, webEngine.executeJavaScriptAndReturnValue(frameId, "$marathon_player"));
        webview.getProperties().put("document" + frameId, webEngine.executeJavaScriptAndReturnValue(frameId, "document"));
        if (mainFrame) {
            frameId = -1;
            webview.getProperties().put("player" + frameId, webEngine.executeJavaScriptAndReturnValue(frameId, "$marathon_player"));
            webview.getProperties().put("document" + frameId, webEngine.executeJavaScriptAndReturnValue(frameId, "document"));
        }
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
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

    private List<IJavaFXElement> selectByProperties(ArrayList<IJavaFXElement> arrayList, JSONObject o) {
        List<IJavaFXElement> r = new ArrayList<>();
        if (o.has("select")) {
            String selectorWithFrameId = o.getString("select");
            int index = selectorWithFrameId.indexOf(':');
            String[] parts = new String[] { selectorWithFrameId.substring(0, index), selectorWithFrameId.substring(index + 1) };
            long frameId = Long.parseLong(parts[0]);
            String selector = parts[1];
            if (documentHasSelector(selector, frameId))
                r.add(new JavaFXBrowserViewItem(this, selector, frameId));
        }
        return r;
    }

    private boolean documentHasSelector(String selector, long frameId) {
        return EventQueueWait.exec(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return ((BrowserView) getComponent()).getBrowser().getDocument() != null
                        && getComponent().getProperties().containsKey("player" + frameId) && hasSelector();
            }

            private boolean hasSelector() {
                JSObject doc = ((JSValue) getComponent().getProperties().get("document" + frameId)).asObject();
                JSValue invoke = doc.getProperty("querySelector").asFunction().invoke(doc, selector);
                return !invoke.isNull();
            }
        });
    }

    public void click(String selector, long frameId) {
        EventQueueWait.exec(new Runnable() {

            @Override public void run() {
                JSObject doc = ((JSValue) getComponent().getProperties().get("player" + frameId)).asObject();
                doc.getProperty("click").asFunction().invoke(doc, selector);
            }
        });
    }

    public boolean select(String selector, String value, long frameId) {
        return EventQueueWait.exec(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                JSObject doc = ((JSValue) getComponent().getProperties().get("player" + frameId)).asObject();
                return doc.getProperty("select").asFunction().invoke(doc, selector, value).getBooleanValue();
            }
        });
    }

    public static String getText(Node component, String selector, long frameId) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                JSObject doc = ((JSValue) component.getProperties().get("player" + frameId)).asObject();
                JSValue invoke = doc.getProperty("text").asFunction().invoke(doc, selector);
                return invoke.isNull() ? null : invoke.getStringValue();
            }
        });
    }

    public static String getLabeledBy(Node component, String selector, long frameId) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                JSObject doc = ((JSValue) component.getProperties().get("player" + frameId)).asObject();
                JSValue invoke = doc.getProperty("label").asFunction().invoke(doc, selector);
                return invoke.isNull() ? null : invoke.getStringValue();
            }
        });
    }

    public static String getValue(Node component, String selector, long frameId) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                JSObject doc = ((JSValue) component.getProperties().get("player" + frameId)).asObject();
                JSValue invoke = doc.getProperty("value").asFunction().invoke(doc, selector);
                return invoke.isNull() ? null : invoke.getStringValue();
            }
        });
    }

    public static Map<String, String> getAttributes(Node component, String selector, long frameId) {
        JSObject doc = ((JSValue) component.getProperties().get("player" + frameId)).asObject();
        JSValue invoke = doc.getProperty("attributes").asFunction().invoke(doc, selector);
        String r = (String) invoke.getStringValue();
        JSONObject o = new JSONObject(r);
        String[] names = JSONObject.getNames(o);
        HashMap<String, String> rm = new HashMap<>();
        for (String name : names) {
            rm.put(name, o.getString(name));
        }
        return rm;
    }
}
