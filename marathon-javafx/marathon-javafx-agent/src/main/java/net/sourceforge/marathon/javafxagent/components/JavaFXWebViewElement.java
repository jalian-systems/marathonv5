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
package net.sourceforge.marathon.javafxagent.components;

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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.sourceforge.marathon.javafxagent.EventQueueWait;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import netscape.javascript.JSObject;

public class JavaFXWebViewElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXWebViewElement.class.getName());

    private static final String script;

    static {
        InputStream r = JavaFXWebViewElement.class.getResourceAsStream("webview.js");
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

    public JavaFXWebViewElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
        init(component);
    }

    public static void init(Node source) {
        WebView webview = (WebView) source;
        if (webview.getProperties().get("marathon_player_installed") == null) {
            webview.getProperties().put("marathon_player_installed", Boolean.TRUE);
            WebEngine webEngine = webview.getEngine();
            if (webEngine.getLoadWorker().stateProperty().get() == State.SUCCEEDED) {
                loadScript(webview, webEngine);
            }
            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                @Override public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
                    if (newState == State.SUCCEEDED) {
                        loadScript(webview, webEngine);
                    }
                }
            });
        }
    }

    private static void loadScript(WebView webview, WebEngine webEngine) {
        webEngine.executeScript(script);
        webview.getProperties().put("player", webEngine.executeScript("$marathon_player"));
        webview.getProperties().put("document", webEngine.executeScript("document"));
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
            if (documentHasSelector(o.getString("select")))
                r.add(new JavaFXWebViewItem(this, o.getString("select")));
        }
        return r;
    }

    private boolean documentHasSelector(String selector) {
        return EventQueueWait.exec(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return ((WebView) getComponent()).getEngine().getLoadWorker().stateProperty().get() == State.SUCCEEDED
                        && getComponent().getProperties().containsKey("player")
                        && ((JSObject) getComponent().getProperties().get("document")).call("querySelector", selector) != null;
            }
        });
    }

    public void click(String selector) {
        EventQueueWait.exec(new Runnable() {

            @Override public void run() {
                ((JSObject) getComponent().getProperties().get("player")).call("click", selector);
            }
        });
    }

    public boolean select(String selector, String value) {
        return EventQueueWait.exec(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                return (Boolean) ((JSObject) getComponent().getProperties().get("player")).call("select", selector, value);
            }
        });
    }

    public String getText(String selector) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return (String) ((JSObject) getComponent().getProperties().get("player")).call("text", selector);
            }
        });
    }

    public String getLabeledBy(String selector) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return (String) ((JSObject) getComponent().getProperties().get("player")).call("label", selector);
            }
        });
    }

    public String getValue(String selector) {
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                return (String) ((JSObject) getComponent().getProperties().get("player")).call("value", selector);
            }
        });
    }

    public Map<String, String> getAttributes(String selector) {
        String r = (String) ((JSObject) getComponent().getProperties().get("player")).call("attributes", selector);
        JSONObject o = new JSONObject(r);
        String[] names = JSONObject.getNames(o);
        HashMap<String, String> rm = new HashMap<>();
        for (String name : names) {
            rm.put(name, o.getString(name));
        }
        return rm;
    }
}
