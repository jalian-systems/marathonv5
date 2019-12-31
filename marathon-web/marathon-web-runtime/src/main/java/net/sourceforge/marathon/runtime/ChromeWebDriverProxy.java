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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import com.google.common.io.Files;

public class ChromeWebDriverProxy implements IWebBrowserProxy {

    public static final Logger LOGGER = Logger.getLogger(ChromeWebDriverProxy.class.getName());

    public static final String BROWSER = DesiredCapabilities.chrome().getBrowserName();

    @Override
    public DriverService createService(int port) {
        BrowserConfig config = BrowserConfig.instance();
        String wdPath = config.getValue(BROWSER, "webdriver-exe-path");
        ChromeDriverService.Builder builder = new ChromeDriverService.Builder();
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
        builder.withVerbose(config.getValue(BROWSER, "webdriver-verbose", false));
        builder.withSilent(config.getValue(BROWSER, "webdriver-silent", true));
        return builder.usingPort(port).build();
    }

    private static class ChromeBrowserTab extends BrowserTab {
        public ChromeBrowserTab(String name) {
            super(name);
        }

        @Override
        protected String getBrowserName() {
            return BROWSER;
        }

        @Override
        protected String getWebDriverExecutableName() {
            return "chromedriver";
        }

        @Override
        public String getExtensionDescription() {
            return "Chrome Browser Extensions";
        }

        @Override
        public String getExtensionExt() {
            return "*.crx";
        }

        @Override
        public void addAssumeUntrustedCertificateIssuer() {
        }

        @Override
        public void addAcceptUntrustedCertificates() {
        }

        @Override
        public void addAlwaysLoadNoFocusLib() {
        }

        @Override
        public void addBrowserPreferences() {
        }

        @Override
        public void addUseCleanSession() {
        }

        @Override
        public void addUseTechnologyPreview() {
        }

        @Override
        public void addWdArguments() {
        }

        @Override
        public void addIELogLevel() {
        }
    }

    @Override
    public BrowserTab getTab(String name) {
        return new ChromeBrowserTab(name);
    }

    @Override
    public Map<String, ?> getCapabilities() {
        BrowserConfig config = BrowserConfig.instance();
        Map<String, Object> chromeOptions = new HashMap<>();
        String value = config.getValue(BROWSER, "browser-exe-path");
        if (value != null)
            chromeOptions.put("binary", value);
        value = config.getValue(BROWSER, "browser-arguments");
        if (value != null) {
            List<String> args = new ArrayList<>();
            BufferedReader br = new BufferedReader(new StringReader(value));
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty())
                        args.add(line);
                }
            } catch (IOException e) {
            }
            chromeOptions.put("args", args);
        }
        value = config.getValue(BROWSER, "browser-extensions");
        if (value != null) {
            List<String> exts = new ArrayList<>();
            BufferedReader br = new BufferedReader(new StringReader(value));
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        String encoded = Base64.getEncoder().encodeToString(Files.toByteArray(new File(line)));
                        exts.add(encoded);
                    }
                }
            } catch (IOException e) {
            }
            chromeOptions.put("extensions", exts);
        }
        Map<String, Object> caps = new HashMap<>();
        caps.put("chromeOptions", chromeOptions);
        value = config.getValue(BROWSER, "browser-page-load-strategy");
        if (value != null)
            caps.put(CapabilityType.PAGE_LOAD_STRATEGY, value);
        value = config.getValue(BROWSER, "browser-unexpected-alert-behaviour");
        if (value != null) {
            caps.put(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, value);
            caps.put(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, value);
        }
        return caps;
    }
}
