package net.sourceforge.marathon.javadriver.recorder;

import java.io.File;
import java.util.List;

import net.sourceforge.marathon.javadriver.ClassPathHelper;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.sun.swingset3.SwingSet3;

@Test public class LaunchWebStartTest extends RecordingTest {

    private JavaDriver driver;

    private void createDriver(String title) {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_WEBSTART);
        File f = findFile();
        profile.setJNLPFile(f);
        profile.setStartWindowTitle(title);
        profile.setRecordingPort(startRecordingServer());
        System.err.println("Launching: " + profile.getCommandLine());
        driver = new JavaDriver(profile);
    }

    @AfterMethod public void quitDriver() {
        if (driver != null)
            driver.quit();
    }

    public void checkBasicRecording() throws Throwable {
        createDriver("SwingSet3");
        new WebDriverWait(driver, 60).until(new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver driver) {
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
        AssertJUnit.assertTrue(scriptElements.size() > 0);
    }

    private File findFile() {
        File f = new File(new File(ClassPathHelper.getClassPath(SwingSet3.class)).getParentFile(), "SwingSet3.jnlp");
        if (f.exists())
            return f;
        f = new File(System.getProperty("SwingSet3.jnlp", "../marathon-test-helpers/swingset3/SwingSet3.jnlp"));
        if (f.exists())
            return f;
        return null;
    }
}
