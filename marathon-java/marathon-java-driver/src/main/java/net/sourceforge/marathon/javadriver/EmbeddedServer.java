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
            try {
                javaServer = (NanoHTTPD) Class.forName("net.sourceforge.marathon.javafxagent.server.JavaServer").getConstructor(Integer.TYPE).newInstance(port);
            } catch (Throwable t) {
                throw new RuntimeException("Unable to instantiate JavaServer", t);
            }
            javaServer.start();
        }
    }

    public void stop() {
        if (javaServer != null)
            javaServer.stop();
    }

}
