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
package net.sourceforge.marathon.javadriver.textfield;

import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TextFieldDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.OSUtils;

@Test public class JTextFieldTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new TextFieldDemo();
                frame.setName("frame-" + JTextFieldTest.class.getSimpleName());
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
        WebElement textField = driver.findElement(By.cssSelector("text-field"));
        textField.clear();
        AssertJUnit.assertEquals("", textField.getText());
        textField.sendKeys("Lewis Carroll");
        AssertJUnit.assertEquals("Lewis Carroll", textField.getText());
        textField.clear();
        AssertJUnit.assertEquals("", textField.getText());
        textField.sendKeys("htt://www.google.com ");
        AssertJUnit.assertEquals("htt://www.google.com", textField.getText().trim());

    }

    void getAttributes() throws Throwable {
        driver = new JavaDriver();
        WebElement textField = driver.findElement(By.cssSelector("text-field"));
        AssertJUnit.assertEquals("true", textField.getAttribute("editable"));
        AssertJUnit.assertEquals("true", textField.getAttribute("enabled"));
    }

    public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        WebElement textField = driver.findElement(By.cssSelector("text-field"));
        AssertJUnit.assertEquals("true", textField.getAttribute("editable"));
        textField.clear();
        AssertJUnit.assertEquals("", textField.getText());
        textField.sendKeys("Lewis CarrollR");
        textField.sendKeys(Keys.BACK_SPACE);
        AssertJUnit.assertEquals("Lewis Carroll", textField.getText());
        textField.sendKeys(Keys.HOME + "Jhon ");
        AssertJUnit.assertEquals("Jhon Lewis Carroll", textField.getText());
        textField.sendKeys(OSUtils.getKeysFor(textField, "select-all"));
        textField.sendKeys(Keys.DELETE);
        AssertJUnit.assertEquals("", textField.getText());
    }

    public void clear() throws Throwable {
        driver = new JavaDriver();
        WebElement textField = driver.findElement(By.cssSelector("text-field"));
        textField.clear();
        AssertJUnit.assertEquals("", textField.getText());
        textField.sendKeys("Lewis Carroll");
        AssertJUnit.assertEquals("Lewis Carroll", textField.getText());
        textField.clear();
        AssertJUnit.assertEquals("", textField.getText());
    }

    public void checkKeystrokesForActions() throws Throwable {
        StringBuilder sb = new StringBuilder();
        driver = new JavaDriver();
        WebElement textField = driver.findElement(By.cssSelector("text-field"));
        JTextField f = new JTextField();
        InputMap inputMap = f.getInputMap();
        KeyStroke[] allKeys = inputMap.allKeys();
        for (KeyStroke keyStroke : allKeys) {
            Object object = inputMap.get(keyStroke);
            try {
                OSUtils.getKeysFor(textField, object.toString());
            } catch (Throwable t) {
                sb.append("failed for(" + object + "): " + keyStroke);
            }
        }
        AssertJUnit.assertEquals("", sb.toString());
    }
}
