/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.io.File;
import java.io.FileFilter;

import org.openqa.selenium.Platform;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import net.sourceforge.marathon.runtime.OperaWebDriverProxy;

@Test
public class OperaWebDriverProxyTest extends WebDriverProxyTest {

    private static DesiredCapabilities caps = DesiredCapabilities.operaBlink();

    static {
        if (Platform.getCurrent().is(Platform.WINDOWS)) {
            OperaOptions options = new OperaOptions();
            File f = findOperaExe();
            options.setBinary(f);
            // caps.setCapability("operaOptions", options);
        }
    }

    public OperaWebDriverProxyTest() {
        super(OperaWebDriverProxy.class, caps);
    }

    private static File findOperaExe() {
        return findFile(new File("C:\\Program Files"));
    }

    private static File findFile(File dir) {
        File[] files = dir.listFiles((FileFilter) pathname -> pathname.isFile() && pathname.getName().equals("opera.exe"));
        if (files != null && files.length == 1)
            return files[0];
        File[] dirs = dir.listFiles((FileFilter) pathname -> pathname.isDirectory());
        for (File sdir : dirs) {
            File f = findFile(sdir);
            if (f != null)
                return f;
        }
        return null;
    }

}
