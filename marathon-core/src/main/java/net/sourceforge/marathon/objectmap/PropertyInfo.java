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

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PropertyInfo {

    private ObservableList<OMapComboBoxProperty> oMapProperties = FXCollections.observableArrayList();
    private String propertyName;
    private String[] methodOptions;
    private String value;
    private String methodOption;
    private OMapProperty property;

    public class OMapComboBoxProperty extends OMapProperty {
        private static final long serialVersionUID = 1L;
        private final OMapProperty property;

        OMapComboBoxProperty(OMapProperty property) {
            super.setName(property.getName());
            super.setValue(property.getValue());
            this.property = property;
        }

        @Override public String toString() {
            return property.getName();
        }
    }

    public PropertyInfo(List<OMapProperty> props, String propertyName, String[] methodOptions, String value) {
        for (OMapProperty prop : props) {
            oMapProperties.add(new OMapComboBoxProperty(prop));
        }
        this.propertyName = propertyName;
        this.methodOptions = methodOptions;
        this.value = value;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ObservableList<OMapComboBoxProperty> getProps() {
        return oMapProperties;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String[] getMethodOptions() {
        return methodOptions;
    }

    public String getValue() {
        return value;
    }

    public void setMethodOption(String methodOption) {
        this.methodOption = methodOption;
    }

    public String getMethodOption() {
        return methodOption;
    }

    public void setProperty(OMapProperty property) {
        this.property = property;
    }

    public OMapProperty getProperty() {
        return property;
    }

    public void addOMapProperty(OMapProperty prop) {
        oMapProperties.add(new OMapComboBoxProperty(prop));
    }
}
