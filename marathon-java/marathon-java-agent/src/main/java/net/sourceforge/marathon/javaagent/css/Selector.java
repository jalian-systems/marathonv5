package net.sourceforge.marathon.javaagent.css;

import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;

public interface Selector {

    List<IJavaElement> findElements(JavaAgent driver, IJavaElement container, long implicitWait);

}
