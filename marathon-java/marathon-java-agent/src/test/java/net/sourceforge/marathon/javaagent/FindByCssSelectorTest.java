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
package net.sourceforge.marathon.javaagent;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class FindByCssSelectorTest {

    private IJavaAgent driver;
    protected JFrame frame;
    protected JTextField textField;
    protected JButton button;
    protected JMenu menu;
    protected JMenuItem exitItem;
    private JCheckBox checkbox;
    private JButton buttonDelayed;

    @BeforeMethod
    public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                frame = new JFrame("My Dialog");
                frame.setName("dialog-1");
                JMenuBar menuBar = new JMenuBar();
                menu = new JMenu("File");
                menu.setMnemonic(KeyEvent.VK_F);
                menuBar.add(menu);
                exitItem = new JMenuItem("Exit");
                menu.add(exitItem);
                frame.setJMenuBar(menuBar);
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(createBox(), BorderLayout.NORTH);
                frame.setContentPane(panel);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }

            private Box createBox() {
                final Box box = new Box(BoxLayout.Y_AXIS);
                box.setName("box-panel");
                textField = new JTextField("");

                box.add(textField);
                button = new JButton("Click Me!!");
                box.add(button);
                button.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                buttonDelayed = new JButton("Click Me Delayed!!");
                                buttonDelayed.setName("click-me-delayed");
                                box.add(buttonDelayed);
                            }
                        });
                    }
                }, 1000);
                button.setName("click-me");
                checkbox = new JCheckBox("Check Box");
                checkbox.setName("check");
                box.add(checkbox);
                box.add(createBox2());
                return box;
            }

            private Box createBox2() {
                final Box box = new Box(BoxLayout.Y_AXIS);
                JTextField textField = new JTextField("");

                box.add(textField);
                JButton button = new JButton("Click Me!!");
                box.add(new JScrollPane(button));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                buttonDelayed = new JButton("Click Me Delayed!!");
                                buttonDelayed.setName("click-me-delayed");
                                box.add(buttonDelayed);
                            }
                        });
                    }
                }, 1000);
                button.setName("click-me");
                JCheckBox checkbox = new JCheckBox("Check Box");
                checkbox.setName("check");
                box.add(checkbox);
                return box;
            }
        });
        driver = new JavaAgent();
    }

    @AfterMethod
    public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void simpleSelector() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("button");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
    }

    public void universalSelectorWithTag() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("#click-me");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
    }

    public void simpleSelectorWithTag() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("button#click-me");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        List<IJavaElement> findElementsByCssSelector = driver.findElementsByCssSelector("button#non-existant");
        AssertJUnit.assertNotNull(findElementsByCssSelector);
        AssertJUnit.assertEquals(0, findElementsByCssSelector.size());
    }

    public void simpleSelectorWithAttribute() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("button[text='Click Me!!']");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector("button[text^='Click']");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector("button[text$='e!!']");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector("button[text*='Me']");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        List<IJavaElement> findElementsByCssSelector = driver.findElementsByCssSelector("button[text='Not There']");
        AssertJUnit.assertNotNull(findElementsByCssSelector);
        AssertJUnit.assertEquals(0, findElementsByCssSelector.size());
        findElementsByCssSelector = driver.findElementsByCssSelector("[text]");
        AssertJUnit.assertNotNull(findElementsByCssSelector);
    }

    public void simpleSelectorWithFunction() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("button:enabled");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector(":instance-of('javax.swing.JButton')");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
    }

    public void simpleSelectorWithFunctionAll() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("button:enabled");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                button.setEnabled(false);
            }
        });
        element = driver.findElementByCssSelector("button:disabled");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector("button:displayed");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                button.setVisible(false);
            }
        });
        element = driver.findElementByCssSelector("button:hidden");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector(":instance-of('javax.swing.JButton')");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector("check-box:deselected");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(checkbox, "getName"), element.getAttribute("name"));
        element = driver.findElementByCssSelector(":instance-of('javax.swing.JCheckBox'):deselected");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(checkbox, "getName"), element.getAttribute("name"));
    }

    public void descendentSelector1() throws Throwable {
        driver.manage().timeouts().implicitlyWait(5000, TimeUnit.MILLISECONDS);
        IJavaElement element = driver.findElementByCssSelector("box button#click-me-delayed");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(buttonDelayed, "getName"), element.getAttribute("name"));
    }

    public void descendentSelector() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("box button:enabled");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
    }

    public void childSelector() throws Throwable {
        IJavaElement element = driver.findElementByCssSelector("box>button:enabled");
        AssertJUnit.assertNotNull(element);
        AssertJUnit.assertEquals(EventQueueWait.call(button, "getName"), element.getAttribute("name"));
    }

    public void childAndDescendentSelector() throws Throwable {
        List<IJavaElement> byName = driver.findElementsByName("click-me");
        List<IJavaElement> byChild = driver.findElementsByCssSelector("box>button#click-me");
        List<IJavaElement> byDescendent = driver.findElementsByCssSelector("box button#click-me");
        AssertJUnit.assertEquals(2, byName.size());
        AssertJUnit.assertEquals(1, byChild.size());
        AssertJUnit.assertEquals(2, byDescendent.size());
    }

    public void adjecentSibling() throws Throwable {
        List<IJavaElement> elements = driver.findElementsByCssSelector("panel > box > button");
        AssertJUnit.assertEquals(1, elements.size());
        elements = driver.findElementsByCssSelector("panel > box > button + check-box");
        AssertJUnit.assertEquals(1, elements.size());
        elements = driver.findElementsByCssSelector("panel > box > text-field + button");
        AssertJUnit.assertEquals(1, elements.size());
        elements = driver.findElementsByCssSelector("panel > box > text-field + check-box");
        AssertJUnit.assertEquals(0, elements.size());
    }

    public void generalSibling() throws Throwable {
        List<IJavaElement> elements = driver.findElementsByCssSelector("panel > box > button");
        AssertJUnit.assertEquals(1, elements.size());
        elements = driver.findElementsByCssSelector("panel > box > button ~ check-box");
        AssertJUnit.assertEquals(1, elements.size());
        elements = driver.findElementsByCssSelector("panel > box > text-field ~ button");
        AssertJUnit.assertEquals(1, elements.size());
        elements = driver.findElementsByCssSelector("panel > box > text-field ~ check-box");
        AssertJUnit.assertEquals(1, elements.size());
    }

    public void findWorksForElements() throws Throwable {
        IJavaElement box = driver.findElementByCssSelector("#box-panel");
        AssertJUnit.assertNotNull(box);
        IJavaElement button = box.findElementByCssSelector("button");
        button.click();
    }

    public void adjecentSiblingForElement() throws Throwable {
        IJavaElement box = driver.findElementByCssSelector("#box-panel");
        AssertJUnit.assertNotNull(box);
        List<IJavaElement> elements = box.findElementsByCssSelector("button");
        AssertJUnit.assertEquals(2, elements.size());
        elements = box.findElementsByCssSelector("button + check-box");
        AssertJUnit.assertEquals(1, elements.size());
        elements = box.findElementsByCssSelector("text-field + button");
        AssertJUnit.assertEquals(1, elements.size());
        elements = box.findElementsByCssSelector("text-field + check-box");
        AssertJUnit.assertEquals(0, elements.size());
    }

    public void generalSiblingForElement() throws Throwable {
        IJavaElement box = driver.findElementByCssSelector("#box-panel");
        List<IJavaElement> elements = box.findElementsByCssSelector("button");
        AssertJUnit.assertEquals(2, elements.size());
        elements = box.findElementsByCssSelector("button ~ check-box");
        AssertJUnit.assertEquals(1, elements.size());
        elements = box.findElementsByCssSelector("text-field ~ button");
        AssertJUnit.assertEquals(1, elements.size());
        elements = box.findElementsByCssSelector("text-field ~ check-box");
        AssertJUnit.assertEquals(2, elements.size());
    }

    public void nthPseudoClass() throws Throwable {
        List<IJavaElement> findElementsByCssSelector = driver.findElementsByCssSelector("button");
        AssertJUnit.assertEquals(2, findElementsByCssSelector.size());
        findElementsByCssSelector = driver.findElementsByCssSelector("button:nth(1)");
        AssertJUnit.assertEquals(1, findElementsByCssSelector.size());
        findElementsByCssSelector = driver.findElementsByCssSelector("button:nth(2)");
        AssertJUnit.assertEquals(1, findElementsByCssSelector.size());
        findElementsByCssSelector = driver.findElementsByCssSelector("button:nth(3)");
        AssertJUnit.assertEquals(0, findElementsByCssSelector.size());
    }

}
