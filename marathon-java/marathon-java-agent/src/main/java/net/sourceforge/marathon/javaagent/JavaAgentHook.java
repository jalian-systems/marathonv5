/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javaagent;

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor.InternalFrameMonitor;
import net.sourceforge.marathon.javaagent.server.JavaServer;

public class JavaAgentHook {

    public static final Logger LOGGER = Logger.getLogger(JavaAgentHook.class.getName());
    @SuppressWarnings("unused")
    private static EventLogger eventLogger;

    protected static String windowTitle;

    public static void premain(final String args) throws Exception {
        InternalFrameMonitor.init();
        final int port;
        if (args != null && args.trim().length() > 0) {
            port = Integer.parseInt(args.trim());
        } else {
            throw new Exception("Port number not specified");
        }
        String eventsToLog = System.getProperty("marathon.logevents");
        new EventLogger(eventsToLog);

        windowTitle = System.getProperty("start.window.title", "");
        final AWTEventListener listener = new AWTEventListener() {
            boolean done = false;

            @Override
            public void eventDispatched(AWTEvent event) {
                if (done) {
                    return;
                }
                LOGGER.info("Checking for window: " + Thread.currentThread());
                if (!"".equals(windowTitle)) {
                    if (!isValidWindow()) {
                        LOGGER.info("Not a valid window");
                        return;
                    }
                }
                done = true;
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            LOGGER.info("JavaVersion: " + System.getProperty("java.version"));
                            LOGGER.info("JavaHome: " + System.getProperty("java.home"));
                            Charset utf8 = Charset.forName("utf-8");
                            if (!Charset.defaultCharset().equals(utf8)) {
                                LOGGER.warning(
                                        "Application is using a non-utf8 charset. Marathon might cause issues while playing");
                            }
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
                        if (getTitle(window).matches(windowTitle.substring(1))) {
                            return true;
                        }
                    } else {
                        if (getTitle(window).equals(windowTitle)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private String getTitle(Window window) {
                if (window instanceof Dialog) {
                    return ((Dialog) window).getTitle();
                } else if (window instanceof Frame) {
                    return ((Frame) window).getTitle();
                }
                return window.getClass().getName();
            }

        };
        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.FOCUS_EVENT_MASK);
    }

}
