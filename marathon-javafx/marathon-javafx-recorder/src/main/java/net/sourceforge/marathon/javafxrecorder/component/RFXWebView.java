package net.sourceforge.marathon.javafxrecorder.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.sourceforge.marathon.javafxagent.components.JavaFXWebViewElement;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;
import netscape.javascript.JSObject;

public class RFXWebView extends RFXComponent {

	private static final String script;

	static {
		InputStream r = RFXWebView.class.getResourceAsStream("webview.js");
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

	public RFXWebView(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
		super(source, omapConfig, point, recorder);
		init(source);
	}

	private void init(Node source) {
		WebView webview = (WebView) source;
		if (webview.getProperties().get("marathon_listener_installed") == null) {
			webview.getProperties().put("marathon_listener_installed", Boolean.TRUE);
			WebEngine webEngine = webview.getEngine();
			if (webEngine.getLoadWorker().stateProperty().get() == State.SUCCEEDED) {
				loadScript(webview);
			}
			webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
				@Override
				public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
					if (newState == State.SUCCEEDED) {
						loadScript(webview);
					}
				}
			});
		}
		JavaFXWebViewElement.init(source);
	}

	@Override
	public void processEvent(Event event) {
	}

	public void record_select(String info, String value) {
		recorder.recordSelect3(this, value, info);
	}

	public void record_click(String info) {
		recorder.recordClick3(this, info);
	}

	public void record_assertion_selector(String selector) {
		getComponent().getProperties().put("current_selector", selector);
	}

	public void log(String message) {
		System.out.println(message);
	}

	private void loadScript(WebView webview) {
		webview.getProperties().put("current_selector", "body");
		WebEngine webEngine = webview.getEngine();
		JSObject win = (JSObject) webEngine.executeScript("window");
		win.setMember("marathon_recorder", RFXWebView.this);
		webEngine.executeScript(script);
	}

	@Override
	public String getCellInfo() {
		return (String) getComponent().getProperties().get("current_selector");
	}

	@Override
	public String _getText() {
		return (String) ((JSObject) getComponent().getProperties().get("player")).call("text",
				(String) getComponent().getProperties().get("current_selector"));
	}
	
	protected String[] getMethodNames() {
		return new String[] { "getAttributes", "getValue" };
	}

	@Override
	protected String _getLabeledBy() {
		return (String) ((JSObject) getComponent().getProperties().get("player")).call("label",
				(String) getComponent().getProperties().get("current_selector"));
	}
	
	public Map<String, String> getAttributes() {
		String r = (String) ((JSObject) getComponent().getProperties().get("player")).call("attributes",
				(String) getComponent().getProperties().get("current_selector"));
		JSONObject o = new JSONObject(r);
		String[] names = JSONObject.getNames(o);
		HashMap<String, String> rm = new HashMap<>();
		for (String name : names) {
			rm.put(name, o.getString(name));
		}
		return rm;
	}
	
	@Override
	public String _getValue() {
		return (String) ((JSObject) getComponent().getProperties().get("player")).call("value",
				(String) getComponent().getProperties().get("current_selector"));
	}
}
