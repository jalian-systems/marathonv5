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
package net.sourceforge.marathon.javadriver;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test public class JavaDriverLogsTest {

    private WebDriver driver;

    protected JFrame frame;
    protected JTextField textField;
    protected JButton button;
    protected JMenu menu;
    protected JMenuItem exitItem;
    protected boolean buttonClicked = false;
    protected StringBuilder buttonMouseActions;

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
                Box box = new Box(BoxLayout.Y_AXIS) {
                    private static final long serialVersionUID = 1L;
                };
                box.setName("box-panel");
                textField = new JTextField("");
                textField.setName("text-field");
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
                button.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        buttonClicked = true;
                    }
                });
                buttonMouseActions = new StringBuilder();
                button.addMouseListener(new MouseListener() {
                    @Override public void mouseReleased(MouseEvent e) {
                        buttonMouseActions.append("released-");
                    }

                    @Override public void mousePressed(MouseEvent e) {
                        buttonMouseActions.append("pressed-");
                    }

                    @Override public void mouseExited(MouseEvent e) {
                        buttonMouseActions.append("exited-");
                    }

                    @Override public void mouseEntered(MouseEvent e) {
                        buttonMouseActions.append("entered-");
                    }

                    @Override public void mouseClicked(MouseEvent e) {
                        buttonMouseActions.append("clicked(" + e.getButton() + ")-");
                    }
                });
                button.addMouseMotionListener(new MouseMotionListener() {

                    @Override public void mouseMoved(MouseEvent e) {
                        buttonMouseActions.append("moved-");
                    }

                    @Override public void mouseDragged(MouseEvent e) {
                        buttonMouseActions.append("dragged-");
                    }
                });
                frame.setContentPane(box);
                frame.setAlwaysOnTop(true);
                frame.pack();
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

    public void loggingWorks() {
        LoggingPreferences prefs = new LoggingPreferences();
        prefs.enable(LogType.DRIVER, Level.INFO);
        DesiredCapabilities caps = JavaDriver.defaultCapabilities();
        caps.setCapability(CapabilityType.LOGGING_PREFS, prefs);
        driver = new JavaDriver(caps, caps);
        LogEntries logEntries = driver.manage().logs().get(LogType.DRIVER);
        List<LogEntry> all = logEntries.getAll();
        AssertJUnit.assertEquals(2, all.size());
        AssertJUnit.assertTrue(all.get(0).getMessage().contains("A new session created. sessionID = "));
    }

    public void loglevelsAreRespected() {
        LoggingPreferences prefs = new LoggingPreferences();
        prefs.enable(LogType.DRIVER, Level.WARNING);
        DesiredCapabilities caps = JavaDriver.defaultCapabilities();
        caps.setCapability(CapabilityType.LOGGING_PREFS, prefs);
        driver = new JavaDriver(caps, caps);
        LogEntries logEntries = driver.manage().logs().get(LogType.DRIVER);
        List<LogEntry> all = logEntries.getAll();
        AssertJUnit.assertEquals(0, all.size());
    }

}
