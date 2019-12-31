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

import org.openqa.selenium.ie.InternetExplorerDriverLogLevel;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

public class IEWebDriverProxy implements IWebBrowserProxy {

    public static final Logger LOGGER = Logger.getLogger(IEWebDriverProxy.class.getName());

    public static final String BROWSER = DesiredCapabilities.internetExplorer().getBrowserName();

    @Override
    public DriverService createService(int port) {
        InternetExplorerDriverService.Builder builder = new InternetExplorerDriverService.Builder();
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
        String logLevel = config.getValue(BROWSER, "webdriver-ie-log-level");
        if (logLevel != null)
            builder.withLogLevel(InternetExplorerDriverLogLevel.valueOf(logLevel));
        builder.withSilent(config.getValue(BROWSER, "webdriver-silent", true));
        return builder.usingPort(port).build();
    }

    private static class IEBrowserTab extends BrowserTab {
        public IEBrowserTab(String name) {
            super(name);
        }

        @Override
        protected String getBrowserName() {
            return BROWSER;
        }

        @Override
        protected String getWebDriverExecutableName() {
            return "IEDriverServer";
        }

        @Override
        public void addVerbose() {
        }

        @Override
        protected void addLogFileBrowse() {
            super.addLogFileBrowse();
        }

        @Override
        protected void addBrowserExeBrowse() {
        }

        @Override
        public void addWdArguments() {
        }

        @Override
        public void addUseCleanSession() {
        }

        @Override
        public void addUseTechnologyPreview() {
        }

        @Override
        public void addExtensions() {
        }

        @Override
        public void addUnexpectedAlertBehavior() {
        }

        @Override
        public void addAcceptUntrustedCertificates() {
        }

        @Override
        public void addAssumeUntrustedCertificateIssuer() {
        }

        @Override
        public void addBrowserPreferences() {
        }

        @Override
        public void addAlwaysLoadNoFocusLib() {
        }
    }

    @Override
    public BrowserTab getTab(String name) {
        return new IEBrowserTab(name);
    }

    @Override
    public Map<String, ?> getCapabilities() {
        BrowserConfig config = BrowserConfig.instance();
        HashMap<String, Object> ieOptions = new HashMap<>();
        String value = config.getValue(BROWSER, "browser-arguments");
        if (value != null) {
            StringBuilder args = new StringBuilder();
            BufferedReader br = new BufferedReader(new StringReader(value));
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty())
                        args.append(line).append(" ");
                }
            } catch (IOException e) {
            }
            ieOptions.put("ie.browserCommandLineSwitches", args.toString().trim());
        }

        HashMap<String, Object> caps = new HashMap<>();
        caps.put("se:ieOptions", ieOptions);
        return caps;
    }
}
