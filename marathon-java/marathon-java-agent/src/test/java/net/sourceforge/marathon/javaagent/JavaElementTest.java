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

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import javax.swing.SwingUtilities;

import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javaagent.Device.Type;
import net.sourceforge.marathon.testhelpers.MissingException;

@Test public class JavaElementTest extends DeviceTest {

    private IJavaElement ejButton;
    private IJavaAgent ejDriver;
    private IJavaElement ejText;
    private IJavaElement ejCheckbox;
    private IJavaElement ejBoxPanel;
    private IJavaElement ejAwtText;

    public JavaElementTest() {
        super(Type.EVENT_QUEUE);
    }

    @BeforeMethod public void createWebDriver() throws Throwable {
        ejDriver = new JavaAgent();
        ejButton = ejDriver.findElementByName("click-me");
        ejText = ejDriver.findElementByName("text");
        ejCheckbox = ejDriver.findElementByName("check-me");
        ejBoxPanel = ejDriver.findElementByName("box-panel");
        ejAwtText = ejDriver.findElementByName("awt-text");
        AssertJUnit.assertNotNull(ejButton);
    }

    public void click() throws Throwable {
        AssertJUnit.assertFalse(buttonClicked);
        ejButton.click();
        new WaitWithoutException("Waiting for the button to be clicked") {
            @Override public boolean until() {
                return buttonClicked;
            }
        };
        AssertJUnit.assertTrue(buttonClicked);
    }

    public void clickDisabled() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                button.setEnabled(false);
            }
        });
        try {
            ejButton.click();
            throw new MissingException(InvalidElementStateException.class);
        } catch (InvalidElementStateException e) {
        }
    }

    public void sendKeys() throws Throwable {
        ejText.sendKeys("Hello World");
        String text = EventQueueWait.call(textField, "getText");
        AssertJUnit.assertEquals("Hello World", text);
    }

    public void sendKeysDisabled() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                textField.setEnabled(false);
            }
        });
        try {
            ejText.sendKeys("Hello World");
            throw new MissingException(InvalidElementStateException.class);
        } catch (InvalidElementStateException e) {
        }
    }

    public void clear() throws Throwable {
        ejText.sendKeys("Hello World");
        String text = EventQueueWait.call(textField, "getText");
        AssertJUnit.assertEquals("Hello World", text);
        ejText.clear();
        text = EventQueueWait.call(textField, "getText");
        AssertJUnit.assertEquals("", text);
    }

    public void getAttribute() throws Throwable {
        String attribute = ejText.getAttribute("size");
        AssertJUnit.assertNotNull(attribute);
    }

    public void getAttributeNotAvailable() throws Throwable {
        AssertJUnit.assertNull(ejText.getAttribute("sizeX"));
    }

    public void isSelected() throws Throwable {
        ejCheckbox.click();
        AssertJUnit.assertTrue(ejCheckbox.isSelected());
    }

    public void isEnabled() throws Throwable {
        AssertJUnit.assertTrue(ejButton.isEnabled());
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                button.setEnabled(false);
            }
        });
        AssertJUnit.assertFalse(ejButton.isEnabled());
    }

    public void getText() throws Throwable {
        ejText.sendKeys("Hello World");
        AssertJUnit.assertEquals("Hello World", ejText.getText());
    }

    public void findElements() throws Throwable {
        List<IJavaElement> e = ejBoxPanel.findElementsByName("click-me");
        AssertJUnit.assertNotNull(e);
        AssertJUnit.assertEquals(1, e.size());
    }

    public void findElementNotAContainer() throws Throwable {
        try {
            ejAwtText.findElementByName("doesnot-matter");
            throw new MissingException(UnsupportedCommandException.class);
        } catch (UnsupportedCommandException e) {

        }
    }

    public void findElement() throws Throwable {
        IJavaElement e = ejBoxPanel.findElementByName("click-me");
        AssertJUnit.assertNotNull(e);
    }

    public void isDisplayed() throws Throwable {
        AssertJUnit.assertTrue(ejButton.isDisplayed());
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                button.setVisible(false);
            }
        });
        AssertJUnit.assertFalse(ejButton.isDisplayed());
    }

    public void getLocation() throws Throwable {
        Point location = ejText.getLocation();
        AssertJUnit.assertNotNull(location);
    }

    public void getSize() throws Throwable {
        Dimension d = ejText.getSize();
        AssertJUnit.assertNotNull(d);
    }

    public void getCssValue() throws Throwable {
        try {
            ejText.getCssValue("something");
            throw new MissingException(UnsupportedCommandException.class);
        } catch (UnsupportedCommandException e) {

        }
    }

}
