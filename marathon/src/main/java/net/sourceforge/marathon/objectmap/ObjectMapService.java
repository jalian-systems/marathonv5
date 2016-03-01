package net.sourceforge.marathon.objectmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.ObjectIdentity;
import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.PropertyList;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

public class ObjectMapService implements IObjectMapService {

    protected ObjectMapConfiguration configuration;
    protected ObjectMap objectMap;

    @SuppressWarnings("unused") private static final Logger logger = Logger.getLogger(ObjectMapService.class.getName());

    public ObjectMapService() {
        init();
    }

    public void init() {
        configuration = new ObjectMapConfiguration();
        objectMap = new ObjectMap();
        // if (mode == MarathonMode.MARK_USED)
        // setDirty(true);
    }

    private IOMapContainer getTopLevelComponent(IPropertyAccessor pa, List<List<String>> rproperties, List<String> gproperties,
            String title) throws ObjectMapException {
        synchronized (objectMap) {
            final OMapContainer topLevelComponent = objectMap.getTopLevelComponent(pa, rproperties, gproperties, title, false);
            return new IOMapContainer() {
                public OMapContainer getOMapContainer(ObjectMap objectMap) {
                    return topLevelComponent;
                }

                @Override public String toString() {
                    return topLevelComponent.toString();
                }

                public List<String> getUsedRecognitionProperties() {
                    return topLevelComponent.getUsedRecognitionProperties();
                }
            };
        }
    }

    public void save() {
        synchronized (objectMap) {
            objectMap.save();
        }
    }

    public void setDirty(boolean b) {
        synchronized (objectMap) {
            objectMap.setDirty(b);
        }
    }

    private OMapComponent findComponentByName(String name, IOMapContainer container) {
        OMapContainer oMapContainer = container.getOMapContainer(objectMap);
        synchronized (oMapContainer) {
            return objectMap.findComponentByName(name, oMapContainer);
        }
    }

    private List<String> getGeneralProperties() {
        return configuration.getGeneralProperties();
    }

    public void load() throws IOException {
        configuration.load();
    }

    public List<ObjectIdentity> getNamingProperties() {
        return configuration.getNamingProperties();
    }

    private List<ObjectIdentity> getContainerRecognitionProperties() {
        return configuration.getContainerRecognitionProperties();
    }

    private OMapComponent insertNameForComponent(String name, Properties urp, Properties properties, IOMapContainer container) {
        OMapContainer oMapContainer = container.getOMapContainer(objectMap);
        synchronized (oMapContainer) {
            return objectMap.insertNameForComponent(name, urp, properties, oMapContainer);
        }
    }

    private IOMapContainer getContainer(Properties urp, Properties attributes) throws ObjectMapException {
        synchronized (objectMap) {
            final OMapContainer topLevelComponent = objectMap.getTopLevelComponent(attributes, urp);
            return new IOMapContainer() {
                public OMapContainer getOMapContainer(ObjectMap objectMap) {
                    return topLevelComponent;
                }

                @Override public String toString() {
                    return topLevelComponent.toString();
                }

                public List<String> getUsedRecognitionProperties() {
                    return topLevelComponent.getUsedRecognitionProperties();
                }
            };
        }
    }

    private List<OMapComponent> findComponentsByProperties(Properties attributes, IOMapContainer container) {
        OMapContainer oMapContainer = container.getOMapContainer(objectMap);
        synchronized (oMapContainer) {
            return objectMap.findComponentsByProperties(attributes, oMapContainer);
        }
    }

    @Override public OMapComponent findComponentByName(String name, IPropertyAccessor containerAccessor) throws ObjectMapException {
        IOMapContainer container = getContainer(containerAccessor);
        return findComponentByName(name, container);
    }

    @Override public List<OMapComponent> findComponentsByProperties(Properties attributes, Properties urpContainer,
            Properties attributesContainer) throws ObjectMapException {
        IOMapContainer container = getContainer(urpContainer, attributesContainer);
        return findComponentsByProperties(attributes, container);
    }

    @Override public OMapComponent insertNameForComponent(String name, Properties urp, Properties properties,
            Properties urpContainer, Properties attributesContainer) throws ObjectMapException {
        IOMapContainer container = getContainer(urpContainer, attributesContainer);
        return insertNameForComponent(name, urp, properties, container);
    }

    @Override public OMapComponent findComponentByName(String name, Properties urpContainer, Properties attributesContainer) throws ObjectMapException {
        IOMapContainer container = getContainer(urpContainer, attributesContainer);
        return findComponentByName(name, container);
    }

    private IOMapContainer getContainer(IPropertyAccessor containerAccessor) throws ObjectMapException {
        String containerClassName = containerAccessor.getProperty("tagName");
        String title = containerAccessor.getProperty("title");
        IOMapContainer container = getTopLevelComponent(containerAccessor, findContainerRecognitionProperties(containerClassName),
                getGeneralProperties(), title);
        return container;
    }

    private List<List<String>> findContainerRecognitionProperties(String c) {
        List<List<String>> findProperties = findProperties(c, getContainerRecognitionProperties());
        List<String> lastResortProperties = new ArrayList<String>();
        lastResortProperties.add("component.class.name");
        lastResortProperties.add("title");
        findProperties.add(lastResortProperties);
        return findProperties;
    }

    private List<List<String>> findProperties(String name, List<ObjectIdentity> list) {
        List<PropertyList> selection = new ArrayList<PropertyList>();
        for (ObjectIdentity objectIdentity : list) {
            if (objectIdentity.getClassName().equals(name))
                selection.addAll(objectIdentity.getPropertyLists());
        }
        Collections.sort(selection, new Comparator<PropertyList>() {
            public int compare(PropertyList o1, PropertyList o2) {
                return o2.getPriority() - o1.getPriority();
            }
        });
        List<List<String>> sortedList = new ArrayList<List<String>>();
        for (PropertyList pl : selection) {
            sortedList.add(new ArrayList<String>(pl.getProperties()));
        }
        return sortedList;
    }

    @Override public String[] findComponentNames(IPropertyAccessor containerAccessor) throws ObjectMapException {
        IOMapContainer container = getContainer(containerAccessor);
        return findComponentNames(container);
    }

    private String[] findComponentNames(IOMapContainer container) {
        OMapContainer oMapContainer = container.getOMapContainer(objectMap);
        synchronized (oMapContainer) {
            return objectMap.findComponentNames(oMapContainer);
        }
    }

}
