package net.sourceforge.marathon;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.file.Path;

import fi.iki.elonen.SimpleWebServer;
import net.sourceforge.marathon.runtime.api.Constants;

public class ProjectHTTPDServer {
    private static int port = -1;

    public static URI getURI(Path filePath) {
        if (port == -1)
            return null;
        Path reportPath = Constants.getProjectPath();
        Path relativize = reportPath.relativize(filePath);
        StringBuilder sb = new StringBuilder();
        sb.append("http://localhost:" + port);
        relativize.forEach((p) -> {
            sb.append('/').append(p.toString());
        });
        return URI.create(sb.toString());
    }

    public static void startServer() throws IOException {
        port = findPort();
        SimpleWebServer sws = new SimpleWebServer("localhost", port, Constants.getMarathonProjectDirectory(), true);
        sws.start();
    }

    private static int findPort() throws IOException {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

}