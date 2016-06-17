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
package net.sourceforge.marathon.javadriver.combobox;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.ComboBoxDemo2;

@Test public class JComboBox2Test {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JComboBox2Test.class.getSimpleName());
                frame.setName("frame-" + JComboBox2Test.class.getSimpleName());
                frame.getContentPane().add(new ComboBoxDemo2(), BorderLayout.CENTER);
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
        List<WebElement> combos = driver.findElements(By.cssSelector("combo-box"));
        String[] patternExamples = { "dd MMMMM yyyy", "dd.MM.yy", "MM/dd/yy", "yyyy.MM.dd G 'at' hh:mm:ss z", "EEE, MMM d, ''yy",
                "h:mm a", "H:mm:ss:SSS", "K:mm a,z", "yyyy.MMMMM.dd GGG hh:mm aaa" };

        AssertJUnit.assertEquals(1, combos.size());
        JSONArray result = new JSONArray();
        result.put(new JSONArray(Arrays.asList(patternExamples)));
        AssertJUnit.assertEquals(result.toString(), combos.get(0).getAttribute("content"));
    }

    public void getAllOptions() throws Throwable {
        driver = new JavaDriver();
        String[] patternExamples = { "dd MMMMM yyyy", "dd.MM.yy", "MM/dd/yy", "yyyy.MM.dd G 'at' hh:mm:ss z", "EEE, MMM d, ''yy",
                "h:mm a", "H:mm:ss:SSS", "K:mm a,z", "yyyy.MMMMM.dd GGG hh:mm aaa" };

        List<WebElement> options = driver.findElements(By.cssSelector("combo-box::all-options"));
        AssertJUnit.assertEquals(patternExamples.length, options.size());
        for (int i = 0; i < patternExamples.length; i++)
            AssertJUnit.assertEquals(patternExamples[i], options.get(i).getText());
    }

    public void getNthOption() throws Throwable {
        driver = new JavaDriver();
        String[] patternExamples = { "dd MMMMM yyyy", "dd.MM.yy", "MM/dd/yy", "yyyy.MM.dd G 'at' hh:mm:ss z", "EEE, MMM d, ''yy",
                "h:mm a", "H:mm:ss:SSS", "K:mm a,z", "yyyy.MMMMM.dd GGG hh:mm aaa" };

        for (int i = 0; i < patternExamples.length; i++) {
            WebElement option = driver.findElement(By.cssSelector("combo-box::nth-option(" + (i + 1) + ")"));
            AssertJUnit.assertEquals(patternExamples[i], option.getText());
        }
    }

    public void clickItem() throws Throwable {
        driver = new JavaDriver();
        @SuppressWarnings("unused")
        String[] patternExamples = { "dd MMMMM yyyy", "dd.MM.yy", "MM/dd/yy", "yyyy.MM.dd G 'at' hh:mm:ss z", "EEE, MMM d, ''yy",
                "h:mm a", "H:mm:ss:SSS", "K:mm a,z", "yyyy.MMMMM.dd GGG hh:mm aaa" };

        WebElement combo = driver.findElement(By.cssSelector("combo-box"));
        AssertJUnit.assertEquals("" + 0, combo.getAttribute("selectedIndex"));
        WebElement option = driver.findElement(By.cssSelector("combo-box::nth-option(3)"));
        option.click();
        AssertJUnit.assertEquals("" + 2, combo.getAttribute("selectedIndex"));
    }

}
