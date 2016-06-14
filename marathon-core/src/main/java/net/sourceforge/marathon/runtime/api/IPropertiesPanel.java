package net.sourceforge.marathon.runtime.api;

import java.util.Properties;
import javax.swing.Icon;
import javax.swing.JPanel;

public interface IPropertiesPanel {
    public abstract JPanel getPanel();

    public abstract String getName();

    public abstract Icon getIcon();

    public abstract void getProperties(Properties props);

    public abstract void setProperties(Properties props);

    public abstract boolean isValidInput();
}
