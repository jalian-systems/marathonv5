package net.sourceforge.marathon.javafxagent;

import java.util.ArrayList;
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
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class JavaFXElement extends JavaElementPropertyAccessor implements IJavaElement {

    private IJavaAgent driver;
    private JWindow window;
    private UUID id;

    public JavaFXElement(Node component, IJavaAgent driver, JWindow window) {
        super(component);
        this.driver = driver;
        this.window = window;
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
        } else
            throw new UnsupportedCommandException("Clear not supported on " + node.getClass().getName(), null);
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
        if (propertyName.equals("all"))
            return cssMetaData.toString();
        for (CssMetaData<? extends Styleable, ?> cssMetaData2 : cssMetaData) {
            if (cssMetaData2.getProperty().equals(propertyName)) {
                Object initialValue = cssMetaData2.getInitialValue(null);
                if (initialValue != null)
                    return initialValue.toString();
                return null;
            }
        }
        return null;
    }

    @Override public IJavaElement findElementByName(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public List<IJavaElement> findElementsByName(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public String getHandle() {
        return id.toString();
    }

    @Override public UUID getId() {
        return id;
    }

    @Override public IJavaElement findElementByTagName(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public List<IJavaElement> findElementsByTagName(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public IJavaElement findElementByCssSelector(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public List<IJavaElement> findElementsByCssSelector(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public boolean filterByPseudoClass(String function, Object... args) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public IJavaElement[] getComponents() {
        List<IJavaElement> elements = new ArrayList<>();
        if (node instanceof Parent) {
            ObservableList<Node> childrenUnmodifiable = ((Parent) node).getChildrenUnmodifiable();
            for (Node child : childrenUnmodifiable) {
                elements.add(JavaElementFactory.createElement(child, driver, window));
            }
        }
        return elements.toArray(new IJavaElement[elements.size()]);
    }

    @Override public List<IJavaElement> getByPseudoElement(String function, Object[] params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public String createId() {
        this.id = UUID.randomUUID();
        return this.id.toString();
    }

    @Override public void setId(UUID id) {
        this.id = id;
    }

    @Override public void moveto() {
        // TODO Auto-generated method stub

    }

    @Override public void moveto(double xoffset, double yoffset) {
        // TODO Auto-generated method stub

    }

    @Override public void click(int button, int clickCount, double x, double y) {
        // TODO Auto-generated method stub

    }

    @Override public void buttonDown(int button, double xoffset, double yoffset) {
        // TODO Auto-generated method stub

    }

    @Override public void buttonUp(int button, double xoffset, double yoffset) {
        // TODO Auto-generated method stub

    }

    @Override public IJavaElement findElementByClassName(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public List<IJavaElement> findElementsByClassName(String using) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public boolean marathon_select(String value) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public boolean marathon_select(JSONArray jsonArray) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override public void submit() {
        // TODO Auto-generated method stub

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
        if (handle == null || !handle.equals(window.getHandle()))
            throw new StaleElementReferenceException(
                    "Element appears to be stale. Did you navigate away from the window that contained it? "
                            + " And is the current window focussed the same as the one holding this element?",
                    null);
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JavaFXElement other = (JavaFXElement) obj;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!getHandle().equals(other.getHandle()))
            return false;
        return true;
    }

}
