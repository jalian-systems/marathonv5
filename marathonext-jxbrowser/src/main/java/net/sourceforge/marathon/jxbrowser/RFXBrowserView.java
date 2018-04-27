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

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;
import net.sourceforge.marathon.javafxrecorder.JavaFxRecorderHook;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;

public class RFXBrowserView extends RFXComponent {

    private static final String script;
	private static final int MAX_TRIES = 5;

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
            loadScript(webview, webEngine, -1);
            if (webEngine.getDocument() != null) {
                List<Long> framesIds = webEngine.getFramesIds();
                for (Long frameId : framesIds) {
                    loadScript(webview, webEngine, frameId);
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
                    long frameId = arg0.getFrameId();
                    if (arg0.isMainFrame())
                        frameId = -1;
                    loadScript(webview, webEngine, frameId);
                }
            });
        }
        JavaFXBrowserViewElement.init(source);
    }

    @Override public void processEvent(Event event) {
    }

    public void record_select(String info, String value, int id) {
        if (recorder.isPaused())
            return;
        if (id != System.identityHashCode(this.getComponent()))
            return;
        if (info.startsWith("-1:"))
            info = info.substring(3);
        recorder.recordSelect3(this, value, info);
    }

    public void record_click(String info, int id) {
        if (recorder.isPaused())
            return;
        if (id != System.identityHashCode(this.getComponent()))
            return;
        if (info.startsWith("-1:"))
            info = info.substring(3);
        recorder.recordClick3(this, info);
    }

    public void show_assertion(String selector, long frameId, int id) {
        if (id != System.identityHashCode(this.getComponent()))
            return;
        getComponent().getProperties().put("current_selector", selector);
        getComponent().getProperties().put("browserview_frame_id", frameId);
        Platform.runLater(() -> {
            MouseEvent contextMenuMouseEvent = JavaFxRecorderHook.instance.get().getContextMenuMouseEvent(this.getComponent());
            JavaFxRecorderHook.instance.get().handle(contextMenuMouseEvent);
        });
    }

    public void log(String message) {
        System.out.println(message);
    }

    private void loadScript(BrowserView webview, Browser webEngine, long frameId) {
    	for(int i = 0; i < MAX_TRIES; i++) {
    		try {
    			loadScriptx(webview, webEngine, frameId);
    			return;
    		} catch(RuntimeException e) {
    			if(i == MAX_TRIES -1)
    				throw e;
    			try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
				}
    		}
    	}
    }
    
    private void loadScriptx(BrowserView webview, Browser webEngine, long frameId) {
        webview.getProperties().put("current_selector", "body");
        JSValue win = webEngine.executeJavaScriptAndReturnValue(frameId, "window");
        win.asObject().setProperty("marathon_recorder", RFXBrowserView.this);
        win.asObject().setProperty("browserview_frame_id", frameId);
        int id = System.identityHashCode(webview);
        win.asObject().setProperty("browserview_id", id);
        webEngine.executeJavaScript(frameId, script);
    }

    @Override public String getCellInfo() {
        if (getFrameId() == -1)
            return (String) getComponent().getProperties().get("current_selector");
        return getFrameId() + ":" + (String) getComponent().getProperties().get("current_selector");
    }

    public String getCellInfo2() {
        return (String) getComponent().getProperties().get("current_selector");
    }

    private long getFrameId() {
        return (long) getComponent().getProperties().get("browserview_frame_id");
    }

    @Override public String _getText() {
        return JavaFXBrowserViewElement.getText(getComponent(), getCellInfo2(), getFrameId());
    }

    protected String[] getMethodNames() {
        return new String[] { "getAttributes", "getValue" };
    }

    @Override protected String _getLabeledBy() {
        return JavaFXBrowserViewElement.getLabeledBy(getComponent(), getCellInfo2(), getFrameId());
    }

    public Map<String, String> getAttributes() {
        return JavaFXBrowserViewElement.getAttributes(getComponent(), getCellInfo2(), getFrameId());
    }

    @Override public String _getValue() {
        return JavaFXBrowserViewElement.getValue(getComponent(), getCellInfo2(), getFrameId());
    }

    public static void generateEvent(Node n) {
        JavaFxRecorderHook.instance.addListener(new ChangeListener<JavaFxRecorderHook>() {
            @Override public void changed(ObservableValue<? extends JavaFxRecorderHook> observable, JavaFxRecorderHook oldValue,
                    JavaFxRecorderHook newValue) {
                Platform.runLater(() -> {
                    MouseEvent e = new MouseEvent(n, n, MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false,
                            false, false, true, false, false, true, false, false, null);
                    JavaFxRecorderHook.instance.get().handle(e);
                });
            }
        });
    }

}
