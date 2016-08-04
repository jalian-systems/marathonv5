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

import javax.imageio.ImageIO;

import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.Device.Type;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.css.FindByCssSelector;

public class JavaAgent implements IJavaAgent {

    private static final String VERSION = "1.0";

    private IDevice devices;
    private JavaTargetLocator targetLocator;
    private JOptions options;
    private long implicitWait;

    public JavaAgent() {
        this(Device.Type.EVENT_QUEUE);
    }

    public JavaAgent(Type type) {
        devices = Device.getDevice(type);
        targetLocator = new JavaTargetLocator(this);
        options = new JOptions(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getDevices()
     */
    @Override public IDevice getDevices() {
        return devices;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getTitle()
     */
    @Override public String getTitle() {
        return targetLocator.getTitle();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getWindowHandles()
     */
    @Override public Collection<String> getWindowHandles() {
        return targetLocator.getWindowHandles();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getWindowHandle()
     */
    @Override public String getWindowHandle() {
        return targetLocator.getWindowHandle();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#switchTo()
     */
    @Override public JavaTargetLocator switchTo() {
        return targetLocator;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#manage()
     */
    @Override public JOptions manage() {
        return options;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getVersion()
     */
    @Override public String getVersion() {
        return VERSION;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getName()
     */
    @Override public String getName() {
        return "javadriver";
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#deleteWindow()
     */
    @Override public void deleteWindow() {
        targetLocator.deleteWindow();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElement(java.lang.
     * String)
     */
    @Override public IJavaElement findElement(String id) {
        return targetLocator.findElement(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getActiveElement()
     */
    @Override public IJavaElement getActiveElement() {
        return targetLocator.getActiveElement();
    }

    /*
     * (non-Javadoc)
     *
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

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#getWindow(java.lang.String)
     */
    @Override public JWindow getWindow(String windowHandle) {
        return targetLocator.getWindowForHandle(windowHandle);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getCurrentWindow()
     */
    @Override public JWindow getCurrentWindow() {
        return targetLocator.getCurrentWindow();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementByTagName(java.
     * lang.String)
     */
    @Override public IJavaElement findElementByTagName(String using) {
        List<IJavaElement> elements = findElementsByTagName(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No component found using name: " + using, null);
        }
        return elements.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByTagName(java.
     * lang.String)
     */
    @Override public List<IJavaElement> findElementsByTagName(final String using) {
        return findByCss(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementByName(java.lang
     * .String)
     */
    @Override public IJavaElement findElementByName(String using) {
        List<IJavaElement> elements = findElementsByName(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No component found using name: " + using, null);
        }
        return elements.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByName(java.
     * lang.String)
     */
    @Override public List<IJavaElement> findElementsByName(final String using) {
        return findByCss("#'" + using + "'");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementByCssSelector(
     * java.lang.String)
     */
    @Override public IJavaElement findElementByCssSelector(String using) {
        List<IJavaElement> elements = findElementsByCssSelector(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        }
        return elements.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByCssSelector(
     * java.lang.String)
     */
    @Override public List<IJavaElement> findElementsByCssSelector(String using) {
        Window window = targetLocator.getTopContainer().getWindow();
        IJavaElement je = JavaElementFactory.createElement(window, this, targetLocator.getTopContainer());
        FindByCssSelector finder = new FindByCssSelector(je, this, implicitWait);
        return finder.findElements(using);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementByClassName(java
     * .lang.String)
     */
    @Override public IJavaElement findElementByClassName(String using) {
        List<IJavaElement> elements = findElementsByClassName(using);
        if (elements.size() == 0) {
            throw new NoSuchElementException("No component found using selector: `" + using + "'", null);
        }
        return elements.get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * net.sourceforge.marathon.javaagent.IJavaAgent#findElementsByClassName(
     * java.lang.String)
     */
    @Override public List<IJavaElement> findElementsByClassName(String using) {
        return findByCss(":instance-of('" + using + "')");
    }

    protected List<IJavaElement> findByCss(String css) {
        Window window = targetLocator.getTopContainer().getWindow();
        IJavaElement je = JavaElementFactory.createElement(window, this, targetLocator.getTopContainer());
        FindByCssSelector finder = new FindByCssSelector(je, this, implicitWait);
        return finder.findElements(css);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getWindowProperties()
     */
    @Override public JSONObject getWindowProperties() {
        return targetLocator.getWindowProperties();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#setImplicitWait(long)
     */
    @Override public void setImplicitWait(long implicitWait) {
        this.implicitWait = implicitWait;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#findElement(java.awt.
     * Component)
     */
    @Override public IJavaElement findElement(Component component) {
        return targetLocator.getTopContainer().findElement(component);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getScreenShot()
     */
    @Override public byte[] getScreenShot() throws IOException {
        BufferedImage bufferedImage;
        Window window = targetLocator.getTopContainer().getWindow();
        try {
            Dimension windowSize = window.getSize();
            Robot robot = new Robot();
            bufferedImage = robot
                    .createScreenCapture(new Rectangle(window.getX(), window.getY(), windowSize.width, windowSize.height));
        } catch (AWTException e) {
            Rectangle rec = window.getBounds();
            bufferedImage = new BufferedImage(rec.width, rec.height, BufferedImage.TYPE_INT_ARGB);
            window.paint(bufferedImage.getGraphics());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.sourceforge.marathon.javaagent.IJavaAgent#getImplicitWait()
     */
    @Override public long getImplicitWait() {
        return implicitWait;
    }
}
