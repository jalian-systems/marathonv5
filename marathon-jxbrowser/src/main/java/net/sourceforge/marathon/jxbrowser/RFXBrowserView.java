package net.sourceforge.marathon.jxbrowser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;
import net.sourceforge.marathon.javafxrecorder.JavaFxRecorderHook;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;

public class RFXBrowserView extends RFXComponent {

    private static final String script;

    static {
        InputStream r = RFXBrowserView.class.getResourceAsStream("browserview_recorder.js");
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

    public RFXBrowserView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        init(source);
    }

    private void init(Node source) {
        BrowserView webview = (BrowserView) source;
        if (webview.getProperties().get("marathon_listener_installed") == null) {
            webview.getProperties().put("marathon_listener_installed", Boolean.TRUE);
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
        JavaFXBrowserViewElement.init(source);
    }

    @Override public void processEvent(Event event) {
    }

    public void record_select(String info, String value) {
        recorder.recordSelect3(this, value, info);
    }

    public void record_click(String info) {
        recorder.recordClick3(this, info);
    }

    public void record_assertion_selector(String selector, long frameId) {
        getComponent().getProperties().put("current_selector", selector);
        getComponent().getProperties().put("browserview_frame_id", frameId);
    }

    public void show_assertion() {
        String char1 = "X";
        KeyCode keyCode = KeyCode.UNDEFINED;
        KeyEvent keyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, char1 + "", char1 + "", keyCode, false, false, false, false);
        keyEvent = keyEvent.copyFor(getComponent(), getComponent());
        JavaFxRecorderHook.instance.get().showContextMenu(keyEvent);
    }

    public void log(String message) {
        System.out.println(message);
    }

    private void loadScript(BrowserView webview, Browser webEngine, long frameId, boolean mainFrame) {
        webview.getProperties().put("current_selector", "body");
        JSValue win = webEngine.executeJavaScriptAndReturnValue(frameId, "window");
        win.asObject().setProperty("marathon_recorder", RFXBrowserView.this);
        win.asObject().setProperty("browserview_frame_id", frameId);
        webEngine.executeJavaScript(frameId, script);
    }

    @Override public String getCellInfo() {
        return (String) getComponent().getProperties().get("current_selector");
    }

    private long getFrameId() {
        System.out.println("RFXBrowserView.getFrameId(" + getComponent() + ")");
        return (long) getComponent().getProperties().get("browserview_frame_id");
    }

    @Override public String _getText() {
        return JavaFXBrowserViewElement.getText(getComponent(), getCellInfo(), getFrameId());
    }

    protected String[] getMethodNames() {
        return new String[] { "getAttributes", "getValue" };
    }

    @Override protected String _getLabeledBy() {
        return JavaFXBrowserViewElement.getLabeledBy(getComponent(), getCellInfo(), getFrameId());
    }

    public Map<String, String> getAttributes() {
        return JavaFXBrowserViewElement.getAttributes(getComponent(), getCellInfo(), getFrameId());
    }

    @Override public String _getValue() {
        return JavaFXBrowserViewElement.getValue(getComponent(), getCellInfo(), getFrameId());
    }

    public static void generateEvent(Node component) {
        JavaFxRecorderHook.instance.addListener(new ChangeListener<JavaFxRecorderHook>() {
            @Override public void changed(ObservableValue<? extends JavaFxRecorderHook> observable, JavaFxRecorderHook oldValue,
                    JavaFxRecorderHook newValue) {
                if (newValue != null) {
                    String char1 = "X";
                    KeyCode keyCode = KeyCode.UNDEFINED;
                    KeyEvent keyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, char1 + "", char1 + "", keyCode, false, false, false,
                            false);
                    keyEvent = keyEvent.copyFor(component, component);
                    newValue.handle(keyEvent);
                }

            }
        });
    }
}
