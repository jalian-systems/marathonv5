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
package net.sourceforge.marathon.javadriver.radiobutton;

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

import components.RadioButtonDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;

@Test public class JRadioButtonTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JRadioButtonTest.class.getSimpleName());
                frame.setName("frame-" + JRadioButtonTest.class.getSimpleName());
                frame.getContentPane().add(new RadioButtonDemo(), BorderLayout.CENTER);
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
        List<WebElement> radioButtons = driver.findElements(By.cssSelector("radio-button"));
        AssertJUnit.assertEquals(5, radioButtons.size());
        AssertJUnit.assertEquals("Bird", radioButtons.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Cat", radioButtons.get(1).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Dog", radioButtons.get(2).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Rabbit", radioButtons.get(3).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Pig", radioButtons.get(4).getAttribute("buttonText"));
        WebElement firstRadioButton = driver.findElement(By.cssSelector("radio-button[buttonText^='Bird']"));
        AssertJUnit.assertEquals("Bird", firstRadioButton.getAttribute("buttonText"));

    }

    void getAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> radioButtons = driver.findElements(By.cssSelector("radio-button"));
        AssertJUnit.assertEquals(5, radioButtons.size());
        AssertJUnit.assertEquals("Bird", radioButtons.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("true", radioButtons.get(0).getAttribute("selected"));
        AssertJUnit.assertEquals("Cat", radioButtons.get(1).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", radioButtons.get(1).getAttribute("selected"));
        radioButtons.get(1).click();
        AssertJUnit.assertEquals("false", radioButtons.get(0).getAttribute("selected"));
        AssertJUnit.assertEquals("true", radioButtons.get(1).getAttribute("selected"));
        AssertJUnit.assertEquals("false", radioButtons.get(2).getAttribute("selected"));
        AssertJUnit.assertEquals("false", radioButtons.get(3).getAttribute("selected"));
        AssertJUnit.assertEquals("false", radioButtons.get(4).getAttribute("selected"));
        AssertJUnit.assertEquals("Pig", radioButtons.get(4).getAttribute("actionCommand"));
    }

    public void click() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> radioButtons = driver.findElements(By.cssSelector("radio-button"));
        AssertJUnit.assertEquals(5, radioButtons.size());
        AssertJUnit.assertEquals("Bird", radioButtons.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("true", radioButtons.get(0).getAttribute("selected"));
        AssertJUnit.assertEquals("Cat", radioButtons.get(1).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", radioButtons.get(1).getAttribute("selected"));
        AssertJUnit.assertEquals("Dog", radioButtons.get(2).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", radioButtons.get(2).getAttribute("selected"));
        AssertJUnit.assertEquals("Rabbit", radioButtons.get(3).getAttribute("buttonText"));
        AssertJUnit.assertEquals("false", radioButtons.get(3).getAttribute("selected"));
        for (WebElement radioButton : radioButtons) {
            radioButton.click();
        }
        AssertJUnit.assertEquals("true", radioButtons.get(4).getAttribute("selected"));
    }

    public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> radioButtons = driver.findElements(By.cssSelector("radio-button"));
        AssertJUnit.assertEquals(5, radioButtons.size());
        WebElement rbutton1 = radioButtons.get(0);
        AssertJUnit.assertEquals("Bird", rbutton1.getAttribute("buttonText"));
        WebElement rbutton2 = radioButtons.get(1);
        AssertJUnit.assertEquals("Cat", rbutton2.getAttribute("buttonText"));
        WebElement rbutton3 = radioButtons.get(2);
        AssertJUnit.assertEquals("Dog", rbutton3.getAttribute("buttonText"));
        WebElement rbutton4 = radioButtons.get(3);
        AssertJUnit.assertEquals("Rabbit", rbutton4.getAttribute("buttonText"));
        WebElement rbutton5 = radioButtons.get(4);
        AssertJUnit.assertEquals("Pig", rbutton5.getAttribute("buttonText"));

        AssertJUnit.assertEquals("true", rbutton1.getAttribute("selected"));
        AssertJUnit.assertEquals("false", rbutton2.getAttribute("selected"));
        AssertJUnit.assertEquals("false", rbutton3.getAttribute("selected"));
        AssertJUnit.assertEquals("false", rbutton4.getAttribute("selected"));
        AssertJUnit.assertEquals("false", rbutton5.getAttribute("selected"));
        rbutton3.sendKeys(Keys.SPACE);
        AssertJUnit.assertEquals("false", rbutton1.getAttribute("selected"));
        AssertJUnit.assertEquals("false", rbutton2.getAttribute("selected"));
        AssertJUnit.assertEquals("true", rbutton3.getAttribute("selected"));
        AssertJUnit.assertEquals("false", rbutton4.getAttribute("selected"));
        AssertJUnit.assertEquals("false", rbutton5.getAttribute("selected"));
    }

}
