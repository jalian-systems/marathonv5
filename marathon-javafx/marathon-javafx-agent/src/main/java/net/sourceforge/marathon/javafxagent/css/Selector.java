package net.sourceforge.marathon.javafxagent.css;

import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.IJavaElement;

public interface Selector {

    List<IJavaElement> findElements(IJavaAgent driver, IJavaElement container, long implicitWait);

}
