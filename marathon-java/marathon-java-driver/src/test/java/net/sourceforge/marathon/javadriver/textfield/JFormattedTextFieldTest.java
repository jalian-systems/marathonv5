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

import java.awt.BorderLayout;
import java.text.NumberFormat;
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

import components.FormattedTextFieldDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;

@Test
public class JFormattedTextFieldTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame = new JFrame(JFormattedTextFieldTest.class.getSimpleName());
                frame.setName("frame-" + JFormattedTextFieldTest.class.getSimpleName());
                frame.getContentPane().add(new FormattedTextFieldDemo(), BorderLayout.CENTER);
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
        List<WebElement> textfields = driver.findElements(By.cssSelector("formatted-text-field"));
        AssertJUnit.assertEquals(4, textfields.size());
        AssertJUnit.assertEquals("100,000", textfields.get(0).getText());
        AssertJUnit.assertEquals("7.500", textfields.get(1).getText());
        AssertJUnit.assertEquals("30", textfields.get(2).getText());
        String expected = NumberFormat.getCurrencyInstance().format(-699.21);
        AssertJUnit.assertEquals(expected, textfields.get(3).getText());
    }

    public void getAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> textfields = driver.findElements(By.cssSelector("formatted-text-field"));
        WebElement tf1 = textfields.get(0);
        AssertJUnit.assertEquals("100,000", tf1.getText());
        AssertJUnit.assertEquals("100000.0", tf1.getAttribute("value"));
        AssertJUnit.assertEquals("30", textfields.get(2).getText());
        AssertJUnit.assertEquals("30", textfields.get(2).getAttribute("value"));

    }

    public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> textfields = driver.findElements(By.cssSelector("formatted-text-field"));
        WebElement tf = textfields.get(0);
        WebElement other = textfields.get(1);
        AssertJUnit.assertEquals("100,000", tf.getText());
        AssertJUnit.assertEquals("100000.0", tf.getAttribute("value"));
        tf.sendKeys(Keys.chord(Keys.SHIFT, Keys.END), "1234");
        AssertJUnit.assertEquals("1234", tf.getText());
        other.click();
        AssertJUnit.assertEquals("1234", tf.getAttribute("value"));
        AssertJUnit.assertEquals("1,234", tf.getText());

        tf = textfields.get(2);
        AssertJUnit.assertEquals("30", tf.getText());
        tf.sendKeys(Keys.chord(Keys.SHIFT, Keys.END), Keys.DELETE);
        AssertJUnit.assertEquals("", tf.getText());
        tf.sendKeys("123");
        AssertJUnit.assertEquals("123", tf.getText());
        other.click();
        AssertJUnit.assertEquals("123", tf.getAttribute("value"));

    }
}
