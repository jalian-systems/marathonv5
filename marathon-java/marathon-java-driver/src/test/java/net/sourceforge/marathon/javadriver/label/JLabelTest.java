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
package net.sourceforge.marathon.javadriver.label;

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

import components.LabelDemo;

@Test public class JLabelTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JLabelTest.class.getSimpleName());
                frame.setName("frame-" + JLabelTest.class.getSimpleName());
                frame.getContentPane().add(new LabelDemo(), BorderLayout.CENTER);
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
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(3, labels.size());
        AssertJUnit.assertEquals("Image and Text", labels.get(0).getText());
        AssertJUnit.assertEquals("Text-Only Label", labels.get(1).getText());
        AssertJUnit.assertEquals(null, labels.get(2).getText());
    }

    public void getAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(3, labels.size());
        WebElement label = labels.get(0);
        AssertJUnit.assertEquals("Image and Text", label.getText());
        AssertJUnit.assertEquals("0", label.getAttribute("horizontalAlignment"));
        AssertJUnit.assertEquals("a pretty but meaningless splat", label.getAttribute("icon"));
        AssertJUnit.assertEquals("22", label.getAttribute("icon.iconHeight"));
    }

    public void getLabelsWithAttributes() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> labels = driver.findElements(By.cssSelector("label[text='Image and Text']"));
        AssertJUnit.assertEquals(1, labels.size());
        labels = driver.findElements(By.cssSelector("label[icon.description*='meaningless']"));
        AssertJUnit.assertEquals(2, labels.size());
    }
}