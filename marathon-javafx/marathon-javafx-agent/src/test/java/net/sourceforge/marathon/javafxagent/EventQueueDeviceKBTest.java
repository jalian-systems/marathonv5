package net.sourceforge.marathon.javafxagent;

import java.util.Arrays;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;

@Test public class EventQueueDeviceKBTest extends EventQueueDeviceTest {

    public void sendKeys() {
        driver.sendKeys(textField, "Hello ", "World");
        final StringBuffer text = new StringBuffer();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                try {
                    text.append((String)EventQueueWait.call(textField, "getText"));
                } catch (Exception e) {
                }
            }
        });
        final String expected = "Hello World";
        new WaitWithoutException() {
            @Override public boolean until() {
                return expected.equals(text.toString());
            }
        }.wait("Text is empty", 3000, 500);
        AssertJUnit.assertEquals(expected, text.toString());
    }

    public void testSendKeys_clearsModifiersWhenReceivesNull() {
        kss.clear();
        driver.sendKeys(textField, JavaAgentKeys.CONTROL);
        driver.sendKeys(textField, JavaAgentKeys.F2);
        final String expected = "[Ctrl pressed CONTROL, Ctrl pressed F2, Ctrl released F2]";
        new WaitWithoutException() {
            @Override public boolean until() {
                return expected.equals(kss.toString());
            }
        }.wait("List is empty", 3000, 500);
        AssertJUnit.assertEquals(expected, kss.toString());
        driver.sendKeys(textField, JavaAgentKeys.NULL);
        new WaitWithoutException() {
            @Override public boolean until() {
                return kss.size() > 3;
            }
        }.wait("", 3000, 500);
        kss.clear();
        driver.sendKeys(textField, JavaAgentKeys.F2);
        final String expected2 = "[pressed F2, released F2]";
        new WaitWithoutException() {
            @Override public boolean until() {
                return expected2.equals(kss.toString());
            }
        }.wait("List is empty", 3000, 500);
        AssertJUnit.assertEquals(expected2, kss.toString());
    }

    public void testSendKeys_selectAll() {
        driver.sendKeys(textField, "Hello World");
        driver.sendKeys(textField, getOSKey(), "a");
        final StringBuffer actual = new StringBuffer();
        Platform.runLater(new Runnable() {
            @Override public void run() {
                try {
                    actual.append((String)EventQueueWait.call(textField, "getSelectedText"));
                } catch (Exception e) {
                }
            }
        });
        final String expected = "Hello World";
        new WaitWithoutException() {
            @Override public boolean until() {
                return expected.equals(actual.toString());
            }
        }.wait("Text is empty", 3000, 500);
        ;
        AssertJUnit.assertEquals(expected, actual.toString());
    }

    public void testPressAndReleaseKeys_withAlt() {
        kss.clear();
        driver.pressKey(textField, JavaAgentKeys.ALT);
        driver.pressKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.ALT);
        List<String> list = Arrays.asList("Alt pressed " + KeyCode.getKeyCode("Alt"), "Alt pressed " + KeyCode.getKeyCode("F1"),
                "Alt released " + KeyCode.getKeyCode("F1"), "released " + KeyCode.getKeyCode("Alt"));
        final String expected = list.toString();
        new WaitWithoutException() {
            @Override public boolean until() {
                return expected.equals(kss.toString());
            }
        }.wait("List is empty", 3000, 500);
        AssertJUnit.assertEquals(expected, kss.toString());
    }

    public void testPressAndReleaseKeys_withShift() {
        kss.clear();
        driver.pressKey(textField, JavaAgentKeys.SHIFT);
        driver.pressKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.F1);
        driver.releaseKey(textField, JavaAgentKeys.SHIFT);
        List<String> list = Arrays.asList("Shift pressed " + KeyCode.getKeyCode("Shift"),
                "Shift pressed " + KeyCode.getKeyCode("F1"), "Shift released " + KeyCode.getKeyCode("F1"),
                "released " + KeyCode.getKeyCode("Shift"));
        final String expected = list.toString();
        new WaitWithoutException() {
            @Override public boolean until() {
                return expected.equals(kss.toString());
            }
        }.wait("List is empty", 3000, 500);
        AssertJUnit.assertEquals(expected, kss.toString());
    }

    private JavaAgentKeys getOSKey() {
        if (net.sourceforge.marathon.javafxagent.Platform.getCurrent().is(net.sourceforge.marathon.javafxagent.Platform.MAC))
            return JavaAgentKeys.META;
        else
            return JavaAgentKeys.CONTROL;
    }

}
