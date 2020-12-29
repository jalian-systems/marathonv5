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
package net.sourceforge.marathon.fx.display;

import java.util.logging.Logger;

import net.sourceforge.marathon.json.JSONObject;
import net.sourceforge.marathon.runtime.api.Preferences;

public class MarathonPreferencesInfo {

    public static final Logger LOGGER = Logger.getLogger(MarathonPreferencesInfo.class.getName());

    private boolean hideBlurb;
    private Preferences preferences = Preferences.instance();
    private JSONObject prefs = preferences.getSection("marathon");
    private boolean needRefresh = true;
    private String mouseTriggerText;
    private String keyTriggerText;

    public MarathonPreferencesInfo(boolean hideBlurb) {
        this.hideBlurb = hideBlurb;
    }

    public void setHideBlurb(boolean hideBlurb) {
        this.hideBlurb = hideBlurb;
    }

    public boolean isHideBlurb() {
        return hideBlurb;
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    public boolean isNeedRefresh() {
        return needRefresh;
    }

    public JSONObject getPreferences() {
        return prefs;
    }

    public void setMouseTriggerText(String mouseTriggerText) {
        this.mouseTriggerText = mouseTriggerText;
    }

    public String getMouseTriggerText() {
        return mouseTriggerText;
    }

    public void setKeyTriggerText(String keyTriggerText) {
        this.keyTriggerText = keyTriggerText;
    }

    public String getKeyTriggerText() {
        return keyTriggerText;
    }

    public void save() {
        preferences.save("marathon");
    }
}
