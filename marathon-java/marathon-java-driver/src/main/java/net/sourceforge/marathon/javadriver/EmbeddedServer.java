package net.sourceforge.marathon.javadriver;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import net.sourceforge.marathon.javaagent.server.JavaServer;

public class EmbeddedServer {

    private NanoHTTPD javaServer;

    public void start(int port) throws IOException {
        javaServer = new JavaServer(port);
        javaServer.start();
    }

    public void stop() {
        if (javaServer != null)
            javaServer.stop();
    }

}
