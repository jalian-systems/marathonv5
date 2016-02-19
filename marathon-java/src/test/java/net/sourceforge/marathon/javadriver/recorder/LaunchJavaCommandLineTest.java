package net.sourceforge.marathon.javadriver.recorder;

import java.io.File;
import java.util.List;

import net.sourceforge.marathon.javadriver.ClassPathHelper;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;

import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.swingset3.SwingSet3;

@Test public class LaunchJavaCommandLineTest extends RecordingTest {

    private JavaDriver driver;
    public LaunchJavaCommandLineTest() {
    }

    @BeforeMethod public void createDriver() {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_COMMAND_LINE);
        File f = findFile();
        profile.addClassPath(f);
        profile.setRecordingPort(startRecordingServer());
        profile.setMainClass("com.sun.swingset3.SwingSet3");
        DesiredCapabilities caps = new DesiredCapabilities("java", "1.5", Platform.ANY);
        driver = new JavaDriver(profile, caps, caps);
    }

    @AfterMethod public void quitDriver() {
        driver.quit();
    }

    public void checkBasicRecording() throws Throwable {
        List<WebElement> buttons = driver.findElements(By.cssSelector("toggle-button"));
        AssertJUnit.assertTrue(buttons.size() > 0);
        buttons.get(3).click();
        buttons.get(0).click();
        System.out.println(scriptElements);
        AssertJUnit.assertTrue(scriptElements.size() > 0);
    }

    private File findFile() {
        return new File(ClassPathHelper.getClassPath(SwingSet3.class));
    }

}
