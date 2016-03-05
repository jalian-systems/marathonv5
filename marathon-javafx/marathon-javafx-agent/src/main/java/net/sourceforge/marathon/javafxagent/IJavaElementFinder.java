package net.sourceforge.marathon.javafxagent;

import javafx.scene.Node;

public interface IJavaElementFinder {

    public abstract Class<? extends IJavaFXElement> get(Node component);

}