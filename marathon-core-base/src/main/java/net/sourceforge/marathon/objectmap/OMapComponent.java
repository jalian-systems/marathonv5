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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.marathon.fx.objectmap.IObjectMapTreeItem;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

public class OMapComponent implements IObjectMapTreeItem, Serializable {

    public static final Logger LOGGER = Logger.getLogger(OMapComponent.class.getName());

    private static final long serialVersionUID = 1L;
    private String name;
    private List<OMapProperty> generalProperties = new ArrayList<>();
    private List<OMapRecognitionProperty> componentRecognitionProperties = new ArrayList<>();
    private transient OMapContainer parent;
    private long created;
    private long modified;
    private boolean entryNeeded = false;
    private boolean used = true;

    public static final List<String> LAST_RESORT_NAMING_PROPERTIES = new ArrayList<String>();
    public static final List<String> LAST_RESORT_RECOGNITION_PROPERTIES = new ArrayList<String>();

    static {
        OMapComponent.LAST_RESORT_NAMING_PROPERTIES.add("tagName");
        OMapComponent.LAST_RESORT_NAMING_PROPERTIES.add("indexOfType");
        OMapComponent.LAST_RESORT_RECOGNITION_PROPERTIES.add("tagName");
        OMapComponent.LAST_RESORT_RECOGNITION_PROPERTIES.add("indexOfType");
    }

    public OMapComponent(OMapContainer parent) {
        this.parent = parent;
    }

    public OMapComponent() {
        this(null);
    }

    @Override
    public void setRootNode(IObjectMapTreeItem rootNode) {
        this.parent = (OMapContainer) rootNode;
    }

    public OMapContainer getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMatched(IPropertyAccessor pa) {
        for (OMapRecognitionProperty rp : componentRecognitionProperties) {
            if (!rp.isMatch(pa)) {
                return false;
            }
        }
        return true;
    }

    public boolean isMatched(IPropertyAccessor mComponent, List<String> rprops) {
        for (String prop : rprops) {
            String cval = mComponent.getProperty(prop);
            String rval = findProperty(prop);
            if (cval == null || rval == null || !rval.equals(cval)) {
                return false;
            }
        }
        return true;
    }

    public List<OMapProperty> getGeneralProperties() {
        return generalProperties;
    }

    public void setGeneralProperties(List<OMapProperty> generalProperties) {
        this.generalProperties = generalProperties;
    }

    public String findProperty(String property) {
        for (OMapProperty p : generalProperties) {
            if (p.getName().equals(property)) {
                return p.getValue();
            }
        }
        return null;
    }

    public List<OMapRecognitionProperty> getComponentRecognitionProperties() {
        return componentRecognitionProperties;
    }

    public void setComponentRecognitionProperties(List<OMapRecognitionProperty> componentRecognitionProperties) {
        this.componentRecognitionProperties = componentRecognitionProperties;
    }

    public void addComponentRecognitionProperty(OMapRecognitionProperty property) {
        componentRecognitionProperties.add(property);
    }

    public void removeComponentRecognitionProperty(OMapRecognitionProperty property) {
        componentRecognitionProperties.remove(property);
    }

    public boolean withLastResortProperties() {
        for (OMapRecognitionProperty p : componentRecognitionProperties) {
            if (!LAST_RESORT_RECOGNITION_PROPERTIES.contains(p.getName())) {
                return false;
            }
        }
        return true;
    }

    public void markEntryNeeded(boolean b) {
        this.entryNeeded = b;
    }

    public boolean isEntryNeeded() {
        return this.entryNeeded;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getCreated() {
        return created;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public long getModified() {
        return modified;
    }
}
