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
package net.sourceforge.marathon.javadriver;

import java.util.Set;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class JavaDriver extends RemoteWebDriver {

    public static final String JAVA6PLUS = "1.6+";
    public static final String JAVA6 = "1.6";
    public static final String JAVA7PLUS = "1.7+";
    public static final String JAVA7 = "1.7";

    public JavaDriver() {
        this(defaultCapabilities());
    }

    public JavaDriver(Capabilities desiredCapabilities) {
        this(desiredCapabilities, null);
    }

    public JavaDriver(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        this(extractProfile(desiredCapabilities, requiredCapabilities), desiredCapabilities, requiredCapabilities);
    }

    public JavaDriver(JavaProfile profile) {
        this(profile, defaultCapabilities());
    }

    public JavaDriver(JavaProfile profile, DesiredCapabilities defaultCapabilities) {
        this(profile, defaultCapabilities(), null);
    }

    public JavaDriver(JavaProfile profile, Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        super(new JavaDriverCommandExecutor(profile), dropCapabilities(desiredCapabilities, CapabilityType.VERSION),
                dropCapabilities(requiredCapabilities, CapabilityType.VERSION));
    }

    private static Capabilities dropCapabilities(Capabilities capabilities, String... keysToRemove) {
        if (capabilities == null) {
            return new DesiredCapabilities();
        }
        final Set<String> toRemove = Sets.newHashSet(keysToRemove);
        DesiredCapabilities caps = new DesiredCapabilities(Maps.filterKeys(capabilities.asMap(), new Predicate<String>() {
            public boolean apply(String key) {
                return !toRemove.contains(key);
            }
        }));

        return caps;
    }

    private static JavaProfile extractProfile(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        JavaProfile javaProfile = new JavaProfile();
        if (requiredCapabilities == null)
            return javaProfile;
        return javaProfile;
    }

    public static DesiredCapabilities defaultCapabilities() {
        return new DesiredCapabilities("java", "1.0", org.openqa.selenium.Platform.ANY);
    }

    @Override public void setFileDetector(FileDetector detector) {
        throw new WebDriverException(
                "Setting the file detector only works on remote webdriver instances obtained " + "via RemoteWebDriver");
    }

    @Override protected void startClient() {
        JavaDriverCommandExecutor executor = (JavaDriverCommandExecutor) getCommandExecutor();
        executor.start();
    }

    @Override protected void stopClient() {
        JavaDriverCommandExecutor executor = (JavaDriverCommandExecutor) getCommandExecutor();
        executor.stop();
    }

    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        // Get the screenshot as base64.
        String base64 = (String) execute(DriverCommand.SCREENSHOT).getValue();
        // ... and convert it.
        return target.convertFromBase64Png(base64);
    }

    public void clearlogs(String logType) {
        Logs logs = manage().logs();
        logs.get(logType);
    }

    public void quit() {
        try {
            super.quit();
        } catch (Throwable t) {
        } finally {
        }
    }

    @Override protected void finalize() throws Throwable {
        quit();
    }
}
