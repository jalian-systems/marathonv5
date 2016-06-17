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
package net.sourceforge.marathon.javadriver.checkbox;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.CheckBoxDemo;

@Test public class JCheckBoxTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JCheckBoxTest.class.getSimpleName());
                frame.setName("frame-" + JCheckBoxTest.class.getSimpleName());
                frame.getContentPane().add(new CheckBoxDemo(), BorderLayout.CENTER);
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

    public void getText() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> checkBoxes = driver.findElements(By.cssSelector("check-box"));
        AssertJUnit.assertEquals(4, checkBoxes.size());
        AssertJUnit.assertEquals("Chin", checkBoxes.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Glasses", checkBoxes.get(1).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Hair", checkBoxes.get(2).getAttribute("buttonText"));
        AssertJUnit.assertEquals("Teeth", checkBoxes.get(3).getAttribute("buttonText"));
        WebElement lastCheckBox = driver.findElement(By.cssSelector("check-box[buttonText^='Teeth']"));
        AssertJUnit.assertEquals("Teeth", lastCheckBox.getAttribute("buttonText"));
    }

    void getAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> checkBoxes = driver.findElements(By.cssSelector("check-box"));
        AssertJUnit.assertEquals(4, checkBoxes.size());
        AssertJUnit.assertEquals("Chin", checkBoxes.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("true", checkBoxes.get(0).getAttribute("selected"));
        checkBoxes.get(0).click();
        AssertJUnit.assertEquals("false", checkBoxes.get(0).getAttribute("selected"));
        AssertJUnit.assertEquals("true", checkBoxes.get(1).getAttribute("selected"));
        AssertJUnit.assertEquals("Hair", checkBoxes.get(2).getAttribute("actionCommand"));

    }

    public void click() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> checkBoxes = driver.findElements(By.cssSelector("check-box"));
        AssertJUnit.assertEquals(4, checkBoxes.size());
        AssertJUnit.assertEquals("Chin", checkBoxes.get(0).getAttribute("buttonText"));
        AssertJUnit.assertEquals("true", checkBoxes.get(0).getAttribute("selected"));
        AssertJUnit.assertEquals("Glasses", checkBoxes.get(1).getAttribute("buttonText"));
        AssertJUnit.assertEquals("true", checkBoxes.get(1).getAttribute("selected"));
        AssertJUnit.assertEquals("Hair", checkBoxes.get(2).getAttribute("buttonText"));
        AssertJUnit.assertEquals("true", checkBoxes.get(2).getAttribute("selected"));
        AssertJUnit.assertEquals("Teeth", checkBoxes.get(3).getAttribute("buttonText"));
        AssertJUnit.assertEquals("true", checkBoxes.get(3).getAttribute("selected"));
        for (WebElement checkBox : checkBoxes) {
            checkBox.click();
            AssertJUnit.assertEquals("false", checkBox.getAttribute("selected"));
        }
        AssertJUnit.assertEquals("false", checkBoxes.get(0).getAttribute("selected"));
    }

    public void sendKeys() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> checkBoxes = driver.findElements(By.cssSelector("check-box"));
        AssertJUnit.assertEquals(4, checkBoxes.size());
        WebElement chkbox1 = checkBoxes.get(0);
        AssertJUnit.assertEquals("Chin", chkbox1.getAttribute("buttonText"));
        WebElement chkbox2 = checkBoxes.get(1);
        AssertJUnit.assertEquals("Glasses", chkbox2.getAttribute("buttonText"));
        WebElement chkbox3 = checkBoxes.get(2);
        AssertJUnit.assertEquals("Hair", chkbox3.getAttribute("buttonText"));
        WebElement chkbox4 = checkBoxes.get(3);
        AssertJUnit.assertEquals("Teeth", chkbox4.getAttribute("buttonText"));

        AssertJUnit.assertEquals("true", chkbox1.getAttribute("selected"));
        AssertJUnit.assertEquals("true", chkbox2.getAttribute("selected"));
        AssertJUnit.assertEquals("true", chkbox3.getAttribute("selected"));
        AssertJUnit.assertEquals("true", chkbox4.getAttribute("selected"));
        chkbox3.sendKeys(Keys.SPACE);
        AssertJUnit.assertEquals("true", chkbox1.getAttribute("selected"));
        AssertJUnit.assertEquals("true", chkbox2.getAttribute("selected"));
        AssertJUnit.assertEquals("false", chkbox3.getAttribute("selected"));
        AssertJUnit.assertEquals("true", chkbox4.getAttribute("selected"));
    }

}
