package net.sourceforge.marathon.javafxagent.css;

import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaElement;

public interface SelectorFilter {

    List<IJavaElement> match(IJavaElement je);

}
