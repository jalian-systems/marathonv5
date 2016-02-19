package net.sourceforge.marathon.javadriver.mcomponent;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.EventQueueWait;
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

@Test(enabled = false) public class NativeEventsTest {
    private WebDriver driver;

    protected JFrame frame;
    protected JButton button;
    protected boolean buttonClicked = false;
    protected JTextArea actionsArea;

    private int events = 0;

    protected Logger logger = Logger.getLogger(this.getClass().getName());

    protected JTextField textField;

    protected String testName;

    private WebElement weButton;

    private WebElement weTextArea;

    @BeforeMethod public void showDialog(Method method) throws Throwable {
        testName = method.getName();
        SwingUtilities.invokeAndWait(new Runnable() {

            public void captureEvent(InputEvent e, String event) {
                if (events != e.getID())
                    return;
                if (events == MouseEvent.MOUSE_MOVED)
                    System.out.println(e);
                String clickCount = "";
                if (e instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e;
                    clickCount = "" + me.getClickCount() + "-" + me.isPopupTrigger();
                } else if (e instanceof KeyEvent) {
                    KeyEvent ke = (KeyEvent) e;
                    clickCount = "" + (int) ke.getKeyChar() + "-" + (int) ke.getKeyCode();
                }
                String mt1 = getModifiersExText(e);
                String mt2 = getModifiersText(e);
                actionsArea.append(event + "(" + clickCount + mt1 + mt2 + ")\n");
            }

            public String getModifiersExText(InputEvent e) {
                String mt = MouseEvent.getModifiersExText(e.getModifiersEx());
                if (mt != null && !"".equals(mt))
                    mt = "-" + mt;
                else
                    mt = "";
                return mt;
            }

            public String getModifiersText(InputEvent e) {
                String mt = MouseEvent.getMouseModifiersText(e.getModifiers());
                if (mt != null && !"".equals(mt))
                    mt = "-" + mt;
                else
                    mt = "";
                return mt;
            }

            @Override public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                JPanel box = new JPanel(new BorderLayout());
                box.setName("box-panel");
                button = new JButton("Click Me!!");
                box.add(button, BorderLayout.NORTH);
                button.setName("click-me");
                button.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        buttonClicked = true;
                    }
                });
                button.addMouseMotionListener(new MouseMotionListener() {

                    @Override public void mouseMoved(MouseEvent e) {
                        captureEvent(e, "moved");
                    }

                    @Override public void mouseDragged(MouseEvent e) {
                        captureEvent(e, "dragged");
                    }
                });

