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

import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

public class EdgeWebDriverProxy implements IWebBrowserProxy {

    public static final Logger LOGGER = Logger.getLogger(EdgeWebDriverProxy.class.getName());

    public static final String BROWSER = DesiredCapabilities.edge().getBrowserName();

    // CheckNetIsolation LoopbackExempt -a
    // -n=Microsoft.MicrosoftEdge_8wekyb3d8bbwe
    public EdgeWebDriverProxy() {
    }

    @Override
    public DriverService createService(int port) {
        EdgeDriverService.Builder builder = new EdgeDriverService.Builder();
        BrowserConfig config = BrowserConfig.instance();
        String wdPath = config.getValue(BROWSER, "webdriver-exe-path");
        if (wdPath != null)
            builder.usingDriverExecutable(new File(wdPath));
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

    private static class EdgeBrowserTab extends BrowserTab {
        public EdgeBrowserTab(String name) {
            super(name);
        }

        @Override
        protected String getBrowserName() {
            return BROWSER;
        }

        @Override
        protected String getWebDriverExecutableName() {
            return "MicroSoftWebDriver";
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
        protected void addBrowserExeBrowse() {
        }

        @Override
        public void addWdArguments() {
        }

        @Override
        public void addUseTechnologyPreview() {
        }

        @Override
        public void addUseCleanSession() {
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
        return new EdgeBrowserTab(name);
    }

    @Override
    public Map<String, ?> getCapabilities() {
        BrowserConfig config = BrowserConfig.instance();
        Map<String, Object> caps = new HashMap<>();
        String value = config.getValue(BROWSER, "browser-page-load-strategy");
        if (value != null)
            caps.put(CapabilityType.PAGE_LOAD_STRATEGY, value);
        return caps;
    }
}
