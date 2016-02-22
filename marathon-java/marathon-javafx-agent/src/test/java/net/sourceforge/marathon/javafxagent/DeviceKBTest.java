package net.sourceforge.marathon.javafxagent;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.testng.AssertJUnit;
import org.testng.SkipException;
import org.testng.annotations.Test;

import net.sourceforge.marathon.javafxagent.EventQueueWait;
import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.Platform;
import net.sourceforge.marathon.javafxagent.Device.Type;

@Test public abstract class DeviceKBTest extends DeviceTest {

    public DeviceKBTest(Type type) {
        super(type);
    }

    public void sendKeys() throws Throwable {
        driver.sendKeys(textField, "Hello ", "World");
        new WaitWithoutException("Waiting for text to be set") {
            @Override public boolean until() {
                String text;
                try {
                    text = EventQueueWait.call(textField, "getText");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return false;
                }
                return "Hello World".equals(text);
            }
        };
        String text = EventQueueWait.call(textField, "getText");
        AssertJUnit.assertEquals("Hello World", text);
    }

    public void sendKeysKeyboardMap() throws Throwable {
        kss.clear();
        driver.sendKeys(textField, "A");
        new WaitWithoutException("Waiting for the keypress") {
            @Override public boolean until() {
                try {
                    return kss.toString().contains(KeyStroke.getKeyStroke("released SHIFT").toString());
                } catch (Throwable t) {
                    return false;
                }
            }
        };
        List<KeyStroke> expected = Arrays.asList(KeyStroke.getKeyStroke("shift pressed SHIFT"),
                KeyStroke.getKeyStroke("shift pressed A"), KeyStroke.getKeyStroke("shift typed A"),
                KeyStroke.getKeyStroke("shift released A"), KeyStroke.getKeyStroke("released SHIFT"));
        AssertJUnit.assertEquals(expected.toString(), kss.toString());
    }

