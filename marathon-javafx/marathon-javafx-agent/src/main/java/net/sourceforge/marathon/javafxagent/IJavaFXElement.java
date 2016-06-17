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
package net.sourceforge.marathon.javafxagent;

import java.util.List;
import java.util.UUID;

import org.json.JSONArray;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;

public interface IJavaFXElement {

    public abstract void click();

    public abstract void sendKeys(CharSequence... keysToSend);

    public abstract void clear();

    public abstract String getAttribute(String name);

    public abstract boolean isSelected();

    public abstract boolean isEnabled();

    public abstract String getText();

    public abstract boolean isDisplayed();

    public abstract Point2D getLocation();

    public abstract Dimension2D getSize();

    public abstract String getCssValue(String propertyName);

    public abstract IJavaFXElement findElementByName(String using);

    public abstract List<IJavaFXElement> findElementsByName(String using);

    public abstract String getHandle();

    public abstract Node getComponent();

    public abstract String getTagName();

    public abstract UUID getElementId();

    public abstract IJavaFXElement findElementByTagName(String using);

    public abstract List<IJavaFXElement> findElementsByTagName(String using);

    public abstract IJavaFXElement findElementByCssSelector(String using);

    public abstract List<IJavaFXElement> findElementsByCssSelector(String using);

    public abstract boolean hasAttribue(String name);

    public abstract boolean filterByPseudoClass(String function, Object... args);

    public abstract IJavaFXElement[] getComponents();

    public abstract List<IJavaFXElement> getByPseudoElement(String function, Object[] params);

    public abstract String createId();

    public abstract void setId(UUID id);

    public abstract void moveto();

    public abstract void moveto(double xoffset, double yoffset);

    public abstract Point2D getMidpoint();

    public abstract void click(int button, int clickCount, double x, double y);

    public abstract void buttonDown(int button, double xoffset, double yoffset);

    public abstract void buttonUp(int button, double xoffset, double yoffset);

    public abstract IJavaFXElement findElementByClassName(String using);

    public abstract List<IJavaFXElement> findElementsByClassName(String using);

    public abstract boolean marathon_select(String value);

    public abstract boolean marathon_select(JSONArray jsonArray);

    public abstract void submit();

}