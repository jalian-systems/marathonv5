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
package net.sourceforge.marathon.javadriver.tabbedpane;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.TabbedPaneDemo;
import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.testhelpers.MissingException;

@Test public class JTabbedPaneTest {

    private WebDriver driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JTabbedPaneTest.class.getSimpleName());
                frame.setName("frame-" + JTabbedPaneTest.class.getSimpleName());
                frame.getContentPane().add(new TabbedPaneDemo(), BorderLayout.CENTER);
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
        WebElement tabbedPane = driver.findElement(By.cssSelector("tabbed-pane"));
        JSONArray expected = new JSONArray();
        expected.put("Tab 1").put("Tab 2").put("Tab 3").put("Tab 4");
        AssertJUnit.assertEquals("Tab 1", tabbedPane.getText());
    }

    public void getAllTabs() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> tabs = driver.findElements(By.cssSelector("tabbed-pane::all-tabs"));
        AssertJUnit.assertEquals(4, tabs.size());
        AssertJUnit.assertEquals("Tab 1", tabs.get(0).getText());
        AssertJUnit.assertEquals("Tab 2", tabs.get(1).getText());
        AssertJUnit.assertEquals("Tab 3", tabs.get(2).getText());
        AssertJUnit.assertEquals("Tab 4", tabs.get(3).getText());
    }

    public void getTextOnATabitem() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> tabs = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(1)"));
        AssertJUnit.assertEquals(1, tabs.size());
        AssertJUnit.assertEquals("Tab 1", tabs.get(0).getText());
        tabs = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(4)"));
        AssertJUnit.assertEquals(1, tabs.size());
        AssertJUnit.assertEquals("Tab 4", tabs.get(0).getText());
    }

    public void isDisplayedTabItem() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setSize(100, frame.getHeight());
            }
        });
        driver = new JavaDriver();
        final List<WebElement> tabs = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(4)"));
        AssertJUnit.assertEquals(1, tabs.size());
        new Wait("Wait for tab4 to non-displayable") {
            @Override public boolean until() {
                return !tabs.get(0).isDisplayed();
            }
        };
        AssertJUnit.assertFalse(tabs.get(0).isDisplayed());
        List<WebElement> tabs2 = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(1)"));
        AssertJUnit.assertEquals(1, tabs.size());
        AssertJUnit.assertTrue(tabs2.get(0).isDisplayed());
    }

    public void clickTabItem() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setSize(100, frame.getHeight());
            }
        });
        driver = new JavaDriver();
        final List<WebElement> tabs = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(4)"));
        AssertJUnit.assertEquals(1, tabs.size());
        new Wait("Wait for tab4 to non-displayable") {
            @Override public boolean until() {
                return !tabs.get(0).isDisplayed();
            }
        };
        AssertJUnit.assertFalse(tabs.get(0).isDisplayed());
        tabs.get(0).click();
        AssertJUnit.assertTrue(tabs.get(0).isDisplayed());
        WebElement tabbedPane = driver.findElement(By.cssSelector("tabbed-pane"));
        AssertJUnit.assertEquals("" + 3, tabbedPane.getAttribute("selectedIndex"));
    }

    public void invalidTabIndex() throws Throwable {
        driver = new JavaDriver();
        WebElement tab = driver.findElement(By.cssSelector("tabbed-pane::nth-tab(10)"));
        try {
            tab.click();
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        }
    }

    public void tabGetUsingText() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> tabs = driver.findElements(By.cssSelector("tabbed-pane::all-tabs[text='Tab 1']"));
        AssertJUnit.assertEquals(1, tabs.size());
        AssertJUnit.assertEquals("Tab 1", tabs.get(0).getText());
    }

    public void tabUseAsContainer() throws Throwable {
        driver = new JavaDriver();
        List<WebElement> tabs = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(1)"));
        AssertJUnit.assertEquals(1, tabs.size());
        AssertJUnit.assertEquals("Tab 1", tabs.get(0).getText());
        List<WebElement> panels = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(1)::component"));
        panels = tabs.get(0).findElements(By.cssSelector(".::component"));
        AssertJUnit.assertEquals(1, panels.size());
        AssertJUnit.assertEquals("panel", panels.get(0).getTagName());
        List<WebElement> labels = panels.get(0).findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(1, labels.size());
        AssertJUnit.assertEquals("Panel #1", labels.get(0).getText());
        panels = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(2)::component"));
        AssertJUnit.assertEquals(1, panels.size());
        AssertJUnit.assertEquals("panel", panels.get(0).getTagName());
        labels = panels.get(0).findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(1, labels.size());
        AssertJUnit.assertEquals("Panel #2", labels.get(0).getText());
    }

}
