package net.sourceforge.marathon.javafxagent;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import javafx.scene.Node;
import javafx.stage.Stage;
import net.sourceforge.marathon.javafxagent.Device.Type;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;
import net.sourceforge.marathon.javafxagent.css.FindByCssSelector;

public class JavaFXAgent implements IJavaFXAgent {

    private static final String VERSION = "1.0";

    private IDevice devices;
    private JavaFXTargetLocator targetLocator;
    private JOptions options ;
    private long implicitWait;

    public JavaFXAgent() {
        this(Device.Type.EVENT_QUEUE);
        options = new JOptions(this);
    }

    public JavaFXAgent(Type type) {
        devices = Device.getDevice(type);
        targetLocator = new JavaFXTargetLocator(this);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getDevices()
     */
    @Override public IDevice getDevices() {
        return devices;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getTitle()
     */
    @Override public String getTitle() {
        return targetLocator.getTitle();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getWindowHandles()
     */
    @Override public Collection<String> getWindowHandles() {
        return targetLocator.getWindowHandles();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getWindowHandle()
     */
    @Override public String getWindowHandle() {
        return targetLocator.getWindowHandle();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#switchTo()
     */
    @Override public JavaFXTargetLocator switchTo() {
        return targetLocator;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#manage()
     */
    @Override public JOptions manage() {
        if(options == null)
            options = new JOptions(this);
        return options;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getVersion()
     */
    @Override public String getVersion() {
        return VERSION;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getName()
     */
    @Override public String getName() {
        return "javadriver";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#deleteWindow()
     */
    @Override public void deleteWindow() {
        targetLocator.deleteWindow();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElement(java.lang.String)
     */
    @Override public IJavaFXElement findElement(String id) {
        return targetLocator.findElement(id);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getActiveElement()
     */
    @Override public IJavaFXElement getActiveElement() {
        return targetLocator.getActiveElement();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#quit()
     */
    @Override public void quit() {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override public void run() {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override public Object run() {
                        Runtime.getRuntime().halt(1);
                        return null;
                    }
                });
            }
        }, 10);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getWindow(java.lang.String)
     */
    @Override public JFXWindow getWindow(String windowHandle) {
        return targetLocator.getWindowForHandle(windowHandle);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getCurrentWindow()
     */
    @Override public JFXWindow getCurrentWindow() {
        return targetLocator.getCurrentWindow();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementByTagName(java.lang.String)
     */
    @Override public IJavaFXElement findElementByTagName(String using) {
        List<IJavaFXElement> elements = findElementsByTagName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using name: " + using, null);
        return elements.get(0);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByTagName(java.lang.String)
     */
    @Override public List<IJavaFXElement> findElementsByTagName(final String using) {
        return findByCss(using);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementByName(java.lang.String)
     */
    @Override public IJavaFXElement findElementByName(String using) {
        List<IJavaFXElement> elements = findElementsByName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using name: " + using, null);
        return elements.get(0);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByName(java.lang.String)
     */
    @Override public List<IJavaFXElement> findElementsByName(final String using) {
        return findByCss("#'" + using + "'");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementByCssSelector(java.lang.String)
     */
    @Override public IJavaFXElement findElementByCssSelector(String using) {
        List<IJavaFXElement> elements = findElementsByCssSelector(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        return elements.get(0);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByCssSelector(java.lang.String)
     */
    @Override public List<IJavaFXElement> findElementsByCssSelector(String using) {
        Stage window = targetLocator.getTopContainer().getWindow();
        IJavaFXElement je = JavaFXElementFactory.createElement(window.getScene().getRoot(), this, targetLocator.getTopContainer());
        FindByCssSelector finder = new FindByCssSelector(je, this, implicitWait);
        return finder.findElements(using);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementByClassName(java.lang.String)
     */
    @Override public IJavaFXElement findElementByClassName(String using) {
        List<IJavaFXElement> elements = findElementsByClassName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        return elements.get(0);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByClassName(java.lang.String)
     */
    @Override public List<IJavaFXElement> findElementsByClassName(String using) {
        return findByCss(":instance-of('" + using + "')");
    }

    protected List<IJavaFXElement> findByCss(String css) {
        Stage window = targetLocator.getTopContainer().getWindow();
        IJavaFXElement je = JavaFXElementFactory.createElement(window.getScene().getRoot(), this, targetLocator.getTopContainer());
        FindByCssSelector finder = new FindByCssSelector(je, this, implicitWait);
        return finder.findElements(css);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getWindowProperties()
     */
    @Override public JSONObject getWindowProperties() {
        return targetLocator.getWindowProperties();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#setImplicitWait(long)
     */
    @Override public void setImplicitWait(long implicitWait) {
        this.implicitWait = implicitWait;
    }

    @Override public IJavaFXElement findElement(Node component) {
        return targetLocator.getTopContainer().findElement(component);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getScreenShot()
     */
    @Override public byte[] getScreenShot() throws IOException {
        return new byte[0];
    }

    /* (non-Javadoc)
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getImplicitWait()
     */
    @Override public long getImplicitWait() {
        return implicitWait;
    }
}
