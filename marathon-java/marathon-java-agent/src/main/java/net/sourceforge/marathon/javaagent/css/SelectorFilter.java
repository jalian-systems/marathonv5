package net.sourceforge.marathon.javaagent.css;

import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaElement;

public interface SelectorFilter {

    List<IJavaElement> match(IJavaElement je);

}
