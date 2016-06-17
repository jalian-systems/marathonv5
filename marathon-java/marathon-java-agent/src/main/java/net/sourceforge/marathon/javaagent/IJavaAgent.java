/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javaagent;

import java.awt.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

public interface IJavaAgent {

    IDevice getDevices();

    String getTitle();

    Collection<String> getWindowHandles();

    String getWindowHandle();

    JavaTargetLocator switchTo();

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

    IJavaElement findElement(Component component);

    byte[] getScreenShot() throws IOException;

    long getImplicitWait();

}