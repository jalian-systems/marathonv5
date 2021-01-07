/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.safari.SafariDriverService;

public class SafariWebDriverProxy implements IWebBrowserProxy {

    public static final Logger LOGGER = Logger.getLogger(SafariWebDriverProxy.class.getName());

    public static final String BROWSER = DesiredCapabilities.safari().getBrowserName();

    @Override
    public DriverService createService(int port) {
        BrowserConfig config = BrowserConfig.instance();
        SafariDriverService.Builder builder = new SafariDriverService.Builder();
        builder.usingTechnologyPreview(config.getValue(BROWSER, "webdriver-use-technology-preview", false));
        String environ = config.getValue(BROWSER, "browser-environment");
        if (environ != null) {
            Map<String, String> envMap = new HashMap<>();
            BufferedReader reader = new BufferedReader(new StringReader(environ));
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("=");
                    if (parts != null && parts.length == 2) {
                        envMap.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
            }
            builder.withEnvironment(envMap);
        }
        String logFile = config.getValue(BROWSER, "webdriver-log-file-path");
        if (logFile != null) {
            builder.withLogFile(new File(logFile));
        }
        return builder.usingPort(port).build();
    }

    private static class SafariBrowserTab extends BrowserTab {
        public SafariBrowserTab(String name) {
            super(name);
        }

        @Override
        protected String getBrowserName() {
            return BROWSER;
        }

        @Override
        protected String getWebDriverExecutableName() {
            return "safaridriver";
        }

        @Override
        public void addAcceptUntrustedCertificates() {
        }

        @Override
        public void addAlwaysLoadNoFocusLib() {
        }

        @Override
        public void addArguments() {
        }

        @Override
        public void addAssumeUntrustedCertificateIssuer() {
        }

        @Override
        public void addBrowserPreferences() {
        }

        @Override
        public void addExtensions() {
        }

        @Override
        public void addSilent() {
        }

        @Override
        public void addVerbose() {
        }

        @Override
        protected void addWebDriverExeBrowse() {
        }

        @Override
        protected void addBrowserExeBrowse() {
        }

        @Override
        public void addWdArguments() {
        }

        @Override
        public void addPageLoadStrategy() {
        }

        @Override
        public void addUnexpectedAlertBehavior() {
        }

        @Override
        public void addIELogLevel() {
        }
    }

    @Override
    public BrowserTab getTab(String name) {
        return new SafariBrowserTab(name);
    }

    @Override
    public Map<String, ?> getCapabilities() {
        BrowserConfig config = BrowserConfig.instance();
        Map<String, Object> safariOptions = new HashMap<>();
        safariOptions.put("browser-use-clean-session", config.getValue(BROWSER, "browser-use-clean-session", false));

        Map<String, Object> caps = new HashMap<>();
        caps.put("safari.options", safariOptions);
        return caps;
    }
}
