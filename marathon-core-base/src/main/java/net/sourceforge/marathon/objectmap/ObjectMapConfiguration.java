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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;

import net.sourceforge.marathon.runtime.api.Constants;

public class ObjectMapConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;

    public static Logger LOGGER = Logger.getLogger(ObjectMapConfiguration.class.getName());

    public static class PropertyList implements Serializable {
        private static final long serialVersionUID = 1L;

        private List<String> properties;
        private int priority;

        public PropertyList() {
        }

        public List<String> getProperties() {
            return properties;
        }

        public void setProperties(List<String> properties) {
            this.properties = properties;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public static PropertyList create(int priority, String... properties) {
            PropertyList pl = new PropertyList();
            pl.priority = priority;
            pl.properties = Arrays.asList(properties);
            return pl;
        }

        @Override public String toString() {
            return "PropertyList [properties=" + properties + ", priority=" + priority + "]";
        }

    }

    public static class ObjectIdentity implements Serializable {
        private static final long serialVersionUID = 1L;

        private String className;
        private List<PropertyList> propertyLists;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public List<PropertyList> getPropertyLists() {
            return propertyLists;
        }

        public void setPropertyLists(List<PropertyList> propertyLists) {
            this.propertyLists = propertyLists;
        }

        public void addPropertyList(PropertyList pl) {
            if (propertyLists == null) {
                propertyLists = new ArrayList<PropertyList>();
            }
            propertyLists.add(pl);
        }

        @Override public String toString() {
            return "ObjectIdentity [className=" + className + ", propertyLists=" + propertyLists + "]";
        }

    }

    private List<ObjectIdentity> namingProperties;
    private List<ObjectIdentity> recognitionProperties;
    private List<String> generalProperties;
    private List<ObjectIdentity> containerNamingProperties;
    private List<ObjectIdentity> containerRecognitionProperties;

    public List<ObjectIdentity> getNamingProperties() {
        return namingProperties;
    }

    public void setNamingProperties(List<ObjectIdentity> namingProperties) {
        this.namingProperties = namingProperties;
    }

    public List<ObjectIdentity> getRecognitionProperties() {
        return recognitionProperties;
    }

    public void setRecognitionProperties(List<ObjectIdentity> recognitionProperties) {
        this.recognitionProperties = recognitionProperties;
    }

    public List<String> getGeneralProperties() {
        return generalProperties;
    }

    public void setGeneralProperties(List<String> generalProperties) {
        this.generalProperties = generalProperties;
    }

    public List<ObjectIdentity> getContainerNamingProperties() {
        return containerNamingProperties;
    }

    public void setContainerNamingProperties(List<ObjectIdentity> containerNamingProperties) {
        this.containerNamingProperties = containerNamingProperties;
    }

    public List<ObjectIdentity> getContainerRecognitionProperties() {
        return containerRecognitionProperties;
    }

    public void setContainerRecognitionProperties(List<ObjectIdentity> containerRecognitionProperties) {
        this.containerRecognitionProperties = containerRecognitionProperties;
    }

    public void createDefault() {
        createDefault(Constants.getFramework());
    }
    
    public void createDefault(String framework) {
        new Exception("Creating Object Map").printStackTrace();
        LOGGER.info("Creating a default object map configuration. Loading from stream...");
        Reader reader = new InputStreamReader(Constants.getOMapConfigurationStream(framework));
        load(reader);
        try {
            reader.close();
        } catch (IOException e) {
        }
    }

    public void load() throws IOException {
        try {
            FileReader reader = new FileReader(getConfigFile());
            load(reader);
        } catch (IOException e) {
            createDefault();
            save();
            FileReader reader = new FileReader(getConfigFile());
            load(reader);
        }
    }

    public void save() throws IOException {
        save(new FileWriter(getConfigFile()));
    }

    public void save(Writer writer) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.AUTO);
        options.setIndent(4);
        Representer representer = new Representer();
        representer.getPropertyUtils().setBeanAccess(BeanAccess.DEFAULT);
        new Yaml(options).dump(this, writer);
    }

    public static File getConfigFile() {
        return new File(System.getProperty(Constants.PROP_PROJECT_DIR),
                System.getProperty(Constants.PROP_OMAP_CONFIGURATION_FILE, Constants.FILE_OMAP_CONFIGURATION));
    }

    public void load(Reader reader) {
        ObjectMapConfiguration configuration = new Yaml().loadAs(reader, ObjectMapConfiguration.class);
        namingProperties = configuration.namingProperties;
        containerNamingProperties = configuration.containerNamingProperties;
        recognitionProperties = configuration.recognitionProperties;
        containerRecognitionProperties = configuration.containerRecognitionProperties;
        generalProperties = configuration.generalProperties;
    }

    @Override public String toString() {
        return "ObjectMapConfiguration [namingProperties=" + namingProperties + ", recognitionProperties=" + recognitionProperties
                + ", generalProperties=" + generalProperties + ", containerNamingProperties=" + containerNamingProperties
                + ", containerRecognitionProperties=" + containerRecognitionProperties + "]";
    }

}
