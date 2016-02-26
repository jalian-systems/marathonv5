package net.sourceforge.marathon.javafxrecorder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.marathon.javafxrecorder.component.RComponent;
import net.sourceforge.marathon.javafxrecorder.component.RComponentFactory;
import net.sourceforge.marathon.javafxrecorder.component.RUnknownComponent;
import net.sourceforge.marathon.javafxrecorder.http.HTTPRecorder;

import org.json.JSONObject;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class JavaHook implements EventHandler<Event> {

    private static final Logger logger = Logger.getLogger(JavaHook.class.getName());

    public static String DRIVER = "Java";
    public static String DRIVER_VERSION = "1.0";
    public static String PLATFORM = System.getProperty("java.runtime.name");
    public static String PLATFORM_VERSION = System.getProperty("java.version");
    public static String OS = System.getProperty("os.name");
    public static String OS_ARCH = System.getProperty("os.arch");
    public static String OS_VERSION = System.getProperty("os.version");

    private static String windowTitle;

    private JSONOMapConfig objectMapConfiguration;
    private RComponentFactory finder;
    private IJSONRecorder recorder;
    private RComponent current;
    private boolean rawRecording;
    private int contextMenuKeyModifiers;
    private int contextMenuKey;
    private int menuModifiers;
    // private ContextMenuHandler contextMenuHandler;

    public JavaHook(int port) {
        try {
            logger.info("Starting HTTP Recorder on : " + port);
            recorder = new HTTPRecorder(port);
            objectMapConfiguration = recorder.getObjectMapConfiguration();
            setContextMenuTriggers(recorder.getContextMenuTriggers());
            finder = new RComponentFactory(objectMapConfiguration);
            ObservableList<Stage> stages = StageHelper.getStages();
            for (Stage stage : stages) {
                addEventFilter(stage);
            }
            stages.addListener(new ListChangeListener<Stage>() {
                @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
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

    private void removeEventFilter(Stage stage) {
        stage.getScene().removeEventFilter(MouseEvent.MOUSE_PRESSED, JavaHook.this);
    }

    private void addEventFilter(Stage stage) {
        logger.info("Stage.scene = " + stage.getScene());
        stage.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, JavaHook.this);
    }

    private void setContextMenuTriggers(JSONObject jsonObject) {
        contextMenuKeyModifiers = jsonObject.getInt("contextMenuKeyModifiers");
        contextMenuKey = jsonObject.getInt("contextMenuKey");
        menuModifiers = jsonObject.getInt("menuModifiers");
    }

    public static void premain(final String args) throws Exception {
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

            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
                if (done)
                    return;
                c.next();
                if (c.wasAdded()) {
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        @Override public Object run() {
                            return new JavaHook(port);
                        }
                    });
                    done = true;
                }
            }
        });
    }

    @Override public void handle(Event event) {
        logger.info("JavaHook.handle(): " + event);
        Object source = event.getTarget();
        if (source instanceof Node) {
            logger.info("Trying to process the event");
            Point2D point = null;
            if (event instanceof MouseEvent) {
                point = new Point2D(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
            }
            RComponent c = finder.findRComponent((Node) source, point, recorder);getClass();
            c.processEvent(event);
        }
    }

    // @Override public void eventDispatched(final AWTEvent event) {
    // AccessController.doPrivileged(new PrivilegedAction<Object>() {
    // @Override public Object run() {
    // Object source = event.getSource();
    // if (!(source instanceof Component))
    // return null;
    // if (event instanceof WindowEvent) {
    // handleWindowEvent((WindowEvent) event);
    // return null;
    // }
    // // if (event instanceof KeyEvent &&
    // // isContextMenuKeySequence((KeyEvent) event)) {
    // // ((KeyEvent) event).consume();
    // // contextMenuHandler.showPopup((KeyEvent) event);
    // // return null;
    // // }
    // // if (event instanceof MouseEvent &&
    // // isContextMenuSequence((MouseEvent) event)) {
    // // ((MouseEvent) event).consume();
    // // if (current != null &&
    // // SwingUtilities.getWindowAncestor(current.getComponent()) !=
    // // null)
    // // current.focusLost(null);
    // // contextMenuHandler.showPopup((MouseEvent) event);
    // // return null;
    // // }
    // // if (contextMenuHandler.isContextMenuOn())
    // // return null;
    // Component component = (Component) source;
    // if (SwingUtilities.getWindowAncestor(component) == null)
    // return null;
    // if (rawRecording) {
    // new RUnknownComponent(component, objectMapConfiguration, null,
    // recorder).handleRawRecording(recorder, event);
    // return null;
    // }
    // int id = event.getID();
    // AWTEvent eventx;
    // if (event instanceof MouseEvent)
    // eventx = SwingUtilities.convertMouseEvent(((MouseEvent)
    // event).getComponent(), (MouseEvent) event,
    // (Component) source);
    // else
    // eventx = event;
    // RComponent c = finder.findRComponent(component,
    // eventx instanceof MouseEvent ? ((MouseEvent) eventx).getPoint() : null,
    // recorder);
    // if (isFocusChangeEvent(id) && !c.equals(current)) {
    // if (current != null &&
    // SwingUtilities.getWindowAncestor(current.getComponent()) != null)
    // current.focusLost(c);
    // c.focusGained(current);
    // current = c;
    // }
    // if (c.equals(current))
    // c = current;
    // c.processEvent(eventx);
    // return null;
    // }
    // });
    // }
    //
}
