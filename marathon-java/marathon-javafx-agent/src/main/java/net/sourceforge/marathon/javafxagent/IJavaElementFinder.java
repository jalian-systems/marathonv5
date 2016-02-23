package net.sourceforge.marathon.javafxagent;

import javafx.scene.Node;

public interface IJavaElementFinder {

    public abstract Class<? extends IJavaElement> get(Node component);

}