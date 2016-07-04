package net.sourceforge.marathon.runtime;

import org.openqa.selenium.Platform;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Test public class SafariWebDriverProxyTest extends WebDriverProxyTest {

    public SafariWebDriverProxyTest() {
        super(SafariWebDriverProxy.class);
    }

    @Override protected void checkPlatform() {

        if (!Platform.getCurrent().is(Platform.MAC))
            throw new SkipException("Safari Driver supported only on mac");
    }
}
