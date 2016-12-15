/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javadriver.button;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.ButtonHtmlDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;

@Test public class JButtonHtmlTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JButtonHtmlTest.class.getSimpleName());
                frame.setName("frame-" + JButtonHtmlTest.class.getSimpleName());
                frame.getContentPane().add(new ButtonHtmlDemo(), BorderLayout.CENTER);
                frame.pack();
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
        if (driver != null) {
            driver.quit();
        }
    }

    public void getText() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(3, buttons.size());
        AssertJUnit.assertEquals("<html><center><b><u>D</u>isable</b><br><font color=#ffffdd>middle button</font>",
                buttons.get(0).getText());
        AssertJUnit.assertEquals("middle button", buttons.get(1).getText());
        AssertJUnit.assertEquals("<html><center><b><u>E</u>nable</b><br><font color=#ffffdd>middle button</font>",
                buttons.get(2).getText());
        WebElement buttonMiddle = driver.findElement(By.cssSelector("button[text^='middle']"));
        AssertJUnit.assertEquals("middle button", buttonMiddle.getText());
    }

    void getAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(3, buttons.size());
        AssertJUnit.assertEquals("<html><center><b><u>D</u>isable</b><br><font color=#ffffdd>middle button</font>",
                buttons.get(0).getAttribute("text"));
        AssertJUnit.assertEquals("true", buttons.get(0).getAttribute("enabled"));
        AssertJUnit.assertEquals("disable", buttons.get(0).getAttribute("actionCommand"));
        AssertJUnit.assertEquals("false", buttons.get(0).getAttribute("selected"));
        buttons.get(0).click();
        AssertJUnit.assertEquals("false", buttons.get(0).getAttribute("enabled"));
    }

    public void click() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(3, buttons.size());
        WebElement b1 = buttons.get(0);
        AssertJUnit.assertEquals("<html><center><b><u>D</u>isable</b><br><font color=#ffffdd>middle button</font>", b1.getText());
        WebElement b2 = buttons.get(1);
        AssertJUnit.assertEquals("middle button", b2.getText());
        WebElement b3 = buttons.get(2);
        AssertJUnit.assertEquals("<html><center><b><u>E</u>nable</b><br><font color=#ffffdd>middle button</font>", b3.getText());
        AssertJUnit.assertEquals("true", b1.getAttribute("enabled"));
        AssertJUnit.assertEquals("true", b2.getAttribute("enabled"));
        AssertJUnit.assertEquals("false", b3.getAttribute("enabled"));
        b1.click();
        AssertJUnit.assertEquals("false", b1.getAttribute("enabled"));
        AssertJUnit.assertEquals("false", b2.getAttribute("enabled"));
        AssertJUnit.assertEquals("true", b3.getAttribute("enabled"));
    }

    public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(3, buttons.size());
        WebElement b1 = buttons.get(0);
        AssertJUnit.assertEquals("<html><center><b><u>D</u>isable</b><br><font color=#ffffdd>middle button</font>", b1.getText());
        WebElement b2 = buttons.get(1);
        AssertJUnit.assertEquals("middle button", b2.getText());
        WebElement b3 = buttons.get(2);
        AssertJUnit.assertEquals("<html><center><b><u>E</u>nable</b><br><font color=#ffffdd>middle button</font>", b3.getText());
        AssertJUnit.assertEquals("true", b1.getAttribute("enabled"));
        AssertJUnit.assertEquals("true", b2.getAttribute("enabled"));
        AssertJUnit.assertEquals("false", b3.getAttribute("enabled"));
        b1.sendKeys(Keys.SPACE);
        AssertJUnit.assertEquals("false", b1.getAttribute("enabled"));
        AssertJUnit.assertEquals("false", b2.getAttribute("enabled"));
        AssertJUnit.assertEquals("true", b3.getAttribute("enabled"));
    }
}
