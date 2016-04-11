package net.sourceforge.marathon.javadriver;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import net.sourceforge.marathon.javaagent.server.JavaServer;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;

public class EmbeddedServer {

    private NanoHTTPD javaServer;
    private JavaProfile profile;

    public EmbeddedServer(JavaProfile profile) {
        this.profile = profile;
    }

    public void start(int port) throws IOException {
        if (profile.getLaunchType() == LaunchType.SWING_APPLICATION) {
            javaServer = new JavaServer(port);
            javaServer.start();
        } else {
            javaServer = new net.sourceforge.marathon.javafxagent.server.JavaServer(port);
            javaServer.start();
        }
    }

    public void stop() {
        if (javaServer != null)
            javaServer.stop();
    }

}
