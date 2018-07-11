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
package net.sourceforge.marathon.javarecorder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONOMapConfig {

    public static final Logger LOGGER = Logger.getLogger(JSONOMapConfig.class.getName());

    private JSONObject config;
    private Collection<String> allProperties;

    public JSONOMapConfig(JSONObject o) {
        this.config = o;
    }

    public Collection<String> findProperties() {
        if (allProperties == null) {
            allProperties = collectProperties();
        }
        return allProperties;
    }

    private Set<String> collectProperties() {
        JSONArray gps = config.getJSONArray("generalProperties");
        Set<String> allProps = new HashSet<String>();
        for (int i = 0; i < gps.length(); i++) {
            allProps.add(gps.getString(i));
        }
        collectProperties(allProps, config.getJSONArray("recognitionProperties"));
        collectProperties(allProps, config.getJSONArray("namingProperties"));
        collectProperties(allProps, config.getJSONArray("containerNamingProperties"));
        collectProperties(allProps, config.getJSONArray("containerRecognitionProperties"));
        allProps.add("type");
        allProps.add("tagName");
        allProps.add("indexOfType");
        return allProps;
    }

    private void collectProperties(Set<String> allProps, JSONArray rps) {
        for (int i = 0; i < rps.length(); i++) {
            JSONObject rp = rps.getJSONObject(i);
            JSONArray pls = rp.getJSONArray("propertyLists");
            for (int j = 0; j < pls.length(); j++) {
                JSONObject pl = pls.getJSONObject(j);
                JSONArray ps = pl.getJSONArray("properties");
                for (int k = 0; k < ps.length(); k++) {
                    allProps.add(ps.getString(k));
                }
            }
        }
    }

    public List<List<String>> findRP(Class<?> componentClass) {
        return findProperties(componentClass, "recognitionProperties");
    }

    private List<List<String>> findProperties(Class<?> componentClass, String key) {
        List<JSONObject> selected = new ArrayList<JSONObject>();
        JSONArray namingProperties = config.getJSONArray(key);
        for (int i = 0; i < namingProperties.length(); i++) {
            JSONObject props = namingProperties.getJSONObject(i);
            String className = props.getString("className");
            try {
                Class<?> klass = Class.forName(className);
                if (!klass.isAssignableFrom(componentClass)) {
                    continue;
                }
                JSONArray plists = props.getJSONArray("propertyLists");
                for (int j = 0; j < plists.length(); j++) {
                    JSONObject o = plists.getJSONObject(j);
                    o.put("class", klass);
                    selected.add(o);
                }
            } catch (ClassNotFoundException e) {
            }
        }
        Collections.sort(selected, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                int o2prio = o2.getInt("priority");
                int o1prio = o1.getInt("priority");
                if (o1prio == o2prio) {
                    Class<?> o1class = (Class<?>) o1.get("class");
                    Class<?> o2class = (Class<?>) o2.get("class");
                    if (o1class.isAssignableFrom(o2class)) {
                        return 1;
                    }
                    return -1;
                }
                return o2prio - o1prio;
            }
        });
        ArrayList<List<String>> r = new ArrayList<List<String>>();
        for (JSONObject o : selected) {
            List<String> item = new ArrayList<String>();
            JSONArray props = o.getJSONArray("properties");
            for (int i = 0; i < props.length(); i++) {
                item.add(props.getString(i));
            }
            r.add(item);
        }
        return r;
    }

    public List<List<String>> findContainerRP(Class<?> containerClass) {
        return findProperties(containerClass, "containerRecognitionProperties");
    }

    public List<List<String>> findContainerNP(Class<?> containerClass) {
        return findProperties(containerClass, "containerNamingProperties");
    }

    public List<List<String>> findNP(Class<?> componentClass) {
        return findProperties(componentClass, "namingProperties");
    }

}
