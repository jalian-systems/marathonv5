package net.sourceforge.marathon.javafxrecorder;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.sourceforge.marathon.javafxrecorder.component.RComponentFactory;
import net.sourceforge.marathon.javafxrecorder.component.RFXComponent;
import net.sourceforge.marathon.javafxrecorder.http.HTTPRecorder;

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

    public JavaHook(int port) {
        try {
            logger.info("Starting HTTP Recorder on : " + port);
            recorder = new HTTPRecorder(port);
            objectMapConfiguration = recorder.getObjectMapConfiguration();
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
        stage.getScene().getRoot().removeEventFilter(Event.ANY, JavaHook.this);
    }

    private void addEventFilter(Stage stage) {
        logger.info("Stage.scene = " + stage.getScene());
        stage.getScene().getRoot().addEventFilter(Event.ANY, JavaHook.this);
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
                if(!"".equals(windowTitle)) {
                    logger.warning("WindowTitle is not supported yet... Ignoring it.");
                }
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
        Object source = event.getTarget();
        if (source instanceof Node) {
            Point2D point = null;
            if (event instanceof MouseEvent) {
                point = new Point2D(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
            }
            RFXComponent c = finder.findRComponent((Node) source, point, recorder);
            c.processEvent(event);
        }
    }
}
