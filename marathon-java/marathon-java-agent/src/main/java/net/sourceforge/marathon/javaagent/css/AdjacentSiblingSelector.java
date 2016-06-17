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
package net.sourceforge.marathon.javaagent.css;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.marathon.javaagent.*;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

import org.json.JSONException;

public class AdjacentSiblingSelector implements Selector {

    private Selector parent;
    private SimpleSelector sibling;

    public AdjacentSiblingSelector(Selector parent, SimpleSelector sibling) {
        this.parent = parent;
        this.sibling = sibling;
    }

    @Override public String toString() {
        return parent + " + " + sibling;
    }

    @SuppressWarnings("unchecked") @Override public List<IJavaElement> findElements(final IJavaAgent driver,
            final IJavaElement container, long implicitWait) {
        final List<IJavaElement> pElements = parent.findElements(driver, container, implicitWait);
        if (pElements.size() == 0)
            return pElements;
        final Object[] r = new Object[] { null };
        if (implicitWait == 0) {
            EventQueueWait.exec(new Runnable() {
                @Override public void run() {
                    try {
                        r[0] = found(pElements, driver);
                    } catch (NoSuchWindowException e) {
                        r[0] = e;
                    } catch (UnsupportedCommandException e) {
                        r[0] = e;
                    } catch (JSONException e) {
                        r[0] = e;
                    }
                }
            });
        } else {
            new EventQueueWait() {
                @Override public boolean till() {
                    List<IJavaElement> list;
                    try {
                        list = found(pElements, driver);
                        r[0] = list;
                        return list.size() > 0;
                    } catch (NoSuchWindowException e) {
                        r[0] = e;
                        return true;
                    } catch (UnsupportedCommandException e) {
                        r[0] = e;
                        return true;
                    } catch (JSONException e) {
                        r[0] = e;
                        return true;
                    }
                }
            }.wait_noexc("Unable to find component", implicitWait, 50);
        }
        if (r[0] instanceof NoSuchWindowException)
            throw (NoSuchWindowException) r[0];
        if (r[0] instanceof UnsupportedCommandException)
            throw (UnsupportedCommandException) r[0];
        if (r[0] instanceof JSONException)
            throw (JSONException) r[0];
        return (List<IJavaElement>) r[0];
    }

    protected List<IJavaElement> found(List<IJavaElement> pElements, IJavaAgent driver) {
        List<IJavaElement> r = new ArrayList<IJavaElement>();
        for (IJavaElement je : pElements) {
            Component component = je.getComponent();
            if (!(component instanceof Container))
                continue;
            int index = getIndexOfComponentInParent(component);
            if (index < 0)
                continue;
            Container parent = component.getParent();
            JWindow topContainer = driver.switchTo().getTopContainer();
            index += 1;
            if (index < parent.getComponentCount()) {
                Component c = parent.getComponent(index);
                IJavaElement je2 = JavaElementFactory.createElement(c, driver, driver.switchTo().getTopContainer());
                List<IJavaElement> matched = sibling.matchesSelector(je2);
                for (IJavaElement javaElement : matched) {
                    IJavaElement e = topContainer.addElement(javaElement);
                    if (!r.contains(e))
                        r.add(e);
                }
            }
        }
        return r;
    }

    private int getIndexOfComponentInParent(Component component) {
        Container parent = component.getParent();
        if (parent == null)
            return -1;
        Component[] components = parent.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == component)
                return i;
        }
        return -1;
    }
}
