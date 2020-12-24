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
package net.sourceforge.marathon.javadriver.cmdlinelauncher;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;

@Test
public class LaunchWebStartTest {

    private JavaDriver driver;

    @SuppressWarnings("unused")
    private void createDriver(String title) {
        if (true) {
            throw new SkipException("WebStartTest skipped due to unreachable application online.");
        }
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_WEBSTART);
        File f = findFile();
        profile.setJNLPPath(f.getAbsolutePath());
        profile.setStartWindowTitle(title);
        driver = new JavaDriver(profile);
    }

    @AfterMethod
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    public void getDriverWithProfile() throws Throwable {
        createDriver("SwingSet3");
        new WebDriverWait(driver, 60).until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    driver.switchTo().window("SwingSet3");
                } catch (NoSuchWindowException e) {
                    System.out.println("LaunchWebStartTest.get_driver_with_profile(): window not found");
                    System.out.println(driver.getTitle());
                    return false;
                }
                List<WebElement> buttons = driver.findElements(By.cssSelector("toggle-button"));
                return buttons.size() > 0;
            }
        });
        List<WebElement> buttons = driver.findElements(By.cssSelector("toggle-button"));
        AssertJUnit.assertTrue(buttons.size() > 0);
        buttons.get(3).click();
        buttons.get(0).click();
    }

    public void getDriverWithProfileUsingRegexForTitle() throws Throwable {
        createDriver("/S.*3");
        new WebDriverWait(driver, 60).until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    driver.switchTo().window("SwingSet3");
                } catch (NoSuchWindowException e) {
                    System.out.println("LaunchWebStartTest.get_driver_with_profile(): window not found");
                    System.out.println(driver.getTitle());
                    return false;
                }
                List<WebElement> buttons = driver.findElements(By.cssSelector("toggle-button"));
                return buttons.size() > 0;
            }
        });
        List<WebElement> buttons = driver.findElements(By.cssSelector("toggle-button"));
        AssertJUnit.assertTrue(buttons.size() > 0);
        buttons.get(3).click();
        buttons.get(0).click();
    }

    private static File findFile() {
        File f = new File(System.getProperty("SwingSet3.jnlp", "../marathon-test-helpers/swingset3/SwingSet3.jnlp"));
        if (f.exists()) {
            return f;
        }
        return null;
    }

    public void checkForArguments() throws Throwable {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_WEBSTART);
        File f = findFile();
        profile.setJNLPPath(f.getAbsolutePath());
        profile.setStartWindowTitle("SwingSet3");
        profile.addVMArgument("-Dhello=world");
        CommandLine commandLine = profile.getCommandLine();
        System.out.println(commandLine);
        AssertJUnit.assertTrue(commandLine.toString().matches(".*JAVA_TOOL_OPTIONS=.*-Dhello=world.*"));
    }

    public void checkGivenExecutableIsUsed() throws Throwable {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_WEBSTART);
        profile.setJavaCommand("java");
        File f = findFile();
        profile.setJNLPPath(f.getAbsolutePath());
        profile.setStartWindowTitle("SwingSet3");
        profile.addVMArgument("-Dhello=world");
        CommandLine commandLine = profile.getCommandLine();
        String exec = findExecutableOnPath("java");
        AssertJUnit.assertTrue(commandLine.toString(), commandLine.toString().contains(exec));
    }

    public static String findExecutableOnPath(String name) {
        if (!Platform.getCurrent().is(Platform.WINDOWS) || name.endsWith(".exe") || name.endsWith(".bat")) {
            return getPathTo(name);
        }
        String path;
        path = getPathTo(name + ".exe");
        if (path != null)
            return path;
        path = getPathTo(name + ".cmd");
        if (path != null)
            return path;
        return getPathTo(name + ".bat");
    }

    public static String getPathTo(String name) throws AssertionError {
        String path = System.getenv("Path");
        if (path == null)
            path = System.getenv("PATH");
        for (String dirname : path.split(File.pathSeparator)) {
            File file = new File(dirname, name);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_WEBSTART);
        File f = findFile();
        profile.setJNLPPath(f.getAbsolutePath());
        profile.setStartWindowTitle("SwingSet3");
        CommandLine commandLine = profile.getCommandLine();
        commandLine.copyOutputTo(System.err);
        System.out.println(commandLine);
        commandLine.execute();

    }
}
