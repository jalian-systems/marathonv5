/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.objectmap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.ObjectIdentity;
import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.PropertyList;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

public class ObjectMapService implements IObjectMapService {

    public static final Logger LOGGER = Logger.getLogger(ObjectMapService.class.getName());

    protected ObjectMapConfiguration configuration;
    protected ObjectMap objectMap;

    private Collection<String> allProperties;

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ObjectMapService.class.getName());

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
                @Override
                public OMapContainer getOMapContainer(ObjectMap objectMap) {
                    return topLevelComponent;
                }

                @Override
                public String toString() {
                    return topLevelComponent.toString();
                }

                @Override
                public List<String> getUsedRecognitionProperties() {
                    return topLevelComponent.getUsedRecognitionProperties();
                }
            };
        }
    }

    @Override
    public void save() {
        synchronized (objectMap) {
            objectMap.save();
        }
    }

    @Override
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

    @Override
    public void load() throws IOException {
        configuration.load();
    }

    @Override
    public List<ObjectIdentity> getNamingProperties() {
        return configuration.getNamingProperties();
    }

    @Override
    public List<ObjectIdentity> getContainerNamingProperties() {
        return configuration.getContainerNamingProperties();
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
                @Override
                public OMapContainer getOMapContainer(ObjectMap objectMap) {
                    return topLevelComponent;
                }

                @Override
                public String toString() {
                    return topLevelComponent.toString();
                }

                @Override
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

    @Override
    public OMapComponent findComponentByName(String name, IPropertyAccessor containerAccessor) throws ObjectMapException {
        IOMapContainer container = getContainer(containerAccessor);
        return findComponentByName(name, container);
    }

    @Override
    public List<OMapComponent> findComponentsByProperties(Properties attributes, Properties urpContainer,
            Properties attributesContainer) throws ObjectMapException {
        IOMapContainer container = getContainer(urpContainer, attributesContainer);
        return findComponentsByProperties(attributes, container);
    }

    @Override
    public OMapComponent insertNameForComponent(String name, Properties urp, Properties properties, Properties urpContainer,
            Properties attributesContainer) throws ObjectMapException {
        IOMapContainer container = getContainer(urpContainer, attributesContainer);
        return insertNameForComponent(name, urp, properties, container);
    }

    @Override
    public OMapComponent findComponentByName(String name, Properties urpContainer, Properties attributesContainer)
            throws ObjectMapException {
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
            if (objectIdentity.getClassName().equals(name)) {
                selection.addAll(objectIdentity.getPropertyLists());
            }
        }
        Collections.sort(selection, new Comparator<PropertyList>() {
            @Override
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

    @Override
    public String[] findComponentNames(IPropertyAccessor containerAccessor) throws ObjectMapException {
        IOMapContainer container = getContainer(containerAccessor);
        return findComponentNames(container);
    }

    private String[] findComponentNames(IOMapContainer container) {
        OMapContainer oMapContainer = container.getOMapContainer(objectMap);
        synchronized (oMapContainer) {
            return objectMap.findComponentNames(oMapContainer);
        }
    }

    @Override
    public Collection<String> findProperties() {
        if (allProperties == null) {
            allProperties = collectProperties();
        }
        return allProperties;
    }

    private Set<String> collectProperties() {
        Set<String> allProps = new HashSet<String>();
        allProps.addAll(getGeneralProperties());
        collectProperties(allProps, new JSONArray(configuration.getRecognitionProperties()));
        collectProperties(allProps, new JSONArray(getNamingProperties()));
        collectProperties(allProps, new JSONArray(getContainerNamingProperties()));
        collectProperties(allProps, new JSONArray(getContainerRecognitionProperties()));
        allProps.add("type");
        allProps.add("tagName");
        allProps.add("indexOfType");
        return allProps;
    }

    private void collectProperties(Set<String> allProps, JSONArray rps) {
        for (int i = 0; i < rps.length(); i++) {
            JSONObject rp = rps.getJSONObject(i);
            JSONArray pls = rp.getJSONArray("propertyLists");
            for (int j = 0; j < pls.length(); j++) {
                JSONObject pl = pls.getJSONObject(j);
                JSONArray ps = pl.getJSONArray("properties");
                for (int k = 0; k < ps.length(); k++) {
                    allProps.add(ps.getString(k));
                }
            }
        }
    }

    @Override
    public ObjectMapConfiguration getObjectMapConfiguration() {
        return configuration;
    }

    @Override
    public List<OMapComponent> findComponents(IPropertyAccessor containerAccessor) throws ObjectMapException {
        IOMapContainer container = getContainer(containerAccessor);
        OMapContainer oMapContainer = container.getOMapContainer(objectMap);
        synchronized (oMapContainer) {
            return objectMap.findComponents(oMapContainer);
        }
    }

}
