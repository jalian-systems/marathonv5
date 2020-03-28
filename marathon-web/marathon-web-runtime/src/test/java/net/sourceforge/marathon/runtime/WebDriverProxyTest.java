/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import fi.iki.elonen.NanoHTTPD;

public abstract class WebDriverProxyTest {

    private IWebBrowserProxy proxy;
    private WebDriver driver;
    private NanoHTTPD server;
    private Class<? extends IWebBrowserProxy> klass;
    private int serverPort;
    private Capabilities capabilities;
    private DriverService service;

    @BeforeClass
    public void startServer() throws IOException {
        System.out.println("WebDriverProxyTest.startServer()");
        serverPort = findPort();
        server = new NanoHTTPD(serverPort) {
            @Override
            public Response serve(IHTTPSession session) {
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

    @AfterClass
    public void stopServer() {
        server.stop();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
        if (service != null) {
            service.stop();
        }
    }

    public WebDriverProxyTest(Class<? extends IWebBrowserProxy> klass, Capabilities capabilities) {
        this.klass = klass;
        this.capabilities = capabilities;
    }

    @Test
    public void WebDriverProxy() throws InterruptedException, InstantiationException, IllegalAccessException, IOException {
        checkPlatform();
        proxy = klass.newInstance();
        service = proxy.createService(findPort());
        service.start();
        driver = new RemoteWebDriver(service.getUrl(), capabilities);
        driver.get("http://localhost:" + serverPort);
        WebElement user = driver.findElement(By.cssSelector("#user_login"));
        WebElement pass = driver.findElement(By.cssSelector("#user_pass"));
        user.sendKeys("joe_bumkin");
        pass.sendKeys("secret_words");
        String value = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", user);
        AssertJUnit.assertEquals("joe_bumkin", value);
        value = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].value;", pass);
        AssertJUnit.assertEquals("secret_words", value);
        driver.findElement(By.cssSelector("input"));
        String url = (String) ((JavascriptExecutor) driver).executeScript("return document.URL;");
        System.out.println("WebDriverProxyTest.WebDriverProxy(" + url + ")");
    }

    private int findPort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e1) {
            throw new WebDriverException("Could not allocate a port: " + e1.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected void checkPlatform() {
    }
}
