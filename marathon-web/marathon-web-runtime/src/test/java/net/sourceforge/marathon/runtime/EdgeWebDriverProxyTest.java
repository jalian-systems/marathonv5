/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test
public class EdgeWebDriverProxyTest extends WebDriverProxyTest {

    public EdgeWebDriverProxyTest() {
        super(EdgeWebDriverProxy.class, DesiredCapabilities.edge());
    }

    @Override
    protected void checkPlatform() {
        if (!Platform.getCurrent().is(Platform.WIN10)) {
            throw new SkipException("Edge driver is supported only on windows 10");
        }
    }
}
