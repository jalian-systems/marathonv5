package net.sourceforge.marathon.javafxagent;

import javafx.scene.Node;

public interface IPseudoElement {

    public IJavaFXElement getParent();

    public String createHandle();

    /**
     * The implementation needs to run under EDT if it accesses the component
     * 
     * @return
     */
    public Node getPseudoComponent();
}