/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.runtime.IWebDriverRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.IWebdriverProxy;
import net.sourceforge.marathon.runtime.TestLauncher;
import net.sourceforge.marathon.runtime.WebDriverRuntimeFactory;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.ITestLauncher;
import net.sourceforge.marathon.runtime.api.Preferences;
import net.sourceforge.marathon.runtime.fx.api.ISubPropertiesLayout;

public class WebRuntimeLauncherModel implements IRuntimeLauncherModel, IWebDriverRuntimeLauncherModel {

    public static final Logger LOGGER = Logger.getLogger(WebRuntimeLauncherModel.class.getName());

    static {
        EnvironmentVariables.setProperties();
    }

    private DriverProxy driverProxy = DriverProxy.instance();

    @Override
    public ISubPropertiesLayout[] getSublayouts(ModalDialog<?> parent) {
        return new ISubPropertiesLayout[] { new WebAppLauncherLayout(parent) };
    }

    @Override
    public List<String> getPropertyKeys() {
        return Arrays.asList(WebAppLauncherLayout.AUT_WEBAPP_URL_PREFIX, WebAppLauncherLayout.AUT_WEBAPP_URL_PATH,
                WebAppLauncherLayout.AUT_WEBAPP_WIDTH, WebAppLauncherLayout.AUT_WEBAPP_HIEGHT);
    }

    @Override
    public IRuntimeFactory getRuntimeFactory() {
        return new WebDriverRuntimeFactory(this);
    }

    @Override
    public ITestLauncher createLauncher(Properties props) {
        Map<String, Object> ps = new HashMap<String, Object>();
        Enumeration<Object> ks = props.keys();
        while (ks.hasMoreElements()) {
            String object = (String) ks.nextElement();
            ps.put(object, props.getProperty(object));
        }
        return new TestLauncher(this, ps);
    }

    @Override
    public String getFramework() {
        return Constants.FRAMEWORK_WEB;
    }

    @Override
    public IWebdriverProxy createDriver(Map<String, Object> props, int recordingPort, OutputStream outputStream) {
        String urlPrefix = (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_URL_PREFIX);
        String urlPath = (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_URL_PATH);
        String url = createURL(urlPrefix, urlPath);
        TestAttributes.put("marathon.profile.url", url);
        TestAttributes.put("initial.width", (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_WIDTH));
        TestAttributes.put("initial.hieght", (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_HIEGHT));
        return createProxy(props);
    }

    private String createURL(String urlPrefix, String urlPath) {
        StringBuilder sb = new StringBuilder();
        if (urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
        }
        sb.append(urlPrefix);
        if (urlPath.length() > 0) {
            if (urlPath.startsWith("/")) {
                urlPath = urlPath.substring(1);
            }
            sb.append("/").append(urlPath);
        }
        return sb.toString();
    }

    private IWebdriverProxy createProxy(Map<String, Object> props) {
        String proxyClassName = (String) props.get(Constants.AUT_WEBAPP_DEFAULT_BROWSER);
        if (proxyClassName == null) {
            String browserOverride = System.getProperty(Constants.AUT_WEBAPP_BROWSER_OVERRIDE);
            if (browserOverride != null)
                proxyClassName = Browser.findBrowserProxyByName(browserOverride);
            if (proxyClassName == null)
                proxyClassName = Preferences.instance().getValue("project", "browser",
                        System.getProperty(Constants.AUT_WEBAPP_DEFAULT_BROWSER));
        }
        String browserName = "firefox";
        Class<?> proxyClass = null;
        IWebdriverProxy webDriverProxy = null;
        try {
            proxyClass = Class.forName(proxyClassName);
            browserName = (String) proxyClass.getField("BROWSER").get(null);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
                | ClassNotFoundException e) {
        }
        driverProxy.startService(proxyClass, browserName);
        webDriverProxy = driverProxy;
        return webDriverProxy;
    }

    @Override
    public URL getProfileAsURL(Map<String, Object> props, int recordingPort, OutputStream outputStream)
            throws URISyntaxException, IOException {
        String urlPrefix = (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_URL_PREFIX);
        String urlPath = (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_URL_PATH);
        String url = createURL(urlPrefix, urlPath);
        TestAttributes.put("marathon.profile.url", url);
        TestAttributes.put("initial.width", (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_WIDTH));
        TestAttributes.put("initial.hieght", (String) props.get(WebAppLauncherLayout.AUT_WEBAPP_HIEGHT));
        String browserOverride = System.getProperty(Constants.AUT_WEBAPP_BROWSER_OVERRIDE);
        String proxyClass = null;
        if (browserOverride != null)
            proxyClass = Browser.findBrowserProxyByName(browserOverride);
        if (proxyClass == null)
            proxyClass = Preferences.instance().getValue("project", "browser",
                    System.getProperty(Constants.AUT_WEBAPP_DEFAULT_BROWSER));
        String browserName = "firefox";
        Class<?> proxyBrowser = null;
        try {
            proxyBrowser = Class.forName(proxyClass);
            browserName = (String) proxyBrowser.getField("BROWSER").get(null);
        } catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException
                | IllegalAccessException e) {
            Logger.getLogger(WebRuntimeLauncherModel.class.getName())
                    .warning("Unable to load class: " + proxyClass + ". Defaulting to " + FirefoxWebDriverProxy.class.getName());
        }
        TestAttributes.put("browserName", browserName);
        return new URL(url);
    }

    @Override
    public boolean needReplaceEnviron() {
        return false;
    }

    @Override
    public boolean confirmConfiguration() {
        return true;
    }

    @Override
    public String getLaunchErrorMessage() {
     // @formatter:off
        return
            "For IE/Edge/Opera browsers a corresponding webdriver executable should be installed and be available in the path.\n";
        // @formatter:on
    }

}
