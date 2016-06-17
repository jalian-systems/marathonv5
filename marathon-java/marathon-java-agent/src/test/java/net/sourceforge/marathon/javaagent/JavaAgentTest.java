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
package net.sourceforge.marathon.javaagent;

import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.testhelpers.MissingException;

@Test public class JavaAgentTest {

    private IJavaAgent driver;
    protected JFrame frame;
    protected JTextField textField;
    protected JButton button;
    protected JMenu menu;
    protected JMenuItem exitItem;
    protected JDialog d2;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                JMenuBar menuBar = new JMenuBar();
                menu = new JMenu("File");
                menu.setMnemonic(KeyEvent.VK_F);
                menuBar.add(menu);
                exitItem = new JMenuItem("Exit");
                menu.add(exitItem);
                frame.setJMenuBar(menuBar);
                Box box = new Box(BoxLayout.Y_AXIS);
                textField = new JTextField("");

                box.add(textField);
                button = new JButton("Click Me!!");
                box.add(button);
                new Timer().schedule(new TimerTask() {
                    @Override public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override public void run() {
                                JButton button1 = new JButton("Click Me Delayed!!");
                                button1.setName("click-me-delayed");
                                frame.getContentPane().add(button1);
                            }
                        });
                    }
                }, 1000);
                button.setName("click-me");
                frame.setContentPane(box);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void getTitle() throws Throwable {
        AssertJUnit.assertEquals("My Dialog", driver.getTitle());
    }

    public void getWindowHandles() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                d2 = new JDialog(frame, false);
                d2.setTitle("My Dialog 2");
                d2.pack();
                d2.setVisible(true);
            }
        });
        Collection<String> windowHandles = driver.getWindowHandles();
        AssertJUnit.assertEquals(2, windowHandles.size());
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                d2.dispose();
            }
        });
    }

    public void getWindowHandle() throws Throwable {
        AssertJUnit.assertNotNull(driver.getWindowHandle());
    }

    public void switchTo() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                d2 = new JDialog(frame, false);
                d2.setName("dialog-2");
                d2.setTitle("My Dialog 2");
                JButton b = new JButton("Click-Me-2");
                b.setName("click-me-2");
                d2.getContentPane().add(b);
                d2.pack();
                d2.setVisible(true);
            }
        });
        Collection<String> windowHandles = driver.getWindowHandles();
        AssertJUnit.assertEquals(2, windowHandles.size());
        driver.switchTo().window("dialog-2");
        AssertJUnit.assertNotNull(driver.findElementByName("click-me-2"));
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                d2.dispose();
            }
        });
    }

    public void staleElementThrowsError() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                d2 = new JDialog(frame, false);
                d2.setName("dialog-2");
                d2.setTitle("My Dialog 2");
                JButton b = new JButton("Click-Me-2");
                b.setName("click-me-2");
                d2.getContentPane().add(b);
                d2.pack();
                d2.setVisible(true);
            }
        });
        Collection<String> windowHandles = driver.getWindowHandles();
        AssertJUnit.assertEquals(2, windowHandles.size());
        driver.switchTo().window("dialog-2");
        IJavaElement e = driver.findElementByName("click-me-2");
        AssertJUnit.assertNotNull(e);
        driver.switchTo().window("dialog-1");
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                d2.dispose();
            }
        });
        try {
            e.click();
            throw new MissingException(StaleElementReferenceException.class);
        } catch (StaleElementReferenceException x) {
        }
    }

    public void switchingToANonExistentWindow() throws Throwable {
        try {
            driver.switchTo().window("dialog-2");
            throw new MissingException(NoSuchWindowException.class);
        } catch (NoSuchWindowException e) {

        }
    }

    public void manage() {
        AssertJUnit.assertNotNull(driver.manage());
    }

    public void findElementByName() throws Throwable {
        IJavaElement element = driver.findElementByName("click-me");
        AssertJUnit.assertNotNull(element);
    }

    public void findElementByNameNoElementExists() throws Throwable {
        try {
            driver.findElementByName("click-me-not-there");
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        }
    }

    public void findElementByNameWithTimeOut() throws Throwable {
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
        IJavaElement element = driver.findElementByName("click-me-delayed");
        AssertJUnit.assertNotNull(element);
    }

    public void findElementsByName() throws Throwable {
        List<IJavaElement> elements = driver.findElementsByName("click-me");
        AssertJUnit.assertEquals(1, elements.size());
    }

    public void findElementsByNameNoElementExists() throws Throwable {
        List<IJavaElement> elements = driver.findElementsByName("click-me-not-there");
        AssertJUnit.assertEquals(0, elements.size());
    }

    public void findElementsByNameNoElementExistsWithImplicitWait() throws Throwable {
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
        List<IJavaElement> elements = driver.findElementsByName("click-me-not-there");
        AssertJUnit.assertEquals(0, elements.size());
    }

    public void findElementsByNameWithTimeOut() throws Throwable {
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
        List<IJavaElement> elements = driver.findElementsByName("click-me-delayed");
        AssertJUnit.assertEquals(1, elements.size());
    }

}
