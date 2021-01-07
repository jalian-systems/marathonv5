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
public class IEWebDriverProxyTest extends WebDriverProxyTest {

    public IEWebDriverProxyTest() {
        super(IEWebDriverProxy.class, DesiredCapabilities.internetExplorer());
    }

    @Override
    protected void checkPlatform() {

        if (!Platform.getCurrent().is(Platform.WINDOWS)) {
            throw new SkipException("IE Driver supported only on windows");
        }
    }
}
