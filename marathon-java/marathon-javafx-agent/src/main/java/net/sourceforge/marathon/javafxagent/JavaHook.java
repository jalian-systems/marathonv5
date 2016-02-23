package net.sourceforge.marathon.javafxagent;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import com.sun.javafx.stage.StageHelper;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
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
    }

}
