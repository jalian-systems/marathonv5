/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.service.DriverService;

import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.runtime.api.IPreferenceChangeListener;

public class DriverProxy implements IWebdriverProxy {

    public static final Logger LOGGER = Logger.getLogger(DriverProxy.class.getName());

    private static DriverProxy _instance;
    private int port;
    private DriverService service;
    private String browserName;

    private IWebBrowserProxy proxy;

    private boolean browserConfigMonitoringInstalled = false;

    private DriverProxy() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                DriverProxy.this.quit(true);
            }
        });
    }

    public static DriverProxy instance() {
        if (_instance == null) {
            _instance = new DriverProxy();
        }
        return _instance;
    }

    @Override
    public String getURL() {
        return "http://localhost:" + port + "/";
    }

    private int findPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new WebDriverException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void startService(Class<?> proxyClass, String rBrowserName) {
        if (rBrowserName.equals(browserName)) {
            TestAttributes.put("browserName", rBrowserName);
            TestAttributes.put("capabilities", proxy.getCapabilities());
            return;
        }
        service = createService(proxyClass);
        try {
            TestAttributes.put("browserName", rBrowserName);
            service.start();
            browserName = rBrowserName;
        } catch (IOException e) {
            throw new RuntimeException("Unable to start " + service.getClass().getName(), e);
        }
        TestAttributes.put("capabilities", proxy.getCapabilities());
        if (!browserConfigMonitoringInstalled) {
            BrowserConfig.instance().addPreferenceChangeListener("all", new IPreferenceChangeListener() {
                @Override
                public void preferencesChanged(String section, JSONObject preferences) {
                    browserName = null;
                }
            });
            browserConfigMonitoringInstalled = true;
        }
    }

    private DriverService createService(Class<?> proxyClass) {
        stopPreviousService();
        this.port = findPort();
        try {
            proxy = (IWebBrowserProxy) proxyClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Logger.getLogger(DriverProxy.class.getName())
                    .warning("Unable to load class: " + proxyClass + ". Defaulting to " + FirefoxWebDriverProxy.class.getName());
            proxy = new FirefoxWebDriverProxy();
        }
        return proxy.createService(port);
    }

    private void stopPreviousService() {
        if (service != null && service.isRunning())
            service.stop();
        service = null;
    }

    @Override
    public void quit(boolean force) {
        if (force) {
            if (service != null && service.isRunning())
                service.stop();
            service = null;
        }
    }

}
