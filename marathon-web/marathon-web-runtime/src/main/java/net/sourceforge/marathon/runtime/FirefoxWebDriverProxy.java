package net.sourceforge.marathon.runtime;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriver.SystemProperty;
import org.openqa.selenium.firefox.FirefoxProfile;

public class FirefoxWebDriverProxy implements IWebdriverProxy {

    private int port;
    private FirefoxDriver driver;

    public FirefoxWebDriverProxy() {
        port = findPort();
        System.setProperty(SystemProperty.DRIVER_USE_MARIONETTE, "false");
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference(FirefoxProfile.PORT_PREFERENCE, port);
        FirefoxBinary binary ;
        if(System.getProperty("firefox_binary") != null) {
            binary = new FirefoxBinary(new File(System.getProperty("firefox_binary")));
        } else {
            binary = new FirefoxBinary();
        }
        driver = new FirefoxDriver(binary, profile);
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
        return "http://localhost:" + port + "/hub";
    }

}
