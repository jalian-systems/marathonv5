package net.sourceforge.marathon.javafxagent.css;

import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public interface Selector {

    List<IJavaFXElement> findElements(IJavaFXAgent driver, IJavaFXElement container, long implicitWait);

}
