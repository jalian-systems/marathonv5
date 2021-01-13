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
package net.sourceforge.marathon.javadriver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Point;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebDriver.Window;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.SessionId;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.json.JSONObject;
import net.sourceforge.marathon.testhelpers.MissingException;

@Test
public class JavaDriverTest {

    private WebDriver driver;

    protected JFrame frame;
    protected JTextField textField;
    protected JButton button;
    protected JMenu menu;
    protected JMenuItem exitItem;
    protected boolean buttonClicked = false;
    protected StringBuilder buttonMouseActions;
    private boolean altPressed = false;
    private boolean altReleased = false;
    protected JLabel label;
    protected JPanel textFieldPane;

    protected String titleOfWindow;

    protected java.awt.Window window;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {

            private JLabel textLabel;

            @Override
            public void run() {
                titleOfWindow = JavaDriverTest.class.getName();
                frame = new JFrame(titleOfWindow);
                frame.setName("dialog-1");
                JMenuBar menuBar = new JMenuBar();
                menu = new JMenu("File");
                menu.setMnemonic(KeyEvent.VK_F);
                menuBar.add(menu);
                exitItem = new JMenuItem("Exit");
                menu.add(exitItem);
                frame.setJMenuBar(menuBar);
                Box box = new Box(BoxLayout.Y_AXIS) {
                    private static final long serialVersionUID = 1L;
                };
                box.setName("box-panel");

                label = new JLabel("Enter The Text");
                box.add(label);
                textLabel = new JLabel("Text Field");
                textField = new JTextField("");
                textLabel.setLabelFor(textField);
                textField.setName("text-field");
                textFieldPane = new JPanel();
                textFieldPane.setLayout(new BoxLayout(textFieldPane, BoxLayout.LINE_AXIS));
                textFieldPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
                textFieldPane.add(Box.createHorizontalGlue());
                textFieldPane.add(textLabel);
                textFieldPane.add(Box.createRigidArea(new java.awt.Dimension(10, 0)));
                textFieldPane.add(textField);
                box.add(textFieldPane);
                button = new JButton("Click Me!!");
                box.add(button);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                JButton button1 = new JButton("Click Me Delayed!!");
                                button1.setName("click-me-delayed");
                                frame.getContentPane().add(button1);
                            }
                        });
                    }
                }, 1000);
                button.setName("click-me");
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttonClicked = true;
                    }
                });
                buttonMouseActions = new StringBuilder();
                button.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        buttonMouseActions.append("released(" + e.getButton() + (e.isPopupTrigger() ? "-popup" : "") + ")-");
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        buttonMouseActions.append("pressed(" + e.getButton() + (e.isPopupTrigger() ? "-popup" : "") + ")-");
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        buttonMouseActions.append("exited-");
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        buttonMouseActions.append("entered-");
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        buttonMouseActions.append("clicked(" + e.getButton() + (e.isPopupTrigger() ? "-popup" : "") + ")-");
                    }
                });
                button.addMouseMotionListener(new MouseMotionListener() {

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        buttonMouseActions.append("moved-");
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        buttonMouseActions.append("dragged-");
                    }
                });
                frame.setContentPane(box);
                frame.pack();
                frame.setAlwaysOnTop(true);
            }

        });

    }

    @AfterMethod
    public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(false);
                frame.dispose();
                if (window != null) {
                    window.setVisible(false);
                    window.dispose();
                    window = null;
                }
            }
        });
        if (driver != null) {
            driver.quit();
        }
    }

    public void javaDriver() {
        driver = new JavaDriver();
        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
        AssertJUnit.assertEquals("java", capabilities.getBrowserName());
        AssertJUnit.assertEquals(true, capabilities.is("takesScreenshot"));
        AssertJUnit.assertEquals(false, capabilities.is("nativeEvents"));
    }

    public void failsWhenRequestingANonJavaDriver() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities("xjava", "1.0", Platform.getCurrent());
        try {
            driver = new JavaDriver(caps, caps);
            throw new MissingException(SessionNotCreatedException.class);
        } catch (SessionNotCreatedException e) {
        }
    }

    public void failsWhenRequestingNonCurrentPlatform() throws Throwable {
        Platform[] values = Platform.values();
        Platform otherPlatform = null;
        for (Platform platform : values) {
            if (Platform.getCurrent().is(platform)) {
                continue;
            }
            otherPlatform = platform;
            break;
        }
        DesiredCapabilities caps = new DesiredCapabilities("java", "1.0", otherPlatform);
        try {
            driver = new JavaDriver(caps, caps);
            throw new MissingException(SessionNotCreatedException.class);
        } catch (SessionNotCreatedException e) {
        }
    }

    public void supportsJavascriptEnabledCapability() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities("java", "1.0", Platform.getCurrent());
        caps.setJavascriptEnabled(true);
        driver = new JavaDriver(caps, caps);
    }

    public void failsWhenRequestingUnsupportedCapability() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities("java", "1.0", Platform.getCurrent());
        caps.setCapability("rotatable", true);
        try {
            driver = new JavaDriver(caps, caps);
            throw new MissingException(SessionNotCreatedException.class);
        } catch (SessionNotCreatedException e) {
        }
    }

    public void succeedsWhenRequestingNativeEventsCapability() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities("java", "1.0", Platform.getCurrent());
        caps.setCapability("nativeEvents", true);
        driver = new JavaDriver(caps, caps);
        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
        AssertJUnit.assertTrue(capabilities.is("nativeEvents"));
    }

    public void succeedsWhenRequestingNonNativeEventsCapability() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities("java", "1.0", Platform.getCurrent());
        caps.setCapability("nativeEvents", false);
        driver = new JavaDriver(caps, caps);
        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
        AssertJUnit.assertTrue(!capabilities.is("nativeEvents"));
    }

    public void getWindowHandles() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
        Set<String> windowHandles = driver.getWindowHandles();
        AssertJUnit.assertEquals(1, windowHandles.size());
    }

    public void close() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
        driver.close();
    }

    public void window() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
        driver.switchTo().window(titleOfWindow);
    }

    public void windowSelectWithProperties() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
        driver.switchTo().window(titleOfWindow);
        JSONObject props = new JSONObject(driver.getCurrentUrl());
        AssertJUnit.assertEquals("javax.swing.JFrame", props.get("component.class.name"));
        AssertJUnit.assertEquals(titleOfWindow, props.get("title"));
    }

    public void windowThrowsExceptionWhenNotExisting() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
        try {
            driver.switchTo().window("My Dialog1");
            throw new MissingException(NoSuchWindowException.class);
        } catch (NoSuchWindowException e) {
        }
    }

    public void windowWithImplicitWait() throws Throwable {
        driver = new JavaDriver();
        driver.manage().timeouts().implicitlyWait(3000, TimeUnit.MILLISECONDS);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            frame.setVisible(true);
                        }
                    });
                } catch (InterruptedException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }, 1000);
        driver.switchTo().window(titleOfWindow);
    }

    public void getSessions() {
        driver = new JavaDriver();
        JavaDriver driver1 = new JavaDriver();
        SessionId id1 = ((RemoteWebDriver) driver).getSessionId();
        SessionId id2 = driver1.getSessionId();
        AssertJUnit.assertFalse(id1.equals(id2));
    }

    public void quit() {
        driver = new JavaDriver();
        driver.quit();
        AssertJUnit.assertNull(((RemoteWebDriver) driver).getSessionId());
    }

    public void windowHandle() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
        AssertJUnit.assertNotNull(driver.getWindowHandle());
    }

    public void windowSize() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        Window window = driver.manage().window();
        Dimension actual = window.getSize();
        AssertJUnit.assertNotNull(actual);
        java.awt.Dimension expected = EventQueueWait.call(frame, "getSize");
        AssertJUnit.assertEquals(expected.width, actual.width);
        AssertJUnit.assertEquals(expected.height, actual.height);
    }

    public void windowPosition() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        Window window = driver.manage().window();
        Point actual = window.getPosition();
        AssertJUnit.assertNotNull(actual);
        java.awt.Point expected = EventQueueWait.call(frame, "getLocation");
        AssertJUnit.assertEquals(expected.x, actual.x);
        AssertJUnit.assertEquals(expected.y, actual.y);
    }

    public void windowSetSize() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        Window window = driver.manage().window();
        Dimension actual = window.getSize();
        AssertJUnit.assertNotNull(actual);
        java.awt.Dimension expected = EventQueueWait.call(frame, "getSize");
        AssertJUnit.assertEquals(expected.width, actual.width);
        AssertJUnit.assertEquals(expected.height, actual.height);
        window.setSize(new Dimension(expected.width * 2, expected.height * 2));
        actual = window.getSize();
        AssertJUnit.assertEquals(expected.width * 2, actual.width);
        AssertJUnit.assertEquals(expected.height * 2, actual.height);
    }

    public void windowSetPosition() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        Window window = driver.manage().window();
        Point actual = window.getPosition();
        AssertJUnit.assertNotNull(actual);
        java.awt.Point expected = EventQueueWait.call(frame, "getLocation");
        AssertJUnit.assertEquals(expected.x, actual.x);
        AssertJUnit.assertEquals(expected.y, actual.y);
        window.setPosition(new Point(expected.x + 10, expected.y + 10));
        actual = window.getPosition();
        AssertJUnit.assertEquals(expected.x + 10, actual.x);
        AssertJUnit.assertEquals(expected.y + 10, actual.y);
    }

    public void windowMaximize() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        Window window = driver.manage().window();
        window.maximize();
    }

    public void findElement() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element = driver.findElement(By.name("click-me"));
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getText"), element.getText());
    }

    public void findElementGetsTheSameElementBetweenCalls() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        String id1 = ((RemoteWebElement) element1).getId();
        WebElement element2 = driver.findElement(By.name("click-me"));
        String id2 = ((RemoteWebElement) element2).getId();
        AssertJUnit.assertEquals(id1, id2);
    }

    public void findElementGetsTheSameElementBetweenWindowCalls() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        String id1 = ((RemoteWebElement) element1).getId();
        driver.switchTo().window(titleOfWindow);
        WebElement element2 = driver.findElement(By.name("click-me"));
        String id2 = ((RemoteWebElement) element2).getId();
        AssertJUnit.assertEquals(id1, id2);
    }

    public void findElementThrowsNoSuchElementIfNotFound() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        try {
            driver.findElement(By.name("click-me-note-there"));
            throw new MissingException(NoSuchElementException.class);
        } catch (NoSuchElementException e) {
        }
    }

    public void findElements() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        List<WebElement> elements = driver.findElements(By.name("click-me"));
        AssertJUnit.assertNotNull(elements);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getText"), elements.get(0).getText());
    }

    public void findActive() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        EventQueueWait.requestFocus(textField);
        WebElement active = driver.switchTo().activeElement();
        AssertJUnit.assertNotNull(active);
        AssertJUnit.assertEquals("text-field", active.getTagName());
    }

    public void findElementOfElement() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element = driver.findElement(By.name("box-panel"));
        AssertJUnit.assertNotNull(element);
        WebElement clickMe = element.findElement(By.name("click-me"));
        AssertJUnit.assertNotNull(clickMe);
    }

    public void findElementsOfElement() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element = driver.findElement(By.name("box-panel"));
        AssertJUnit.assertNotNull(element);
        List<WebElement> clickMe = element.findElements(By.name("click-me"));
        AssertJUnit.assertNotNull(clickMe);
    }

    public void elementClick() throws Throwable {
        buttonClicked = false;
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        element1.click();
        AssertJUnit.assertTrue(buttonClicked);
    }

    public void elementSubmit() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        element1.submit();
    }

    public void elementSendKeys() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        AssertJUnit.assertEquals("", EventQueueWait.call(textField, "getText"));
        WebElement element1 = driver.findElement(By.name("text-field"));
        element1.sendKeys("Hello", " ", "World");
        AssertJUnit.assertEquals("Hello World", EventQueueWait.call(textField, "getText"));
    }

    public void keys() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                textField.requestFocusInWindow();
            }
        });
        AssertJUnit.assertEquals("", EventQueueWait.call(textField, "getText"));
        WebElement element1 = driver.findElement(By.name("text-field"));
        new Actions(driver).sendKeys(element1, "Hello World").perform();
        AssertJUnit.assertEquals("Hello World", EventQueueWait.call(textField, "getText"));
    }

    public void getName() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                textField.setText("Hello World");
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("text-field"));
        AssertJUnit.assertEquals("text-field", element1.getTagName());
    }

    public void getLabeledBy() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                textField.setText("Hello World");
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        driver.findElement(By.cssSelector("[type='JTextField'][labeledBy='Text Field'][name='text-field']"));
    }

    public void getNameReturnsProperTagNameForAnonClasses() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                textField.setText("Hello World");
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("box-panel"));
        AssertJUnit.assertEquals("box", element1.getTagName());
    }

    public void elementClear() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        AssertJUnit.assertEquals("", EventQueueWait.call(textField, "getText"));
        WebElement element1 = driver.findElement(By.name("text-field"));
        element1.sendKeys("Hello", " ", "World");
        AssertJUnit.assertEquals("Hello World", EventQueueWait.call(textField, "getText"));
        element1.clear();
        AssertJUnit.assertEquals("", EventQueueWait.call(textField, "getText"));
    }

    public void elementClearOnNonClearableComponents() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        try {
            element1.clear();
            throw new MissingException(UnsupportedCommandException.class);
        } catch (UnsupportedCommandException e) {

        }
    }

    public void isSelected() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        element1.isSelected();
    }

    public void isSelectedOnNonSelectableComponents() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("text-field"));
        try {
            element1.isSelected();
            throw new MissingException(UnsupportedCommandException.class);
        } catch (UnsupportedCommandException e) {

        }
    }

    public void isEnabled() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        AssertJUnit.assertTrue(element1.isEnabled());
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(false);
            }
        });
        EventQueueWait.waitTillDisabled(button);
        AssertJUnit.assertFalse(element1.isEnabled());
    }

    public void getAttribute() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        AssertJUnit.assertTrue(Boolean.valueOf(element1.getAttribute("enabled")));
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(false);
            }
        });
        EventQueueWait.waitTillDisabled(button);
        AssertJUnit.assertFalse(Boolean.valueOf(element1.getAttribute("enabled")));
    }

    public void getAttributeUnsupportedAttribute() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        AssertJUnit.assertNull(element1.getAttribute("enabledX"));
    }

    public void elementEquals() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        // Note that there is no remote communication in this case (since a
        // unique ID is provided for each component however we get it).
        WebElement element1 = driver.findElement(By.name("click-me"));
        WebElement element2 = driver.findElement(By.name("box-panel")).findElement(By.name("click-me"));
        AssertJUnit.assertEquals(element1, element2);
    }

    public void elementNotEquals() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        // Note that there is no remote communication in this case (since a
        // unique ID is provided for each component however we get it).
        WebElement element1 = driver.findElement(By.name("click-me"));
        WebElement element2 = driver.findElement(By.name("text-field"));
        AssertJUnit.assertFalse(element1.equals(element2));
    }

    public void isDisplayed() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        AssertJUnit.assertTrue(element1.isDisplayed());
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                button.setVisible(false);
            }
        });
        EventQueueWait.waitTillInvisibled(button);
        AssertJUnit.assertFalse(element1.isDisplayed());
    }

    public void getLocation() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        Point location = element1.getLocation();
        java.awt.Point p = EventQueueWait.call(button, "getLocation");
        AssertJUnit.assertEquals(p.x, location.x);
        AssertJUnit.assertEquals(p.y, location.y);
    }

    public void getLocationInView() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        try {
            ((RemoteWebElement) element1).getCoordinates().inViewPort();
            throw new MissingException(WebDriverException.class);
        } catch (WebDriverException e) {
        }
    }

    public void getSize() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        Dimension size = element1.getSize();
        java.awt.Dimension d = EventQueueWait.call(button, "getSize");
        AssertJUnit.assertEquals(d.width, size.width);
        AssertJUnit.assertEquals(d.height, size.height);
    }

    @Test(enabled = false)
    public void moveTo() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        new Actions(driver).moveToElement(element1).click().perform();
        buttonMouseActions.setLength(0);
        new Actions(driver).moveToElement(element1).perform();
        AssertJUnit.assertTrue(buttonMouseActions.length() > 0);
        AssertJUnit.assertEquals("moved-", buttonMouseActions.toString());
        new Actions(driver).moveToElement(element1, 10, 10).perform();
        AssertJUnit.assertEquals("moved-moved-", buttonMouseActions.toString());
    }

    public void click() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        element1.click();
        AssertJUnit.assertTrue(buttonClicked);
        buttonClicked = false;
        new Actions(driver).click().perform();
        AssertJUnit.assertTrue(buttonClicked);
        AssertJUnit.assertTrue(buttonMouseActions.toString().contains("clicked(1)"));
        buttonMouseActions.setLength(0);
        new Actions(driver).contextClick().perform();
        AssertJUnit.assertTrue(buttonMouseActions.toString(), buttonMouseActions.toString().contains("pressed(3-popup)"));
    }

    public void doubleClick() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        element1.click();
        AssertJUnit.assertTrue(buttonClicked);
        buttonMouseActions.setLength(0);
        new Actions(driver).doubleClick().perform();
        AssertJUnit.assertTrue(buttonMouseActions.toString().matches(".*clicked.*clicked.*"));
    }

    public void buttonDownUp() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                button.requestFocusInWindow();
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        buttonMouseActions.setLength(0);
        new Actions(driver).clickAndHold(element1).moveByOffset(10, 10).release().perform();
        AssertJUnit.assertTrue(buttonMouseActions.toString().matches(".*dragged.*released.*"));
    }

    public void logs() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        element1.click();
        Logs logs = driver.manage().logs();
        LogEntries logEntries = logs.get("driver");
        Iterator<LogEntry> iterator = logEntries.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }

    public void logTypes() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
        AssertJUnit.assertTrue(logTypes.contains("driver"));
    }

    public void findElementsByTagName() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        List<WebElement> elements = driver.findElements(By.tagName("menu"));
        AssertJUnit.assertNotNull(elements);
        AssertJUnit.assertEquals(1, elements.size());
        AssertJUnit.assertEquals("File", elements.get(0).getText());
    }

    public void findElementByTagName() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element = driver.findElement(By.tagName("menu"));
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals("File", element.getText());
    }

    public void findElementsByTagNameOfElement() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement parent = driver.findElement(By.tagName("box"));
        List<WebElement> elements = parent.findElements(By.tagName("label"));
        AssertJUnit.assertNotNull(elements);
        AssertJUnit.assertEquals(2, elements.size());
        AssertJUnit.assertEquals("Enter The Text", elements.get(0).getText());
        AssertJUnit.assertEquals("Text Field", elements.get(1).getText());
    }

    public void findElementByTagNameOfElement() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement parent = driver.findElement(By.tagName("box"));
        WebElement element = parent.findElement(By.tagName("label"));
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals("Enter The Text", element.getText());
    }

    public void findElementsByCSS() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        List<WebElement> elements = driver.findElements(By.cssSelector("menu"));
        AssertJUnit.assertNotNull(elements);
        AssertJUnit.assertEquals(1, elements.size());
        AssertJUnit.assertEquals("File", elements.get(0).getText());
    }

    public void findElementsByCSSWithUniversalSelector() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        List<WebElement> elements = driver.findElements(By.cssSelector("*"));
        AssertJUnit.assertNotNull(elements);
    }

    public void findElementsByCSSWithSelfSelector() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("text-field"));
        WebElement element2 = element1.findElement(By.cssSelector("."));
        AssertJUnit.assertEquals(element1, element2);
        WebElement element3 = element1.findElement(By.cssSelector(".:enabled"));
        AssertJUnit.assertEquals(element1, element3);
        WebElement element4 = element1.findElement(By.cssSelector(".#text-field"));
        AssertJUnit.assertEquals(element1, element4);
        List<WebElement> none = element1.findElements(By.cssSelector(".#text-fieldx"));
        AssertJUnit.assertEquals(0, none.size());
    }

    public void getAccessibleName() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("text-field"));
        AssertJUnit.assertEquals("Text Field", element1.getAttribute("accessibleName"));
    }

    public void getLabelText() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.cssSelector("label"));
        AssertJUnit.assertEquals("lbl:Enter The Text", element1.getAttribute("labelText"));
    }

    public void getInstanceOf() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement textField = driver.findElement(By.name("text-field"));
        AssertJUnit.assertEquals("javax.swing.JTextField", textField.getAttribute("instanceOf"));
        WebElement menu = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("javax.swing.JMenu", menu.getAttribute("instanceOf"));
    }

    public void getPrecedingLabel() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement webElement = driver.findElement(By.cssSelector("#text-field"));
        AssertJUnit.assertEquals("Text Field", webElement.getAttribute("precedingLabel"));
    }

    public void getIndexOfType() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("text-field"));
        AssertJUnit.assertEquals("0", element1.getAttribute("indexOfType"));
        List<WebElement> labels = driver.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals("0", labels.get(0).getAttribute("indexOfType"));
        AssertJUnit.assertEquals("1", labels.get(1).getAttribute("indexOfType"));
    }

    public void getCText() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        WebElement element1 = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Click Me!!", element1.getAttribute("cText"));
        WebElement element2 = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("File", element2.getAttribute("cText"));
    }

    public void getButtonText() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        WebElement element1 = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Click Me!!", element1.getAttribute("buttonText"));
        WebElement element2 = driver.findElement(By.cssSelector("menu"));
        AssertJUnit.assertEquals("File", element2.getAttribute("buttonText"));
    }

    public void getFieldName() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.cssSelector("menu-bar"));
        AssertJUnit.assertEquals("menuBar", element1.getAttribute("fieldName"));
    }

    public void getEnabled() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement button = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("Click Me!!", button.getText());
        AssertJUnit.assertEquals("true", button.getAttribute("enabled"));
        button.click();
    }

    public void getType() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element = driver.findElement(By.cssSelector("[type='JTextField'][labeledBy='Text Field'][name='text-field']"));
        AssertJUnit.assertEquals("JTextField", element.getAttribute("type"));
        WebElement button = driver.findElement(By.cssSelector("[type='JButton'][accessibleName='Click Me!!'][name='click-me']"));
        AssertJUnit.assertEquals("JButton", button.getAttribute("type"));
    }

    public void sendkeysDriverDoesNotReleaseModifierKeys() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                textField.addKeyListener(new KeyListener() {

                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                            altReleased = e.getKeyCode() == KeyEvent.VK_ALT;
                        }
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                            altPressed = e.getKeyCode() == KeyEvent.VK_ALT;
                        }
                    }
                });
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("text-field"));
        altPressed = false;
        altReleased = false;
        new Actions(driver).sendKeys(element1, Keys.ALT).perform();
        AssertJUnit.assertTrue(altPressed);
        AssertJUnit.assertFalse(altReleased);
        new Actions(driver).sendKeys(element1, Keys.ALT).perform();
    }

    public void sendkeysElementImplicitlyReleasesModifierKeys() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                textField.addKeyListener(new KeyListener() {

                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                            altReleased = e.getKeyCode() == KeyEvent.VK_ALT;
                        }
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) {
                            altPressed = e.getKeyCode() == KeyEvent.VK_ALT;
                        }
                    }
                });
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("text-field"));
        altPressed = false;
        altReleased = false;
        element1.sendKeys(Keys.ALT);
        AssertJUnit.assertTrue(altPressed);
        AssertJUnit.assertTrue(altReleased);
    }

    public void setAttribute() throws Throwable {
        try {
            driver = new JavaDriver();
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
            WebElement element1 = driver.findElement(By.name("text-field"));
            String value = "Hello\\ World";
            element1.findElement(By.cssSelector(".::call-select('" + value.replace("\\", "\\\\").replace("'", "\\'") + "')"));
            AssertJUnit.assertEquals(value, element1.getText());
        } finally {
            JavaElementFactory.reset();
        }
    }

    public void getTitle() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        String title = driver.getTitle();
        AssertJUnit.assertNotNull(title);
        AssertJUnit.assertEquals(EventQueueWait.call(frame, "getTitle"), title);
    }

    public void windowTitleWithPercentage() throws Throwable {
        driver = new JavaDriver();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setTitle("My %Dialog%");
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        String id1 = ((RemoteWebElement) element1).getId();
        // driver.switchTo().window("My %25Dialog%25");
        TargetLocator switchTo = driver.switchTo();
        switchTo.window("My %Dialog%");
        WebElement element2 = driver.findElement(By.name("click-me"));
        String id2 = ((RemoteWebElement) element2).getId();
        AssertJUnit.assertEquals(id1, id2);
    }

    public void executeScript() throws Throwable {
        try {
            driver = new JavaDriver();
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
            WebElement element1 = driver.findElement(By.name("text-field"));
            WebElement element2 = (WebElement) ((JavascriptExecutor) driver).executeScript("return $1;", element1);
            AssertJUnit.assertEquals(element1, element2);
        } finally {
            JavaElementFactory.reset();
        }
    }

    public void executeScriptWithPrimitiveReturn() throws Throwable {
        try {
            driver = new JavaDriver();
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
            WebElement element1 = driver.findElement(By.name("text-field"));
            Number width = (Number) ((JavascriptExecutor) driver).executeScript("return Double.valueOf($1.getSize().getWidth());",
                    element1);
            AssertJUnit.assertTrue(width.doubleValue() > 0);
        } finally {
            JavaElementFactory.reset();
        }
    }

    public void executeAsyncScript() throws Throwable {
        try {
            driver = new JavaDriver();
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
            WebElement element1 = driver.findElement(By.name("text-field"));
            WebElement element2 = (WebElement) ((JavascriptExecutor) driver).executeAsyncScript("$2.call($1);", element1);
            AssertJUnit.assertEquals(element1, element2);
        } finally {
            JavaElementFactory.reset();
        }
    }

    public void executeAsyncScriptWithoutCallback() throws Throwable {
        try {
            driver = new JavaDriver();
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
            WebElement element1 = driver.findElement(By.name("text-field"));
            ((JavascriptExecutor) driver).executeAsyncScript("$1.setText(\"hello world\");", element1);
            throw new MissingException(WebDriverException.class);
        } catch (WebDriverException e) {
        } finally {
            JavaElementFactory.reset();
        }
    }

    public void executeAsyncScriptWithNullReturn() throws Throwable {
        try {
            driver = new JavaDriver();
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
            WebElement element1 = driver.findElement(By.name("text-field"));
            WebElement element2 = (WebElement) ((JavascriptExecutor) driver).executeAsyncScript("$2.call(null);", element1);
            AssertJUnit.assertNull(element2);
        } finally {
            JavaElementFactory.reset();
        }
    }

    public void keysWithNativeEvents() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("nativeEvents", true);
        driver = new JavaDriver(caps, caps);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                textField.requestFocusInWindow();
            }
        });
        AssertJUnit.assertEquals("", EventQueueWait.call(textField, "getText"));
        WebElement element1 = driver.findElement(By.name("text-field"));
        new Actions(driver).sendKeys(element1, "Hello World").perform();
        AssertJUnit.assertEquals("Hello World", EventQueueWait.call(textField, "getText"));
    }

    public void clickWithNativeEvents() throws Throwable {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("nativeEvents", true);
        driver = new JavaDriver(new JavaProfile(), caps, caps);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
        WebElement element1 = driver.findElement(By.name("click-me"));
        element1.click();
        AssertJUnit.assertTrue(buttonClicked);
        buttonClicked = false;
        new Actions(driver).click().perform();
        AssertJUnit.assertTrue(buttonClicked);
        AssertJUnit.assertTrue(buttonMouseActions.toString().contains("clicked(1)"));
        buttonMouseActions.setLength(0);
        new Actions(driver).contextClick().perform();
        AssertJUnit.assertTrue(buttonMouseActions.toString(), buttonMouseActions.toString().contains("pressed(3-popup)")
                || buttonMouseActions.toString().contains("released(3-popup)"));
    }

    public void screenshot() throws Throwable {
        try {
            driver = new JavaDriver();
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
            if (driver instanceof TakesScreenshot) {
                Thread.sleep(1000);
                File screenshotAs = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                System.out.println(screenshotAs.getAbsolutePath());
                Thread.sleep(20000);
            }
        } finally {
            JavaElementFactory.reset();
        }
    }

}
