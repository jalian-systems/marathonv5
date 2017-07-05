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
package net.sourceforge.marathon.javafxagent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.charset.Charset;
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
import net.sourceforge.marathon.javafxagent.components.FileChooserTransformer;
import net.sourceforge.marathon.javafxagent.server.JavaServer;

public class JavaFxAgentHook {

    public static final Logger LOGGER = Logger.getLogger(JavaFxAgentHook.class.getName());
    protected static String windowTitle;

    public static void premain(final String args, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new FileChooserTransformer());
        final int port;
        if (args != null && args.trim().length() > 0) {
            port = Integer.parseInt(args.trim());
        } else {
            throw new Exception("Port number not specified");
        }
        windowTitle = System.getProperty("start.window.title", "");
        ObservableList<Stage> stages = StageHelper.getStages();
        stages.addListener(new ListChangeListener<Stage>() {
            boolean done = false;

            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends Stage> c) {
                if (done) {
                    return;
                }
                c.next();
                if (c.wasAdded()) {
                    LOGGER.info("Checking for window: " + Thread.currentThread());
                    if (!"".equals(windowTitle)) {
                        LOGGER.info("Checking for windowTitle is not implemented.. ignoring and continuing...");
                    }
                    AccessController.doPrivileged(new PrivilegedAction<Object>() {
                        @Override public Object run() {
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
                            done = true;
                            return null;
                        }
                    });
                }
            }
        });
        EventHandler<MouseEvent> mouseEventLogger = new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                LOGGER.info(event.toString());
            }
        };
        EventHandler<KeyEvent> keyEventLogger = new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                LOGGER.info(event.toString());
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
