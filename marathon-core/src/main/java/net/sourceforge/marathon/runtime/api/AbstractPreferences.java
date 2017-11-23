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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.io.Files;

public abstract class AbstractPreferences {

    public static final Logger LOGGER = Logger.getLogger(AbstractPreferences.class.getName());

    private JSONObject prefs;

    private Map<String, Set<IPreferenceChangeListener>> listeners = new HashMap<>();
    protected File mpd;

    private String fileName;

    public AbstractPreferences(String fileName) {
        this(fileName, Constants.getMarathonProjectDirectory());
        this.fileName = fileName;
    }

    public AbstractPreferences(String fileName, File mpd) {
        this.fileName = fileName;
        this.mpd = mpd;
        prefs = getProjectAbstractPreferences();
    }

    public JSONObject getSection(String section) {
        if (!prefs.has(section)) {
            prefs.put(section, new JSONObject());
        }
        return prefs.getJSONObject(section);
    }

    public void saveSection(String section, JSONObject o) {
        prefs.put(section, o);
        save(section);
    }

    private JSONObject getProjectAbstractPreferences() {
        File preferenceFile = new File(mpd, fileName);
        if (preferenceFile.exists()) {
            try {
                return new JSONObject(Files.asCharSource(preferenceFile, Charset.forName("utf-8")).read());
            } catch (JSONException e) {
            } catch (IOException e) {
            }
        }
        return new JSONObject();
    }

    public void save(String section) {
        File preferenceFile = new File(mpd, fileName);
        try {
            Files.write(prefs.toString(2).getBytes(), preferenceFile);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        firePreferenceChange(section);
    }

    private void firePreferenceChange(String section) {
        fire(section);
        fire("all");
    }

    public void fire(String section) {
        Set<IPreferenceChangeListener> sectionListeners = listeners.get(section);
        if (sectionListeners == null) {
            return;
        }
        for (IPreferenceChangeListener l : sectionListeners) {
            l.preferencesChanged(section, section.equals("all") ? null : getSection(section));
        }
    }

    public void addPreferenceChangeListener(String section, IPreferenceChangeListener l) {
        Set<IPreferenceChangeListener> list = listeners.get(section);
        if (list == null) {
            list = new HashSet<>();
            listeners.put(section, list);
        }
        list.add(l);
    }

    @SuppressWarnings("unchecked") public <T> T getValue(String section, String property, T defaultValue) {
        JSONObject oSection = getSection(section);
        Object value = oSection.opt(property);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    public <T> T getValue(String section, String property) {
        return getValue(section, property, null);
    }

    public void setValue(String section, String property, Object value) {
        JSONObject oSection = getSection(section);
        if (value == null)
            oSection.remove(property);
        else
            oSection.put(property, value);
        save(section);
    }

    public void resetInstance(AbstractPreferences oldInstance) {
        listeners = oldInstance.listeners;
        String[] names = JSONObject.getNames(prefs);
        if (names != null) {
            for (String section : names) {
                firePreferenceChange(section);
            }
        }
    }
}
