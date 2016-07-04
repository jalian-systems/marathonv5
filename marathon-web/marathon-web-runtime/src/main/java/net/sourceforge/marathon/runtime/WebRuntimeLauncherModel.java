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

import javax.swing.JDialog;

import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IRuntimeFactory;
import net.sourceforge.marathon.runtime.api.IRuntimeLauncherModel;
import net.sourceforge.marathon.runtime.api.ISubPropertiesPanel;
import net.sourceforge.marathon.runtime.api.ITestLauncher;

public class WebRuntimeLauncherModel implements IRuntimeLauncherModel, IWebDriverRuntimeLauncherModel {

    @Override public ISubPropertiesPanel[] getSubPanels(JDialog parent) {
        return new ISubPropertiesPanel[] { new WebAppLauncherPanel(parent) };
    }

    @Override public List<String> getPropertyKeys() {
        return Arrays.asList(WebAppLauncherPanel.AUT_WEBAPP_URL_PREFIX, WebAppLauncherPanel.AUT_WEBAPP_URL_PATH);
    }

    @Override public IRuntimeFactory getRuntimeFactory() {
        return new WebDriverRuntimeFactory(this);
    }

    public ITestLauncher createLauncher(Properties props) {
        Map<String, Object> ps = new HashMap<String, Object>();
        Enumeration<Object> ks = props.keys();
        while (ks.hasMoreElements()) {
            String object = (String) ks.nextElement();
            ps.put(object, props.getProperty(object));
        }
        return new TestLauncher(this, ps);
    }

    @Override public String getFramework() {
        return Constants.FRAMEWORK_WEB;
    }

    @Override public IWebdriverProxy createDriver(Map<String, Object> props, int recordingPort, OutputStream outputStream) {
        String urlPrefix = (String) props.get(WebAppLauncherPanel.AUT_WEBAPP_URL_PREFIX);
        String urlPath = (String) props.get(WebAppLauncherPanel.AUT_WEBAPP_URL_PATH);
        String url = createURL(urlPrefix, urlPath);
        TestAttributes.put("marathon.profile.url", url);
        return createProxy();
    }

    private String createURL(String urlPrefix, String urlPath) {
        StringBuilder sb = new StringBuilder();
        if(urlPrefix.endsWith("/"))
            urlPrefix = urlPrefix.substring(0, urlPrefix.length() - 1);
        sb.append(urlPrefix);
        if(urlPath.length() > 0) {
            if(urlPath.startsWith("/"))
                urlPath = urlPath.substring(1);
            sb.append("/").append(urlPath);
        }
        return sb.toString();
    }

    private IWebdriverProxy createProxy() {
        String proxyClass = System.getProperty(Constants.PROP_BROWSER, FirefoxWebDriverProxy.class.getName());
        try {
            return (IWebdriverProxy) Class.forName(proxyClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            return new FirefoxWebDriverProxy();
        }
    }

    @Override public URL getProfileAsURL(Map<String, Object> props, int recordingPort, OutputStream outputStream)
            throws URISyntaxException, IOException {
        return null;
    }

    @Override public boolean needReplaceEnviron() {
        return false;
    }

    @Override public boolean confirmConfiguration() {
        return true;
    }
}
