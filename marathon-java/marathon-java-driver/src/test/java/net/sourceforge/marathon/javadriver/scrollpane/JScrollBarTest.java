/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javadriver.scrollpane;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.ScrollDemo;

@Test public class JScrollBarTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JScrollBarTest.class.getSimpleName());
                frame.setSize(320, 350);
                frame.setName("frame-" + JScrollBarTest.class.getSimpleName());
                frame.getContentPane().add(new ScrollDemo(), BorderLayout.CENTER);
                // frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        if (driver != null)
            driver.quit();
    }

    public void getText() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> scrollBars = driver.findElements(By.cssSelector("scroll-bar"));
        WebElement scrollbar = scrollBars.get(0);
        AssertJUnit.assertEquals(null, scrollbar.getText());
    }

    public void getAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> scrollBars = driver.findElements(By.cssSelector("scroll-bar"));
        WebElement scrollbar = scrollBars.get(0);
        AssertJUnit.assertEquals("0", scrollbar.getAttribute("value"));
        new Actions(driver).moveToElement(scrollbar).moveByOffset(0, 40).click().perform();
        int scrollBarValue = Integer.parseInt(scrollbar.getAttribute("value"));
        AssertJUnit.assertTrue(scrollBarValue > 100);

        scrollbar = scrollBars.get(1);
        AssertJUnit.assertEquals("0", scrollbar.getAttribute("value"));
        new Actions(driver).moveToElement(scrollbar).moveByOffset(80, 0).click().perform();
        scrollBarValue = Integer.parseInt(scrollbar.getAttribute("value"));
        AssertJUnit.assertTrue(scrollBarValue > 10);
    }

    @Test(enabled = false) public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> scrollBars = driver.findElements(By.cssSelector("scroll-bar"));
        WebElement scrollbar = scrollBars.get(0);
        AssertJUnit.assertEquals("0", scrollbar.getAttribute("value"));
        scrollbar.sendKeys(Keys.ARROW_DOWN);
        AssertJUnit.assertEquals("28", scrollbar.getAttribute("value"));
        scrollbar.sendKeys(Keys.ARROW_UP);
        AssertJUnit.assertEquals("0", scrollbar.getAttribute("value"));

        scrollbar = scrollBars.get(1);
        AssertJUnit.assertEquals("0", scrollbar.getAttribute("value"));
        scrollbar.sendKeys(Keys.ARROW_RIGHT);
        AssertJUnit.assertEquals("28", scrollbar.getAttribute("value"));
        scrollbar.sendKeys(Keys.ARROW_LEFT);
        AssertJUnit.assertEquals("0", scrollbar.getAttribute("value"));
    }
}
