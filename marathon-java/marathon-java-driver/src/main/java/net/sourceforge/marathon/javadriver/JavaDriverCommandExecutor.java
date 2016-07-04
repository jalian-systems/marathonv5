/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javadriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.remote.HttpCommandExecutor;

import net.sourceforge.marathon.javaagent.Wait;

public class JavaDriverCommandExecutor extends HttpCommandExecutor {
    private static final String MARATHON_APPLICATION_DONT_MONITOR = "marathon.application.dont.monitor";
    private EmbeddedServer server;
    private JavaProfile profile;

    public JavaDriverCommandExecutor(JavaProfile profile) {
        super(getURL(profile));
        this.profile = profile;
    }

    private static URL getURL(JavaProfile profile) {
        try {
            return new URL("http", "localhost", profile.getPort(), "/");
        } catch (MalformedURLException e) {
            throw new WebDriverException("Unable to create URL for the server", e);
        }
    }

    public void start() {
        if (profile.isEmbedded()) {
            if (server != null)
                return;
            int port = getAddressOfRemoteServer().getPort();
            server = new EmbeddedServer(profile);
            try {
                server.start(port);
            } catch (IOException e) {
                throw new WebDriverException("Unable to start the server on port " + port, e);
            }
        } else {
            final CommandLine command = profile.getCommandLine();
            Logger.getLogger(JavaDriverCommandExecutor.class.getName()).info("Executing: " + command);
            command.copyOutputTo(profile.getOutputStream());
            command.executeAsync();
            new Wait() {
                @Override public boolean until() {
                    return isConnected() || (!profile.isJavaWebStart() && !Boolean.getBoolean(MARATHON_APPLICATION_DONT_MONITOR)
                            && !command.isRunning());
                }
            }.wait("Timedout waiting for the server to start", Long.getLong("marathon.application.wait", Wait.DEFAULT_TIMEOUT * 5));
            if (!isConnected() && !command.isRunning()) {
                throw new WebDriverException("Unable to launch the application. command = " + command);
            }

        }
    }

    public boolean isConnected() {
        try {
            getAddressOfRemoteServer().openConnection().connect();
            return true;
        } catch (IOException e) {
            // Cannot connect yet.
            return false;
        }
    }

    public void stop() {
        if (server != null)
            server.stop();
        server = null;
    }

}