    public void sendKeysSelectAll() throws Throwable {
        driver.sendKeys(textField, "Hello World");
        driver.sendKeys(textField, getOSKey(), "a");
        driver.sendKeys(textField, JavaAgentKeys.NULL);
        new WaitWithoutException("Waiting for the select event") {
            @Override public boolean until() {
                try {
                    Object text = EventQueueWait.call(textField, "getSelectedText");
                    return text != null && text.equals("Hello World");
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        String expected = "Hello World";
        AssertJUnit.assertEquals(expected, EventQueueWait.<String> call(textField, "getSelectedText"));
    }

    public void sendKeysClearsModifiersWhenReceivesNull() throws Throwable {
        JavaAgentKeys osKey = getOSKey();
        final String osString = osKey.equals(JavaAgentKeys.CONTROL) ? "ctrl" : "meta";
        kss.clear();
        driver.sendKeys(textField, osKey);
        new WaitWithoutException("Waiting for meta press") {
            @Override public boolean until() {
                return kss.size() > 0;
            }
        };
        kss.clear();
        driver.sendKeys(textField, JavaAgentKeys.F2);
        final String expected1 = "[" + osString + " pressed F2, " + osString + " released F2]";
        new WaitWithoutException("Waiting for ctrl+f2") {
            @Override public boolean until() {
                return expected1.equals(kss.toString());
            }
        };
        AssertJUnit.assertEquals(expected1, kss.toString());
        kss.clear();
        driver.sendKeys(textField, JavaAgentKeys.NULL);
        new WaitWithoutException("Waiting for meta press") {
            @Override public boolean until() {
                return kss.size() > 0;
            }
        };
        kss.clear();
        driver.sendKeys(textField, JavaAgentKeys.F2);
        final String expected2 = "[pressed F2, released F2]";
        new WaitWithoutException("Waiting for ctrl+f2") {
            @Override public boolean until() {
                return expected2.equals(kss.toString());
            }
        };
        AssertJUnit.assertEquals(expected2, kss.toString());
    }

    public void pressAndReleaseKeysWithAlt() throws Throwable {
        kss.clear();
        driver.pressKey(textField, JavaAgentKeys.ALT);
        driver.pressKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.ALT);
        new WaitWithoutException("Waiting for alt release") {
            @Override public boolean until() {
                return kss.toString().contains("released ALT");
            }
        };
        List<KeyStroke> expected = Arrays.asList(KeyStroke.getKeyStroke("alt pressed ALT"),
                KeyStroke.getKeyStroke("alt pressed F1"), KeyStroke.getKeyStroke("alt released F1"),
                KeyStroke.getKeyStroke("released ALT"));
        AssertJUnit.assertEquals(expected.toString(), kss.toString());
    }

    public void sendKeysWithAlt() throws Throwable {
        kss.clear();
        driver.sendKeys(textField, JavaAgentKeys.ALT, JavaAgentKeys.F1, JavaAgentKeys.ALT);
        new WaitWithoutException("Waiting for alt release") {
            @Override public boolean until() {
                return kss.toString().contains("released ALT");
            }
        };
        List<KeyStroke> expected = Arrays.asList(KeyStroke.getKeyStroke("alt pressed ALT"),
                KeyStroke.getKeyStroke("alt pressed F1"), KeyStroke.getKeyStroke("alt released F1"),
                KeyStroke.getKeyStroke("released ALT"));
        AssertJUnit.assertEquals(expected.toString(), kss.toString());
    }

    public void pressAndReleaseKeysWithShift() throws Throwable {
        kss.clear();
        driver.pressKey(textField, JavaAgentKeys.SHIFT);
        driver.pressKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.SHIFT);
        new WaitWithoutException("Waiting for shift release") {
            @Override public boolean until() {
                return kss.toString().contains("released SHIFT");
            }
        };
        List<KeyStroke> expected = Arrays.asList(KeyStroke.getKeyStroke("shift pressed SHIFT"),
                KeyStroke.getKeyStroke("shift pressed F1"), KeyStroke.getKeyStroke("shift released F1"),
                KeyStroke.getKeyStroke("released SHIFT"));
        AssertJUnit.assertEquals(expected.toString(), kss.toString());
    }

    public void whetherMenusAreAccessible() throws Throwable {
        if (Platform.getCurrent() == Platform.MAC && System.getProperty("java.version", "").matches("1.[78].*")) {
            throw new SkipException("Menu mneomonics are not handled on Mac under Java 1.7+");
        }
        exitItemCalled = false;
        driver.sendKeys(textField, JavaAgentKeys.chord(JavaAgentKeys.ALT, "f"));
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return menu.isPopupMenuVisible();
            }
        };
        driver.sendKeys(exitItem, JavaAgentKeys.ENTER);
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return exitItemCalled;
            }
        };
        AssertJUnit.assertEquals(true, exitItemCalled);
    }

    public void whetherAllKeysAreMapped() throws Throwable {
        driver.sendKeys(textField, ");!@#$%^&*(");
        new WaitWithoutException("Waiting for exit item to be called") {
            @Override public boolean until() {
                return !textField.getText().equals("");
            }
        };
        AssertJUnit.assertEquals(");!@#$%^&*(", textField.getText());
    }

    private JavaAgentKeys getOSKey() {
        KeyStroke selectall = null;
        InputMap inputMap = new JTextField().getInputMap();
        KeyStroke[] allKeys = inputMap.allKeys();
        for (KeyStroke keyStroke : allKeys) {
            Object object = inputMap.get(keyStroke);
            if (object.equals("select-all")) {
                selectall = keyStroke;
                break;
            }
        }
        if ((selectall.getModifiers() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK)
            return JavaAgentKeys.CONTROL;
        if ((selectall.getModifiers() & KeyEvent.META_DOWN_MASK) == KeyEvent.META_DOWN_MASK)
            return JavaAgentKeys.META;
        throw new RuntimeException("Which key?");
    }

}
