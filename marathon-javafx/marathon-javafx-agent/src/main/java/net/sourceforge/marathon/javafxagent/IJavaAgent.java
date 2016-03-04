package net.sourceforge.marathon.javafxagent;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import javafx.scene.Node;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JWindow;

public interface IJavaAgent {

    IDevice getDevices();

    String getTitle();

    Collection<String> getWindowHandles();

    String getWindowHandle();

    JavaFXTargetLocator switchTo();

    JOptions manage();

    String getVersion();

    String getName();

    void deleteWindow();

    IJavaElement findElement(String id);

    IJavaElement getActiveElement();

    void quit();

    JWindow getWindow(String windowHandle);

    JWindow getCurrentWindow();

    IJavaElement findElementByTagName(String using);

    List<IJavaElement> findElementsByTagName(String using);

    IJavaElement findElementByName(String using);

    List<IJavaElement> findElementsByName(String using);

    IJavaElement findElementByCssSelector(String using);

    List<IJavaElement> findElementsByCssSelector(String using);

    IJavaElement findElementByClassName(String using);

    List<IJavaElement> findElementsByClassName(String using);

    JSONObject getWindowProperties();

    void setImplicitWait(long implicitWait);

    IJavaElement findElement(Node component);

    byte[] getScreenShot() throws IOException;

    long getImplicitWait();

}