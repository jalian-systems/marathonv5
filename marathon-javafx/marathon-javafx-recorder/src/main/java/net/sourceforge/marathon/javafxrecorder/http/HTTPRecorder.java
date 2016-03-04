package net.sourceforge.marathon.javafxrecorder.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
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
import net.sourceforge.marathon.javafxrecorder.JavaHook;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;
import sun.net.www.protocol.http.HttpURLConnection;

public class HTTPRecorder implements IJSONRecorder {

	private int port;
	private String base;
	private String sessionID;
	private Timer clickTimer;
	private static Integer timerinterval;
	private Timer windowStateTimer;

	static {
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

	@Override
	public void recordSelect(RFXComponent r, String state) {
		recordSelect2(r, state, false);
	}

	@Override
	public void recordClick(RFXComponent r, MouseEvent e) {
		recordClick2(r, e, false);
	}

	@Override
	public void recordClick2(final RFXComponent r, MouseEvent e, boolean withCellInfo) {
		final JSONObject event = new JSONObject();
		event.put("type", "click");
		event.put("button", e.getButton());
		event.put("clickCount", e.getClickCount());
		event.put("modifiersEx", buildModifiersText(e));
		double x = e.getX();
		double y = e.getY();
		Node source = (Node) e.getSource();
		Node target = r.getComponent();
		Point2D sts = source.localToScreen(new Point2D(0,0));
		Point2D tts = target.localToScreen(new Point2D(0, 0));
		x = e.getX() - tts.getX() + sts.getX();
		y = e.getY() - tts.getY() + sts.getY();
		event.put("x", x);
		event.put("y", y);
		if (withCellInfo)
			event.put("cellinfo", r.getCellInfo());
		final JSONObject o = new JSONObject();
		o.put("event", event);
		fill(r, o);
		if (e.getClickCount() == 1) {
			clickTimer = new Timer();
			clickTimer.schedule(new TimerTask() {
				@Override
				public void run() {
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

	@Override
	public void recordRawMouseEvent(final RFXComponent r, MouseEvent e) {
		final JSONObject event = new JSONObject();
		event.put("type", "click_raw");
		int button = e.getButton() == MouseButton.PRIMARY ? 0 : 2;
		event.put("button", button);
		event.put("clickCount", e.getClickCount());
		event.put("modifiersEx", buildModifiersText(e));
		double x = e.getX();
		double y = e.getY();
		Node source = (Node) e.getSource();
		Node target = r.getComponent();
		Point2D sts = source.localToScreen(new Point2D(0,0));
		Point2D tts = target.localToScreen(new Point2D(0, 0));
		System.out.println("HTTPRecorder.recordRawMouseEvent(): sts = " + sts + " tts = " + tts + " event = " + new Point2D(e.getX(), e.getY()));
		x = e.getX() - tts.getX() + sts.getX();
		y = e.getY() - tts.getY() + sts.getY();
		event.put("x", x);
		event.put("y", y);
		final JSONObject o = new JSONObject();
		o.put("event", event);
		fill(r, o);
		if (e.getClickCount() == 1) {
			clickTimer = new Timer();
			clickTimer.schedule(new TimerTask() {
				@Override
				public void run() {
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
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);
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
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);
		String mtext = sb.toString();
		return mtext;
	}

	@Override
	public void recordRawKeyEvent(RFXComponent r, KeyEvent e) {
		JSONObject event = new JSONObject();
		event.put("type", "key_raw");
		KeyCode keyCode = e.getCode();
		if (keyCode.isModifierKey())
			return;
		if ((keyCode.isFunctionKey() || keyCode.isArrowKey() || keyCode.isKeypadKey() || keyCode.isMediaKey()
				|| keyCode.isNavigationKey() || e.isControlDown() || e.isMetaDown() || e.isAltDown() || needManualRecording(keyCode))
				&& (e.getEventType() == KeyEvent.KEY_PRESSED)) {
			String mtext = buildModifiersText(e);
			event.put("modifiersEx", mtext);
			KeysMap keysMap = KeysMap.findMap(e.getCode());
			if (keysMap == KeysMap.NULL)
				return;
			String keyText;
			if (keysMap == null)
				keyText = e.getText();
			else
				keyText = keysMap.toString();
			event.put("keyCode", keyText);
		} else if (e.getEventType() == KeyEvent.KEY_TYPED && !e.isControlDown() && !needManualRecording(keyCode)) {
			char[] cs = e.getCharacter().toCharArray();
			if (cs.length == 0)
				return;
			for (char c : cs) {
				if (Character.isISOControl(c) && hasMapping(c)) {
					event.put("keyChar", getMapping(c));
				} else
					event.put("keyChar", "" + c);
			}
		} else {
			return;
		}
		recordEvent(r, event);
	}

	private boolean needManualRecording(KeyCode keyCode) {
		return keyCode == KeyCode.BACK_SPACE || keyCode == KeyCode.DELETE || keyCode == KeyCode.ESCAPE;
	}

	private String getMapping(char keyChar) {
		return controlKeyMappings.get(keyChar);
	}

	private boolean hasMapping(char keyChar) {
		return controlKeyMappings.get(keyChar) != null;
	}

	private static final Map<Character, String> controlKeyMappings = new HashMap<Character, String>();

	static {
		controlKeyMappings.put('\n', "Enter");
		controlKeyMappings.put('\t', "Tab");
		controlKeyMappings.put('\b', "Backspace");
		controlKeyMappings.put('\033', "Escape");
		controlKeyMappings.put('\177', "Delete");
	}

	@Override
	public void recordSelect2(RFXComponent r, String state, boolean withCellInfo) {
		JSONObject event = new JSONObject();
		event.put("type", "select");
		event.put("value", state);
		if (withCellInfo)
			event.put("cellinfo", r.getCellInfo());
		recordEvent(r, event);
	}

	private void recordEvent(RFXComponent r, JSONObject event) {
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

	private void fill(RFXComponent r, JSONObject o) {
		o.put("request", "record-action");
		o.put("urp", r.findURP());
		o.put("attributes", r.findAttributes());
		o.put("container", r.findContextHeirarchy());
	}

	@Override
	public boolean isCreatingObjectMap() {
		return false;
	}

	@Override
	public void recordAction(RFXComponent r, String action, String property, Object value) {
		JSONObject event = new JSONObject();
		event.put("type", action);
		event.put("value", value);
		event.put("property", property);
		event.put("cellinfo", r.getCellInfo());
		recordEvent(r, event);
	}

	@Override
	public void recordSelectMenu(RFXComponent r, String selection) {
		JSONObject event = new JSONObject();
		event.put("type", "select_menu");
		event.put("value", selection);
		recordEvent(r, event);
	}

	@Override
	public void recordWindowClosing(RFXComponent r) {
		JSONObject event = new JSONObject();
		event.put("type", "window_closed");
		recordEvent(r, event);
	}

	@Override
	public void recordWindowState(final RFXComponent r, Rectangle2D bounds) {
		final JSONObject event = new JSONObject();
		event.put("type", "window_state");
		event.put("bounds",
				bounds.getMinX() + ":" + bounds.getMinY() + ":" + bounds.getWidth() + ":" + bounds.getHeight());
		final JSONObject o = new JSONObject();
		o.put("event", event);
		if (windowStateTimer != null)
			windowStateTimer.cancel();
		windowStateTimer = new Timer();
		windowStateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				recordEvent(r, event);
				windowStateTimer = null;
			}
		}, 200);
	}

	@Override
	public JSONOMapConfig getObjectMapConfiguration() throws IOException {
		return new JSONOMapConfig(getJSON("object_map_configuration").getJSONObject("value"));
	}

	@Override
	public JSONObject getContextMenuTriggers() throws JSONException, IOException {
		return getJSON("context_menu_triggers").getJSONObject("value");
	}

	@Override
	public boolean isRawRecording() throws IOException {
		boolean r = getJSON("raw_recording").getBoolean("value");
		return r;
	}

	public Response get(final String path) throws IOException {
		return AccessController.doPrivileged(new PrivilegedAction<Response>() {
			@Override
			public Response run() {
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
			@Override
			public Response run() {
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
					wr.writeBytes(URLEncoder.encode(postData, "utf-8"));
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

	@Override
	public void recordMenuItem(RFXComponent r) {
		JSONObject event = new JSONObject();
		event.put("type", "menu_item");
		event.put("value", "");
		recordEvent(r, event);

	}

	@Override
	public void recordFocusedWindow(RFXComponent r) throws IOException {
		JSONObject o = new JSONObject();
		o.put("container", r.findContextHeirarchy((Parent) r.getComponent()));
		postJSON("focused_window", o);
	}

}
