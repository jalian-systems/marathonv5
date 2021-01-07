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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;

public class FirefoxWebDriverProxy implements IWebBrowserProxy {

    public static final Logger LOGGER = Logger.getLogger(FirefoxWebDriverProxy.class.getName());

    public static final String BROWSER = DesiredCapabilities.firefox().getBrowserName();

    @Override
    public DriverService createService(int port) {
        GeckoDriverService.Builder builder = new GeckoDriverService.Builder();
        BrowserConfig config = BrowserConfig.instance();
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
        String wdPath = config.getValue(BROWSER, "webdriver-exe-path");
        if (wdPath != null)
            builder.usingDriverExecutable(new File(wdPath));
        String logFile = config.getValue(BROWSER, "webdriver-log-file-path");
        if (logFile != null) {
            builder.withLogFile(new File(logFile));
        }
        return builder.usingPort(port).build();
    }

    private static class FirefoxBrowserTab extends BrowserTab {
        public FirefoxBrowserTab(String name) {
            super(name);
        }

        @Override
        protected String getBrowserName() {
            return BROWSER;
        }

        @Override
        protected String getWebDriverExecutableName() {
            return "geckodriver";
        }

        @Override
        public void addVerbose() {
        }

        @Override
        public void addSilent() {
        }

        @Override
        public String getExtensionDescription() {
            return "Firefox Browser Extensions";
        }

        @Override
        public String getExtensionExt() {
            return "*.xpi";
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
        return new FirefoxBrowserTab(name);
    }

    @Override
    public Map<String, ?> getCapabilities() {
        BrowserConfig config = BrowserConfig.instance();
        Map<String, Object> firefoxOptions = new HashMap<>();

        FirefoxProfile profile = new FirefoxProfile();
        profile.setAssumeUntrustedCertificateIssuer(config.getValue(BROWSER, "browser-assume-untrusted-certificate-issuer", false));
        profile.setAlwaysLoadNoFocusLib(config.getValue(BROWSER, "browser-always-load-no-focus-lib", false));
        profile.setAcceptUntrustedCertificates(config.getValue(BROWSER, "browser-accept-untrusted-certificates", false));
        String value = config.getValue(BROWSER, "browser-preferences");
        if (value != null && !"".equals(value)) {
            JSONArray prefs = new JSONArray(value);
            for (int i = 0; i < prefs.length(); i++) {
                JSONObject p = prefs.getJSONObject(i);
                String type = p.getString("type");
                if (type.equals("string")) {
                    profile.setPreference(p.getString("name"), p.getString("value"));
                } else if (type.equals("integer")) {
                    profile.setPreference(p.getString("name"), Integer.valueOf(p.getString("value")));
                } else {
                    profile.setPreference(p.getString("name"), Boolean.valueOf(p.getString("value")));
                }
            }
        }
        profile.setPreference("network.websocket.allowInsecureFromHTTPS", true);
        value = config.getValue(BROWSER, "browser-extensions");
        if (value != null) {
            BufferedReader br = new BufferedReader(new StringReader(value));
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        profile.addExtension(new File(line));
                    }
                }
            } catch (IOException e) {
            }
        }
        try {
            String json = profile.toJson();
            firefoxOptions.put("profile", json);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            firefoxOptions.put("args", args);
        }
        value = config.getValue(BROWSER, "browser-exe-path");
        if (value != null)
            firefoxOptions.put("binary", value);
        Map<String, Object> caps = new HashMap<>();
        caps.put("moz:firefoxOptions", firefoxOptions);
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
