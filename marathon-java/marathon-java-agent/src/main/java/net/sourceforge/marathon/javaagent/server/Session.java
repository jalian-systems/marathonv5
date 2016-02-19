package net.sourceforge.marathon.javaagent.server;

import java.awt.AWTException;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.sourceforge.marathon.javaagent.Device;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

import org.json.JSONArray;
import org.json.JSONObject;

public class Session {
    private String id;
    private JavaAgent agent;

    private List<LogEntry> logEntries = new ArrayList<LogEntry>();
    private Level logLevel = Level.ALL;

    public Session(Device.Type type) {
        this.id = UUID.randomUUID().toString();
        agent = new JavaAgent(type);
    }

    public void log(Level level, String message) {
        if (level.intValue() >= logLevel.intValue())
            logEntries.add(new LogEntry(level, message));
    }

    public String getID() {
        return id;
    }

    public Collection<String> getWindowHandles() {
        return agent.getWindowHandles();
    }

    public void deleteWindow() {
        agent.deleteWindow();
    }

    public void window(String name) {
        agent.switchTo().window(name);
    }

    public void setTimeout(long millis) {
        agent.manage().timeouts().implicitlyWait(millis, TimeUnit.MILLISECONDS);
    }

    public String getWindowHandle() {
        return agent.getWindowHandle();
    }

    public String getTitle() {
        return agent.getTitle();
    }

    public IJavaElement findElement(String using, String value) {
        if ("name".equals(using)) {
            return agent.findElementByName(value);
        } else if ("tag name".equals(using)) {
            return agent.findElementByTagName(value);
        } else if ("css selector".equals(using)) {
            return agent.findElementByCssSelector(value);
        } else if ("class name".equals(using)) {
            return agent.findElementByClassName(value);
        } else if ("id".equals(using)) {
            return agent.findElementByName(value);
        }
        throw new UnsupportedCommandException("Unsupported look up strategy " + using, null);
    }

    public IJavaElement findElement(String id) {
        return agent.findElement(id);
    }

    public List<IJavaElement> findElements(String using, String value) {
        if ("name".equals(using)) {
            return agent.findElementsByName(value);
        } else if ("tag name".equals(using)) {
            return agent.findElementsByTagName(value);
        } else if ("css selector".equals(using)) {
            return agent.findElementsByCssSelector(value);
        } else if ("class name".equals(using)) {
            return agent.findElementsByClassName(value);
        } else if ("id".equals(using)) {
            return agent.findElementsByName(value);
        }
        throw new UnsupportedCommandException("Unsupported look up strategy " + using, null);
    }

    public IJavaElement getActiveElement() {
        return agent.getActiveElement();
    }

    public IJavaElement findElement(IJavaElement parent, String using, String value) {
        if ("name".equals(using)) {
            return parent.findElementByName(value);
        } else if ("tag name".equals(using)) {
            return parent.findElementByTagName(value);
        } else if ("css selector".equals(using)) {
            return parent.findElementByCssSelector(value);
        } else if ("class name".equals(using)) {
            return parent.findElementByClassName(value);
        } else if ("id".equals(using)) {
            return parent.findElementByName(value);
        }
        throw new UnsupportedCommandException("Unsupported look up strategy " + using, null);
    }

    public List<IJavaElement> findElements(IJavaElement parent, String using, String value) {
        if ("name".equals(using)) {
            return parent.findElementsByName(value);
        } else if ("tag name".equals(using)) {
            return parent.findElementsByTagName(value);
        } else if ("css selector".equals(using)) {
            return parent.findElementsByCssSelector(value);
        } else if ("class name".equals(using)) {
            return parent.findElementsByClassName(value);
        } else if ("id".equals(using)) {
            return parent.findElementsByName(value);
        }
        throw new UnsupportedCommandException("Unsupported look up strategy " + using, null);
    }

    public void fillLog(JSONArray dest) {
        for (LogEntry logEntry : logEntries) {
            dest.put(new JSONObject().put("level", logEntry.getLevel()).put("timestamp", logEntry.getTimestamp())
                    .put("message", logEntry.getMessage()));
        }
        logEntries.clear();
    }

    public void quit() {
        agent.quit();
    }

    public JWindow getWindow(String windowHandle) {
        if ("current".equals(windowHandle))
            return agent.getCurrentWindow();
        return agent.getWindow(windowHandle);
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public JSONObject getWindowProperties() {
        return agent.getWindowProperties();
    }

    public IJavaElement findElement(Component component) {
        return agent.findElement(component);
    }

    public byte[] getScreenShot() throws AWTException, IOException {
        return agent.getScreenShot();
    }

}
