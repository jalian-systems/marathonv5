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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import net.sourceforge.marathon.fx.objectmap.ObjectMapItem;
import net.sourceforge.marathon.runtime.api.ILogger;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;
import net.sourceforge.marathon.runtime.api.PropertiesPropertyAccessor;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;

public class ObjectMap extends ObjectMapItem {

    public static final Logger LOGGER = Logger.getLogger(ObjectMap.class.getName());

    private static final ILogger logger = RuntimeLogger.getRuntimeLogger();

    private static final String MODULE = "Object Map";

    public ObjectMap() {
    }

    public OMapContainer getTopLevelComponent(IPropertyAccessor pa, List<List<String>> rproperties, List<String> gproperties,
            String title, boolean createIfNeeded) throws ObjectMapException {
        OMapContainer currentContainer;
        List<OMapContainer> matched = new ArrayList<OMapContainer>();
        for (OMapContainer com : data) {
            if (com.isMatched(pa)) {
                matched.add(com);
            }
        }
        if (matched.size() == 1) {
            currentContainer = matched.get(0);
            try {
                loadContainer(currentContainer);
                logger.info(MODULE, "Setting current container to: " + currentContainer);
            } catch (FileNotFoundException e) {
                logger.warning(MODULE, "File not found for container: " + title + ". Recreating object map file for container.");
                data.remove(currentContainer);
                currentContainer = createNewContainer(pa, rproperties, gproperties, title);
            }
        } else if (matched.size() == 0) {
            if (createIfNeeded) {
                currentContainer = createNewContainer(pa, rproperties, gproperties, title);
            } else {
                throw new ObjectMapException("No top level component matched for the given properties: " + pa);
            }
        } else {
            throw new ObjectMapException("More than one toplevel container matched for given properties");
        }
        currentContainer.addTitle(title);
        return currentContainer;
    }

    private OMapContainer createNewContainer(IPropertyAccessor pa, List<List<String>> rproperties, List<String> gproperties,
            String title) {
        OMapContainer container = new OMapContainer(this, title);
        setDirty(true);
        add(container);
        List<OMapRecognitionProperty> toplevelContainer = createPropertyList(pa, rproperties);
        container.setContainerRecognitionProperties(toplevelContainer);
        List<OMapProperty> generalProperties = getGeneralProperties(pa, gproperties, rproperties);
        container.setContainerGeneralProperties(generalProperties);
        container.setCreated(Calendar.getInstance().getTime().getTime());
        logger.info(MODULE, "Created a new container: " + container);
        return container;
    }

    private List<OMapProperty> getGeneralProperties(IPropertyAccessor pa, List<String> gproperties,
            List<List<String>> rproperties) {
        ArrayList<OMapProperty> gprops = new ArrayList<OMapProperty>();
        Set<String> props = new HashSet<String>();
        props.addAll(gproperties);
        props.add("instanceOf");
        props.add("component.class.simpleName");
        props.add("component.class.name");
        props.add("oMapClassName");
        props.add("oMapClassSimpleName");
        for (List<String> list : rproperties) {
            for (String string : list) {
                props.add(string);
            }
        }
        for (String gprop : props) {
            String gpropValue = pa.getProperty(gprop);
            if (gpropValue != null && !"".equals(gpropValue)) {
                OMapProperty o = new OMapProperty();
                o.setName(gprop);
                o.setValue(gpropValue);
                gprops.add(o);
            }
        }
        return gprops;
    }

    private List<OMapRecognitionProperty> createPropertyList(IPropertyAccessor pa, List<List<String>> properties) {
        List<OMapRecognitionProperty> omrpl = new ArrayList<OMapRecognitionProperty>();
        for (List<String> proplist : properties) {
            if (validProperties(pa, proplist)) {
                copyProperties(pa, proplist, omrpl);
                return omrpl;
            }
        }
        return omrpl;
    }

    private void copyProperties(IPropertyAccessor pa, List<String> proplist, List<OMapRecognitionProperty> omrpl) {
        for (String p : proplist) {
            OMapRecognitionProperty omrp = new OMapRecognitionProperty();
            omrp.setMethod(IPropertyAccessor.METHOD_EQUALS);
            omrp.setName(p);
            omrp.setValue(pa.getProperty(p));
            omrpl.add(omrp);
        }
    }

    private boolean validProperties(IPropertyAccessor pa, List<String> proplist) {
        for (String p : proplist) {
            if (pa.getProperty(p) == null) {
                return false;
            }
        }
        return true;
    }

    public OMapComponent findComponentByName(String name, OMapContainer currentContainer) {
        OMapComponent omapComponent = currentContainer.findComponentByName(name);
        logger.info(MODULE, "findComponentByName(" + name + "): " + omapComponent);
        if (omapComponent == null) {
            return null;
        }
        omapComponent.setUsed(true);
        return omapComponent;
    }

    public OMapComponent findComponentByProperties(IPropertyAccessor w, OMapContainer currentContainer) throws ObjectMapException {
        OMapComponent omapComponent = currentContainer.findComponentByProperties(w);
        logger.info(MODULE, "findComponentByProperties(" + w.getProperty("component.class.name") + "): " + omapComponent);
        return omapComponent;
    }

    public OMapComponent insertNameForComponent(String name, IPropertyAccessor w, List<String> rprops,
            List<List<String>> rproperties, List<List<String>> nproperties, List<String> gproperties,
            OMapContainer currentContainer) {
        logger.info(MODULE, "insertNameForComponent(" + name + "): with index of type: " + w.getProperty("indexOfType"));
        OMapComponent omapComponent = currentContainer.insertNameForComponent(name, w, rprops, rproperties, nproperties,
                gproperties);
        logger.info(MODULE, "insertNameForComponent(" + name + "): " + omapComponent);
        if (omapComponent != null) {
            setDirty(true);
        }
        return omapComponent;
    }

