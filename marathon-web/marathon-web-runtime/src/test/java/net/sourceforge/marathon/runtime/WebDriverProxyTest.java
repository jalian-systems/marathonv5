package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import fi.iki.elonen.NanoHTTPD;

public abstract class WebDriverProxyTest {

    private IWebdriverProxy proxy;
    private WebDriver driver;
    private NanoHTTPD server;
    private Class<? extends IWebdriverProxy> klass;

    @BeforeClass public void startServer() throws IOException {
        System.out.println("WebDriverProxyTest.startServer()");
        server = new NanoHTTPD(21346) {
            @Override public Response serve(IHTTPSession session) {
                try {
                    String data = IOUtils.toString(WebDriverProxyTest.class.getResourceAsStream("form.html"),
                            Charset.defaultCharset());
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
        if (proxy != null)
            proxy.quit();
    }

    public WebDriverProxyTest(Class<? extends IWebdriverProxy> klass) {
        this.klass = klass;
    }

    @Test public void WebDriverProxy()
            throws MalformedURLException, InterruptedException, InstantiationException, IllegalAccessException {
        checkPlatform();
        proxy = klass.newInstance();
        driver = new RemoteWebDriver(new URL(proxy.getURL()), DesiredCapabilities.firefox());
        driver.get("http://localhost:21346");
        WebElement user = driver.findElement(By.cssSelector("#user_login"));
        WebElement pass = driver.findElement(By.cssSelector("#user_pass"));
        user.sendKeys("joe_bumkin");
        pass.sendKeys("secret_words");
        String value = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", user);
        AssertJUnit.assertEquals("joe_bumkin", value);
        value = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", pass);
        AssertJUnit.assertEquals("secret_words", value);
    }

    protected void checkPlatform() {
    }
}
