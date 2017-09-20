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
package net.sourceforge.marathon.javadriver.tree;

import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.DynamicTreeDemo;
import net.sourceforge.marathon.javadriver.JavaDriver;

@Test public class JTreeDynamicTreeTest {

    private WebDriver driver;
    protected JFrame frame;
    private DynamicTreePage page;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JTreeDynamicTreeTest.class.getSimpleName());
                frame.setName("frame-" + JTreeDynamicTreeTest.class.getSimpleName());
                frame.getContentPane().add(new DynamicTreeDemo(), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaDriver();
        page = PageFactory.initElements(driver, DynamicTreePage.class);
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

    public void sanityCheck() throws Throwable {
        AssertJUnit.assertEquals("tree", page.getTree().getTagName());
        AssertJUnit.assertEquals("button", page.getAddButton().getTagName());
        AssertJUnit.assertEquals("button", page.getRemoveButton().getTagName());
        AssertJUnit.assertEquals("button", page.getClearButton().getTagName());
    }

    public void getroot() throws Throwable {
        WebElement tree = page.getTree();
        tree.click();
        WebElement root = tree.findElement(By.cssSelector(".::root"));
        AssertJUnit.assertEquals("Root Node", root.getText());
    }

    public void clickANode() throws Throwable {
        WebElement tree = page.getTree();
        tree.click();
        WebElement root = tree.findElement(By.cssSelector(".::root"));
        AssertJUnit.assertEquals(0 + "", tree.getAttribute("selectionCount"));
        root.click();
        AssertJUnit.assertEquals(1 + "", tree.getAttribute("selectionCount"));
    }

    public void editANode() throws Throwable {
        System.err.println("Ignore the following NPE. The DynamicTree class has a bug");
        WebElement tree = page.getTree();
        tree.click();
        WebElement root = tree.findElement(By.cssSelector(".::root"));
        AssertJUnit.assertEquals("Root Node", root.getText());
        root.click();
        Thread.sleep(500);
        root.click();
        new WebDriverWait(driver, 3).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("default-text-field")));
        WebElement findElement = driver.findElement(By.cssSelector("default-text-field"));
        findElement.sendKeys("Hello World", Keys.ENTER);
        AssertJUnit.assertEquals("Hello World", root.getText());
    }

    public void editANodeWithEditor() throws Throwable {
        System.err.println("Ignore the following NPE. The DynamicTree class has a bug");
        WebElement tree = page.getTree();
        tree.click();
        final WebElement root = tree.findElement(By.cssSelector(".::root"));
        AssertJUnit.assertEquals("Root Node", root.getText());
        WebElement editor = root.findElement(By.cssSelector(".::editor"));
        editor.clear();
        editor.sendKeys("Hello World", Keys.ENTER);
        root.submit();
        new WebDriverWait(driver, 3).until(new Function<WebDriver, Boolean>() {
            @Override public Boolean apply(WebDriver input) {
                return root.getText().equals("Hello World");
            }
        });
        AssertJUnit.assertEquals("Hello World", root.getText());
    }

    public void expandANode() throws Throwable {
        WebElement tree = page.getTree();
        tree.click();
        tree.click();
        WebElement root = tree.findElement(By.cssSelector(".::root"));
        AssertJUnit.assertEquals(1 + "", tree.getAttribute("rowCount"));
        new Actions(driver).doubleClick(root).perform();
        new WebDriverWait(driver, 3).until(hasAttributeValue(tree, "rowCount", 3 + ""));
        AssertJUnit.assertEquals(3 + "", tree.getAttribute("rowCount"));
    }

    public void getNodesByRow() throws Throwable {
        WebElement tree = page.getTree();
        tree.click();
        WebElement root = tree.findElement(By.cssSelector(".::nth-node(1)"));
        AssertJUnit.assertEquals(1 + "", tree.getAttribute("rowCount"));
        new Actions(driver).doubleClick(root).perform();
        new WebDriverWait(driver, 3).until(hasAttributeValue(tree, "rowCount", 3 + ""));
        AssertJUnit.assertEquals(3 + "", tree.getAttribute("rowCount"));
        WebElement node1 = tree.findElement(By.cssSelector(".::nth-node(2)"));
        AssertJUnit.assertEquals("Parent 1", node1.getText());
        WebElement node2 = tree.findElement(By.cssSelector(".::nth-node(3)"));
        AssertJUnit.assertEquals("Parent 2", node2.getText());
    }

    public void expandTree() throws Throwable {
        WebElement tree = page.getTree();
        tree.click();
        WebElement root = tree.findElement(By.cssSelector(".::nth-node(1)"));
        AssertJUnit.assertEquals("false", root.getAttribute("expanded"));
        AssertJUnit.assertEquals(1 + "", tree.getAttribute("rowCount"));
        new Actions(driver).doubleClick(root).perform();
        new WebDriverWait(driver, 3).until(hasAttributeValue(root, "expanded", "true"));
        AssertJUnit.assertEquals("true", root.getAttribute("expanded"));
        AssertJUnit.assertEquals(3 + "", tree.getAttribute("rowCount"));
        WebElement node1 = tree.findElement(By.cssSelector(".::nth-node(2)"));
        AssertJUnit.assertEquals("Parent 1", node1.getText());
        new Actions(driver).doubleClick(node1).perform();
        WebElement node2 = tree.findElement(By.cssSelector(".::nth-node(3)"));
        AssertJUnit.assertEquals("Child 1", node2.getText());
        WebElement node3 = tree.findElement(By.cssSelector(".::nth-node(4)"));
        AssertJUnit.assertEquals("Child 2", node3.getText());
        WebElement node4 = tree.findElement(By.cssSelector(".::nth-node(5)"));
        AssertJUnit.assertEquals("Parent 2", node4.getText());
        new Actions(driver).doubleClick(node4).perform();
        WebElement node5 = tree.findElement(By.cssSelector(".::nth-node(6)"));
        AssertJUnit.assertEquals("Child 1", node5.getText());
        WebElement node6 = tree.findElement(By.cssSelector(".::nth-node(7)"));
        AssertJUnit.assertEquals("Child 2", node6.getText());
    }

    private Function<WebDriver, Boolean> hasAttributeValue(final WebElement element, final String attribute, final String value) {
        return new Function<WebDriver, Boolean>() {
            @Override public Boolean apply(WebDriver input) {
                return value.equals(element.getAttribute(attribute));
            }

            @Override public String toString() {
                return "[hasAttributeValue attribute = " + attribute + ", value = " + value + "] on element = " + element + "]";
            }
        };
    }

    public void nodeEditor() throws Throwable {
        System.err.println("Ignore the following NPE. The DynamicTree class has a bug");
        WebElement tree = page.getTree();
        tree.click();
        WebElement root = tree.findElement(By.cssSelector(".::nth-node(1)"));
        AssertJUnit.assertEquals("Root Node", root.getText());
        WebElement editor = root.findElement(By.cssSelector(".::editor"));
        editor.clear();
        editor.sendKeys("Hello World", Keys.ENTER);
        root.submit();
        AssertJUnit.assertEquals("Hello World", root.getText());
    }

    public void getAllNodes() throws Throwable {
        expandTree();
        WebElement tree = page.getTree();
        tree.click();
        List<WebElement> nodes = tree.findElements(By.cssSelector(".::all-nodes"));
        AssertJUnit.assertEquals(7, nodes.size());
    }

    public void gettext() throws Throwable {
        WebElement tree = page.getTree();
        tree.click();
        AssertJUnit.assertEquals("[\"Root Node\"]", tree.getText());
        expandTree();
        AssertJUnit.assertEquals("[\"Root Node\",\"Parent 1\",\"Child 1\",\"Child 2\",\"Parent 2\",\"Child 1\",\"Child 2\"]",
                tree.getText());
    }

    public void getNodesByText() throws Throwable {
        WebElement tree = page.getTree();
        tree.click();
        AssertJUnit.assertEquals("[\"Root Node\"]", tree.getText());
        expandTree();
        AssertJUnit.assertEquals("[\"Root Node\",\"Parent 1\",\"Child 1\",\"Child 2\",\"Parent 2\",\"Child 1\",\"Child 2\"]",
                tree.getText());
        List<WebElement> nodes;
        nodes = tree.findElements(By.cssSelector(".::all-nodes[text='Root Node']"));
        AssertJUnit.assertEquals(1, nodes.size());
        nodes = tree.findElements(By.cssSelector(".::all-nodes[text='Child 2']"));
        AssertJUnit.assertEquals(2, nodes.size());
    }
}
