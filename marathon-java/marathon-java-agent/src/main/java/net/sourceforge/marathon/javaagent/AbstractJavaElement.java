package net.sourceforge.marathon.javaagent;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.TextComponent;
import java.awt.Window;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import net.sourceforge.marathon.javaagent.IDevice.Buttons;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.css.FindByCssSelector;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractJavaElement extends JavaElementPropertyAccessor implements IJavaElement {

    protected JavaAgent driver;
    protected JWindow window;
    private UUID id;

    public AbstractJavaElement(Component component, JavaAgent driver, JWindow window) {
        super(component);
        this.driver = driver;
        this.window = window;
    }

    public AbstractJavaElement(AbstractJavaElement parent) {
        this(parent.component, parent.driver, parent.window);
        this.id = parent.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#click()
     */
    @Override public void click() {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(component);
        Point p = getMidpoint();
        click(0, 1, p.x, p.y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#sendKeys(java.lang.
     * CharSequence)
     */
    @Override public void sendKeys(CharSequence... keysToSend) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(component);
        IDevice kb = driver.getDevices();
        kb.sendKeys(component, keysToSend);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#clear()
     */
    @Override public void clear() {
        EventQueueWait.call_noexc(this, "_clear");
    }

    public void _clear() {
        verifyCanInteractWithElement();
        final Component active = Device.getActiveComponent(component);
        if (active instanceof JTextComponent) {
            ((JTextComponent) active).setText("");
        } else if (active instanceof TextComponent) {
            ((TextComponent) active).setText("");
        } else
            throw new UnsupportedCommandException("Clear not supported on " + active.getClass().getName(), null);
    }

    @Override public String getAttribute(final String name) {
        if (name.startsWith("matches-css-"))
            return matchesCSS(name.substring("matches-css-".length()));
        else if (name.startsWith("call-"))
            return callMethod(new JSONObject(name.substring("call-".length())));
        return super.getAttribute(name);
    }

    private String matchesCSS(String selector) {
        long implicitWait = getDriver().implicitWait;
        try {
            getDriver().setImplicitWait(0);
            return Boolean.toString(findElementsByCssSelector(selector).size() == 1);
        } finally {
            driver.setImplicitWait(implicitWait);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#getCssValue(java.lang
     * .String)
     */
    @Override public String getCssValue(String propertyName) {
        throw new UnsupportedCommandException("Java driver does not support getCssValue()", null);
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
                            + " And is the current window focussed the same as the one holding this element?", null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#findElementByName(java
     * .lang.String)
     */
    @Override public final IJavaElement findElementByName(String using) {
        List<IJavaElement> elements = findElementsByName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using name: " + using, null);
        return elements.get(0);
    }

    @Override public IJavaElement findElementByClassName(String using) {
        List<IJavaElement> elements = findElementsByClassName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using name: " + using, null);
        return elements.get(0);
    }

    @Override public List<IJavaElement> findElementsByClassName(String using) {
        return findByCss(":instance-of('" + using + "')");
    }

    protected List<IJavaElement> findByCss(String css) {
        if (!(component instanceof Container))
            throw new UnsupportedCommandException("findElements unsupported for non container objects", null);
        FindByCssSelector finder = new FindByCssSelector(this, driver, driver.implicitWait);
        return finder.findElements(css);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#findElementsByName(java
     * .lang.String)
     */
    @Override public List<IJavaElement> findElementsByName(final String using) {
        return findByCss("#'" + using + "'");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getHandle()
     */
    @Override final public String getHandle() {
        if (this instanceof IPseudoElement) {
            try {
                return URLEncoder.encode(((IPseudoElement) this).createHandle(), "utf8");
            } catch (UnsupportedEncodingException e) {
                // Can't happen
            }
        }
        return id.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getId()
     */
    @Override public UUID getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#findElementByTagName(
     * java.lang.String)
     */
    @Override public final IJavaElement findElementByTagName(String using) {
        List<IJavaElement> elements = findElementsByTagName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using name: " + using, null);
        return elements.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#findElementsByTagName
     * (java.lang.String)
     */
    @Override public List<IJavaElement> findElementsByTagName(final String using) {
        return findByCss(using);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#findElementByCssSelector
     * (java.lang.String)
     */
    @Override public final IJavaElement findElementByCssSelector(String using) {
        List<IJavaElement> elements = findElementsByCssSelector(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        return elements.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#findElementsByCssSelector
     * (java.lang.String)
     */
    @Override public List<IJavaElement> findElementsByCssSelector(String using) {
        if (!(component instanceof Container))
            throw new UnsupportedCommandException("findElements unsupported for non container objects", null);
        FindByCssSelector finder = new FindByCssSelector(this, driver, driver.implicitWait);
        return finder.findElements(using);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#filterByPseudoClass(java
     * .lang.String, java.lang.Object)
     */
    @Override public boolean filterByPseudoClass(String function, Object... args) {
        if (function.equals("enabled"))
            return isEnabled();
        else if (function.equals("disabled"))
            return !isEnabled();
        else if (function.equals("displayed"))
            return isDisplayed();
        else if (function.equals("hidden"))
            return !isDisplayed();
        else if (function.equals("selected") && hasAttribue("selected"))
            return isSelected();
        else if (function.equals("unselected") && hasAttribue("selected"))
            return !isSelected();
        else if (function.equals("deselected") && hasAttribue("selected"))
            return !isSelected();
        else if (function.equals("instance-of"))
            return isInstance((String) args[0]);
        throw new UnsupportedCommandException("Unsupported psuedo class " + function + " component = "
                + component.getClass().getName(), null);
    }

    private boolean isInstance(String classname) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Component> c = (Class<? extends Component>) Class.forName(classname);
            return c.isInstance(component);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override public String toString() {
        String ids = id.toString();
        return "@    " + component + "\n     id=" + ids + "\n";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#getComponents()
     */
    @Override public IJavaElement[] getComponents() {
        if (component instanceof Container) {
            Window[] ownedWindows = new Window[0];
            if (component instanceof Window) {
                ownedWindows = ((Window) component).getOwnedWindows();
            }
            Component[] components = ((Container) component).getComponents();
            IJavaElement[] r = new IJavaElement[components.length + ownedWindows.length];
            for (int i = 0; i < components.length; i++) {
                r[i] = JavaElementFactory.createElement(components[i], driver, window);
            }
            for (int i = components.length; i < components.length + ownedWindows.length; i++) {
                r[i] = JavaElementFactory.createElement(ownedWindows[i - components.length], driver, window);
            }
            return r;
        }
        return new IJavaElement[0];
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
        AbstractJavaElement other = (AbstractJavaElement) obj;
        if (id == null) {
            if (other.getId() != null)
                return false;
        } else if (!getHandle().equals(other.getHandle()))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#getByPseudoElement(java
     * .lang.String, java.lang.Object[])
     */
    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("call-select")) {
            if (marathon_select((String) params[0]))
                return Arrays.asList((IJavaElement) this);
            return Arrays.<IJavaElement> asList();
        }
        if (selector.equals("call-select-by-properties")) {
            if (marathon_select(new JSONArray((String) params[0])))
                return Arrays.asList((IJavaElement) this);
            return Arrays.<IJavaElement> asList();
        }
        throw new UnsupportedCommandException("Pseudo element selector " + selector + " is not applicable for "
                + component.getClass().getName(), null);
    }

    public boolean marathon_select(JSONArray jsonArray) {
        throw new UnsupportedCommandException("Select method by properties" + " is not applicable for "
                + component.getClass().getName() + " (" + this.getClass().getName() + ")", null);
    }

    public boolean marathon_select(String value) {
        throw new UnsupportedCommandException("Select method" + " is not applicable for " + component.getClass().getName() + " ("
                + this.getClass().getName() + ")", null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.marathon.javaagent.IJavaElement#createId()
     */
    @Override public String createId() {
        this.id = UUID.randomUUID();
        return this.id.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.marathon.javaagent.IJavaElement#setId(java.util.UUID)
     */
    @Override public void setId(UUID id) {
        this.id = id;
    }

    @Override public void moveto() {
        EventQueueWait.call_noexc(this, "_moveto");
    }

    public void _moveto() {
        driver.getDevices().moveto(component);
    }

    @Override public void moveto(int xoffset, int yoffset) {
        EventQueueWait.call_noexc(this, "_moveto", xoffset, yoffset);
    }

    public void _moveto(int xoffset, int yoffset) {
        driver.getDevices().moveto(component, xoffset, yoffset);
    }

    public JavaAgent getDriver() {
        return driver;
    }

    public JWindow getWindow() {
        return window;
    }

    @Override public void click(int button, int clickCount, int xoffset, int yoffset) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(component);
        IDevice mouse = driver.getDevices();
        mouse.click(component, Buttons.getButtonFor(button), clickCount, xoffset, yoffset);
    }

    @Override public void buttonDown(int button, int xoffset, int yoffset) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(component);
        IDevice mouse = driver.getDevices();
        mouse.buttonDown(component, Buttons.getButtonFor(button), xoffset, yoffset);
    }

    @Override public void buttonUp(int button, int xoffset, int yoffset) {
        verifyCanInteractWithElement();

        EventQueueWait.requestFocus(component);
        IDevice mouse = driver.getDevices();
        mouse.buttonUp(component, Buttons.getButtonFor(button), xoffset, yoffset);
    }

    @Override public void submit() {
        if (component instanceof JComponent) {
            JComponent tc = (JComponent) component;
            Object clientProperty = (Object) tc.getClientProperty("marathon.celleditor.parent");
            if (clientProperty != null) {
                EventQueueWait.call_noexc(clientProperty, "stopEditing");
            }
        }
    }
}
