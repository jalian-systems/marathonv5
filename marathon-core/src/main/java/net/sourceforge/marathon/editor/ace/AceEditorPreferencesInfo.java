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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class AceEditorPreferencesInfo {

    private List<AceEditorTheme> themes = new ArrayList<>();
    private JSONObject options;

    public AceEditorPreferencesInfo(ACEEditor aceEditor) {
        JSONArray o = aceEditor.getThemes().getJSONArray("themes");
        for (int i = 0; i < o.length(); i++) {
            themes.add(new AceEditorTheme(o.getJSONObject(i)));
        }
        options = aceEditor.getOptions();
    }

    public List<AceEditorTheme> getThemes() {
        return themes;
    }

    public AceEditorTheme getSelectedTheme() {
        String selectedTheme = options.getString("theme");
        if (selectedTheme == null) {
            return null;
        }
        for (AceEditorTheme aceEditorTheme : themes) {
            if (aceEditorTheme.getTheme().equals(selectedTheme)) {
                return aceEditorTheme;
            }
        }
        return null;
    }

    public String getKeyboardHandler() {
        return options.getString("keyboardHandler");
    }

    public int getTabSize() {
        return options.getInt("tabSize");
    }

    public boolean getTabConversion() {
        return options.getBoolean("tabConversion");
    }

    public boolean getShowLineNumbers() {
        return options.getBoolean("showLineNumbers");
    }

    public String getFontSize() {
        return options.getString("fontSize");
    }

}
