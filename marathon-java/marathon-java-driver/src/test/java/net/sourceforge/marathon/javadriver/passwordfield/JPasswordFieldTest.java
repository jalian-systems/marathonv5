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
package net.sourceforge.marathon.javadriver.passwordfield;

import java.awt.BorderLayout;

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

import components.PasswordDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.OSUtils;

@Test public class JPasswordFieldTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JPasswordFieldTest.class.getSimpleName());
                frame.setName("frame-" + JPasswordFieldTest.class.getSimpleName());
                frame.getContentPane().add(new PasswordDemo(frame), BorderLayout.CENTER);
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
        WebElement passField = driver.findElement(By.cssSelector("password-field"));
        AssertJUnit.assertEquals("true", passField.getAttribute("enabled"));
        passField.clear();
        AssertJUnit.assertEquals("", passField.getText());
        passField.sendKeys("password");
        AssertJUnit.assertEquals("password", passField.getText());
        passField.clear();
        AssertJUnit.assertEquals("", passField.getText());
        passField.sendKeys("pass@123");
        AssertJUnit.assertEquals("pass@123", passField.getText());
    }

    public void getText2() throws Throwable {
        driver = new JavaDriver();
        WebElement passField = driver.findElement(By.cssSelector("password-field"));
        passField.sendKeys("pass@123");
        AssertJUnit.assertEquals("pass@123", passField.getText());
    }

    public void login() throws Throwable {
        driver = new JavaDriver();
        WebElement passField = driver.findElement(By.cssSelector("password-field"));
        AssertJUnit.assertEquals("true", passField.getAttribute("enabled"));
        passField.clear();
        AssertJUnit.assertEquals("", passField.getText());
        passField.sendKeys("password");
        WebElement button = driver.findElement(By.cssSelector("button"));
        button.click();
        driver.switchTo().window("Error Message");
        WebElement label = driver.findElement(By.cssSelector("label[text*='Invalid']"));
        AssertJUnit.assertEquals("Invalid password. Try again.", label.getText());
        WebElement button1 = driver.findElement(By.cssSelector("button"));
        button1.click();
        driver.switchTo().window("JPasswordFieldTest");
        passField.clear();
        passField.sendKeys("bugaboo", Keys.ENTER);
        driver.switchTo().window("Message");
        WebElement label2 = driver.findElement(By.cssSelector("label[text*='Success']"));
        AssertJUnit.assertEquals("Success! You typed the right password.", label2.getText());
    }

    void getAttributes() throws Throwable {
        driver = new JavaDriver();
        WebElement passField = driver.findElement(By.cssSelector("password-field"));
        AssertJUnit.assertEquals("true", passField.getAttribute("enabled"));
    }

    public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        WebElement passField = driver.findElement(By.cssSelector("password-field"));
        AssertJUnit.assertEquals("true", passField.getAttribute("enabled"));
        passField.clear();
        AssertJUnit.assertEquals("", passField.getText());
        passField.sendKeys("password ");
        passField.sendKeys(Keys.BACK_SPACE);
        AssertJUnit.assertEquals("password", passField.getText());
        passField.sendKeys(Keys.HOME + "correct ");
        AssertJUnit.assertEquals("correct password", passField.getText());
        passField.sendKeys(OSUtils.getKeysFor(passField, "select-all"));
        passField.sendKeys(Keys.DELETE);
        AssertJUnit.assertEquals("", passField.getText());
    }

    public void clear() throws Throwable {
        driver = new JavaDriver();
        WebElement passField = driver.findElement(By.cssSelector("password-field"));
        passField.clear();
        AssertJUnit.assertEquals("", passField.getText());
        passField.sendKeys("password");
        AssertJUnit.assertEquals("password", passField.getText());
        passField.clear();
        AssertJUnit.assertEquals("", passField.getText());
    }

}
