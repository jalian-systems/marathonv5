package net.sourceforge.marathon.javafxrecorder;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.sun.glass.ui.CommonDialogs;
import com.sun.javafx.stage.StageHelper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.sourceforge.marathon.fxcontextmenu.ContextMenuHandler;
import net.sourceforge.marathon.javafxrecorder.component.FileChooserTransformer;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponentFactory;
import net.sourceforge.marathon.javafxrecorder.component.RFXFileChooser;
import net.sourceforge.marathon.javafxrecorder.component.RFXFolderChooser;
import net.sourceforge.marathon.javafxrecorder.http.HTTPRecorder;

public class JavaFxRecorderHook implements EventHandler<Event> {

	private static final Logger logger = Logger.getLogger(JavaFxRecorderHook.class.getName());

	public static String DRIVER = "Java";
	public static String DRIVER_VERSION = "1.0";
	public static String PLATFORM = System.getProperty("java.runtime.name");
	public static String PLATFORM_VERSION = System.getProperty("java.version");
	public static String OS = System.getProperty("os.name");
	public static String OS_ARCH = System.getProperty("os.arch");
	public static String OS_VERSION = System.getProperty("os.version");

	public static EventType<Event> fileChooserEventType = new EventType<Event>("filechooser");
	public static EventType<Event> folderChooserEventType = new EventType<Event>("folderchooser");

	private static String windowTitle;

	private JSONOMapConfig objectMapConfiguration;
	private RFXComponentFactory finder;
	private IJSONRecorder recorder;
	private RFXComponent current;

	ContextMenuHandler contextMenuHandler;

	public JavaFxRecorderHook(int port) {
		try {
			logger.info("Starting HTTP Recorder on : " + port);
			recorder = new HTTPRecorder(port);
			objectMapConfiguration = recorder.getObjectMapConfiguration();
			setContextMenuTriggers(recorder.getContextMenuTriggers());
			finder = new RFXComponentFactory(objectMapConfiguration);
			contextMenuHandler = new ContextMenuHandler(recorder, finder);
			ObservableList<Stage> stages = StageHelper.getStages();
			for (Stage stage : stages) {
				addEventFilter(stage);
			}
			stages.addListener(new ListChangeListener<Stage>() {
				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
					c.next();
					if (c.wasAdded()) {
						List<? extends Stage> addedSubList = c.getAddedSubList();
						for (Stage stage : addedSubList) {
							addEventFilter(stage);
						}
					}
					if (c.wasRemoved()) {
						List<? extends Stage> removed = c.getRemoved();
						for (Stage stage : removed) {
							removeEventFilter(stage);
						}
					}
				}

			});
			// contextMenuHandler = new ContextMenuHandler(recorder, finder);
		} catch (UnknownHostException e) {
			logger.log(Level.WARNING, "Error in Recorder startup", e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error in Recorder startup", e);
		}
	}

	private static class ContextMenuTriggerCheck {
		private int keyModifiers;
		private int key;
		private int mouseModifiers;

		public ContextMenuTriggerCheck(int contextMenuKeyModifiers, int contextMenuKey, int menuModifiers) {
			this.keyModifiers = contextMenuKeyModifiers;
			this.key = contextMenuKey;
			this.mouseModifiers = menuModifiers;
		}

		public boolean isContextMenuEvent(Event event) {
			if (event instanceof MouseEvent)
				return isContextMenuMouseEvent((MouseEvent) event);
			else if (event instanceof KeyEvent)
				return isContextMenuKeyEvent((KeyEvent) event);
			return false;
		}

		@SuppressWarnings("deprecation")
		private boolean isContextMenuKeyEvent(KeyEvent event) {
			if (!event.getEventType().equals(KeyEvent.KEY_PRESSED))
				return false;
			if (isModifierKeyPressed(keyModifiers, InputEvent.ALT_DOWN_MASK) && !event.isAltDown())
				return false;
			if (isModifierKeyPressed(keyModifiers, InputEvent.META_DOWN_MASK) && !event.isMetaDown())
				return false;
			if (isModifierKeyPressed(keyModifiers, InputEvent.CTRL_DOWN_MASK) && !event.isControlDown())
				return false;
			if (isModifierKeyPressed(keyModifiers, InputEvent.SHIFT_DOWN_MASK) && !event.isShiftDown())
				return false;
			if (event.getCode().impl_getCode() != key)
				return false;
			return true;
		}

		private boolean isContextMenuMouseEvent(MouseEvent event) {
			if (!event.getEventType().equals(MouseEvent.MOUSE_PRESSED))
				return false;
			if (isModifierKeyPressed(mouseModifiers, InputEvent.ALT_DOWN_MASK) && !event.isAltDown())
				return false;
			if (isModifierKeyPressed(mouseModifiers, InputEvent.META_DOWN_MASK) && !event.isMetaDown())
				return false;
			if (isModifierKeyPressed(mouseModifiers, InputEvent.CTRL_DOWN_MASK) && !event.isControlDown())
				return false;
			if (isModifierKeyPressed(mouseModifiers, InputEvent.SHIFT_DOWN_MASK) && !event.isShiftDown())
				return false;
			if (isModifierKeyPressed(mouseModifiers, InputEvent.BUTTON1_DOWN_MASK) && !event.isPrimaryButtonDown())
				return false;
			if (isModifierKeyPressed(mouseModifiers, InputEvent.BUTTON2_DOWN_MASK) && !event.isMiddleButtonDown())
				return false;
			if (isModifierKeyPressed(mouseModifiers, InputEvent.BUTTON3_DOWN_MASK) && !event.isSecondaryButtonDown())
				return false;
			return true;
		}

