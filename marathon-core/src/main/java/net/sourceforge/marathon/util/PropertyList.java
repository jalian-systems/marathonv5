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
package net.sourceforge.marathon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PropertyList {
    
    public static final Logger LOGGER = Logger.getLogger(PropertyList.Property.class.getName());

    public static class Property {
        private String key;
        private SimpleStringProperty propertyName;
        private SimpleStringProperty value;
        private Class<?> klass;
        private String[] items;

        Property(String key, String pName, String pValue, Class<?> klass) {
            this.key = key;
            this.propertyName = new SimpleStringProperty(pName);
            this.value = new SimpleStringProperty(pValue);
            this.klass = klass;
        }

        public Property(String key, String description, String value, Class<String[]> klass, String[] items) {
            this(key, description, value, klass);
            this.items = items;
        }

        public String getKey() {
            return key;
        }

        public String getPropertyName() {
            return propertyName.get();
        }

        public String getValue() {
            return value.get();
        }

        public Class<?> getKlass() {
            return klass;
        }

        public void setValue(String pValue) {
            this.value.set(pValue);
        }

        public String[] getItems() {
            return items;
        }
    }

    private ArrayList<Property> propList;

    public PropertyList() {
        propList = new ArrayList<Property>();
    }

    private void addProperty(String key, String description, String value, Class<?> klass) {
        propList.add(new Property(key, description, value, klass));
    }

    public void addColorProperty(String key, String description, Color color) {
        String value = String.format("#%02x%02x%02x", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        addProperty(key, description, value, Color.class);
    }

    public void addStringProperty(String key, String description, String value) {
        addProperty(key, description, value, String.class);
    }

    public void addFontProperty(String key, String description, Font font) {
        addProperty(key, description, font.getName(), Font.class);
    }

    public Property getProperty(String key) {
        for (Property prop : propList) {
            if (prop.getKey().equals(key)) {
                return prop;
            }
        }
        return null;
    }

    public int getSize() {
        return propList.size();
    }

    public Property getProperty(int index) {
        return propList.get(index);
    }

    public void addIntegerProperty(String key, String description, int value) {
        addProperty(key, description, value + "", Integer.class);
    }

    public void addBooleanProperty(String key, String description, boolean value) {
        addProperty(key, description, value + "", Boolean.class);
    }

    public void addSelectionProperty(String key, String description, String value, String[] items) {
        addProperty(key, description, value, String[].class, items);
    }

    private void addProperty(String key, String description, String value, Class<String[]> klass, String[] items) {
        propList.add(new Property(key, description, value, klass, items));
    }

    public List<Property> getProperties() {
        return propList;
    }
}
