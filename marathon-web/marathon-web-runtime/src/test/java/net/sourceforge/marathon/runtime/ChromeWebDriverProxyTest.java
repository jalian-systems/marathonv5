/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import net.sourceforge.marathon.runtime.ChromeWebDriverProxy;

@Test
public class ChromeWebDriverProxyTest extends WebDriverProxyTest {

    public ChromeWebDriverProxyTest() {
        super(ChromeWebDriverProxy.class, DesiredCapabilities.chrome());
    }

}
