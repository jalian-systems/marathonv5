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
import java.util.logging.Logger;

public class Preferences extends AbstractPreferences {

    public static final Logger LOGGER = Logger.getLogger(Preferences.class.getName());

    public static final String PREFERENCES_FILE = "project.json";

    private static Preferences _instance;

    public Preferences() {
        super(PREFERENCES_FILE);
    }

    public Preferences(File mpd) {
        super(PREFERENCES_FILE, mpd);
    }

    public static Preferences instance() {
        if (_instance == null)
            _instance = new Preferences();
        return _instance;
    }

    public static void resetInstance() {
        if (Constants.getMarathonProjectDirectory() == null) {
            _instance = null;
            return;
        }
        Preferences oldInstance = _instance;
        _instance = new Preferences();
        _instance.resetInstance(oldInstance);
    }

}