                button.addMouseListener(new MouseListener() {
                    @Override public void mouseReleased(MouseEvent e) {
                        captureEvent(e, "released");
                    }

                    @Override public void mousePressed(MouseEvent e) {
                        captureEvent(e, "pressed");
                    }

                    @Override public void mouseExited(MouseEvent e) {
                        captureEvent(e, "exited");
                    }

                    @Override public void mouseEntered(MouseEvent e) {
                        captureEvent(e, "entered");
                    }

                    @Override public void mouseClicked(MouseEvent e) {
                        captureEvent(e, "clicked");
                    }

                });
                actionsArea = new JTextArea(5, 80);
                actionsArea.setName("actions");
                box.add(new JScrollPane(actionsArea), BorderLayout.CENTER);
                textField = new JTextField();
                textField.setName("enter-text");
                textField.addKeyListener(new KeyListener() {
                    @Override public void keyTyped(KeyEvent e) {
                        captureEvent(e, "typed");
                    }

                    @Override public void keyReleased(KeyEvent e) {
                        captureEvent(e, "released");
                    }

                    @Override public void keyPressed(KeyEvent e) {
                        captureEvent(e, "pressed");
                    }
                });
                box.add(textField, BorderLayout.SOUTH);
                frame.setContentPane(box);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
                frame.requestFocus();
            }
        });
        driver = new JavaDriver();
        weButton = driver.findElement(By.name("click-me"));
        weTextArea = driver.findElement(By.name("actions"));

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

    public void altClickGeneratesSameEventsClicked() throws Throwable {
        checkAltClickEvent(MouseEvent.MOUSE_CLICKED);
    }

    public void altClickGeneratesSameEventsReleased() throws Throwable {
        checkAltClickEvent(MouseEvent.MOUSE_RELEASED);
    }

    public void altClickGeneratesSameEventsPressed() throws Throwable {
        checkAltClickEvent(MouseEvent.MOUSE_PRESSED);
    }

    public void altClickGeneratesSameEventsEntered() throws Throwable {
        checkAltClickEvent(MouseEvent.MOUSE_ENTERED);
    }

    public void altClickGeneratesSameEventsExited() throws Throwable {
        checkAltClickEvent(MouseEvent.MOUSE_EXITED);
    }

    public void altClickGeneratesSameEventsMoved() throws Throwable {
        checkAltClickEvent(MouseEvent.MOUSE_MOVED);
    }

    private void checkAltClickEvent(int eventToCheck) throws InterruptedException, InvocationTargetException, AWTException {
        events = eventToCheck;
        tclear();
        Point locationButton = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension sizeButton = EventQueueWait.call_noexc(button, "getSize");
        Point locationTextArea = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension sizeTextArea = EventQueueWait.call_noexc(actionsArea, "getSize");

        Robot robot = new Robot();
        robot.setAutoDelay(10);
        robot.setAutoWaitForIdle(true);
        robot.mouseMove(locationTextArea.x + sizeTextArea.width / 2, locationTextArea.y + sizeTextArea.height / 2);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.mouseMove(locationButton.x + sizeButton.width / 2 + 1, locationButton.y + sizeButton.height / 2 + 1);
        robot.mouseMove(locationButton.x + sizeButton.width / 2, locationButton.y + sizeButton.height / 2);
        robot.keyPress(KeyEvent.VK_ALT);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.mouseMove(locationTextArea.x + sizeTextArea.width / 2, locationTextArea.y + sizeTextArea.height / 2);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = weTextArea.getText();
        tclear();
        System.err.println("================================");
        System.err.println(expected);
        System.err.println("=================================");
        new Actions(driver).moveToElement(weButton).keyDown(Keys.ALT).click().keyUp(Keys.ALT).moveToElement(weTextArea).perform();
        AssertJUnit.assertEquals(expected, weTextArea.getText());
    }

    public void altRightClickGeneratesSameEventsClicked() throws Throwable {
        checkAltRightClickEvent(MouseEvent.MOUSE_CLICKED);
    }

    public void altRightClickGeneratesSameEventsPressed() throws Throwable {
        checkAltRightClickEvent(MouseEvent.MOUSE_PRESSED);
    }

    public void altRightClickGeneratesSameEventsReleased() throws Throwable {
        checkAltRightClickEvent(MouseEvent.MOUSE_RELEASED);
    }

    private void checkAltRightClickEvent(int eventToCheck) throws InterruptedException, InvocationTargetException, AWTException {
        events = eventToCheck;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.keyPress(KeyEvent.VK_ALT);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON3_MASK);
        r.mouseRelease(InputEvent.BUTTON3_MASK);
        r.keyRelease(KeyEvent.VK_ALT);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(button, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        new Actions(driver).moveToElement(b).keyDown(Keys.ALT).contextClick().keyUp(Keys.ALT).perform();
        AssertJUnit.assertEquals(expected, t.getText());
        // Wait till the previous click is processed by the EDT
        Thread.sleep(500);
    }

    public void doubleClickGeneratesSameEventsClicked() throws Throwable {
        checkDoubleClickEvent(MouseEvent.MOUSE_CLICKED);
    }

    public void doubleClickGeneratesSameEventsPressed() throws Throwable {
        checkDoubleClickEvent(MouseEvent.MOUSE_PRESSED);
    }

    public void doubleClickGeneratesSameEventsReleased() throws Throwable {
        checkDoubleClickEvent(MouseEvent.MOUSE_RELEASED);
    }

    private void checkDoubleClickEvent(int eventToCheck) throws InterruptedException, InvocationTargetException, AWTException {
        events = eventToCheck;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        Thread.sleep(50);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().contains("(2");
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(button, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        new Actions(driver).moveToElement(b).doubleClick().perform();
        AssertJUnit.assertEquals(expected, t.getText());
    }

    public void enteredGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_ENTERED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.keyPress(KeyEvent.VK_ALT);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        r.keyRelease(KeyEvent.VK_ALT);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        new Actions(driver).moveToElement(t).keyDown(Keys.ALT).moveToElement(b).click().keyUp(Keys.ALT).perform();
        AssertJUnit.assertEquals(expected, t.getText());

    }

    public void exitedGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_EXITED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.keyPress(KeyEvent.VK_ALT);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(button, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        r.keyRelease(KeyEvent.VK_ALT);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");

        String expected = t.getText();
        tclear();
        new Actions(driver).moveToElement(t).keyDown(Keys.ALT).moveToElement(b).moveToElement(t).keyUp(Keys.ALT).perform();
        AssertJUnit.assertEquals(expected, t.getText());
    }

    public void pressGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_PRESSED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        b.click();
        AssertJUnit.assertEquals(expected, t.getText());

        tclear();
        new Actions(driver).moveToElement(b).click().perform();
        AssertJUnit.assertEquals(expected, t.getText());

    }

    public void releaseGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_RELEASED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        b.click();
        AssertJUnit.assertEquals(expected, t.getText());

        tclear();
        new Actions(driver).moveToElement(b).click().perform();
        AssertJUnit.assertEquals(expected, t.getText());

    }

    public void rightClickGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_CLICKED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON3_MASK);
        r.mouseRelease(InputEvent.BUTTON3_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        new Actions(driver).moveToElement(b).contextClick().perform();
        AssertJUnit.assertEquals(expected, t.getText());

    }

    public void singleClickGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_CLICKED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        b.click();
        AssertJUnit.assertEquals(expected, t.getText());

        tclear();
        new Actions(driver).moveToElement(b).click().perform();
        AssertJUnit.assertEquals(expected, t.getText());

    }

    public void movedGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_MOVED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        b.click();
        AssertJUnit.assertEquals(expected, t.getText());

        tclear();
        new Actions(driver).moveToElement(b).click().perform();
        AssertJUnit.assertEquals(expected, t.getText());
    }

    public void draggedGeneratesSameEvents() throws Throwable {
        events = MouseEvent.MOUSE_DRAGGED;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("click-me"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(button, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(button, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");

        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        b.click();
        tclear();
        System.err.println("============================");
        new Actions(driver).clickAndHold(b).moveToElement(b).release().perform();
        System.err.println("============================");
        AssertJUnit.assertEquals(expected, t.getText());
    }

    public void normalPressedLowerAlphabetGeneratesSameEvents() throws Throwable {
        checkKeyEvent(KeyEvent.KEY_PRESSED, "a", KeyEvent.VK_A);
    }

    public void normalRealeasedLowerAlphabetGeneratesSameEvents() throws Throwable {
        checkKeyEvent(KeyEvent.KEY_RELEASED, "a", KeyEvent.VK_A);
    }

    public void normalTypedLowerAlphabetGeneratesSameEvents() throws Throwable {
        checkKeyEvent(KeyEvent.KEY_TYPED, "a", KeyEvent.VK_A);
    }

    public void normalPressedUpperAlphabetGeneratesSameEvents() throws Throwable {
        checkKeyEvent(KeyEvent.KEY_PRESSED, "A", KeyEvent.VK_SHIFT, KeyEvent.VK_A);
    }

    public void normalReleasedUpperAlphabetGeneratesSameEvents() throws Throwable {
        checkKeyEvent(KeyEvent.KEY_RELEASED, "A", KeyEvent.VK_SHIFT, KeyEvent.VK_A);
    }

    public void normalTypedUpperAlphabetGeneratesSameEvents() throws Throwable {
        checkKeyEvent(KeyEvent.KEY_TYPED, "A", KeyEvent.VK_SHIFT, KeyEvent.VK_A);
    }

    private void checkKeyEvent(int eventToCheck, String keysToSend, int... keysToPress) throws InterruptedException,
            InvocationTargetException, AWTException {
        events = eventToCheck;
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
        driver = new JavaDriver();
        WebElement b = driver.findElement(By.name("enter-text"));
        WebElement t = driver.findElement(By.name("actions"));

        Point location = EventQueueWait.call_noexc(textField, "getLocationOnScreen");
        Dimension size = EventQueueWait.call_noexc(textField, "getSize");
        Robot r = new Robot();
        r.setAutoDelay(10);
        r.setAutoWaitForIdle(true);
        r.mouseMove(location.x + size.width / 2, location.y + size.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);
        for (int i = 0; i < keysToPress.length; i++) {
            r.keyPress(keysToPress[i]);
        }
        for (int i = keysToPress.length - 1; i >= 0; i--) {
            r.keyRelease(keysToPress[i]);
        }
        new EventQueueWait() {
            @Override public boolean till() {
                return actionsArea.getText().length() > 0;
            }
        }.wait("Waiting for actionsArea failed?");
        String expected = t.getText();
        tclear();
        Point location2 = EventQueueWait.call_noexc(actionsArea, "getLocationOnScreen");
        Dimension size2 = EventQueueWait.call_noexc(actionsArea, "getSize");
        r.mouseMove(location2.x + size2.width / 2, location2.y + size2.height / 2);
        r.mousePress(InputEvent.BUTTON1_MASK);
        r.mouseRelease(InputEvent.BUTTON1_MASK);

        b.sendKeys(keysToSend);
        System.out.println("Expected: " + expected);
        AssertJUnit.assertEquals(expected, t.getText());

        new Actions(driver).moveToElement(b).click().perform();
        AssertJUnit.assertEquals(expected, t.getText());
    }

    private void tclear() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                actionsArea.setText("");
            }
        });
    }

}
