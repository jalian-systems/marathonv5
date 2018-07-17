/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javadriver;

import java.util.Set;
import java.util.logging.Logger;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.FileDetector;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UselessFileDetector;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link WebDriver} interface that driver Java applications.
 * 
 * <p>
 * A {@code JavaDriver} can be created by using {@link JavaProfile} as follows:
 * </p>
 * 
 * <pre>
 * <code>
 *       JavaProfile profile = new JavaProfile(LaunchMode.COMMAND_LINE);
 *       profile.setWorkingDirectory("some-folder").setCommand("path-to-batch-script");
 *       JavaDriver driver = new JavaDriver(profile);
 * </code>
 * </pre>
 *
 */
public class JavaDriver extends RemoteWebDriver {

    public static final Logger LOGGER = Logger.getLogger(JavaDriver.class.getName());

    /**
     * Create a {@code JavaDriver}
     * 
     * <p>
     * Use {@link JavaDriver#JavaDriver(JavaProfile)}
     */
    public JavaDriver() {
        this(defaultCapabilities());
    }

    /**
     * Create a {@code JavaDriver}
     * 
     * <p>
     * Use {@link JavaDriver#JavaDriver(JavaProfile)}
     * </p>
     * 
     * @param desiredCapabilities
     *            desired capabilities
     */
    public JavaDriver(Capabilities desiredCapabilities) {
        this(desiredCapabilities, null);
    }

    /**
     * Create a {@code JavaDriver}
     * 
     * <p>
     * Use {@link JavaDriver#JavaDriver(JavaProfile)}
     * </p>
     * 
     * @param desiredCapabilities
     *            desired capabilities
     * @param requiredCapabilities
     *            required capabilities
     */
    public JavaDriver(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        this(extractProfile(desiredCapabilities, requiredCapabilities), desiredCapabilities, requiredCapabilities);
    }

    /**
     * Constructs a {@code JavaDriver} with the given profile
     * 
     * <p>
     * Once the {@code JavaDriver} is constructed, the AUT will be launched and
     * the driver is ready for operations. Unlike in {@link WebDriver}
     * implementations for browsers, there is no need for calling a
     * {@link WebDriver#get(String)} method.
     * </p>
     * 
     * @param profile
     *            the java profile
     */
    public JavaDriver(JavaProfile profile) {
        this(profile, defaultCapabilities());
    }

    /**
     * Constructs a {@code JavaDriver} with the given profile
     * 
     * <p>
     * Once the {@code JavaDriver} is constructed, the AUT will be launched and
     * the driver is ready for operations. Unlike in {@link WebDriver}
     * implementations for browsers, there is no need for calling a
     * {@link WebDriver#get(String)} method.
     * </p>
     * 
     * @param profile
     *            the java profile
     * @param desiredCapabilities
     *            desired capabilities
     */
    public JavaDriver(JavaProfile profile, DesiredCapabilities desiredCapabilities) {
        this(profile, defaultCapabilities(), null);
    }

    /**
     * Constructs a {@code JavaDriver} with the given profile
     * 
     * <p>
     * Once the {@code JavaDriver} is constructed, the AUT will be launched and
     * the driver is ready for operations. Unlike in {@link WebDriver}
     * implementations for browsers, there is no need for calling a
     * {@link WebDriver#get(String)} method.
     * </p>
     * 
     * <p>
     * The only capability of interest may be <code>nativeEvents</code>
     * </p>
     * 
     * @param profile
     *            the java profile
     * @param desiredCapabilities
     *            desired capabilities
     * @param requiredCapabilities
     *            required capabilities
     */
    public JavaDriver(JavaProfile profile, Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        super(new JavaDriverCommandExecutor(profile), dropCapabilities(desiredCapabilities, CapabilityType.VERSION));
    }

    private static Capabilities dropCapabilities(Capabilities capabilities, String... keysToRemove) {
        if (capabilities == null) {
            return new DesiredCapabilities();
        }
        final Set<String> toRemove = Sets.newHashSet(keysToRemove);
        DesiredCapabilities caps = new DesiredCapabilities(Maps.filterKeys(capabilities.asMap(), new Predicate<String>() {
            @Override
            public boolean apply(String key) {
                return !toRemove.contains(key);
            }
        }));

        return caps;
    }

    private static JavaProfile extractProfile(Capabilities desiredCapabilities, Capabilities requiredCapabilities) {
        JavaProfile javaProfile = new JavaProfile();
        if (requiredCapabilities == null) {
            return javaProfile;
        }
        return javaProfile;
    }

    /**
     * Default capabilities for {@code JavaDriver}
     * 
     * @return default capabilities
     */
    public static DesiredCapabilities defaultCapabilities() {
        return new DesiredCapabilities("java", "1.0", org.openqa.selenium.Platform.ANY);
    }

    /**
     * Not implemented
     *
     * @param detector
     *            The detector to use. Must not be null.
     * @see FileDetector
     * @see LocalFileDetector
     * @see UselessFileDetector
     */
    @Override
    public void setFileDetector(FileDetector detector) {
        throw new WebDriverException(
                "Setting the file detector only works on remote webdriver instances obtained " + "via RemoteWebDriver");
    }

    /**
     * Capture the screenshot and store it in the specified location.
     *
     * <p>
     * For WebDriver extending TakesScreenshot, this makes a best effort
     * depending on the browser to return the following in order of preference:
     * <ul>
     * <li>Entire page</li>
     * <li>Current window</li>
     * <li>Visible portion of the current frame</li>
     * <li>The screenshot of the entire display containing the browser</li>
     * </ul>
     *
     * <p>
     * For WebElement extending TakesScreenshot, this makes a best effort
     * depending on the browser to return the following in order of preference:
     * - The entire content of the HTML element - The visible portion of the
     * HTML element
     *
     * @param <X>
     *            Return type for getScreenshotAs.
     * @param target
     *            target type, @see OutputType
     * @return Object in which is stored information about the screenshot.
     * @throws WebDriverException
     *             on failure.
     * @throws UnsupportedOperationException
     *             if the underlying implementation does not support screenshot
     *             capturing.
     */
    @Override
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

    /**
     * Quits the driver
     */
    @Override
    public void quit() {
        try {
            super.quit();
        } catch (Throwable t) {
        } finally {
        }
    }

    @Override
    protected void finalize() throws Throwable {
        quit();
    }
}
