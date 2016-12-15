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
package net.sourceforge.marathon.javafxagent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;

import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextInputControl;
import net.sourceforge.marathon.javafxagent.IDevice.Buttons;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.javafxagent.css.FindByCssSelector;

public class JavaFXElement extends JavaFXElementPropertyAccessor implements IJavaFXElement {

    protected IJavaFXAgent driver;
    protected JFXWindow window;
    protected UUID id;

    public JavaFXElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component);
        this.driver = driver;
        this.window = window;
    }

    public JavaFXElement(JavaFXElement parent) {
        super(parent.getComponent());
        this.driver = parent.driver;
        this.window = parent.window;
        this.id = parent.getElementId();
    }

    @Override public void click() {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(node);
        Point2D p = getMidpoint();
        click(0, 1, (int) p.getX(), (int) p.getY());
    }

    @Override public void sendKeys(CharSequence... keysToSend) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(node);
        IDevice kb = driver.getDevices();
        kb.sendKeys(node, keysToSend);
    }

    @Override public void clear() {
        EventQueueWait.call_noexc(this, "_clear");
    }

    public void _clear() {
        verifyCanInteractWithElement();
        if (node instanceof TextInputControl) {
            ((TextInputControl) node).setText("");
        } else {
            throw new UnsupportedCommandException("Clear not supported on " + node.getClass().getName(), null);
        }
    }

    @Override public Point2D getLocation() {
        Bounds bounds = node.getBoundsInParent();
        return new Point2D(bounds.getMinX(), bounds.getMinY());
    }

    @Override public Dimension2D getSize() {
        Bounds bounds = node.getBoundsInParent();
        return new Dimension2D(bounds.getWidth(), bounds.getHeight());
    }

    @Override public String getCssValue(String propertyName) {
        List<CssMetaData<? extends Styleable, ?>> cssMetaData = node.getCssMetaData();
        if (propertyName.equals("all")) {
            return cssMetaData.toString();
        }
        for (CssMetaData<? extends Styleable, ?> cssMetaData2 : cssMetaData) {
            if (cssMetaData2.getProperty().equals(propertyName)) {
                Object initialValue = cssMetaData2.getInitialValue(null);
                if (initialValue != null) {
                    return initialValue.toString();
                }
                return null;
            }
        }
        return null;
    }

    @Override public String getHandle() {
        if (this instanceof IPseudoElement) {
            try {
                return URLEncoder.encode(((IPseudoElement) this).createHandle(), "utf8");
            } catch (UnsupportedEncodingException e) {
                // Can't happen
            }
        }
        return id.toString();
    }

    @Override public UUID getElementId() {
        return id;
    }

    @Override public boolean filterByPseudoClass(String function, Object... args) {
        if (function.equals("enabled")) {
            return isEnabled();
        } else if (function.equals("disabled")) {
            return !isEnabled();
        } else if (function.equals("displayed")) {
            return isDisplayed();
        } else if (function.equals("hidden")) {
            return !isDisplayed();
        } else if (function.equals("selected") && hasAttribue("selected")) {
            return isSelected();
        } else if (function.equals("unselected") && hasAttribue("selected")) {
            return !isSelected();
        } else if (function.equals("deselected") && hasAttribue("selected")) {
            return !isSelected();
        } else if (function.equals("instance-of")) {
            return isInstance((String) args[0]);
        }
        throw new UnsupportedCommandException("Unsupported psuedo class " + function + " node = " + node.getClass().getName(),
                null);
    }

    private boolean isInstance(String classname) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Node> c = (Class<? extends Node>) Class.forName(classname);
            return c.isInstance(node);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override public IJavaFXElement[] getComponents() {
        List<IJavaFXElement> elements = new ArrayList<>();
        if (node instanceof Parent) {
            ObservableList<Node> childrenUnmodifiable = ((Parent) node).getChildrenUnmodifiable();
            for (Node child : childrenUnmodifiable) {
                elements.add(JavaFXElementFactory.createElement(child, driver, window));
            }
        }
        return elements.toArray(new IJavaFXElement[elements.size()]);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("call-select")) {
            if (marathon_select((String) params[0])) {
                return Arrays.asList((IJavaFXElement) this);
            }
            return Arrays.<IJavaFXElement> asList();
        }
        if (selector.equals("call-select-by-properties")) {
            if (marathon_select(new JSONArray((String) params[0]))) {
                return Arrays.asList((IJavaFXElement) this);
            }
            return Arrays.<IJavaFXElement> asList();
        }
        throw new UnsupportedCommandException(
                "Pseudo element selector " + selector + " is not applicable for " + node.getClass().getName(), null);
    }

    @Override public String createId() {
        this.id = UUID.randomUUID();
        return this.id.toString();
    }

    @Override public void setId(UUID id) {
        this.id = id;
    }

    @Override public void moveto() {
        EventQueueWait.call_noexc(this, "_moveto");
    }

    public void _moveto() {
        driver.getDevices().moveto(node);
    }

    @Override public void moveto(double xoffset, double yoffset) {
        EventQueueWait.call_noexc(this, "_moveto", xoffset, yoffset);
    }

    public void _moveto(double xoffset, double yoffset) {
        driver.getDevices().moveto(node, xoffset, yoffset);
    }

    @Override public void click(int button, int clickCount, double xoffset, double yoffset) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(node);
        IDevice mouse = driver.getDevices();
        mouse.click(node, Buttons.getButtonFor(button), clickCount, xoffset, yoffset);
    }

    @Override public void buttonDown(int button, double xoffset, double yoffset) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(node);
        IDevice mouse = driver.getDevices();
        mouse.buttonDown(node, Buttons.getButtonFor(button), xoffset, yoffset);
    }

    @Override public void buttonUp(int button, double xoffset, double yoffset) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(node);
        IDevice mouse = driver.getDevices();
        mouse.buttonUp(node, Buttons.getButtonFor(button), xoffset, yoffset);
    }

    @Override public boolean marathon_select(JSONArray jsonArray) {
        throw new UnsupportedCommandException("Select method by properties" + " is not applicable for " + node.getClass().getName()
                + " (" + this.getClass().getName() + ")", null);
    }

    @Override public boolean marathon_select(String value) {
        throw new UnsupportedCommandException(
                "Select method is not applicable for " + node.getClass().getName() + " (" + this.getClass().getName() + ")", null);
    }

    @Override public void submit() {
    }

    private void verifyCanInteractWithElement() {
        try {
            if (!isEnabled()) {
                throw new InvalidElementStateException("You may only interact with enabled elements", null);
            }
        } catch (UnsupportedCommandException e) {
        }
        verifyElementNotStale();
    }

    private void verifyElementNotStale() {
        String handle = driver.getWindowHandle();
        if (handle == null || !handle.equals(window.getHandle())) {
            throw new StaleElementReferenceException(
                    "Element appears to be stale. Did you navigate away from the window that contained it? "
                            + " And is the current window focussed the same as the one holding this element?",
                    null);
        }
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (id == null ? 0 : id.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        JavaFXElement other = (JavaFXElement) obj;
        if (id == null) {
            if (other.getElementId() != null) {
                return false;
            }
        } else if (!getHandle().equals(other.getHandle())) {
            return false;
        }
        return true;
    }

    @Override public IJavaFXElement findElementByName(String using) {
        List<IJavaFXElement> elements = findElementsByName(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No node found using name: " + using, null);
        }
        return elements.get(0);
    }

    @Override public IJavaFXElement findElementByClassName(String using) {
        List<IJavaFXElement> elements = findElementsByClassName(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No component found using name: " + using, null);
        }
        return elements.get(0);
    }

    @Override public List<IJavaFXElement> findElementsByClassName(String using) {
        return findElementsByCssSelector(":instance-of('" + using + "')");
    }

    @Override public List<IJavaFXElement> findElementsByName(String using) {
        return findElementsByCssSelector("#'" + using + "'");
    }

    @Override public IJavaFXElement findElementByTagName(String using) {
        List<IJavaFXElement> elements = findElementsByTagName(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No component found using name: " + using, null);
        }
        return elements.get(0);
    }

    @Override public List<IJavaFXElement> findElementsByTagName(String using) {
        return findElementsByCssSelector(using);
    }

    @Override public IJavaFXElement findElementByCssSelector(String using) {
        List<IJavaFXElement> elements = findElementsByCssSelector(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        }
        return elements.get(0);
    }

    @Override public List<IJavaFXElement> findElementsByCssSelector(String using) {
        FindByCssSelector finder = new FindByCssSelector(this, driver, driver.getImplicitWait());
        return finder.findElements(using);
    }

}
