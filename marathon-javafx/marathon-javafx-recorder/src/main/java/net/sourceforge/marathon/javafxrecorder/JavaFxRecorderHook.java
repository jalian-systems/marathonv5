package net.sourceforge.marathon.javafxrecorder;

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

	private static String windowTitle;

	private JSONOMapConfig objectMapConfiguration;
	private RFXComponentFactory finder;
	private IJSONRecorder recorder;
	private RFXComponent current;

	public JavaFxRecorderHook(int port) {
		try {
			logger.info("Starting HTTP Recorder on : " + port);
			recorder = new HTTPRecorder(port);
			objectMapConfiguration = recorder.getObjectMapConfiguration();
			finder = new RFXComponentFactory(objectMapConfiguration);
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

	private static final EventType<?> events[] = { MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED,
			MouseEvent.MOUSE_CLICKED, KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, KeyEvent.KEY_TYPED };

	private void removeEventFilter(Stage stage) {
		stage.getScene().getRoot().removeEventFilter(Event.ANY, JavaFxRecorderHook.this);
	}

	private void addEventFilter(Stage stage) {
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

}
