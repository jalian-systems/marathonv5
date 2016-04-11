package net.sourceforge.marathon.javafxagent;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import javafx.scene.Node;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public interface IJavaFXAgent {

    IDevice getDevices();

    String getTitle();

    Collection<String> getWindowHandles();

    String getWindowHandle();

    JavaFXTargetLocator switchTo();

    JOptions manage();

    String getVersion();

    String getName();

    void deleteWindow();

    IJavaFXElement findElement(String id);

    IJavaFXElement getActiveElement();

    void quit();

    JFXWindow getWindow(String windowHandle);

    JFXWindow getCurrentWindow();

    IJavaFXElement findElementByTagName(String using);

    List<IJavaFXElement> findElementsByTagName(String using);

    IJavaFXElement findElementByName(String using);

    List<IJavaFXElement> findElementsByName(String using);

    IJavaFXElement findElementByCssSelector(String using);

    List<IJavaFXElement> findElementsByCssSelector(String using);

    IJavaFXElement findElementByClassName(String using);

    List<IJavaFXElement> findElementsByClassName(String using);

    JSONObject getWindowProperties();

    void setImplicitWait(long implicitWait);

    IJavaFXElement findElement(Node component);

    byte[] getScreenShot() throws IOException;

    long getImplicitWait();

}