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
package net.sourceforge.marathon.javadriver.textarea;

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

import components.TextAreaDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.OSUtils;

@Test
public class JTextAreaTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new TextAreaDemo();
                frame.setName("frame-" + JTextAreaTest.class.getSimpleName());
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
    }

    @AfterMethod
    public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
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
        WebElement textArea = driver.findElement(By.cssSelector("text-area"));
        textArea.clear();
        AssertJUnit.assertEquals("", textArea.getText());

        textArea.sendKeys("Lewis Carroll");
        AssertJUnit.assertEquals("Lewis Carroll", textArea.getText());
        textArea.clear();
        AssertJUnit.assertEquals("", textArea.getText());
        textArea.sendKeys("Welcome to Jalian Systems");
        AssertJUnit.assertEquals("Welcome to Jalian Systems", textArea.getText());

    }

    void getAttributes() throws Throwable {
        driver = new JavaDriver();
        WebElement textArea = driver.findElement(By.cssSelector("text-area"));
        AssertJUnit.assertEquals("true", textArea.getAttribute("editable"));
        textArea.sendKeys("Systems", Keys.SPACE);
        String previousText = textArea.getText();
        textArea.clear();
        textArea.sendKeys("Jalian" + previousText);
    }

    public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        WebElement textArea = driver.findElement(By.cssSelector("text-area"));
        AssertJUnit.assertEquals("true", textArea.getAttribute("editable"));
        textArea.clear();
        AssertJUnit.assertEquals("", textArea.getText());
        textArea.sendKeys("Lewis CarrollR");
        textArea.sendKeys(Keys.BACK_SPACE);
        AssertJUnit.assertEquals("Lewis Carroll", textArea.getText());
        textArea.sendKeys(Keys.HOME + "Jhon ");
        AssertJUnit.assertEquals("Jhon Lewis Carroll", textArea.getText());
        for (int i = 0; i < 5; i++) {
            textArea.sendKeys(OSUtils.getKeysFor(textArea, "select-all"), OSUtils.getKeysFor(textArea, "copy-to-clipboard"),
                    OSUtils.getKeysFor(textArea, "paste-from-clipboard"), Keys.END,
                    OSUtils.getKeysFor(textArea, "paste-from-clipboard"));
        }
        textArea.sendKeys(OSUtils.getKeysFor(textArea, "select-all"));
        textArea.sendKeys(Keys.DELETE);
        AssertJUnit.assertEquals("", textArea.getText());
    }

    public void clear() throws Throwable {
        driver = new JavaDriver();
        WebElement textArea = driver.findElement(By.cssSelector("text-area"));
        textArea.clear();
        AssertJUnit.assertEquals("", textArea.getText());
        textArea.sendKeys("Lewis Carroll");
        AssertJUnit.assertEquals("Lewis Carroll", textArea.getText());
        textArea.clear();
        AssertJUnit.assertEquals("", textArea.getText());
    }

    public void sendKeysWithNewline() throws Throwable {
        driver = new JavaDriver();
        WebElement textArea = driver.findElement(By.cssSelector("text-area"));
        AssertJUnit.assertEquals("true", textArea.getAttribute("editable"));
        textArea.clear();
        AssertJUnit.assertEquals("", textArea.getText());
        textArea.sendKeys("Lewis Carroll\nJohn");
        AssertJUnit.assertEquals("Lewis Carroll\nJohn", textArea.getText());
    }

}
