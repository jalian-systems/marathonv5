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
package net.sourceforge.marathon.fx.objectmap;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import net.sourceforge.marathon.objectmap.OMapComponent;
import net.sourceforge.marathon.objectmap.OMapContainer;
import net.sourceforge.marathon.runtime.api.Constants;

public class ObjectMapItem implements IObjectMapTreeItem {

    protected List<OMapContainer> data;
    private List<OMapContainer> deletedContainers = new ArrayList<>();
    private boolean dirty = false;

    private final static Logger logger = Logger.getLogger(ObjectMapItem.class.getName());

    public ObjectMapItem() {
        load();
    }

    @SuppressWarnings("unchecked") private void load() {
        try {
            data = (List<OMapContainer>) loadYaml(getOMapFile());
            for (OMapContainer container : data) {
                container.setRootNode(this);
            }
        } catch (IOException e) {
            data = new ArrayList<>();
            setDirty(true);
            logger.info("Creating a new ObjectMap");
        }
    }

    public void setDirty(boolean b) {
        this.dirty = b;
    }

    public boolean isDirty() {
        return dirty;
    }

    private Object loadYaml(File omapfile) throws IOException {
        FileReader reader = new FileReader(omapfile);
        try {
            Constructor constructor = new Constructor();
            PropertyUtils putils = new PropertyUtils();
            putils.setSkipMissingProperties(true);
            constructor.setPropertyUtils(putils);
            Yaml yaml = new Yaml(constructor);
            return yaml.load(reader);
        } catch (Throwable t) {
            throw new RuntimeException("Error loading yaml from: " + omapfile.getAbsolutePath() + "\n" + t.getMessage(), t);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadAll() {
        Iterator<OMapContainer> iterator = data.iterator();
        while (iterator.hasNext()) {
            OMapContainer container = iterator.next();
            try {
                container.load();
            } catch (FileNotFoundException e) {
                iterator.remove();
            }
        }
    }

    public Enumeration<IObjectMapTreeItem> children() {
        return new Enumeration<IObjectMapTreeItem>() {
            int index = 0;

            @Override public boolean hasMoreElements() {
                return index < data.size();
            }

            @Override public IObjectMapTreeItem nextElement() {
                return data.get(index++);
            }
        };
    }

    public IObjectMapTreeItem getChildAt(int childIndex) {
        if (childIndex < data.size()) {
            return data.get(childIndex);
        }
        return null;
    }

    public int getChildCount() {
        return data.size();
    }

    @Override public void setRootNode(IObjectMapTreeItem parentNode) {
    }

    public void removeComponent(OMapComponent omapComponent) {
        OMapContainer parent = omapComponent.getParent();
        parent.removeComponent(omapComponent);
    }

    public void remove(OMapContainer container) {
        if (data.contains(container)) {
            deletedContainers.add(container);
            data.remove(container);
        } else {
            logger.warning("Container " + container + " does not exist in the objectmap");
        }
    }

    public void add(OMapContainer omapContainer) {
        data.add(omapContainer);
    }

    public void save() {
        logger.info("Saving object map");
        if (!isDirty()) {
            logger.info("Object map is not modified. Skipping save.");
            return;
        }
        try {
            DumperOptions options = new DumperOptions();
            options.setIndent(4);
            options.setDefaultFlowStyle(FlowStyle.AUTO);

            Representer representer = new Representer() {
                @Override protected Set<Property> getProperties(Class<? extends Object> type) throws IntrospectionException {
                    Set<Property> properties = super.getProperties(type);
                    Property parentProperty = null;
                    for (Property property : properties) {
                        if (property.getName().equals("parent")) {
                            parentProperty = property;
                        }
                    }
                    if (parentProperty != null) {
                        properties.remove(parentProperty);
                    }
                    return properties;
                }
            };
            FileWriter writer = null;
            try{
            writer = new FileWriter(getOMapFile());
            new Yaml(representer, options).dump(data, writer);
            }finally{
                if(writer != null)
                    writer.close();
            }
            for (OMapContainer container : data) {
                container.save();
            }
        } catch (IOException e) {
            logger.warning("Unable to save object map");
            e.printStackTrace();
        } catch (YAMLException e1) {
            logger.warning("Unable to save object map: " + this);
            e1.printStackTrace();
            throw e1;
        }
        for (OMapContainer oc : deletedContainers) {
            oc.deleteFile();
        }
        setDirty(false);
    }

    public static File getOMapFile() {
        return new File(System.getProperty(Constants.PROP_PROJECT_DIR),
                System.getProperty(Constants.PROP_OMAP_FILE, Constants.FILE_OMAP));
    }
}
