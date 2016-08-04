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
package net.sourceforge.marathon.javaagent.components;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.CheckBoxDemo;
import components.RadioButtonDemo;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaElementFactory;

@Test public class JToggleButtonJavaElementTest extends JavaElementTest {
    private IJavaAgent driver;
    protected JFrame frame;

    @BeforeMethod public void showDialog() throws Throwable {
        JavaElementFactory.add(JToggleButton.class, JToggleButtonJavaElement.class);
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JToggleButtonJavaElementTest.class.getSimpleName());
                frame.setName("frame-" + JToggleButtonJavaElementTest.class.getSimpleName());
                frame.getContentPane().add(new CheckBoxDemo(), BorderLayout.CENTER);
                frame.getContentPane().add(new RadioButtonDemo(), BorderLayout.EAST);
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

    public void selectCheckboxSelectedNotSelected() throws Throwable {
        List<IJavaElement> boxes = driver.findElementsByTagName("check-box");
        IJavaElement checkBox = boxes.get(2);
        String attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
        marathon_select(checkBox, "false");
        attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("false", attribute);
    }

    public void selectCheckboxSelectedSelected() throws Throwable {
        List<IJavaElement> boxes = driver.findElementsByTagName("check-box");
        IJavaElement checkBox = boxes.get(2);
        String attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
        marathon_select(checkBox, "true");
        attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
    }

    public void selectCheckboxNotSelectedSelected() throws Throwable {
        List<IJavaElement> boxes = driver.findElementsByTagName("check-box");
        IJavaElement checkBox = boxes.get(2);
        marathon_select(checkBox, "false");
        String attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("false", attribute);
        marathon_select(checkBox, "true");
        attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
    }

    public void selectCheckboxNotSelectedNotSelected() throws Throwable {
        List<IJavaElement> boxes = driver.findElementsByTagName("check-box");
        IJavaElement checkBox = boxes.get(2);
        marathon_select(checkBox, "false");
        String attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("false", attribute);
        marathon_select(checkBox, "false");
        attribute = checkBox.getAttribute("isSelected");
        AssertJUnit.assertEquals("false", attribute);
    }

    public void selectRadioBottonSelectedSelected() throws Throwable {
        List<IJavaElement> buttons = driver.findElementsByTagName("radio-button");
        IJavaElement button = buttons.get(0);
        String attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
        marathon_select(button, "true");
        attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
    }

    public void selectRadiobuttonSelectedNotSelected() throws Throwable {
        List<IJavaElement> buttons = driver.findElementsByTagName("radio-button");
        IJavaElement button = buttons.get(0);
        String attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
        marathon_select(button, "false");
        attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
    }

    public void selectRadiobuttonNotSelectedSelected() throws Throwable {
        List<IJavaElement> buttons = driver.findElementsByTagName("radio-button");
        IJavaElement button = buttons.get(1);
        String attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("false", attribute);
        marathon_select(button, "true");
        attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("true", attribute);
    }

    public void selectRadiobuttoNotSelectedNotSelected() throws Throwable {
        List<IJavaElement> buttons = driver.findElementsByTagName("radio-button");
        IJavaElement button = buttons.get(1);
        String attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("false", attribute);
        marathon_select(button, "false");
        attribute = button.getAttribute("isSelected");
        AssertJUnit.assertEquals("false", attribute);
    }

}
