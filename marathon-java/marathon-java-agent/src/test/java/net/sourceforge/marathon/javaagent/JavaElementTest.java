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
        ejButton = (IJavaElement) ejDriver.findElementByName("click-me");
        ejText = (IJavaElement) ejDriver.findElementByName("text");
        ejCheckbox = (IJavaElement) ejDriver.findElementByName("check-me");
        ejBoxPanel = (IJavaElement) ejDriver.findElementByName("box-panel");
        ejAwtText = (IJavaElement) ejDriver.findElementByName("awt-text");
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
