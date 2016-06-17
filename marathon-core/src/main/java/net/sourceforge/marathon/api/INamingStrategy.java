/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.api;

import net.sourceforge.marathon.objectmap.ObjectMapException;
import net.sourceforge.marathon.runtime.api.ComponentId;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

import org.json.JSONException;
import org.json.JSONObject;

public interface INamingStrategy {

    void save();

    String getName(JSONObject query, String name) throws JSONException, ObjectMapException;

    String getContainerName(JSONObject container) throws JSONException, ObjectMapException;

    void setTopLevelComponent(IPropertyAccessor driver);

    String[] toCSS(ComponentId componentId, boolean visibility) throws ObjectMapException;

    void setDirty();

    String[] getComponentNames() throws ObjectMapException;

}
