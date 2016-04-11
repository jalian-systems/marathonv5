package net.sourceforge.marathon.javafxagent.css;

import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public interface SelectorFilter {

    List<IJavaFXElement> match(IJavaFXElement je);

}
