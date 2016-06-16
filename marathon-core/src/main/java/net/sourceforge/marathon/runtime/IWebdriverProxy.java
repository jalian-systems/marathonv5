package net.sourceforge.marathon.runtime;

import org.openqa.selenium.WebDriver;

public interface IWebdriverProxy {

    WebDriver getDriver();

    String getURL();

}
