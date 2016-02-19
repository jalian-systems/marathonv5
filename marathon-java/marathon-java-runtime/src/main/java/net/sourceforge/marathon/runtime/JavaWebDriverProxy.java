package net.sourceforge.marathon.runtime;

import org.openqa.selenium.WebDriver;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;

public class JavaWebDriverProxy implements IWebdriverProxy {

    private JavaProfile profile;
    private JavaDriver driver;

    public JavaWebDriverProxy(JavaProfile profile, JavaDriver driver) {
        this.profile = profile;
        this.driver = driver;
    }

    @Override public WebDriver getDriver() {
        return driver;
    }

    @Override public String getURL() {
        String url = profile.getURL();
        return url;
    }
}
