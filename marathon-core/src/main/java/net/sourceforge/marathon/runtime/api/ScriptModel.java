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

import java.util.logging.Logger;

public abstract class ScriptModel {

    public static final Logger LOGGER = Logger.getLogger(ScriptModel.class.getName());

    private static IScriptModel instance;

    public static void initialize() {
        String property = System.getProperty(Constants.PROP_PROJECT_SCRIPT_MODEL);
        if (property == null) {
            throw new IllegalArgumentException("Script model not set");
        }
        try {
            instance = getModel(property);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Script model " + property + " not found - check class path");
        }
    }

    private static IScriptModel getModel(String selectedScript)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> klass = Class.forName(selectedScript);
        return (IScriptModel) klass.newInstance();
    }

    public static IScriptModel getModel() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }
}
