package net.sourceforge.marathon.javadriver.cmdlinelauncher;

import java.io.File;
import java.util.List;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;

import org.apache.commons.exec.OS;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.os.CommandLine;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

@Test public class LaunchAppletTest {

    private JavaDriver driver;

    private void createDriver(String title) {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_APPLET);
        File f = findFile();
        profile.setAppletURL(f.getAbsolutePath());
        if (title != null)
            profile.setStartWindowTitle(title);
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

    public void getDriverWithProfile() throws Throwable {
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
    }

    public void getDriverWithProfileNoWindowTitle() throws Throwable {
        createDriver(null);
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
    }

    public void getDriverWithProfileUsingRegexForTitle() throws Throwable {
        createDriver("/.*SwingSet3Init.class");
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
    }

    private File findFile() {
        File f = new File(System.getProperty("applet.html", "../marathon-test-helpers/swingset3/applet.html"));
        if (f.exists())
            return f;
        return null;
    }

    public void checkForArguments() throws Throwable {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_APPLET);
        File f = findFile();
        profile.setAppletURL(f.getAbsolutePath());
        profile.setStartWindowTitle("Applet Viewer: SwingSet3Init.class");
        profile.addVMArgument("-Dhello=world");
        CommandLine commandLine = profile.getCommandLine();
        AssertJUnit.assertTrue(commandLine.toString().contains("-Dhello=world"));
    }

    public void checkGivenExecutableIsUsed() throws Throwable {
        JavaProfile profile = new JavaProfile(LaunchMode.JAVA_APPLET);
        File f = findFile();
        profile.setAppletURL(f.getAbsolutePath());
        profile.setStartWindowTitle("Applet Viewer: SwingSet3Init.class");
        String actual = "";
        if (OS.isFamilyWindows()) {
            String path = System.getenv("Path");
            String[] split = path.split(";");
            File file = new File(split[0]);
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].getName().contains(".exe")) {
                    profile.setJavaCommand(listFiles[i].getAbsolutePath());
                    actual = listFiles[i].getAbsolutePath();
                    break;
                }
            }
        } else {
            actual = "ls";
            profile.setJavaCommand(actual);
        }
        CommandLine commandLine = profile.getCommandLine();
        AssertJUnit.assertTrue(commandLine.toString().contains(actual));
    }
}
