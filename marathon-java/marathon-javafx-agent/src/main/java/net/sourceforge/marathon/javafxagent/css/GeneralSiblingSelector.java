package net.sourceforge.marathon.javafxagent.css;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import net.sourceforge.marathon.javafxagent.*;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

public class GeneralSiblingSelector implements Selector {

    private Selector parent;
    private SimpleSelector sibling;

    public GeneralSiblingSelector(Selector parent, SimpleSelector sibling) {
        this.parent = parent;
        this.sibling = sibling;
    }

    @Override public String toString() {
        return parent + " ~ " + sibling;
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
            for (int i = index + 1; i < parent.getComponentCount(); i++) {
                Component c = parent.getComponent(i);
                IJavaElement je2 = JavaElementFactory.createElement(c, driver, driver.switchTo().getTopContainer());
                if (sibling.matchesSelector(je2).size() > 0) {
                    IJavaElement e = topContainer.addElement(JavaElementFactory.createElement(c, driver, topContainer));
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
