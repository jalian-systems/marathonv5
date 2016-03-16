package net.sourceforge.marathon.javafxagent;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.logging.Logger;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sourceforge.marathon.javafxagent.server.JavaServer;

public class JavaHook {

    private static final Logger logger = Logger.getLogger(JavaHook.class.getName());
    protected static String windowTitle;

    public static void premain(final String args) throws Exception {
        logger.info("JavaVersion: " + System.getProperty("java.version"));
        logger.info("JavaHome: " + System.getProperty("java.home"));
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
                    logger.info("Checking for window: " + Thread.currentThread());
                    if (!"".equals(windowTitle)) {
                        logger.info("Checking for windowTitle is not implemented.. ignoring and continuing...");
                    }
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        @Override public Object run() {
                            try {
                                JavaServer server = new JavaServer(port, true);
                                server.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            done = true;
                            return null;
                        }
                    });
                }
            }
        });
        EventHandler<MouseEvent> mouseEventLogger = new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                logger.info(event.toString());
            }
        };
        EventHandler<KeyEvent> keyEventLogger = new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                logger.info(event.toString());
            }
        };
        stages.addListener(new ListChangeListener<Stage>() {
            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
                c.next();
                if (c.wasAdded()) {
                    List<? extends Stage> added = c.getAddedSubList();
                    for (Stage stage : added) {
                        stage.addEventFilter(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
                            @Override public void handle(WindowEvent event) {
                                stage.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEventLogger);
                                stage.getScene().getRoot().addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEventLogger);
                                stage.getScene().getRoot().addEventFilter(KeyEvent.KEY_PRESSED, keyEventLogger);
                                stage.getScene().getRoot().addEventFilter(KeyEvent.KEY_RELEASED, keyEventLogger);
                            }
                        });
                        stage.addEventFilter(WindowEvent.WINDOW_HIDING, new EventHandler<WindowEvent>() {
                            @Override public void handle(WindowEvent event) {
                                stage.getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_PRESSED, mouseEventLogger);
                                stage.getScene().getRoot().removeEventFilter(MouseEvent.MOUSE_RELEASED, mouseEventLogger);
                                stage.getScene().getRoot().removeEventFilter(KeyEvent.KEY_PRESSED, keyEventLogger);
                                stage.getScene().getRoot().removeEventFilter(KeyEvent.KEY_RELEASED, keyEventLogger);
                            }
                        });
                    }
                }
            }
        });
    }

}