		private boolean isModifierKeyPressed(int menuModifiers, int mkey) {
			return (menuModifiers & mkey) == mkey;
		}
	}

	private ContextMenuTriggerCheck contextMenuTriggerCheck;

	private void setContextMenuTriggers(JSONObject jsonObject) {
		int contextMenuKeyModifiers = jsonObject.getInt("contextMenuKeyModifiers");
		int contextMenuKey = jsonObject.getInt("contextMenuKey");
		int menuModifiers = jsonObject.getInt("menuModifiers");
		contextMenuTriggerCheck = new ContextMenuTriggerCheck(contextMenuKeyModifiers, contextMenuKey, menuModifiers);
	}

	private static final EventType<?> events[] = { MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED,
			MouseEvent.MOUSE_CLICKED, KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, KeyEvent.KEY_TYPED };

	private void removeEventFilter(Stage stage) {
		stage.getScene().getRoot().removeEventFilter(Event.ANY, JavaFxRecorderHook.this);
	}

	private void addEventFilter(Stage stage) {
		stage.getScene().getRoot().getProperties().put("marathon.fileChooser.eventType", fileChooserEventType);
		stage.getScene().getRoot().getProperties().put("marathon.folderChooser.eventType", folderChooserEventType);
		stage.getScene().getRoot().addEventFilter(Event.ANY, JavaFxRecorderHook.this);
	}

	public static void premain(final String args, Instrumentation instrumentation) throws Exception {
		instrumentation.addTransformer(new FileChooserTransformer());
		logger.info("JavaVersion: " + System.getProperty("java.version"));
		final int port;
		if (args != null && args.trim().length() > 0)
			port = Integer.parseInt(args.trim());
		else
			throw new Exception("Port number not specified");
		windowTitle = System.getProperty("start.window.title", "");
		ObservableList<Stage> stages = StageHelper.getStages();
		stages.addListener(new ListChangeListener<Stage>() {
			boolean done = false;

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
				if (done)
					return;
				if (!"".equals(windowTitle)) {
					logger.warning("WindowTitle is not supported yet... Ignoring it.");
				}
				c.next();
				if (c.wasAdded()) {
					AccessController.doPrivileged(new PrivilegedAction<Object>() {
						@Override
						public Object run() {
							return new JavaFxRecorderHook(port);
						}
					});
					done = true;
				}
			}
		});
	}

	@Override
	public void handle(Event event) {
		if (contextMenuTriggerCheck.isContextMenuEvent(event) || contextMenuHandler.isShowing()) {
			event.consume();
			contextMenuHandler.showPopup(event);
			return;
		}
		if (event.getEventType().getName().equals("filechooser")) {
			handleFileChooser(event);
			return;
		}
		if (event.getEventType().getName().equals("folderchooser")) {
			handleFolderChooser(event);
			return;
		}
		if (!isVaildEvent(event.getEventType()))
			return;
		if (!(event.getTarget() instanceof Node) || !(event.getSource() instanceof Node))
			return;
		Point2D point = null;
		if (event instanceof MouseEvent) {
			point = new Point2D(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
		}
		RFXComponent c = finder.findRComponent((Node) event.getTarget(), point, recorder);
		if (!c.equals(current) && isFocusChangeEvent(event)) {
			if (current != null && isShowing(current))
				current.focusLost(c);
			c.focusGained(current);
			current = c;
		}
		// We Need This.
		if (c.equals(current))
			c = current;
		c.processEvent(event);
	}

	private void handleFolderChooser(Event event) {
		Node source = (Node) event.getSource();
		File folder = (File) source.getProperties().get("marathon.selectedFolder");
		new RFXFolderChooser(recorder).record(folder);
	}

	private void handleFileChooser(Event event) {
		Node source = (Node) event.getSource();
		CommonDialogs.FileChooserResult files = (CommonDialogs.FileChooserResult) source.getProperties()
				.get("marathon.selectedFiles");
		List<File> selectedFiles = null;
		if (files != null)
			selectedFiles = files.getFiles();
		new RFXFileChooser(recorder).record(selectedFiles);
	}

	private boolean isVaildEvent(EventType<? extends Event> eventType) {
		return Arrays.asList(events).contains(eventType);
	}

	private boolean isFocusChangeEvent(Event event) {
		return event.getEventType() == MouseEvent.MOUSE_PRESSED;
	}

	private boolean isShowing(RFXComponent component) {
		try {
			return component.getComponent().getScene().getWindow().isShowing();
		} catch (Throwable t) {
			return false;
		}
	}

	public static String getModifiersExText(int modifiers) {
		StringBuilder buf = new StringBuilder();
		if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
			buf.append("Meta");
			buf.append("+");
		}
		if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
			buf.append("Ctrl");
			buf.append("+");
		}
		if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
			buf.append("Alt");
			buf.append("+");
		}
		if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
			buf.append("Shift");
			buf.append("+");
		}
		if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
			buf.append("Alt Graph");
			buf.append("+");
		}

		int buttonNumber = 1;
		for (int mask : new int[] { InputEvent.BUTTON1_DOWN_MASK, InputEvent.BUTTON2_DOWN_MASK,
				InputEvent.BUTTON3_DOWN_MASK }) {
			if ((modifiers & mask) != 0) {
				buf.append(Toolkit.getProperty("AWT.button" + buttonNumber, "Button" + buttonNumber));
				buf.append("+");
			}
			buttonNumber++;
		}
		if (buf.length() > 0) {
			buf.setLength(buf.length() - 1); // remove trailing '+'
		}
		return buf.toString();
	}

}
