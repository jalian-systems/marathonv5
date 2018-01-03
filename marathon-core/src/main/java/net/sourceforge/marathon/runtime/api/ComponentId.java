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
package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.jruby.runtime.builtin.IRubyObject;

public class ComponentId implements Serializable {

    public static final Logger LOGGER = Logger.getLogger(ComponentId.class.getName());

    private static final String INFO_KEY = ComponentId.class.getName() + ".info";
    private static final String NAME_KEY = ComponentId.class.getName() + ".name";
    private static final long serialVersionUID = 1L;
    private final Properties nameProps = new Properties();
    private final Properties componentInfoProps = new Properties();

    private IRubyObject webElement = null;

    public ComponentId(String name) {
        this(name, null);
    }

    public ComponentId(Object name, Object componentInfo) {
        if (name instanceof String) {
            nameProps.put(NAME_KEY, name);
        } else if (name instanceof Map) {
            @SuppressWarnings("unchecked")
            Set<Entry<Object, Object>> values = ((Map<Object, Object>) name).entrySet();
            for (Entry<Object, Object> entry : values) {
                nameProps.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } else if (name instanceof IRubyObject) {
            if (componentInfo != null)
                throw new RuntimeException("Invalid component id");
            webElement = (IRubyObject) name;
        } else {
            throw new RuntimeException("Invalid component id");
        }
        if (componentInfo != null) {
            if (componentInfo instanceof String) {
                componentInfoProps.put(INFO_KEY, componentInfo);
            } else if (componentInfo instanceof Map) {
                @SuppressWarnings("unchecked")
                Set<Entry<Object, Object>> values = ((Map<Object, Object>) componentInfo).entrySet();
                for (Entry<Object, Object> entry : values) {
                    componentInfoProps.put(entry.getKey().toString(), entry.getValue().toString());
                }
            } else {
                throw new RuntimeException("Invalid component id");
            }
        }
    }

    public String getName() {
        return nameProps.getProperty(NAME_KEY);
    }

    public String getComponentInfo() {
        return componentInfoProps.getProperty(INFO_KEY);
    }

    @Override public String toString() {
        return "('" + nameProps + "'" + (componentInfoProps != null ? ", '" + componentInfoProps + "'" : "") + ")";
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((componentInfoProps == null) ? 0 : componentInfoProps.hashCode());
        result = prime * result + ((nameProps == null) ? 0 : nameProps.hashCode());
        result = prime * result + ((webElement == null) ? 0 : webElement.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComponentId other = (ComponentId) obj;
        if (componentInfoProps == null) {
            if (other.componentInfoProps != null)
                return false;
        } else if (!componentInfoProps.equals(other.componentInfoProps))
            return false;
        if (nameProps == null) {
            if (other.nameProps != null)
                return false;
        } else if (!nameProps.equals(other.nameProps))
            return false;
        if (webElement == null) {
            if (other.webElement != null)
                return false;
        } else if (!webElement.equals(other.webElement))
            return false;
        return true;
    }

    public Properties getComponentInfoProps() {
        if (componentInfoProps.size() == 0) {
            return null;
        }
        if (componentInfoProps.size() > 1) {
            return componentInfoProps;
        }
        return componentInfoProps.get(INFO_KEY) == null ? componentInfoProps : null;
    }

    public Properties getNameProps() {
        if (componentInfoProps.size() > 1) {
            return nameProps;
        }
        return nameProps.get(NAME_KEY) == null ? nameProps : null;
    }

    public IRubyObject getWebElement() {
        return webElement;
    }
}
