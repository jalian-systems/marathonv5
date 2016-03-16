package net.sourceforge.marathon.javadriver.recorder;

import java.io.File;
import java.util.List;

import net.sourceforge.marathon.javadriver.ClassPathHelper;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.sun.swingset3.SwingSet3;

@Test public class LaunchAppletTest extends RecordingTest {

    private JavaDriver driver;

    private void createDriver(String title) {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_APPLET);
        File f = findFile();
        profile.setAppletURL(f.getAbsolutePath());
        if (title != null)
            profile.setStartWindowTitle(title);
        profile.setRecordingPort(startRecordingServer());
        System.out.println(profile.getCommandLine());
        driver = new JavaDriver(profile);
    }

    @AfterMethod public void quitDriver() {
        if (driver != null)
            driver.quit();
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
        new WebDriverWait(driver, 10).until(new Predicate<WebDriver>() {
            @Override public boolean apply(WebDriver driver) {
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
        if (f.exists())
            return f;
        return null;
    }

}