    public OMapComponent findComponentByProperties(IPropertyAccessor w, List<String> rprops, OMapContainer currentContainer) {
        OMapComponent omapComponent = currentContainer.findComponentByProperties(w, rprops);
        logger.info(MODULE,
                "findComponentByProperties(" + w.getProperty("component.class.name") + ", " + rprops + "): " + omapComponent);
        return omapComponent;
    }

    public void updateComponent(OMapComponent omapComponent, List<String> rprops, OMapContainer currentContainer) {
        logger.info(MODULE, "updateComponent(" + omapComponent.getName() + "): with properties: " + rprops);
        currentContainer.updateComponent(omapComponent, rprops);
    }

    public int getIndexOfContainer(OMapContainer container) {
        return data.indexOf(container);
    }

    public OMapContainer getContainerByIndex(int containerIndex) {
        return data.get(containerIndex);
    }

    public void markEntryNeeded(String name, OMapContainer container) {
        OMapComponent oMapComponent = container.findComponentByName(name);
        if (oMapComponent != null) {
            if (oMapComponent.withLastResortProperties()) {
                String desc = "Recording " + name + " using last resort recognition properties\n"
                        + "    Using the indexOfType as recognition property is inherently unstable under application changes.\n"
                        + "    Try using other set of properties for this component by updating the objectmap.";
                logger.warning(MODULE, "Recording " + name + " using last resort recognition properties", desc);
            }
            oMapComponent.markEntryNeeded(true);
        } else {
            logger.error(MODULE, "Could not find component " + name + "in " + container + ". MarkUsed failed.");
        }
        setDirty(true);
    }

    public List<OMapComponent> findComponentsByProperties(IPropertyAccessor wrapper, OMapContainer container) {
        return container.findComponentsByProperties(wrapper);
    }

    public OMapComponent insertNameForComponent(String name, Properties urp, Properties properties,
            OMapContainer currentContainer) {
        logger.info(MODULE, "insertNameForComponent(" + name + ")");
        OMapComponent omapComponent = currentContainer.insertNameForComponent(name, urp, properties);
        logger.info(MODULE, "insertNameForComponent(" + name + "): " + omapComponent);
        if (omapComponent != null) {
            setDirty(true);
        }
        omapComponent.markEntryNeeded(true);
        return omapComponent;
    }

    public OMapContainer getTopLevelComponent(Properties attributes, Properties urp) throws ObjectMapException {
        IPropertyAccessor pa = new PropertiesPropertyAccessor(attributes);
        OMapContainer currentContainer;
        List<OMapContainer> matched = new ArrayList<OMapContainer>();
        for (OMapContainer com : data) {
            if (com.isMatched(pa)) {
                matched.add(com);
            }
        }
        String title = getTitle(attributes);
        if (matched.size() == 1) {
            currentContainer = matched.get(0);
            try {
                loadContainer(currentContainer);
                logger.info(MODULE, "Setting current container to: " + currentContainer);
            } catch (FileNotFoundException e) {
                logger.warning(MODULE, "File not found for container: " + title + ". Recreating object map file for container.");
                data.remove(currentContainer);
                currentContainer = createNewContainer(attributes, urp);
            }
        } else if (matched.size() == 0) {
            currentContainer = createNewContainer(attributes, urp);
        } else {
            throw new ObjectMapException("More than one toplevel container matched for given properties");
        }
        currentContainer.addTitle(title);
        return currentContainer;
    }

    private String getTitle(Properties attributes) {
        String title = attributes.getProperty("title");
        if (title == null) {
            title = "<NO_TITLE>";
        }
        return title;
    }

    private OMapContainer createNewContainer(Properties attributes, Properties urp) {
        OMapContainer container = new OMapContainer(this, getTitle(attributes));
        setDirty(true);
        add(container);
        List<OMapRecognitionProperty> toplevelContainer = createPropertyList(urp);
        container.setContainerRecognitionProperties(toplevelContainer);
        List<OMapProperty> generalProperties = getGeneralProperties(attributes);
        container.setContainerGeneralProperties(generalProperties);
        container.setCreated(Calendar.getInstance().getTime().getTime());
        logger.info(MODULE, "Created a new container: " + container);
        return container;
    }

    private List<OMapProperty> getGeneralProperties(Properties attributes) {
        List<OMapProperty> ompl = new ArrayList<OMapProperty>();
        Enumeration<Object> keys = attributes.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            OMapProperty omp = new OMapProperty();
            omp.setName(key);
            omp.setValue(attributes.getProperty(key));
            ompl.add(omp);
        }
        return ompl;
    }

    private List<OMapRecognitionProperty> createPropertyList(Properties urp) {
        List<OMapRecognitionProperty> omrpl = new ArrayList<OMapRecognitionProperty>();
        Enumeration<Object> keys = urp.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            OMapRecognitionProperty omrp = new OMapRecognitionProperty();
            omrp.setMethod(IPropertyAccessor.METHOD_EQUALS);
            omrp.setName(key);
            omrp.setValue(urp.getProperty(key));
            omrpl.add(omrp);
        }
        return omrpl;
    }

    public List<OMapComponent> findComponentsByProperties(Properties attributes, OMapContainer container) {
        IPropertyAccessor w = new PropertiesPropertyAccessor(attributes);
        return container.findComponentsByProperties(w);
    }

    public String[] findComponentNames(OMapContainer currentContainer) {
        return currentContainer.findComponentNames();
    }

    public List<OMapComponent> findComponents(OMapContainer container) {
        return container.findComponents();
    }

}
