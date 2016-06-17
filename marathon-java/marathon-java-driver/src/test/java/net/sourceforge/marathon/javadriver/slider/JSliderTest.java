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
package net.sourceforge.marathon.javadriver.slider;

import java.awt.BorderLayout;

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

import components.SliderDemo;

@Test public class JSliderTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JSliderTest.class.getSimpleName());
                frame.setName("frame-" + JSliderTest.class.getSimpleName());
                frame.getContentPane().add(new SliderDemo(), BorderLayout.CENTER);
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
        if (driver != null)
            driver.quit();
    }

    public void getAttributes() throws Throwable {
        driver = new JavaDriver();
        WebElement slider = driver.findElement(By.cssSelector("slider"));
        AssertJUnit.assertEquals("15", slider.getAttribute("value"));
        slider.sendKeys(Keys.ARROW_RIGHT);
        AssertJUnit.assertEquals("16", slider.getAttribute("value"));
        slider.sendKeys(Keys.ARROW_LEFT);
        AssertJUnit.assertEquals("15", slider.getAttribute("value"));
        slider.sendKeys(Keys.ARROW_UP);
        AssertJUnit.assertEquals("16", slider.getAttribute("value"));

        for (int i = 0; i < 15; i++)
            slider.sendKeys(Keys.ARROW_DOWN);
        AssertJUnit.assertEquals("1", slider.getAttribute("value"));

        for (int i = 0; i < 15; i++)
            slider.sendKeys(Keys.ARROW_UP);
        AssertJUnit.assertEquals("16", slider.getAttribute("value"));

        for (int i = 0; i < 15; i++)
            slider.sendKeys(Keys.ARROW_LEFT);
        AssertJUnit.assertEquals("1", slider.getAttribute("value"));

        for (int i = 0; i < 15; i++)
            slider.sendKeys(Keys.ARROW_RIGHT);
        AssertJUnit.assertEquals("16", slider.getAttribute("value"));

        new Actions(driver).moveToElement(slider).moveByOffset(40, 0).click().perform();
        int sliderValue = Integer.parseInt(slider.getAttribute("value"));
        AssertJUnit.assertTrue(sliderValue > 16);

    }
}
