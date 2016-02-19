package net.sourceforge.marathon.javaagent;

import java.awt.Component;

public interface IJavaElementFinder {

    public abstract Class<? extends IJavaElement> get(Component component);

}