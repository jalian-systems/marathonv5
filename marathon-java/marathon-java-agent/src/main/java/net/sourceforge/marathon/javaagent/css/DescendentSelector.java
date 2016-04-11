package net.sourceforge.marathon.javaagent.css;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;

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

    @Override public List<IJavaElement> findElements(IJavaAgent driver, IJavaElement container, long implicitWait) {
        List<IJavaElement> result = new ArrayList<IJavaElement>();
        List<IJavaElement> parents = parent.findElements(driver, container, implicitWait);
        for (IJavaElement parent : parents) {
            List<IJavaElement> es = descendent.findElements(driver, parent, implicitWait);
            for (IJavaElement e : es) {
                if (!result.contains(e))
                    result.add(e);
            }
        }
        return result;
    }
}
