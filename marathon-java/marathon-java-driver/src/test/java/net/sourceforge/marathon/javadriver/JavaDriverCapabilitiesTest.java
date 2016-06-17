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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Test public class JavaDriverCapabilitiesTest {

    private WebDriver driver;

    protected JFrame frame;
    protected JTextField textField;
    protected JButton button;
    protected JMenu menu;
    protected JMenuItem exitItem;
    protected boolean buttonClicked = false;
    protected StringBuilder buttonMouseActions;

    @AfterMethod public void quitDriver() {
        if (driver != null)
            driver.quit();
    }

    public void javaDriver() {
        driver = new JavaDriver();
        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
        AssertJUnit.assertEquals("java", capabilities.getBrowserName());
        AssertJUnit.assertEquals(true, capabilities.is("takesScreenshot"));
        AssertJUnit.assertEquals(false, capabilities.is("nativeEvents"));
    }

    public void createSessionWithDefaultCapabilities() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setVersion("");
        driver = new JavaDriver(desiredCapabilities, desiredCapabilities);
    }
}
