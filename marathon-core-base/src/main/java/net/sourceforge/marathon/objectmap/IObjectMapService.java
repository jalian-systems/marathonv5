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
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.ObjectIdentity;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

public interface IObjectMapService {

    public abstract void save();

    public abstract void setDirty(boolean b);

    public abstract void load() throws IOException;

    public List<ObjectIdentity> getNamingProperties();

    public abstract OMapComponent findComponentByName(String name, IPropertyAccessor containerAccessor) throws ObjectMapException;

    public abstract List<OMapComponent> findComponentsByProperties(Properties attributes, Properties urpContainer,
            Properties attributesContainer) throws ObjectMapException;

    public abstract OMapComponent insertNameForComponent(String name, Properties urp, Properties properties,
            Properties urpContainer, Properties attributesContainer) throws ObjectMapException;

    public abstract OMapComponent findComponentByName(String name, Properties urpContainer, Properties attributesContainer)
            throws ObjectMapException;

    public abstract String[] findComponentNames(IPropertyAccessor topContainerAccessor) throws ObjectMapException;

    public abstract List<ObjectIdentity> getContainerNamingProperties();

    public Collection<String> findProperties();

    public abstract ObjectMapConfiguration getObjectMapConfiguration();

}
