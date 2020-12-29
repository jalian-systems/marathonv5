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
package net.sourceforge.marathon.editor.ace;

import java.util.logging.Logger;

import net.sourceforge.marathon.json.JSONObject;

public class AceEditorTheme {

    public static final Logger LOGGER = Logger.getLogger(AceEditorTheme.class.getName());

    private boolean dark;
    private String name;
    private String caption;
    private String theme;

    public AceEditorTheme(JSONObject object) {
        setDark(object.getBoolean("isDark"));
        setName(object.getString("name"));
        setCaption(object.getString("caption"));
        setTheme(object.getString("theme"));
    }

    public boolean isDark() {
        return dark;
    }

    public void setDark(boolean dark) {
        this.dark = dark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (theme == null ? 0 : theme.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AceEditorTheme other = (AceEditorTheme) obj;
        if (theme == null) {
            if (other.theme != null) {
                return false;
            }
        } else if (!theme.equals(other.theme)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return caption + " (" + (isDark() ? "Dark)" : "Light)");
    }
}
