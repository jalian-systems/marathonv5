package net.sourceforge.marathon.javafxagent;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;

public interface IJavaElement {

    public abstract void click();

    public abstract void sendKeys(CharSequence... keysToSend);

    public abstract void clear();

    public abstract String getAttribute(String name);

    public abstract boolean isSelected();

    public abstract boolean isEnabled();

    public abstract String getText();

    public abstract boolean isDisplayed();

    public abstract Point getLocation();

    public abstract Dimension getSize();

    public abstract String getCssValue(String propertyName);

    public abstract IJavaElement findElementByName(String using);

    public abstract List<IJavaElement> findElementsByName(String using);

    public abstract String getHandle();

    public abstract Component getComponent();

    public abstract String getTagName();

    public abstract UUID getId();

    public abstract IJavaElement findElementByTagName(String using);

    public abstract List<IJavaElement> findElementsByTagName(String using);

    public abstract IJavaElement findElementByCssSelector(String using);

    public abstract List<IJavaElement> findElementsByCssSelector(String using);

    public abstract boolean hasAttribue(String name);

    public abstract boolean filterByPseudoClass(String function, Object... args);

    public abstract IJavaElement[] getComponents();

    public abstract List<IJavaElement> getByPseudoElement(String function, Object[] params);

    public abstract String createId();

    public abstract void setId(UUID id);

    public abstract void moveto();

    public abstract void moveto(int xoffset, int yoffset);

    public abstract Point getMidpoint();

    public abstract void click(int button, int clickCount, int xoffset, int yoffset);

    public abstract void buttonDown(int button, int xoffset, int yoffset);

    public abstract void buttonUp(int button, int xoffset, int yoffset);

    public abstract IJavaElement findElementByClassName(String using);

    public abstract List<IJavaElement> findElementsByClassName(String using);

    public abstract boolean marathon_select(String value);

    public abstract boolean marathon_select(JSONArray jsonArray);

    public abstract void submit();

}