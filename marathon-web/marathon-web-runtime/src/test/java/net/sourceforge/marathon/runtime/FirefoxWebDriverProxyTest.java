/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

@Test
public class FirefoxWebDriverProxyTest extends WebDriverProxyTest {

    public FirefoxWebDriverProxyTest() {
        super(FirefoxWebDriverProxy.class, DesiredCapabilities.firefox());
    }

}
