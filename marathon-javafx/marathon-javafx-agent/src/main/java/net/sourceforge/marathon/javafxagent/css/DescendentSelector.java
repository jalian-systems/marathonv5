package net.sourceforge.marathon.javafxagent.css;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public class DescendentSelector implements Selector {

    private Selector parent;
    private SimpleSelector descendent;

    public DescendentSelector(Selector parent, SimpleSelector child) {
        this.parent = parent;
        this.descendent = child;
    }

    @Override public String toString() {
        return parent + " " + descendent;
    }

    @Override public List<IJavaFXElement> findElements(IJavaFXAgent driver, IJavaFXElement container, long implicitWait) {
        List<IJavaFXElement> result = new ArrayList<IJavaFXElement>();
        List<IJavaFXElement> parents = parent.findElements(driver, container, implicitWait);
        for (IJavaFXElement parent : parents) {
            List<IJavaFXElement> es = descendent.findElements(driver, parent, implicitWait);
            for (IJavaFXElement e : es) {
                if (!result.contains(e))
                    result.add(e);
            }
        }
        return result;
    }
}
