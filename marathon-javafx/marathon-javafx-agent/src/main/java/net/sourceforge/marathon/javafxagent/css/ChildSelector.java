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
package net.sourceforge.marathon.javafxagent.css;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import net.sourceforge.marathon.javafxagent.*;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class ChildSelector implements Selector {

    private Selector parent;
    private SimpleSelector child;

    public ChildSelector(Selector parent, SimpleSelector child) {
        this.parent = parent;
        this.child = child;
    }

    @Override public String toString() {
        return parent + " > " + child;
    }

    @SuppressWarnings("unchecked") @Override public List<IJavaFXElement> findElements(final IJavaFXAgent driver,
            final IJavaFXElement container, long implicitWait) {
        final List<IJavaFXElement> pElements = parent.findElements(driver, container, implicitWait);
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
                    List<IJavaFXElement> list;
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
        return (List<IJavaFXElement>) r[0];
    }

    protected List<IJavaFXElement> found(List<IJavaFXElement> pElements, IJavaFXAgent driver) {
        List<IJavaFXElement> r = new ArrayList<IJavaFXElement>();
        for (IJavaFXElement je : pElements) {
            if (!(je.getComponent() instanceof Parent))
                continue;
            JFXWindow topContainer = driver.switchTo().getTopContainer();
            ObservableList<Node> components = ((Parent) je.getComponent()).getChildrenUnmodifiable();
            for (Node c : components) {
                IJavaFXElement je2 = JavaFXElementFactory.createElement(c, driver, driver.switchTo().getTopContainer());
                List<IJavaFXElement> matched = child.matchesSelector(je2);
                for (IJavaFXElement javaElement : matched) {
                    IJavaFXElement e = topContainer.addElement(javaElement);
                    if (!r.contains(e))
                        r.add(e);
                }
            }
        }
        return r;
    }
}
