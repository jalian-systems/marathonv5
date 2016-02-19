package net.sourceforge.marathon.javaagent;

import java.awt.Component;

public interface IPseudoElement {

    public IJavaElement getParent();

    public String createHandle();

    /**
     * The implementation needs to run under EDT if it accesses the component
     * 
     * @return
     */
    public Component getPseudoComponent();
}