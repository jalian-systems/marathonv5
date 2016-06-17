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
package net.sourceforge.marathon.javadriver.cmdlinelauncher;

import java.io.File;
import java.util.List;

import net.sourceforge.marathon.javadriver.ClassPathHelper;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;

import org.apache.commons.exec.OS;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sun.swingset3.SwingSet3;

@Test public class LaunchCommandLineTest {

    private JavaDriver driver;

    public LaunchCommandLineTest() {
    }

    @BeforeClass public void createDriver() {
        JavaProfile profile = new JavaProfile(LaunchMode.COMMAND_LINE);
        File f = findFile();
        profile.setCommand(f.getAbsolutePath());
        profile.addApplicationArguments("Argument1");
        DesiredCapabilities caps = new DesiredCapabilities("java", "1.5", Platform.ANY);
        driver = new JavaDriver(profile, caps, caps);
    }

    @AfterClass public void quitDriver() {
        driver.quit();
    }

    public void getDriverWithProfile() throws Throwable {
        List<WebElement> buttons = driver.findElements(By.cssSelector("toggle-button"));
        AssertJUnit.assertTrue(buttons.size() > 0);
        buttons.get(3).click();
        buttons.get(0).click();
    }

    private File findFile() {
        if (OS.isFamilyWindows())
            return new File(new File(ClassPathHelper.getClassPath(SwingSet3.class)).getParentFile(), "swingset3.bat");
        return new File(new File(ClassPathHelper.getClassPath(SwingSet3.class)).getParentFile(), "swingset3.sh");
    }
}
