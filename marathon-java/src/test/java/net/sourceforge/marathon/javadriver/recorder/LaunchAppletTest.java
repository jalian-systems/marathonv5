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
package net.sourceforge.marathon.javadriver.recorder;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.sun.swingset3.SwingSet3;

import net.sourceforge.marathon.javadriver.ClassPathHelper;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.runtime.api.Constants;

@Test
public class LaunchAppletTest extends RecordingTest {

    private JavaDriver driver;

    private void createDriver(String title) {
        System.setProperty(Constants.PROP_PROJECT_FRAMEWORK, Constants.FRAMEWORK_SWING);
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_APPLET);
        File f = findFile();
        profile.setAppletURL(f.getAbsolutePath());
        if (title != null) {
            profile.setStartWindowTitle(title);
        }
        profile.setRecordingPort(startRecordingServer());
        System.out.println(profile.getCommandLine());
        driver = new JavaDriver(profile);
    }

    @AfterMethod
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static String formatDate() {
        // @formatter:off
        return
        "java.util.Calendar c = java.util.Calendar.getInstance();"+
        "c.set(2015, java.util.Calendar.MARCH, 12);"+
        "try {" +
            "return $1.getFormatter().valueToString(c.getTime());" +
        "} catch (Exception e) {" +
            "return null;" +
        "}" ;
        // @formatter:on
    }

    public void checkBasicRecording() throws Throwable {
        createDriver("Applet Viewer: SwingSet3Init.class");
        driver.switchTo().window("Applet Viewer: SwingSet3Init.class");
        new WebDriverWait(driver, 10).until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
                return buttons.size() > 0;
            }
        });
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        AssertJUnit.assertTrue(buttons.size() > 0);
        WebElement tfs = driver.findElement(By.cssSelector("formatted-text-field"));
        tfs.clear();
        String toSend = (String) driver.executeScript(formatDate(), tfs);
        System.out.println("To send = " + toSend);
        tfs.sendKeys(toSend);
        buttons.get(0).click();
        driver.findElement(By.cssSelector("label[text='Thursday']"));
        AssertJUnit.assertTrue(scriptElements.size() > 0);
    }

    private File findFile() {
        File f = new File(new File(ClassPathHelper.getClassPath(SwingSet3.class)).getParentFile(), "applet.html");
        if (f.exists()) {
            return f;
        }
        return null;
    }

}
