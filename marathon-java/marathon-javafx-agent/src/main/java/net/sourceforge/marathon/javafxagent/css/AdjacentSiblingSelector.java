package net.sourceforge.marathon.javafxagent.css;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import net.sourceforge.marathon.javafxagent.*;
import net.sourceforge.marathon.javafxagent.JavaTargetLocator.JWindow;

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
            Node component = je.getComponent();
            if (!(component instanceof Parent))
                continue;
            int index = getIndexOfComponentInParent(component);
            if (index < 0)
                continue;
            Parent parent = component.getParent();
            JWindow topContainer = driver.switchTo().getTopContainer();
            index += 1;
            if (index < parent.getChildrenUnmodifiable().size()) {
                Node c = parent.getChildrenUnmodifiable().get(index);
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

    private int getIndexOfComponentInParent(Node component) {
        Parent parent = component.getParent();
        if (parent == null)
            return -1;
        ObservableList<Node> components = parent.getChildrenUnmodifiable();
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) == component)
                return i;
        }
        return -1;
    }
}
