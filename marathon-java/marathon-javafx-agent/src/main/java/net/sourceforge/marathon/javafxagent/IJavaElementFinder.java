package net.sourceforge.marathon.javafxagent;

import java.awt.Component;

public interface IJavaElementFinder {

    public abstract Class<? extends IJavaElement> get(Component component);

}