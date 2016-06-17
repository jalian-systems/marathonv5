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
package net.sourceforge.marathon.javadriver.editorpane;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javadriver.JavaDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.sun.swingset3.demos.editorpane.EditorPaneDemo;

@Test public class JEditorPaneTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JEditorPaneTest.class.getSimpleName());
                frame.setName("frame-" + JEditorPaneTest.class.getSimpleName());
                frame.getContentPane().add(new EditorPaneDemo(), BorderLayout.CENTER);
                frame.setSize(640, 480);
                frame.setLocationRelativeTo(null);
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

    public void getEditorPane() throws Throwable {
        driver = new JavaDriver();
        driver.findElement(By.cssSelector("editor-pane"));
    }

    public void gettext() throws Throwable {
        driver = new JavaDriver();
        WebElement editor = driver.findElement(By.cssSelector("editor-pane"));
        AssertJUnit.assertTrue(editor.getText().contains("Of the curious"));
    }

    public void getTags() throws Throwable {
        driver = new JavaDriver();
        WebElement editor = driver.findElement(By.cssSelector("editor-pane"));
        AssertJUnit.assertTrue(editor.getText().contains("Of the curious"));
        List<WebElement> aTags = editor.findElements(By.cssSelector(".::tag('a')"));
        AssertJUnit.assertEquals(6, aTags.size());
        AssertJUnit.assertEquals("Title Page", aTags.get(0).getText());
        AssertJUnit.assertEquals("To The King", aTags.get(1).getText());
        AssertJUnit.assertEquals("The Preface", aTags.get(2).getText());
        AssertJUnit.assertEquals("Of the curious texture of Sea-weeds", aTags.get(3).getText());
        AssertJUnit.assertEquals("Of an Ant or Pismire", aTags.get(4).getText());
        AssertJUnit.assertEquals("Of a Louse", aTags.get(5).getText());
    }

    public void getTagAttributes() throws Throwable {
        driver = new JavaDriver();
        WebElement editor = driver.findElement(By.cssSelector("editor-pane"));
        WebElement title = editor.findElement(By.cssSelector(".::tag('a')[text='Title Page']"));
        AssertJUnit.assertEquals("Title Page", title.getText());
        AssertJUnit.assertEquals("title.html", title.getAttribute("href"));
        title = editor.findElement(By.cssSelector(".::tag('a')[href='title.html']"));
        AssertJUnit.assertEquals("Title Page", title.getText());
        AssertJUnit.assertEquals("title.html", title.getAttribute("href"));
    }

    public void clickTag() throws Throwable {
        driver = new JavaDriver();
        WebElement editor = driver.findElement(By.cssSelector("editor-pane"));
        WebElement title = editor.findElement(By.cssSelector(".::tag('a')[text='Title Page']"));
        AssertJUnit.assertEquals("Title Page", title.getText());
        title.click();
        new WebDriverWait(driver, 3).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver driver) {
                WebElement editor = driver.findElement(By.cssSelector("editor-pane"));
                List<WebElement> links = editor.findElements(By.cssSelector(".::tag('a')"));
                return links.size() == 2;
            }
        });
        List<WebElement> links = editor.findElements(By.cssSelector(".::tag('a')"));
        AssertJUnit.assertEquals(2, links.size());
    }
}
