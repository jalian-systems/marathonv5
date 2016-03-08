package net.sourceforge.marathon.javaagent.css;

import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;

public interface Selector {

    List<IJavaElement> findElements(IJavaAgent driver, IJavaElement container, long implicitWait);

}
