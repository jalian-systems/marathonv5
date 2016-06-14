package net.sourceforge.marathon.runtime.api;

import javax.swing.JDialog;

public interface ISubpanelProvider {

    public abstract ISubPropertiesPanel[] getSubPanels(JDialog parent);

}
