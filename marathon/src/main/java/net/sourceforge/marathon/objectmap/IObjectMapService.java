package net.sourceforge.marathon.objectmap;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.ObjectIdentity;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

public interface IObjectMapService {

    public abstract void save();

    public abstract void setDirty(boolean b);

    public abstract void load() throws IOException;

    public List<ObjectIdentity> getNamingProperties();

    public abstract OMapComponent findComponentByName(String name, IPropertyAccessor containerAccessor) throws ObjectMapException;

    public abstract List<OMapComponent> findComponentsByProperties(Properties attributes, Properties urpContainer,
            Properties attributesContainer) throws ObjectMapException;

    public abstract OMapComponent insertNameForComponent(String name, Properties urp, Properties properties,
            Properties urpContainer, Properties attributesContainer) throws ObjectMapException;

    public abstract OMapComponent findComponentByName(String name, Properties urpContainer, Properties attributesContainer) throws ObjectMapException;

    public abstract String[] findComponentNames(IPropertyAccessor topContainerAccessor) throws ObjectMapException;

}
