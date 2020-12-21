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
package net.sourceforge.marathon.javaagent.css;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.json.JSONException;
import net.sourceforge.marathon.javaagent.NoSuchWindowException;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

public class ChildSelector implements Selector {

    public static final Logger LOGGER = Logger.getLogger(ChildSelector.class.getName());

    private Selector parent;
    private SimpleSelector child;

    public ChildSelector(Selector parent, SimpleSelector child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public String toString() {
        return parent + " > " + child;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<IJavaElement> findElements(final IJavaAgent driver, final IJavaElement container, long implicitWait) {
        final List<IJavaElement> pElements = parent.findElements(driver, container, implicitWait);
        if (pElements.size() == 0) {
            return pElements;
        }
        final Object[] r = new Object[] { null };
        if (implicitWait == 0) {
            EventQueueWait.exec(new Runnable() {
                @Override
                public void run() {
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
                @Override
                public boolean till() {
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
        if (r[0] instanceof NoSuchWindowException) {
            throw (NoSuchWindowException) r[0];
        }
        if (r[0] instanceof UnsupportedCommandException) {
            throw (UnsupportedCommandException) r[0];
        }
        if (r[0] instanceof JSONException) {
            throw (JSONException) r[0];
        }
        return (List<IJavaElement>) r[0];
    }

    protected List<IJavaElement> found(List<IJavaElement> pElements, IJavaAgent driver) {
        List<IJavaElement> r = new ArrayList<IJavaElement>();
        for (IJavaElement je : pElements) {
            if (!(je.getComponent() instanceof Container)) {
                continue;
            }
            JWindow topContainer = driver.switchTo().getTopContainer();
            Component[] components = ((Container) je.getComponent()).getComponents();
            for (Component c : components) {
                IJavaElement je2 = JavaElementFactory.createElement(c, driver, driver.switchTo().getTopContainer());
                List<IJavaElement> matched = child.matchesSelector(je2);
                for (IJavaElement javaElement : matched) {
                    IJavaElement e = topContainer.addElement(javaElement);
                    if (!r.contains(e)) {
                        r.add(e);
                    }
                }
            }
        }
        return r;
    }
}
