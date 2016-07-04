package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.net.ServerSocket;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

public class SafariWebDriverProxy implements IWebdriverProxy {

    private int port;
    private SafariDriver driver;

    public SafariWebDriverProxy() {
        port = findPort();
        SafariOptions options = new SafariOptions();
        options.setPort(port);
        driver = new SafariDriver(options);
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

    public void quit() {
        driver.quit();
    }

    @Override public String getURL() {
        return "http://localhost:" + port + "/";
    }

}
