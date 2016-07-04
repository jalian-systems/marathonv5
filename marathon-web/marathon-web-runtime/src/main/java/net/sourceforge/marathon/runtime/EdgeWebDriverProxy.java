package net.sourceforge.marathon.runtime;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.edge.EdgeDriverService;

public class EdgeWebDriverProxy implements IWebdriverProxy {

    private int port;
    private EdgeDriverService service;

    public EdgeWebDriverProxy() {
        port = findPort();
        File exe = new File("c:\\Program Files (x86)\\Microsoft Web Driver\\MicrosoftWebDriver.exe");
        service = new EdgeDriverService.Builder().usingPort(port).usingDriverExecutable(exe).build();
        try {
            service.start();
        } catch (IOException e) {
            throw new RuntimeException("Unable to start InternetExplorerDriverService", e);
        }
    }

    @Override public String getURL() {
        return "http://localhost:" + port ;
    }

    private int findPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new WebDriverException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                }
        }
    }

    @Override public void quit() {
        if(service != null)
            service.stop();
    }
}
