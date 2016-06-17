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
package net.sourceforge.marathon.javadriver.spinner;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.SpinnerDemo4;

@Test public class JSpinner4Test {

    private WebDriver driver;
    protected JFrame frame;
    private WebElement spinner;
    private WebElement spinnerButttonUp;
    private WebElement spinnerButtonDown;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JSpinner2Test.class.getSimpleName());
                frame.setName("frame-" + JSpinner2Test.class.getSimpleName());
                frame.getContentPane().add(new SpinnerDemo4(), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });

        driver = new JavaDriver();
        spinner = driver.findElement(By.cssSelector("spinner"));

        spinnerButttonUp = spinner.findElement(By.cssSelector("basic-arrow-button:nth(1)"));
        spinnerButtonDown = spinner.findElement(By.cssSelector("basic-arrow-button:nth(2)"));

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

    public void color() throws Throwable {
        List<WebElement> spinnerElements = spinner.findElements(By.cssSelector("*"));
        AssertJUnit.assertEquals(3, spinnerElements.size());
        WebElement spinnerLabel = spinnerElements.get(2);
        AssertJUnit.assertEquals("(170,170,170)", spinnerLabel.getAttribute("toolTipText"));
        spinnerButttonUp.click();
        spinnerButttonUp.click();
        AssertJUnit.assertEquals("(180,180,180)", spinnerLabel.getAttribute("toolTipText"));
        spinnerButtonDown.click();
        AssertJUnit.assertEquals("(175,175,175)", spinnerLabel.getAttribute("toolTipText"));
    }
}
