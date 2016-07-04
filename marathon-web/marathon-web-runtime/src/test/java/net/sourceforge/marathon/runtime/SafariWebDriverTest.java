package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import fi.iki.elonen.NanoHTTPD;

public class SafariWebDriverTest {

    private WebDriver driver;
    private NanoHTTPD server;

    @BeforeClass public void startServer() throws IOException {
        server = new NanoHTTPD(21346) {
            @Override public Response serve(IHTTPSession session) {
                try {
                    String data = IOUtils.toString(SafariWebDriverTest.class.getResourceAsStream("form.html"));
                    return newFixedLengthResponse(data);
                } catch (IOException e) {
                    return super.serve(session);
                }
            }
        };
        server.start();
    }

    @AfterClass public void stopServer() {
        server.stop();
    }

    @AfterMethod public void teardown() {
        if (driver != null)
            driver.quit();
    }

    @Test public void WebDriverProxy() throws MalformedURLException, InterruptedException, InstantiationException, IllegalAccessException {
        checkPlatform();
        driver = new SafariDriver();
        synchronized(this) {
            this.wait();
        }
        driver.get("http://localhost:21346");
        WebElement user = driver.findElement(By.cssSelector("#user_login"));
        WebElement pass = driver.findElement(By.cssSelector("#user_pass"));
        user.sendKeys("joe_bumkin");
        pass.sendKeys("secret_words");
        AssertJUnit.assertEquals("joe_bumkin", user.getAttribute("value"));
        AssertJUnit.assertEquals("secret_words", pass.getAttribute("value"));
    }

    protected void checkPlatform() {
    }
}
