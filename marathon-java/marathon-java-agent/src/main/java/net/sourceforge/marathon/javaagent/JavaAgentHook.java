package net.sourceforge.marathon.javaagent;

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor.InternalFrameMonitor;
import net.sourceforge.marathon.javaagent.server.JavaServer;

public class JavaAgentHook {

    private static final Logger logger = Logger.getLogger(JavaAgentHook.class.getName());
    @SuppressWarnings("unused") private static EventLogger eventLogger ;
    
    protected static String windowTitle;

    public static void premain(final String args) throws Exception {
        logger.info("JavaVersion: " + System.getProperty("java.version"));
        logger.info("JavaHome: " + System.getProperty("java.home"));
        InternalFrameMonitor.init();
        final int port;
        if (args != null && args.trim().length() > 0)
            port = Integer.parseInt(args.trim());
        else
            throw new Exception("Port number not specified");
        String eventsToLog = System.getProperty("marathon.logevents");
        new EventLogger(eventsToLog);
        
        windowTitle = System.getProperty("start.window.title", "");
        final AWTEventListener listener = new AWTEventListener() {
            boolean done = false;

            @Override public void eventDispatched(AWTEvent event) {
                if (done)
                    return;
                logger.info("Checking for window: " + Thread.currentThread());
                if (!"".equals(windowTitle)) {
                    if (!isValidWindow()) {
                        logger.info("Not a valid window");
                        return;
                    }
                }
                done = true;
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override public Object run() {
                        try {
                            JavaServer server = new JavaServer(port, true);
                            server.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }

            private boolean isValidWindow() {
                Window[] windows = Window.getWindows();
                for (Window window : windows) {
                    if (windowTitle.startsWith("/")) {
                        if (getTitle(window).matches(windowTitle.substring(1)))
                            return true;
                    } else {
                        if (getTitle(window).equals(windowTitle))
                            return true;
                    }
                }
                return false;
            }

            private String getTitle(Window window) {
                if (window instanceof Dialog)
                    return ((Dialog) window).getTitle();
                else if (window instanceof Frame)
                    return ((Frame) window).getTitle();
                return window.getClass().getName();
            }

        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
    }

}
