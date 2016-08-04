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

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.representer.Representer;

import net.sourceforge.marathon.fx.objectmap.IObjectMapTreeItem;
import net.sourceforge.marathon.fx.objectmap.ObjectMapItem;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

public class OMapContainer implements IObjectMapTreeItem {

    private List<String> containerTitles;
    private List<OMapProperty> containerGeneralProperties;
    private List<OMapComponent> components;
    private List<OMapRecognitionProperty> containerRecognitionProperties;
    private Map<String, OMapComponent> nameComponentMap;
    private String fileName;
    private boolean loaded;

    private static final Logger logger = Logger.getLogger(OMapContainer.class.getName());
    private ObjectMapItem parent;

    public OMapContainer(ObjectMapItem parent, String title) {
        this.parent = parent;
        containerTitles = new ArrayList<>();
        components = new ArrayList<OMapComponent>();
        nameComponentMap = new HashMap<String, OMapComponent>();
        if (this.parent != null) {
            fileName = createFileName(title);
            loaded = true;
        }
    }

    public OMapContainer() {
        this(null, "");
    }

    private String createFileName(String title) {
        try {
            if (title.length() < 3) {
                title = title + "___";
            } else if (title.length() > 64) {
                title = title.substring(0, 64);
            }
            return File.createTempFile(sanitize(title) + "_", ".yaml", Constants.omapDirectory()).getName();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String sanitize(String title) {
        StringBuilder sb = new StringBuilder();
        char[] cs = title.toCharArray();
        CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
        for (char c : cs) {
            if (!valid(c) || !asciiEncoder.canEncode(c)) {
                c = unaccent(c);
            }
            sb.append(c);

        }
        return sb.toString();
    }

    private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"',
            ':' };

    private boolean valid(char c) {
        for (char ic : ILLEGAL_CHARACTERS) {
            if (c == ic) {
                return false;
            }
        }
        return true;
    }

 // @formatter:off
    private static final String PLAIN_ASCII =
            "AaEeIiOoUu"    // grave
          + "AaEeIiOoUuYy"  // acute
          + "AaEeIiOoUuYy"  // circumflex
          + "AaOoNn"        // tilde
          + "AaEeIiOoUuYy"  // umlaut
          + "Aa"            // ring
          + "Cc"            // cedilla
          + "OoUu"          // double acute
          ;

   private static final String UNICODE =
           "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
          + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
          + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
          + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
          + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
          + "\u00C5\u00E5"
          + "\u00C7\u00E7"
          + "\u0150\u0151\u0170\u0171"
          ;

   // @formatter:on

    private char unaccent(char c) {
        int pos = UNICODE.indexOf(c);
        if (pos > -1) {
            return PLAIN_ASCII.charAt(pos);
        }
        return '_';
    }

    @Override public void setRootNode(IObjectMapTreeItem parentNode) {
        this.parent = (ObjectMapItem) parentNode;
    }

    @SuppressWarnings("unchecked") public void load() throws FileNotFoundException {
        if (loaded) {
            return;
        }
        logger.info("Loading container from " + fileName);
        components = (List<OMapComponent>) loadYaml(new File(Constants.omapDirectory(), fileName));
        if (components == null) {
            components = new ArrayList<OMapComponent>();
        }
        for (OMapComponent component : components) {
            component.setRootNode(this);
            component.markEntryNeeded(true);
        }
        createMap();
        loaded = true;
    }

    private void createMap() {
        nameComponentMap = new HashMap<String, OMapComponent>();
        for (OMapComponent omapComponent : components) {
            nameComponentMap.put(omapComponent.getName(), omapComponent);
        }
    }

    public boolean isMatched(IPropertyAccessor pa) {
        for (OMapRecognitionProperty rp : containerRecognitionProperties) {
            if (!rp.isMatch(pa)) {
                return false;
            }
        }
        return true;
    }

    public OMapComponent insertNameForComponent(String name, IPropertyAccessor w, List<String> rprops,
            List<List<String>> rproperties, List<List<String>> nproperties, List<String> gproperties) {
        OMapComponent omapComponent = new OMapComponent(this);
        omapComponent.setName(name);
        List<OMapRecognitionProperty> omapRProps = new ArrayList<OMapRecognitionProperty>();
        for (String rprop : rprops) {
            OMapRecognitionProperty rproperty = new OMapRecognitionProperty();
            rproperty.setName(rprop);
            rproperty.setMethod(IPropertyAccessor.METHOD_EQUALS);
            rproperty.setValue(w.getProperty(rprop));
            omapRProps.add(rproperty);
        }
        omapComponent.setComponentRecognitionProperties(omapRProps);
        List<OMapProperty> others = new ArrayList<OMapProperty>();
        List<String> otherProps = flattenLists(rprops, rproperties, nproperties, gproperties);
        for (String otherProp : otherProps) {
            String v = w.getProperty(otherProp);
            if (v != null && !"".equals(v)) {
                OMapProperty p = new OMapProperty();
                p.setName(otherProp);
                p.setValue(v);
                others.add(p);
            }
        }
        omapComponent.setGeneralProperties(others);
        OMapComponent existing = nameComponentMap.get(name);
        if (existing == null) {
            components.add(omapComponent);
            nameComponentMap.put(name, omapComponent);
        } else {
            components.remove(existing);
            components.add(omapComponent);
            nameComponentMap.put(name, omapComponent);
        }
        return omapComponent;
    }

    private List<String> flattenLists(List<String> rpropList, List<List<String>> rproperties, List<List<String>> nproperties,
            List<String> gproperties) {
        Set<String> props = new HashSet<String>();
        for (String prop : rpropList) {
            props.add(prop);
        }
        for (List<String> nprops : nproperties) {
            for (String nprop : nprops) {
                props.add(nprop);
            }
        }
        for (List<String> rprops : rproperties) {
            for (String rprop : rprops) {
                props.add(rprop);
            }
        }
        for (String gprop : gproperties) {
            props.add(gprop);
        }
        for (String prop : OMapComponent.LAST_RESORT_RECOGNITION_PROPERTIES) {
            props.add(prop);
        }
        for (String prop : OMapComponent.LAST_RESORT_NAMING_PROPERTIES) {
            props.add(prop);
        }
        props.add("instanceOf");
        props.add("component.class.name");
        props.add("oMapClassName");
        props.add("component.class.simpleName");
        return new ArrayList<String>(props);
    }

    private Object loadYaml(File file) throws FileNotFoundException {
        FileReader reader = new FileReader(file);
        try {
            Constructor constructor = new Constructor();
            PropertyUtils putils = new PropertyUtils();
            putils.setSkipMissingProperties(true);
            constructor.setPropertyUtils(putils);
            Yaml yaml = new Yaml(constructor);
            return yaml.load(reader);
        } catch (Throwable t) {
            throw new RuntimeException("Error loading yaml from: " + file.getAbsolutePath() + "\n" + t.getMessage(), t);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public OMapComponent findComponentByProperties(IPropertyAccessor w, List<String> rprops) {
        List<OMapComponent> matched = new ArrayList<OMapComponent>();
        for (OMapComponent omapComponent : components) {
            if (omapComponent.isMatched(w, rprops)) {
                matched.add(omapComponent);
            }
        }
        if (matched.size() == 1) {
            return matched.get(0);
        } else if (matched.size() == 0) {
            return null;
        } else if (matched.size() == 2) {
            if (matched.get(0).withLastResortProperties()) {
                return matched.get(1);
            }
            if (matched.get(1).withLastResortProperties()) {
                return matched.get(0);
            }
        }
        return null;
    }

    public void updateComponent(OMapComponent omapComponent, List<String> rprops) {
        String name = omapComponent.getName();
        omapComponent = findComponentByName(omapComponent.getName());
        if (omapComponent == null) {
            logger.warning("updateComponent: unable to find omap component for: " + name);
            return;
        }
        List<OMapRecognitionProperty> omapRProps = new ArrayList<OMapRecognitionProperty>();
        for (String rprop : rprops) {
            OMapRecognitionProperty rproperty = new OMapRecognitionProperty();
            rproperty.setName(rprop);
            rproperty.setMethod(IPropertyAccessor.METHOD_EQUALS);
            rproperty.setValue(omapComponent.findProperty(rprop));
            omapRProps.add(rproperty);
        }
        omapComponent.setComponentRecognitionProperties(omapRProps);
    }

    public List<OMapComponent> getChildren() {
        return components;
    }

    public Enumeration<IObjectMapTreeItem> children() {
        return new Enumeration<IObjectMapTreeItem>() {
            int index = 0;

            @Override public boolean hasMoreElements() {
                return index < components.size();
            }

            @Override public IObjectMapTreeItem nextElement() {
                return components.get(index++);
            }
        };
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public List<OMapRecognitionProperty> getContainerRecognitionProperties() {
        return containerRecognitionProperties;
    }

    public void setContainerRecognitionProperties(List<OMapRecognitionProperty> containerRecognitionProperties) {
        this.containerRecognitionProperties = containerRecognitionProperties;
    }

    public void addContainerRecognitionProperty(OMapRecognitionProperty property) {
        containerRecognitionProperties.add(property);
    }

    public List<OMapProperty> getContainerGeneralProperties() {
        return containerGeneralProperties;
    }

    public void setContainerGeneralProperties(List<OMapProperty> containerGeneralProperties) {
        this.containerGeneralProperties = containerGeneralProperties;
    }

    public List<String> getContainerTitles() {
        return containerTitles;
    }

    public void setContainerTitles(List<String> containerTitles) {
        this.containerTitles = containerTitles;
    }

    public String findProperty(String property) {
        for (OMapProperty p : containerGeneralProperties) {
            if (p.getName().equals(property)) {
                return p.getValue();
            }
        }
        return null;
    }

    public void removeComponent(OMapComponent omapComponent) {
        String name = omapComponent.getName();
        omapComponent = findComponentByName(omapComponent.getName());
        if (omapComponent == null) {
            logger.warning("updateComponent: unable to find omap component for: " + name);
            return;
        }
        components.remove(omapComponent);
    }

    public OMapComponent findComponentByName(String name) {
        return nameComponentMap.get(name);
    }

    public void addTitle(String title) {
        if (containerTitles.contains(title)) {
            return;
        }
        containerTitles.add(title);
    }

    public void add(OMapComponent oc) {
        components.add(oc);
        nameComponentMap.put(oc.getName(), oc);
    }

    public int getChildCount() {
        return components.size();
    }

    public void save() throws IOException {
        logger.info("Saving object map container " + containerRecognitionProperties);
        File file = new File(Constants.omapDirectory(), fileName);
        if (components.size() == 0) {
            if (loaded) {
                logger.info("Nothing to save. Removing the file... " + file.getName());
                file.delete();
            } else {
                logger.info("Nothing to save. Skipping the file... " + file.getName());
            }
            return;
        }
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
        new Yaml(representer, options).dump(getUsed(components), new FileWriter(file));
    }

    private List<OMapComponent> getUsed(List<OMapComponent> components) {
        List<OMapComponent> usedComponents = new ArrayList<OMapComponent>();
        for (OMapComponent oMapComponent : components) {
            if (oMapComponent.isEntryNeeded()) {
                usedComponents.add(oMapComponent);
            }
        }
        return usedComponents;
    }

    public void deleteFile() {
        File file = new File(Constants.omapDirectory(), fileName);
        logger.info("Deleting container file " + file);
        file.delete();
    }

    public OMapComponent insertNameForComponent(String name, Properties urp, Properties properties) {
        OMapComponent omapComponent = new OMapComponent(this);
        omapComponent.setName(name);
        List<OMapRecognitionProperty> omapRProps = new ArrayList<OMapRecognitionProperty>();
        for (Object rprop : urp.keySet()) {
            OMapRecognitionProperty rproperty = new OMapRecognitionProperty();
            rproperty.setName(rprop.toString());
            rproperty.setMethod(IPropertyAccessor.METHOD_EQUALS);
            rproperty.setValue(urp.getProperty(rprop.toString()));
            omapRProps.add(rproperty);
        }
        omapComponent.setComponentRecognitionProperties(omapRProps);
        List<OMapProperty> others = new ArrayList<OMapProperty>();
        for (Object otherProp : properties.keySet()) {
            String v = properties.getProperty(otherProp.toString());
            if (v != null && !"".equals(v)) {
                OMapProperty p = new OMapProperty();
                p.setName(otherProp.toString());
                p.setValue(v);
                others.add(p);
            }
        }
        omapComponent.setGeneralProperties(others);
        OMapComponent existing = nameComponentMap.get(name);
        if (existing == null) {
            components.add(omapComponent);
            nameComponentMap.put(name, omapComponent);
        } else {
            components.remove(existing);
            components.add(omapComponent);
            nameComponentMap.put(name, omapComponent);
        }
        return omapComponent;
    }

    public OMapComponent findComponentByProperties(IPropertyAccessor w) throws ObjectMapException {
        List<OMapComponent> matched = findComponentsByProperties(w);
        if (matched.size() == 1) {
            return matched.get(0);
        } else if (matched.size() == 0) {
            return null;
        } else if (matched.size() == 2) {
            if (matched.get(0).withLastResortProperties()) {
                return matched.get(1);
            }
            if (matched.get(1).withLastResortProperties()) {
                return matched.get(0);
            }
        }
        throw new ObjectMapException("More than one component matched: " + matched + " Looking for: " + w, matched);
    }

    public List<OMapComponent> findComponentsByProperties(IPropertyAccessor w) {
        List<OMapComponent> matched = new ArrayList<OMapComponent>();
        for (OMapComponent omapComponent : components) {
            if (omapComponent.isMatched(w)) {
                matched.add(omapComponent);
            }
        }
        return matched;
    }

    public String[] findComponentNames() {
        Set<String> keySet = nameComponentMap.keySet();
        return keySet.toArray(new String[keySet.size()]);
    }

    public void removeUnused() {
        Iterator<OMapComponent> iterator = components.iterator();
        while (iterator.hasNext()) {
            OMapComponent component = iterator.next();
            if (!component.isUsed()) {
                iterator.remove();
            }
        }
    }

    public List<String> getUsedRecognitionProperties() {
        List<String> rprops = new ArrayList<String>();
        for (OMapRecognitionProperty p : containerRecognitionProperties) {
            rprops.add(p.getName());
        }
        return rprops;
    }

}
