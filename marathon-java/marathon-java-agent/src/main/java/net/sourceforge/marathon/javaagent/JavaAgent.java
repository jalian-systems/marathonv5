package net.sourceforge.marathon.javaagent;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import net.sourceforge.marathon.javaagent.Device.Type;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.css.FindByCssSelector;

import org.json.JSONObject;

public class JavaAgent {

    public class JTimeouts {

        public JTimeouts implicitlyWait(long time, TimeUnit unit) {
            setImplicitWait(TimeUnit.MILLISECONDS.convert(Math.max(0, time), unit));
            return this;
        }

    }

    public class JOptions {

        private JTimeouts timeouts = new JTimeouts();

        public JTimeouts timeouts() {
            return timeouts;
        }

    }

    private static final String VERSION = "1.0";

    private IDevice devices;
    private JavaTargetLocator targetLocator;
    private JOptions options = new JOptions();
    public long implicitWait;

    public JavaAgent() {
        this(Device.Type.EVENT_QUEUE);
    }

    public JavaAgent(Type type) {
        devices = Device.getDevice(type);
        targetLocator = new JavaTargetLocator(this);
    }

    public IDevice getDevices() {
        return devices;
    }

    public String getTitle() {
        return targetLocator.getTitle();
    }

    public Collection<String> getWindowHandles() {
        return targetLocator.getWindowHandles();
    }

    public String getWindowHandle() {
        return targetLocator.getWindowHandle();
    }

    public JavaTargetLocator switchTo() {
        return targetLocator;
    }

    public JOptions manage() {
        return options;
    }

    public String getVersion() {
        return VERSION;
    }

    public String getName() {
        return "javadriver";
    }

    public void deleteWindow() {
        targetLocator.deleteWindow();
    }

    public IJavaElement findElement(String id) {
        return targetLocator.findElement(id);
    }

    public IJavaElement getActiveElement() {
        return targetLocator.getActiveElement();
    }

    public void quit() {
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

    public JWindow getWindow(String windowHandle) {
        return targetLocator.getWindowForHandle(windowHandle);
    }

    public JWindow getCurrentWindow() {
        return targetLocator.getCurrentWindow();
    }

    public IJavaElement findElementByTagName(String using) {
        List<IJavaElement> elements = findElementsByTagName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using name: " + using, null);
        return elements.get(0);
    }

    public List<IJavaElement> findElementsByTagName(final String using) {
        return findByCss(using);
    }

    public IJavaElement findElementByName(String using) {
        List<IJavaElement> elements = findElementsByName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using name: " + using, null);
        return elements.get(0);
    }

    public List<IJavaElement> findElementsByName(final String using) {
        return findByCss("#'" + using + "'");
    }

    public IJavaElement findElementByCssSelector(String using) {
        List<IJavaElement> elements = findElementsByCssSelector(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        return elements.get(0);
    }

    public List<IJavaElement> findElementsByCssSelector(String using) {
        Window window = targetLocator.getTopContainer().getWindow();
        IJavaElement je = JavaElementFactory.createElement(window, this, targetLocator.getTopContainer());
        FindByCssSelector finder = new FindByCssSelector(je, this, implicitWait);
        return finder.findElements(using);
    }

    public IJavaElement findElementByClassName(String using) {
        List<IJavaElement> elements = findElementsByClassName(using);
        if (elements.size() == 0)
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        return elements.get(0);
    }

    public List<IJavaElement> findElementsByClassName(String using) {
        return findByCss(":instance-of('" + using + "')");
    }

    protected List<IJavaElement> findByCss(String css) {
        Window window = targetLocator.getTopContainer().getWindow();
        IJavaElement je = JavaElementFactory.createElement(window, this, targetLocator.getTopContainer());
        FindByCssSelector finder = new FindByCssSelector(je, this, implicitWait);
        return finder.findElements(css);
    }

    public JSONObject getWindowProperties() {
        return targetLocator.getWindowProperties();
    }

    public void setImplicitWait(long implicitWait) {
        this.implicitWait = implicitWait;
    }

    public IJavaElement findElement(Component component) {
        return targetLocator.getTopContainer().findElement(component);
    }

    public byte[] getScreenShot() throws IOException {
        BufferedImage bufferedImage;
        Window window = targetLocator.getTopContainer().getWindow();
        try {
            Dimension windowSize = window.getSize();
            Robot robot = new Robot();
            bufferedImage = robot.createScreenCapture(new Rectangle(window.getX(), window.getY(), windowSize.width,
                    windowSize.height));
        } catch (AWTException e) {
            Rectangle rec = window.getBounds();
            bufferedImage = new BufferedImage(rec.width, rec.height, BufferedImage.TYPE_INT_ARGB);
            window.paint(bufferedImage.getGraphics());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }
}
