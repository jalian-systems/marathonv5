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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.UUID;

import net.sourceforge.marathon.json.JSONArray;

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
