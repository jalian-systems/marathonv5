package net.sourceforge.marathon.javadriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.remote.HttpCommandExecutor;

import com.thoughtworks.selenium.Wait;

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
